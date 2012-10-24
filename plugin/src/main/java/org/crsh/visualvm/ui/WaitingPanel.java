package org.crsh.visualvm.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class WaitingPanel extends JPanel {

  public boolean waiting = false;
  private Image waitingImage;

  public WaitingPanel(URL imageUrl) {
    updateImageUrl(imageUrl);
  }

  @Override
  public void paint(final Graphics g) {

    //
    super.paint(g);

    //
    if (waiting) {
      g.setColor(new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), 150));
      g.fillRect(0, 0, getWidth(), getHeight());
      g.drawImage(waitingImage, xPos(), yPos(), this);
    }

  }

  private int xPos() {
    return (getWidth() - waitingImage.getWidth(this)) / 2;
  }

  private int yPos() {
    return (getHeight() - waitingImage.getHeight(this)) / 2;
  }

  public void setWaiting(boolean waiting) {
    this.waiting = waiting;
    repaint();
  }

  public boolean isWaiting() {
    return waiting;
  }

  public void updateImageUrl(URL imageUrl) {
    this.waitingImage = this.getToolkit().createImage(imageUrl);
  }
  
}
