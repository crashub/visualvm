package org.crsh.visualvm.listener;

import org.crsh.visualvm.CrashSwingController;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class BufferEntryListener implements KeyListener {

  private final CrashSwingController controller;

  public BufferEntryListener(CrashSwingController controller) {
    this.controller = controller;
  }

  public void keyPressed(KeyEvent e) {
    if (Character.isLetter(e.getKeyChar()) || Character.isDigit(e.getKeyChar())) {
      controller.bufferAppend(String.valueOf(e.getKeyChar()));
    }
  }
  
  public void keyTyped(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}
}
