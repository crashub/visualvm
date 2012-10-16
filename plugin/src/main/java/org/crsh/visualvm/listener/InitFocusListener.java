package org.crsh.visualvm.listener;

import org.crsh.visualvm.CrashSwingController;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class InitFocusListener implements AncestorListener {

  private final CrashSwingController controller;

  public InitFocusListener(CrashSwingController controller) {

    if (controller == null) {
      throw new NullPointerException();
    }

    this.controller = controller;

  }

  public void ancestorAdded(AncestorEvent event) {
    controller.inputFocus();
  }

  public void ancestorRemoved(AncestorEvent event) {}
  public void ancestorMoved(AncestorEvent event) {}
}
