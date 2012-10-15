package org.crsh.visualvm.listener;

import org.crsh.visualvm.CrashView;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class CancelListener implements MouseListener {

  private final CrashView view;

  public CancelListener(CrashView view) {

    if (view == null) {
      throw new NullPointerException();
    }

    this.view = view;

  }

  public void mouseClicked(MouseEvent e) {
    view.cancelWaiting();
  }

  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
}
