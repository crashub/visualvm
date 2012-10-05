package org.crsh.visualvm.listener;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class TransferFocusListener implements MouseListener {

  private final JTextArea input;

  public TransferFocusListener(JTextArea input) {

    if (input == null) {
      throw new NullPointerException();
    }

    this.input = input;

  }

  public void mouseClicked(MouseEvent e) {
    input.requestFocusInWindow();
  }

  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  
}
