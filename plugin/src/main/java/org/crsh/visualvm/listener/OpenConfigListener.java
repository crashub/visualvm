package org.crsh.visualvm.listener;

import org.crsh.visualvm.CrashSwingController;
import org.crsh.visualvm.ui.ConfigFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class OpenConfigListener implements ActionListener {

  private final CrashSwingController controller;
  private ConfigFrame configFrame;

  public OpenConfigListener(CrashSwingController controller) {
    this.controller = controller;
  }

  public void actionPerformed(ActionEvent e) {

    if (configFrame == null) {
      configFrame = new ConfigFrame(controller, (Frame) SwingUtilities.getRoot(controller.getPane()));
    }
    configFrame.setVisible(true);
    
  }
  
}
