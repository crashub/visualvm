package org.crsh.visualvm.listener;

import org.crsh.visualvm.CrashSwingController;
import org.crsh.visualvm.ui.ColorIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class ColorChangeListener implements ActionListener {

  private final CrashSwingController controller;
  private final Type type;

  public ColorChangeListener(CrashSwingController controller, Type type) {
    this.controller = controller;
    this.type = type;
  }

  public static enum Type {
    BACKGROUND, FOREGROUND
  }

  public void actionPerformed(ActionEvent e) {

    switch (type) {
      case BACKGROUND:
        controller.setBackgroundColor(
            JColorChooser.showDialog(controller.getPane(), "Choose Background Color", controller.getBackgroundColor())
        );
        break;
      case FOREGROUND:
        controller.setForegroundColor(
            JColorChooser.showDialog(controller.getPane(), "Choose Foreground Color", controller.getForegroundColor())
        );
        break;
    }
  }
}