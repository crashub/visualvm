package org.crsh.visualvm.listener;

import org.crsh.visualvm.CrashSwingController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class ClearListener implements ActionListener {

  private final CrashSwingController controller;

  public ClearListener(CrashSwingController controller) {
    this.controller = controller;
  }

  public void actionPerformed(ActionEvent e) {
    controller.clearContent();
    controller.inputFocus();
    controller.historyClear();
  }
  
}
