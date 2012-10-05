package org.crsh.visualvm;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent;
import org.crsh.shell.Shell;
import org.crsh.shell.impl.remoting.RemoteServer;
import org.crsh.visualvm.listener.CompletionActionListener;
import org.crsh.visualvm.listener.InitFocusListener;
import org.crsh.visualvm.listener.TermKeyListener;
import org.crsh.visualvm.listener.TransferFocusListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Properties;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class CrashView extends DataSourceView {

  //
  private final Application application;
  private final RemoteServer server;

  //
  private final JPanel pane;
  private final JPanel bottomPane;
  private final JTextPane editor;
  private final StyledDocument doc;
  private final JScrollPane scrollPane;
  private final JTextArea input;
  private final JPopupMenu candidates;
  private final Font font;
  private final Border border;
  private final JLabel promptLabel;

  //
  private final Shell shell;
  private final String prompt;

  //
  private final ActionListener completionListener;
  private final MouseListener transferFocusListener;
  private final KeyListener termKeyListener;
  private final AncestorListener initFocusListener;

  public CrashView(Application application) {
    super(
        application,
        "Crash",
        new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("org/crsh/image/icon.png")).getImage(),
        60,
        false);

    //
    this.application = application;
    this.server = new RemoteServer(0);
    this.shell = attach();
    this.prompt = shell.getPrompt();
    this.promptLabel = new JLabel(prompt);

    //
    this.pane = new JPanel();
    this.bottomPane = new JPanel();
    this.editor = new JTextPane();
    this.doc = editor.getStyledDocument();
    this.scrollPane = new JScrollPane(editor);
    this.input = new JTextArea();
    this.candidates = new JPopupMenu();
    this.font = new Font("Monospaced", Font.PLAIN, 14);
    this.border = BorderFactory.createEmptyBorder(14, 8, 14, 8);

    //
    this.completionListener = new CompletionActionListener(candidates, input);
    this.transferFocusListener = new TransferFocusListener(input);
    this.termKeyListener = new TermKeyListener(this, shell, prompt, input, candidates, scrollPane, completionListener);
    this.initFocusListener = new InitFocusListener(input);

    //
    this.editor.setBorder(border);
    this.editor.setFont(font);
    this.editor.setBackground(Color.BLACK);
    this.editor.setForeground(Color.GRAY);
    this.editor.setText(shell.getWelcome());
    this.editor.setEditable(false);
    this.editor.addMouseListener(transferFocusListener);

    this.scrollPane.setBorder(BorderFactory.createEmptyBorder());

    this.input.setBorder(border);
    this.input.setFont(font);
    this.input.setBackground(Color.BLACK);
    this.input.setForeground(Color.GRAY);
    this.input.setCaretColor(Color.GRAY);
    this.input.addKeyListener(termKeyListener);

    this.pane.setBackground(Color.BLACK);
    this.pane.setLayout(new BorderLayout());
    this.pane.add(scrollPane, BorderLayout.CENTER);
    this.pane.add(bottomPane, BorderLayout.SOUTH);

    this.promptLabel.setBackground(Color.BLACK);
    this.promptLabel.setForeground(Color.GRAY);
    this.promptLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

    this.bottomPane.setBackground(Color.BLACK);
    this.bottomPane.setLayout(new BorderLayout());
    this.bottomPane.add(this.promptLabel, BorderLayout.WEST);
    this.bottomPane.add(input, BorderLayout.CENTER);

  }

  public void append(String content) {
    
    try {
      doc.insertString(doc.getLength(), content, new SimpleAttributeSet());
      scrollPane.validate();
      scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    } catch (BadLocationException ignore) {}

  }

  public int getWidth() {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(editor.getFont());
    int charWidth = metrics.charWidth('a');
    int charNumber = editor.getWidth() / charWidth;
    return charNumber - 5; // 5 handle the margin.
  }

  @Override
  protected DataViewComponent createComponent() {

    //
    DataViewComponent.MasterView masterView = new DataViewComponent.MasterView("Crash", null, pane);
    DataViewComponent.MasterViewConfiguration masterConfiguration = new DataViewComponent.MasterViewConfiguration(false);

    //
    DataViewComponent dvc = new DataViewComponent(masterView, masterConfiguration);
    dvc.configureDetailsArea(new DataViewComponent.DetailsAreaConfiguration("Terminal", true), DataViewComponent.BOTTOM_LEFT);
    dvc.addDetailsView(new DataViewComponent.DetailsView("Terminal", null, 10, pane, null), DataViewComponent.BOTTOM_LEFT);
    dvc.addAncestorListener(initFocusListener);

    //
    return dvc;

  }

  private Shell attach() {

    final VirtualMachine vm;
    try {
      
      vm = VirtualMachine.attach("" + this.application.getPid());
      vm.loadAgent(agentPath(), String.valueOf(listen()));

      //
      server.accept();

      //
      return server.getShell();



    } catch (Exception e) {
      fail(e);
    }

    return null;

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

  private int listen() {

    try {
      return server.bind();
    } catch (IOException e) {
      e.printStackTrace();
      return 0;
    }

  }

  private void fail(Exception e) {
    JOptionPane.showMessageDialog(null, "Agent deployment failed.");
    throw new RuntimeException(e);
  }

}