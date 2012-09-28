package org.crsh.visualvm;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent;

import javax.swing.*;
import java.io.IOException;
import java.util.Properties;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class CrashView extends DataSourceView {

  private DataViewComponent dvc;
  private Application application;

  public CrashView(Application application) {
    super(
        application,
        "Crash",
        new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("org/crsh/image/icon.png")).getImage(),
        60,
        false);
    this.application = application;
  }

  @Override
  protected DataViewComponent createComponent() {

    attach();

    //
    JEditorPane editor = new JEditorPane();
    editor.setBorder(BorderFactory.createEmptyBorder(14, 8, 14, 8));

    //
    DataViewComponent.MasterView masterView = new DataViewComponent.MasterView("Crash", null, editor);
    DataViewComponent.MasterViewConfiguration masterConfiguration = new DataViewComponent.MasterViewConfiguration(false);

    //
    dvc = new DataViewComponent(masterView, masterConfiguration);
    dvc.configureDetailsArea(new DataViewComponent.DetailsAreaConfiguration("Terminal", true), DataViewComponent.BOTTOM_LEFT);
    dvc.addDetailsView(new DataViewComponent.DetailsView("Terminal", null, 10, editor, null), DataViewComponent.BOTTOM_LEFT);

    //
    return dvc;

  }

  private void attach() {

    final VirtualMachine vm;
    try {
      
      vm = VirtualMachine.attach("" + this.application.getPid());
      vm.loadAgent(agentPath());
      
    } catch (Exception e) {
      fail(e);
    }

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

}