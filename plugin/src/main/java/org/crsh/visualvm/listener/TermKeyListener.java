package org.crsh.visualvm.listener;

import org.crsh.cmdline.CommandCompletion;
import org.crsh.cmdline.Delimiter;
import org.crsh.cmdline.spi.ValueCompletion;
import org.crsh.shell.Shell;
import org.crsh.shell.ShellProcess;
import org.crsh.visualvm.CrashView;
import org.crsh.visualvm.context.ExecuteProcessContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class TermKeyListener implements KeyListener {

  private final CrashView view;
  private final Shell shell;
  private final String prompt;
  private final JTextArea input;
  private final JPopupMenu candidates;
  private final ActionListener completionListener;

  private final List<String> history;
  private int historyPos = 0;

  public TermKeyListener(
      CrashView view,
      Shell shell,
      String prompt,
      JTextArea input,
      JPopupMenu candidates,
      ActionListener completionListener) {

    if (view == null) {
      throw new NullPointerException();
    }

    if (shell == null) {
      throw new NullPointerException();
    }

    if (prompt == null) {
      throw new NullPointerException();
    }

    if (input == null) {
      throw new NullPointerException();
    }

    if (candidates == null) {
      throw new NullPointerException();
    }

    if (completionListener == null) {
      throw new NullPointerException();
    }

    this.view = view;
    this.shell = shell;
    this.prompt = prompt;
    this.input = input;
    this.candidates = candidates;
    this.completionListener = completionListener;
    this.history = new ArrayList<String>();

  }

  public void keyPressed(KeyEvent e) {

    final String value = input.getText();

    switch (e.getKeyCode()) {
      case KeyEvent.VK_ENTER:
        e.consume();
        view.append("\n\n" + prompt + value + "\n\n");
        history.add(value);
        historyPos = history.size();
        input.setText("");

        //
        new SwingWorker<Void, Void>() {
          @Override
          protected Void doInBackground() throws Exception {
            ShellProcess process = shell.createProcess(value);
            process.execute(new ExecuteProcessContext(view));
            return null;
          }
        }.execute();

        break;

      case KeyEvent.VK_UP:
        e.consume();
        if (historyPos > 0) {
          input.setText(history.get(--historyPos));
          input.setCaretPosition(input.getText().length());
        }
        break;

      case KeyEvent.VK_DOWN:
        e.consume();
        if (historyPos < history.size() - 1) {
          input.setText(history.get(++historyPos));
          input.setCaretPosition(input.getText().length());
        }
        else {
          input.setText("");
        }
        break;

      case KeyEvent.VK_TAB:
        e.consume();
        final Point caretPosition = input.getCaret().getMagicCaretPosition();

        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            String prefix = value.substring(0, input.getCaretPosition());
            CommandCompletion completion = shell.complete(prefix);
            ValueCompletion vc = completion.getValue();
            if (vc.isEmpty()) {
              return;
            }

            Delimiter delimiter = completion.getDelimiter();
            try {
              if (vc.getSize() == 1) {
                StringBuilder sb = new StringBuilder();
                delimiter.escape(vc.iterator().next().getKey(), sb);
                sb.append(completion.getDelimiter().getValue());
                input.insert(sb.toString(), input.getCaretPosition());
                input.requestFocus();
              } else {
                candidates.removeAll();
                for (Map.Entry<String, Boolean> entry : vc) {
                  StringBuilder sb = new StringBuilder();
                  sb.append(vc.getPrefix());
                  delimiter.escape(entry.getKey(), sb);
                  if (entry.getValue()) {
                    sb.append(completion.getDelimiter().getValue());
                  }
                  int start = input.getText().length() - (input.getCaretPosition() - vc.getPrefix().length());
                  JMenuItem item = new JMenuItem(sb.toString().substring(start));
                  item.setSelected(true);
                  item.addActionListener(completionListener);
                  candidates.add(item);

                }

                if (caretPosition == null) {
                  candidates.show(input, 0, 0);
                } else {
                  candidates.show(input, (int) caretPosition.getX(), (int) caretPosition.getY());
                }
                MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[]{candidates, (JMenuItem) candidates.getComponent(0)});
              }
            }
            catch (IOException ignore) {}
          }
        });
        break;
    }
  }

  public void keyTyped(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}
  
}
