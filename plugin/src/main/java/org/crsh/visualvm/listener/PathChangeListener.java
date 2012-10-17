package org.crsh.visualvm.listener;

import org.crsh.visualvm.CrashSwingController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class PathChangeListener implements ActionListener {

  private final CrashSwingController controller;

  public PathChangeListener(CrashSwingController controller) {
    this.controller = controller;
  }

  public void actionPerformed(ActionEvent e) {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new java.io.File(controller.getCrashHome()));
    chooser.setDialogTitle("Select crash home");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);

    if (chooser.showOpenDialog(controller.getPane()) == JFileChooser.APPROVE_OPTION) {
      controller.setCrashHome((chooser.getSelectedFile().getAbsolutePath()));
    }
  }
}
