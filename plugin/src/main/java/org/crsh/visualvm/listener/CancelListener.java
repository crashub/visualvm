package org.crsh.visualvm.listener;

import org.crsh.visualvm.CrashSwingController;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class CancelListener implements MouseListener {

  private final CrashSwingController controller;

  public CancelListener(CrashSwingController controller) {

    if (controller == null) {
      throw new NullPointerException();
    }

    this.controller = controller;

  }

  public void mouseClicked(MouseEvent e) {
    controller.cancelWaiting();
  }

  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
}
