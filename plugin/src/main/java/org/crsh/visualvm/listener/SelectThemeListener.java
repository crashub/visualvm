package org.crsh.visualvm.listener;

import org.crsh.visualvm.CrashSwingController;
import org.crsh.visualvm.Theme;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class SelectThemeListener implements ItemListener {

  private final CrashSwingController controller;

  public SelectThemeListener(CrashSwingController controller) {
    this.controller = controller;
  }

  public void itemStateChanged(ItemEvent e) {
    controller.setTheme((Theme) e.getItem());
    controller.updateColor();
    controller.inputFocus();
  }
}
