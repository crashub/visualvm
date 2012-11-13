package org.crsh.visualvm;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public enum Resources {

  ICON("icon.png"),
  CLEAR("clear.png"),
  CONNECT("connect.png"),
  DISCONNECT("disconnect.png"),
  CONFIG("config.png"),
  WAITING_DARK("waiting-dark.gif"),
  WAITING_LIGHT("waiting-light.gif");

  private final String name;

  private Resources(String name) {
    this.name = name;
  }

  public URL url() {
    return getClass().getClassLoader().getResource("org/crsh/image/" + name);
  }

  public ImageIcon asIcon() {
    return new ImageIcon(url());
  }
  
  public Image asImage() {
    return asIcon().getImage();
  }

}
