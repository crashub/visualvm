package org.crsh.visualvm;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.datasource.Storage;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent;
import org.crsh.shell.Shell;
import org.crsh.shell.impl.remoting.RemoteServer;
import org.crsh.visualvm.listener.*;
import org.crsh.visualvm.ui.ColorIcon;
import org.crsh.visualvm.ui.WaitingPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class CrashView extends DataSourceView {

  private final Application application;
  private final RemoteServer server;
  private final CrashSwingController controller;

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
    this.controller = initUI();

  }

  private CrashSwingController initUI() {
    Shell shell = attach();

    //
    JPopupMenu candidates = new JPopupMenu();
    Font font = new Font("Monospaced", Font.PLAIN, 14);
    Border border = BorderFactory.createEmptyBorder(14, 8, 14, 8);

    //
    JTextPane editor = new JTextPane();
    editor.setBorder(border);
    editor.setFont(font);
    editor.setText(shell.getWelcome());
    editor.setEditable(false);

    JScrollPane scrollPane = new JScrollPane(editor);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());

    JTextArea input = new JTextArea();
    input.setBorder(border);
    input.setFont(font);

    JLabel promptLabel = new JLabel(shell.getPrompt());
    promptLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

    JPanel bottomPane = new JPanel();
    bottomPane.setLayout(new BorderLayout());
    bottomPane.add(promptLabel, BorderLayout.WEST);
    bottomPane.add(input, BorderLayout.CENTER);

    WaitingPanel pane = new WaitingPanel();
    pane.setLayout(new BorderLayout());
    pane.add(scrollPane, BorderLayout.CENTER);
    pane.add(bottomPane, BorderLayout.SOUTH);

    JPanel configPanel = new JPanel();
    configPanel.setBackground(Color.WHITE);

    JLabel crashHomeLabel = new JLabel();
    configPanel.add(crashHomeLabel);

    JButton browse = new JButton("Browse");
    configPanel.add(browse);

    JButton bgColor = new JButton("Background", new ColorIcon(null));
    configPanel.add(bgColor);

    JButton fgColor = new JButton("Foreground", new ColorIcon(null));
    configPanel.add(fgColor);

    pane.add(configPanel, BorderLayout.NORTH);

    CrashSwingController controller = new CrashSwingController(shell, pane, editor, promptLabel, input, candidates, scrollPane, bottomPane, crashHomeLabel, bgColor, fgColor);

    input.addKeyListener(new TermKeyListener(controller));
    editor.addMouseListener(new TransferFocusListener(controller));
    editor.addMouseListener(new CancelListener(controller));
    bgColor.addActionListener(new ColorChangeListener(controller, ColorChangeListener.Type.BACKGROUND));
    fgColor.addActionListener(new ColorChangeListener(controller, ColorChangeListener.Type.FOREGROUND));
    browse.addActionListener(new PathChangeListener(controller));

    return controller;

  }

  @Override
  protected DataViewComponent createComponent() {

    //
    DataViewComponent.MasterView masterView = new DataViewComponent.MasterView("Crash", null, controller.getPane());
    DataViewComponent.MasterViewConfiguration masterConfiguration = new DataViewComponent.MasterViewConfiguration(false);

    //
    DataViewComponent dvc = new DataViewComponent(masterView, masterConfiguration);
    dvc.configureDetailsArea(new DataViewComponent.DetailsAreaConfiguration("Terminal", true), DataViewComponent.BOTTOM_LEFT);
    dvc.addDetailsView(new DataViewComponent.DetailsView("Terminal", null, 10, controller.getPane(), null), DataViewComponent.BOTTOM_LEFT);
    dvc.addAncestorListener(new InitFocusListener(controller));

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