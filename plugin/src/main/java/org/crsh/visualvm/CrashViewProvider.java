package org.crsh.visualvm;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.DataSourceViewProvider;
import com.sun.tools.visualvm.core.ui.DataSourceViewsManager;

import java.io.IOException;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class CrashViewProvider extends DataSourceViewProvider<Application> {
  
  private static DataSourceViewProvider<Application> instance =  new CrashViewProvider();

  @Override
  public boolean supportsViewFor(Application application) {
    System.setProperty("MARKER", "MARKER");
    try {
      VirtualMachine vm = VirtualMachine.attach(String.valueOf(application.getPid()));
      if (vm.getSystemProperties().getProperty("MARKER") != null) {
        return false;
      }
    } catch (Exception e) {}
    return true;
  }

  @Override
  public synchronized DataSourceView createView(final Application application) {
    return new CrashView(application);

  }

  static void initialize() {
    DataSourceViewsManager.sharedInstance().addViewProvider(instance, Application.class);
  }

  static void unregister() {
    DataSourceViewsManager.sharedInstance().removeViewProvider(instance);
  }

}
