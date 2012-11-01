package org.crsh.visualvm.ui;

import org.crsh.visualvm.CrashSwingController;
import org.crsh.visualvm.Resources;
import org.crsh.visualvm.Theme;
import org.crsh.visualvm.listener.DeployAgentListener;
import org.crsh.visualvm.listener.OpenConfigListener;
import org.crsh.visualvm.listener.SelectThemeListener;
import org.crsh.visualvm.listener.UnDeployAgentListener;

import javax.swing.*;
import java.awt.*;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class TopPanel extends JPanel {

  private final CrashSwingController controller;

  private final JPanel rightPanel;
  private final JPanel actionPanel;
  private final JComboBox themesCombo;

  private final JButton configButton;
  private final JButton connectButton;
  private final JButton disconnectButton;

  public TopPanel(CrashSwingController controller) {

    //
    this.controller = controller;

    //
    themesCombo = new JComboBox(new Theme[]{ Theme.DARK, Theme.LIGHT });
    themesCombo.setSelectedItem(controller.getTheme());

    //
    configButton = new JButton(Resources.CONFIG.asIcon());
    connectButton = new JButton("Connect", Resources.CONNECT.asIcon());
    disconnectButton = new JButton("Disconnect", Resources.DISCONNECT.asIcon());

    //
    actionPanel = new JPanel();
    actionPanel.setBackground(Color.WHITE);
    actionPanel.add(connectButton);

    //
    rightPanel = new JPanel();
    rightPanel.add(new JLabel("Theme :"));
    rightPanel.setBackground(Color.WHITE);
    rightPanel.add(themesCombo);
    rightPanel.add(configButton);

    setLayout(new BorderLayout());
    setBackground(Color.WHITE);
    add(actionPanel, BorderLayout.CENTER);
    add(rightPanel, BorderLayout.EAST);

    connectButton.addActionListener(new DeployAgentListener(controller));
    disconnectButton.addActionListener(new UnDeployAgentListener(controller));
    themesCombo.addItemListener(new SelectThemeListener(controller));
    configButton.addActionListener(new OpenConfigListener(controller));

  }

  public void showConnect() {
    actionPanel.removeAll();
    actionPanel.add(connectButton);
    actionPanel.repaint();
  }

  public void showDisconnect() {
    actionPanel.removeAll();
    actionPanel.add(disconnectButton);
    actionPanel.repaint();
  }
  
}
