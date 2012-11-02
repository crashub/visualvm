package org.crsh.visualvm.ui;

import org.crsh.visualvm.CrashSwingController;
import org.crsh.visualvm.listener.BufferEntryListener;
import org.crsh.visualvm.listener.CtrlCListener;
import org.crsh.visualvm.listener.TermKeyListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class BottomPanel extends JPanel {

  private final CrashSwingController controller;
  private final JTextArea input;
  private final JLabel promptLabel;
  private final TermKeyListener keyListener;

  public BottomPanel(CrashSwingController controller, Font font, Border border) {

    //
    this.controller = controller;
    
    //
    this.keyListener = new TermKeyListener(controller);

    //
    input = new JTextArea();
    input.setCaretColor(controller.getTheme().fg());
    input.setBorder(border);
    input.setFont(font);
    input.addKeyListener(keyListener);
    input.addKeyListener(new CtrlCListener(controller));
    input.addKeyListener(new BufferEntryListener(controller));

    //
    promptLabel = new JLabel("");
    promptLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

    //
    setLayout(new BorderLayout());
    add(promptLabel, BorderLayout.WEST);
    add(input, BorderLayout.CENTER);
    
  }

  public void setPrompt(String prompt) {
    this.promptLabel.setText(prompt);
  }

  public void insertAtCaret(String value) {
    input.insert(value, input.getCaretPosition());
  }

  public void moveCaretToEnd() {
    input.setCaretPosition(input.getText().length());
  }

  public void setText(String value) {
    input.setText(value);
  }

  public String getText() {
    return input.getText();
  }

  public int getCaretPosition() {
    return input.getCaretPosition();
  }

  public Point getCaretPoint() {
    return input.getCaret().getMagicCaretPosition();
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if (enabled) {
      input.setEditable(true);
      input.setCaretColor(getForeground());
      moveCaretToEnd();
      input.setBackground(controller.getTheme().input());
    } else {
      input.setEditable(false);
      input.setCaretColor(controller.getTheme().input());
      input.setBackground(controller.getTheme().bg());
    }
    promptLabel.setVisible(enabled);
    keyListener.setEnabled(enabled);
  }

  @Override
  public void setForeground(Color fg) {
    super.setForeground(fg);
    if (input != null) {
      input.setForeground(fg);
      input.setCaretColor(fg);
    }
    if (promptLabel != null) {
      promptLabel.setForeground(fg);
    }
  }

  @Override
  public void setBackground(Color bg) {
    super.setBackground(bg);
    if (input != null) {
      input.setBackground(bg);
    }
    if (promptLabel != null) {
      promptLabel.setBackground(bg);
    }
  }

  @Override
  public void requestFocus() {
    input.requestFocus();
  }

  @Override
  public boolean requestFocusInWindow() {
    return input.requestFocusInWindow();
  }

}
