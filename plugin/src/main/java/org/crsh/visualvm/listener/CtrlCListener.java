package org.crsh.visualvm.listener;

import org.crsh.visualvm.CrashSwingController;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class CtrlCListener implements KeyListener {

  private final CrashSwingController controller;

  public CtrlCListener(CrashSwingController controller) {
    this.controller = controller;
  }

  public void keyTyped(KeyEvent e) {
  }

  public void keyPressed(KeyEvent e) {

    switch (e.getKeyCode()) {
        case KeyEvent.VK_C :
        if (e.isControlDown()) {
          controller.cancelProcess();
        }
        break;
    }
    
  }

  public void keyReleased(KeyEvent e) {
  }
}
