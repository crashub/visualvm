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
  private final JTextField input;

  public PathChangeListener(CrashSwingController controller, JTextField input) {
    this.controller = controller;
    this.input = input;
  }

  public void actionPerformed(ActionEvent e) {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new java.io.File(controller.getCrashHome()));
    chooser.setDialogTitle("Select crash home");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);

    if (chooser.showOpenDialog(controller.getPane()) == JFileChooser.APPROVE_OPTION) {
      input.setText((chooser.getSelectedFile().getAbsolutePath()));
    }
  }
}
