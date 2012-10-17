package org.crsh.visualvm.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class ColorIcon implements Icon {

  private Color color;

  public ColorIcon(Color color) {
    this.color = color;
  }

  public void paintIcon(Component c, Graphics g, int x, int y) {
    g.setColor(color);
    g.fillRect(x, y, getIconWidth(), getIconHeight());
  }

  public int getIconWidth() {
    return 10;
  }

  public int getIconHeight() {
    return 10;
  }

  public void setColor(Color color) {
    this.color = color;
  }
}
