package org.crsh.visualvm.listener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class CompletionActionListener implements ActionListener {

  private final JTextArea input;
  private final JPopupMenu candidates;

  public CompletionActionListener(JPopupMenu candidates, JTextArea input) {

    if (candidates == null) {
      throw new NullPointerException();
    }

    if (input == null) {
      throw new NullPointerException();
    }

    this.candidates = candidates;
    this.input = input;

  }

  public void actionPerformed(ActionEvent e) {
    String value = ((JMenuItem) e.getSource()).getText();
    input.insert(value, input.getCaretPosition());
    candidates.removeAll();
  }
  
}
