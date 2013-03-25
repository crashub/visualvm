package org.crsh.visualvm.ui;

import org.crsh.visualvm.CrashSwingController;
import org.crsh.visualvm.listener.PathChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class ConfigFrame extends JDialog {

  private final CrashSwingController controller;

  private final JPanel panel;
  private final JPanel topPanel;
  private final JPanel bottomPanel;
  private final JLabel label;
  private final JTextField input;
  private final JButton browseButton;
  private final JButton cancelButton;
  private final JButton saveButton;

  public ConfigFrame(final CrashSwingController controller, Frame owner) {
    super(owner, "Configuration", ModalityType.APPLICATION_MODAL);
    setLocationRelativeTo(null);

    this.controller = controller;
    this.panel = new JPanel();
    this.topPanel = new JPanel();
    this.bottomPanel = new JPanel();
    this.label = new JLabel("Crash home :");
    this.browseButton = new JButton("Browse");
    this.cancelButton = new JButton("Cancel");
    this.saveButton = new JButton("Save");
    this.input = new JTextField();
    input.setText(controller.getCrashHome());

    topPanel.add(label);
    topPanel.add(input);
    topPanel.add(browseButton);

    bottomPanel.add(cancelButton);
    bottomPanel.add(saveButton);

    panel.setLayout(new BorderLayout());
    panel.add(topPanel, BorderLayout.NORTH);
    panel.add(bottomPanel, BorderLayout.SOUTH);

    add(panel);

    browseButton.addActionListener(new PathChangeListener(controller, input));
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    saveButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        controller.setCrashHome(input.getText());
        dispose();
      }
    });

    setPreferredSize(new Dimension(500, 100));
    pack();
    setResizable(false);

  }
}
