package org.crsh.visualvm.listener;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class InitFocusListener implements AncestorListener {

  private final JTextArea input;

  public InitFocusListener(JTextArea input) {

    if (input == null) {
      throw new NullPointerException();
    }

    this.input = input;

  }

  public void ancestorAdded(AncestorEvent event) {
    input.requestFocusInWindow();
  }
  public void ancestorRemoved(AncestorEvent event) {}
  public void ancestorMoved(AncestorEvent event) {}
}
