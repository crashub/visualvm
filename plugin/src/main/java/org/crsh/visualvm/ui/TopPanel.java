package org.crsh.visualvm.ui;

import org.crsh.visualvm.CrashSwingController;
import org.crsh.visualvm.Resources;
import org.crsh.visualvm.Theme;
import org.crsh.visualvm.listener.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class TopPanel extends JPanel {

  private final CrashSwingController controller;

  private final JPanel rightPanel;
  private final JComboBox themesCombo;

  private final JButton configButton;
  private final JButton disconnectButton;
  private final JButton clearButton;

  public TopPanel(CrashSwingController controller) {

    //
    this.controller = controller;

    //
    themesCombo = new JComboBox(new Theme[]{ Theme.DARK, Theme.LIGHT });
    themesCombo.setToolTipText("Change theme");
    themesCombo.setSelectedItem(controller.getTheme());

    //
    configButton = new JButton(Resources.CONFIG.asIcon());
    configButton.setToolTipText("Configure");
    disconnectButton = new JButton(Resources.DISCONNECT.asIcon());
    disconnectButton.setToolTipText("Disconnect");
    clearButton = new JButton(Resources.CLEAR.asIcon());
    clearButton.setToolTipText("Clear");

    //
    rightPanel = new JPanel();
    rightPanel.add(new JLabel("Theme :"));
    rightPanel.setBackground(Color.WHITE);
    rightPanel.add(themesCombo);
    rightPanel.add(configButton);
    rightPanel.add(clearButton);

    setLayout(new BorderLayout());
    setBackground(Color.WHITE);
    add(rightPanel, BorderLayout.EAST);

    disconnectButton.addActionListener(new UnDeployAgentListener(controller));
    themesCombo.addItemListener(new SelectThemeListener(controller));
    configButton.addActionListener(new OpenConfigListener(controller));
    clearButton.addActionListener(new ClearListener(controller));

  }

  public void hideDisconnect() {
    rightPanel.remove(disconnectButton);
  }

  public void showDisconnect() {
    rightPanel.add(disconnectButton);
  }
  
}
