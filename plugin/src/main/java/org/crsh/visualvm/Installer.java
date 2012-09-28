package org.crsh.visualvm;

import org.openide.modules.ModuleInstall;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class Installer extends ModuleInstall {
  
  @Override
  public void restored() {
    CrashViewProvider.initialize();
  }

  @Override
  public void uninstalled() {
    CrashViewProvider.unregister();
  }

}
