package org.crsh.visualvm;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.datasource.Storage;
import org.crsh.cmdline.CommandCompletion;
import org.crsh.shell.Shell;
import org.crsh.shell.ShellProcess;
import org.crsh.shell.impl.remoting.RemoteServer;
import org.crsh.visualvm.context.ExecuteProcessContext;
import org.crsh.visualvm.listener.DeployAgentListener;
import org.crsh.visualvm.ui.BottomPanel;
import org.crsh.visualvm.ui.ContentPanel;
import org.crsh.visualvm.ui.TopPanel;
import org.crsh.visualvm.ui.WaitingPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class CrashSwingController {

  //
  private final Application application;

  //
  private WaitingPanel pane;
  private TopPanel topPanel;
  private ContentPanel contentPanel;
  private BottomPanel bottomPane;
  private JScrollPane scrolledContent;

  private JPopupMenu candidates;
  private Shell shell;
  private String prompt;

  private JPanel connectPanel;
  private JButton connectButton;

  private final List<String> history;
  private int historyPos = 0;
  private ShellProcess process;
  private ExecuteProcessContext processCtx;

  private String crashHome;
  private final Storage storage;
  private final File data;
  private RemoteServer server;
  private StringBuilder inputBuffer;

  private Theme theme;

  public CrashSwingController(Application application) {
    this.application = application;
    this.history = new ArrayList<String>();
    this.data = new File(Storage.getPersistentStorageDirectory(), "crash");
    this.storage = new Storage(Storage.getPersistentStorageDirectory(), "crash");
    this.inputBuffer = new StringBuilder();
  }

  public void initUI() {

    //
    String themeName = readData("crash.theme", Theme.DARK.name());
    theme = Theme.valueOf(themeName);

    //
    pane = new WaitingPanel(theme.waiting());
    connectPanel = new JPanel(new GridBagLayout());
    candidates = new JPopupMenu();
    Font font = new Font("Monospaced", Font.PLAIN, 12);
    Border border = BorderFactory.createEmptyBorder(14, 8, 14, 8);
    connectButton = new JButton(Resources.CONNECT.asIcon());

    //
    topPanel = new TopPanel(this);
    contentPanel = new ContentPanel(this, font, border);
    bottomPane = new BottomPanel(this, font, border);
    scrolledContent = contentPanel.asScrollable();

    connectPanel.setBackground(new Color(0,0,0,0));
    connectPanel.add(connectButton, new GridBagConstraints());

    pane.add(topPanel, BorderLayout.NORTH);
    pane.add(connectPanel, BorderLayout.CENTER);

    connectButton.addActionListener(new DeployAgentListener(this));
    
    setCrashHome(readData("crash.home", System.getProperty("user.home") + "/crash"));
    updateColor();

  }

  public void showTerminal() {
    pane.remove(connectPanel);
    pane.add(scrolledContent, BorderLayout.CENTER);
    pane.add(bottomPane, BorderLayout.SOUTH);
    inputFocus();
    topPanel.showDisconnect();
    pane.repaint();
  }

  public void reinitUI() {
    pane.remove(scrolledContent);
    pane.remove(bottomPane);
    pane.add(connectPanel, BorderLayout.CENTER);
    topPanel.hideDisconnect();
    pane.repaint();
  }

  public void clearContent() {
    contentPanel.clear();
  }

  public void snapshot() {
    contentPanel.snapshot();
  }

  public void restore() {
    contentPanel.restore();
  }

  public void updateColor() {

    //Foreground
    contentPanel.setForeground(theme.fg());
    bottomPane.setForeground(theme.fg());
    pane.setForeground(theme.fg());

    // Background
    contentPanel.setBackground(theme.bg());
    contentPanel.setCaretColor(theme.bg());
    bottomPane.setBackground(theme.input());
    pane.setBackground(theme.bg());

    //
    pane.updateImage(theme.waiting());
  }

  private String agentPath() {

    Properties properties = new Properties();
    try {
      properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("org/crsh/visualvm/conf.properties"));
    } catch (IOException e) {
      fail(e);
    }

    String home = System.getProperty("netbeans.user");
    String separator = System.getProperty("file.separator");
    StringBuilder sb = new StringBuilder();
    sb.append(home);
    sb.append(separator);
    sb.append("modules");
    sb.append(separator);
    sb.append("ext");
    sb.append(separator);
    sb.append("org.crsh");
    sb.append(separator);
    sb.append("crsh.shell.core-");
    sb.append(properties.getProperty("crash.version"));
    sb.append("-standalone.jar");

    return sb.toString();

  }

  private void fail(Exception e) {
    JOptionPane.showMessageDialog(null, "Agent deployment failed.");
    throw new RuntimeException(e);
  }

  private int listen(RemoteServer server) {

    try {
      return server.bind();
    } catch (IOException e) {
      fail(e);
      return 0;
    }

  }

  /*
   * Deployment
   */

  public boolean deploy() {

    server = new RemoteServer(0);

    try {

      File cmdDir = new File(crashHome + "/cmd");
      File confDir = new File(crashHome + "/conf");

      if (!cmdDir.exists()) {
        cmdDir.mkdirs();
      }

      if (!confDir.exists()) {
        confDir.mkdirs();
      }

      StringBuilder options = new StringBuilder();

      options.append("--cmd ");
      options.append(cmdDir.getAbsolutePath());
      options.append(" --conf ");
      options.append(confDir.getAbsolutePath());
      options.append(" ");
      options.append(String.valueOf(listen(server)));

      VirtualMachine vm = VirtualMachine.attach("" + this.application.getPid());
      vm.loadAgent(agentPath(), options.toString());

      //
      server.accept();

      //
      this.shell = server.getShell();
      this.prompt = shell.getPrompt();

      this.contentPanel.setText(shell.getWelcome());
      this.bottomPane.setPrompt(shell.getPrompt());
      return true;

    } catch (Exception e) {
      fail(e);
      return false;
    }

  }

  public void undeploy() {
    server.close();
  }

  /*
   * History
   */

  public void historyClear() {
    history.clear();
    historyPos = 0;
  }

  public void historyAdd(String value) {
    history.add(value);
    historyPos = history.size();
  }

  public void historyPrevious() {
    if (historyPos > 0) {
      bottomPane.setText(history.get(--historyPos));
      bottomPane.moveCaretToEnd();
    }
  }

  public void historyNext() {
    if (historyPos < history.size() - 1) {
      bottomPane.setText(history.get(++historyPos));
      bottomPane.moveCaretToEnd();
    }
    else {
      historyPos = history.size();
      bottomPane.setText("");
    }
  }

  /*
   * Completion
   */

  public void candidatesClear() {
    candidates.removeAll();
  }

  public void candidatesAdd(JMenuItem candidate) {
    candidates.add(candidate);
  }

  public void candidatesShow() {

    Point caretPosition = bottomPane.getCaretPoint();

    if (caretPosition == null) {
      candidates.show(bottomPane, 0, 0);
    } else {
      candidates.show(bottomPane, (int) caretPosition.getX(), (int) caretPosition.getY());
    }
    MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[]{candidates, (JMenuItem) candidates.getComponent(0)});

  }

  public void insertCompletion(String value) {
    bottomPane.insertAtCaret(value);
    bottomPane.requestFocus();
    candidates.removeAll();
  }

  /*
   * Storage
   */

  public String readData(String key, String defaultValue) {

    String value = storage.getCustomProperty(key);
    if (value != null) {
      return value;
    } else {
      writeData(key, defaultValue);
      return defaultValue;
    }

  }

  public void writeData(String key, String value) {
    storage.setCustomProperty(key, value);
    storage.saveCustomPropertiesTo(data);
  }

  /*
   * Input
   */

  public String getPrompt() {
    return prompt;
  }

  public void inputDisable() {
    bottomPane.setEnabled(false);
    bufferClear();
  }

  public void inputEnable() {
    bottomPane.setEnabled(true);
    bottomPane.setText(inputBuffer.toString());
    bottomPane.moveCaretToEnd();
  }

  public void inputClear() {
    bottomPane.setText("");
  }

  public void inputFocus() {
    bottomPane.requestFocusInWindow();
  }

  public String inputRead() {
    return bottomPane.getText();
  }

  public String inputReadToCaret() {
    return inputRead().substring(0, inputCaretPosition());
  }

  public int inputCaretPosition() {
    return bottomPane.getCaretPosition();
  }

  /*
   * Content
   */

  public void appendTypedCommand(String content) {
    contentPanel.appendTypedCommand(content);
  }
  
  public void append(java.util.List<ExecuteProcessContext.ResultOuput> output) {
    contentPanel.append(output);
  }

  public void reloadContent(java.util.List<ExecuteProcessContext.ResultOuput> output, StyledDocument doc) {
    contentPanel.reloadContent(output, doc);
  }

  /*
   * Buffer
   */

  public void bufferClear() {
    inputBuffer = new StringBuilder();
  }

  public void bufferAppend(String value) {
    inputBuffer.append(value);
  }
  
  /*
   * Misc
   */

  public Theme getTheme() {
    return theme;
  }

  public WaitingPanel getPane() {
    return pane;
  }

  public int getContentWidth() {
    return contentPanel.getWidthInChar();
  }

  public int getContentHeight() {
    return contentPanel.getHeightInChar();
  }

  public void execute(String cmd) {
    process = shell.createProcess(cmd);
    setWaiting(true);
    processCtx = new ExecuteProcessContext(this);
    inputDisable();
    process.execute(processCtx);
  }

  public CommandCompletion complete(String prefix) {
    return shell.complete(prefix);
  }
  public boolean isWaiting() {
    return this.pane.isWaiting();
  }

  public void setWaiting(boolean b) {
    this.pane.setWaiting(b);
    this.contentPanel.setFocusable(!b);
  }

  public void cancelProcess() {
    if (process != null && processCtx != null) {
      process.cancel();
      processCtx.cancel();
      contentPanel.setFocusable(true);
      pane.setWaiting(false);
      contentPanel.append("\nCommand interrupted\n");
      inputEnable();
      inputFocus();
      process = null;
      processCtx = null;
    }
  }

  public void setTheme(Theme theme) {
    this.theme = theme;
    writeData("crash.theme", theme.name());
  }

  public String getCrashHome() {
    return crashHome;
  }

  public void setCrashHome(String crashHome) {
    this.crashHome = crashHome;
    writeData("crash.home",  crashHome);
  }
  
}
