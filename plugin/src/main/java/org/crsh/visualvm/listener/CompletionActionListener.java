package org.crsh.visualvm.listener;

import org.crsh.visualvm.CrashSwingController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class CompletionActionListener implements ActionListener {

  private final CrashSwingController controller;

  public CompletionActionListener(CrashSwingController controller) {

    if (controller == null) {
      throw new NullPointerException();
    }

    this.controller = controller;

  }

  public void actionPerformed(ActionEvent e) {
    String value = ((JMenuItem) e.getSource()).getText();
    controller.insertCompletion(value);
  }
  
}
