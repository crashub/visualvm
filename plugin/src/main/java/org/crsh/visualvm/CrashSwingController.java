package org.crsh.visualvm;

import com.sun.tools.visualvm.core.datasource.Storage;
import org.crsh.cmdline.CommandCompletion;
import org.crsh.shell.Shell;
import org.crsh.shell.ShellProcess;
import org.crsh.text.Decoration;
import org.crsh.text.Style;
import org.crsh.visualvm.context.ExecuteProcessContext;
import org.crsh.visualvm.ui.ColorIcon;
import org.crsh.visualvm.ui.WaitingPanel;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class CrashSwingController {

  private final WaitingPanel pane;
  private final JTextPane editor;
  private final StyledDocument doc;
  private final JScrollPane scrollPane;
  private final JLabel promptLabel;
  private final JTextArea input;
  private final JPopupMenu candidates;
  private final JPanel bottomPane;

  private final JLabel homeLabel;
  private final JButton bgButton;
  private final JButton fgButton;

  private final Shell shell;
  private final String prompt;

  private final List<String> history;
  private int historyPos = 0;
  private ExecuteProcessContext currentCtx;

  private Color backgroundColor;
  private Color foregroundColor;

  private String crashHome;
  private final Storage storage;
  private final File data;

  public CrashSwingController(Shell shell, WaitingPanel pane, JTextPane editor, JLabel promptLabel, JTextArea input, JPopupMenu candidates, JScrollPane scrollPane, JPanel bottomPane, JLabel homeLabel, JButton bgButton, JButton fgButton) {
    this.shell = shell;
    this.pane = pane;
    this.editor = editor;
    this.doc = editor.getStyledDocument();
    this.candidates = candidates;
    this.scrollPane = scrollPane;
    this.promptLabel = promptLabel;
    this.input = input;
    this.bottomPane = bottomPane;

    this.homeLabel = homeLabel;
    this.bgButton = bgButton;
    this.fgButton = fgButton;

    this.prompt = shell.getPrompt();
    this.history = new ArrayList<String>();
    
    this.data = new File(Storage.getPersistentStorageDirectory(), "crash");
    this.storage = new Storage(Storage.getPersistentStorageDirectory(), "crash");

    setCrashHome(readData("crash.home", System.getProperty("user.home") + "/crash"));
    setBackgroundColor(new Color(Integer.valueOf(readData("crash.bg", String.valueOf(Color.BLACK.getRGB())))), false);
    setForegroundColor(new Color(Integer.valueOf(readData("crash.fg", String.valueOf(Color.GRAY.getRGB())))), false);

  }

  public void setBackgroundColor(Color color, boolean persist) {
    this.backgroundColor = color;
    if (persist) writeData("crash.bg",  String.valueOf(color.getRGB()));

    editor.setBackground(color);
    input.setBackground(color);
    promptLabel.setBackground(color);
    bottomPane.setBackground(color);
    pane.setBackground(color);
    ((ColorIcon) bgButton.getIcon()).setColor(color);
  }

  public void setBackgroundColor(Color color) {
    setBackgroundColor(color, true);
  }

  public void setForegroundColor(Color color, boolean persist) {
    this.foregroundColor = color;
    if (persist) writeData("crash.fg",  String.valueOf(color.getRGB()));

    editor.setForeground(color);
    input.setForeground(color);
    input.setCaretColor(color);
    promptLabel.setForeground(color);
    pane.setForeground(color);
    ((ColorIcon) fgButton.getIcon()).setColor(color);
  }

  public void setForegroundColor(Color color) {
    setForegroundColor(color, true);
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public Color getForegroundColor() {
    return foregroundColor;
  }

  public String getCrashHome() {
    return crashHome;
  }

  public void setCrashHome(String crashHome, boolean persist) {
    this.crashHome = crashHome;
    this.homeLabel.setText("Crash home : " + crashHome);
    if (persist) writeData("crash.home",  crashHome);
  }

  public void setCrashHome(String crashHome) {
    setCrashHome(crashHome, true);
  }

  public void insertCompletion(String value) {
    input.insert(value, input.getCaretPosition());
    input.requestFocus();
    candidates.removeAll();
  }

  public boolean isWaiting() {
    return this.pane.isWaiting();
  }

  public void setWaiting(boolean b) {
    this.pane.setWaiting(b);
    editor.setFocusable(!b);
  }

  public void cancelWaiting() {
    if (this.pane.cancelWaiting() && currentCtx != null) {
      currentCtx.cancel();
      editor.setFocusable(true);
    }
  }

  public void execute(String cmd) {
    ShellProcess process = shell.createProcess(cmd);
    setWaiting(true);
    currentCtx = new ExecuteProcessContext(this);
    process.execute(currentCtx);
  }

  public CommandCompletion complete(String prefix) {
    return shell.complete(prefix);
  }

  public int getWidth() {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(editor.getFont());
    int charWidth = metrics.charWidth('a');
    int charNumber = editor.getWidth() / charWidth;
    return charNumber - 5; // 5 handle the margin.
  }

  public void historyAdd(String value) {
    history.add(value);
    historyPos = history.size();
  }

  public void historyPrevious() {
    if (historyPos > 0) {
      input.setText(history.get(--historyPos));
      input.setCaretPosition(input.getText().length());
    }
  }

  public void historyNext() {
    if (historyPos < history.size() - 1) {
      input.setText(history.get(++historyPos));
      input.setCaretPosition(input.getText().length());
    }
    else {
      input.setText("");
    }
  }

  public void inputClear() {
    input.setText("");
  }

  public void inputFocus() {
    input.requestFocusInWindow();
  }

  public String inputRead() {
    return input.getText();
  }

  public String inputReadToCaret() {
    return inputRead().substring(0, caretPosition());
  }

  public int caretPosition() {
    return input.getCaretPosition();
  }

  public void candidatesShow() {
    
    Point caretPosition = input.getCaret().getMagicCaretPosition();

    if (caretPosition == null) {
      candidates.show(input, 0, 0);
    } else {
      candidates.show(input, (int) caretPosition.getX(), (int) caretPosition.getY());
    }
    MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[]{candidates, (JMenuItem) candidates.getComponent(0)});

  }

  public void candidatesClear() {
    candidates.removeAll();
  }

  public void candidatesAdd(JMenuItem candidate) {
    candidates.add(candidate);
  }

  public void append(String content) {
    append(content, null);
  }

  public void appendTypedCommand(String content) {
    append("\n\n" + prompt + content + "\n\n", null);
  }

  public String readData(String key, String defaultValue) {

    String value = storage.getCustomProperty(key);
    if (value != null) {
      return value;
    } else {
      writeData(key, defaultValue);
      return defaultValue;
    }

  }

  public void writeData(String key, String value) {
    storage.setCustomProperty(key, value);
    storage.saveCustomPropertiesTo(data);
  }

  public void append(String content, Style style) {

    MutableAttributeSet attributes = null;
    if (style == null) {
      attributes = new SimpleAttributeSet();
    } else {
      attributes = buildTextAttribute(style);
    }

    try {
      doc.insertString(doc.getLength(), content, attributes);
      scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
      scrollPane.validate();
    } catch (BadLocationException ignore) {}

  }

  public WaitingPanel getPane() {
    return pane;
  }

  public MutableAttributeSet buildTextAttribute(Style style) {

    //
    if (style == null) {
      throw new NullPointerException();
    }

    //
    MutableAttributeSet attributes = new SimpleAttributeSet();
    Color fg = mapColor(style.getForeground());
    Color bg = mapColor(style.getBackground());
    Decoration decoration = style.getDecoration();

    //
    if (fg != null) {
      StyleConstants.setForeground(attributes, fg);
    }

    //
    if (bg != null) {
      StyleConstants.setBackground(attributes, bg);
    }

    //
    if (decoration != null) {
      switch (decoration) {
        case bold:
          StyleConstants.setBold(attributes, true);
          break;
        case underline:
          StyleConstants.setUnderline(attributes, true);
          break;
        case blink: // blink is not supported by swing.
        default:
          break;
      }
    }

    //
    return attributes;

  }

  private Color mapColor(org.crsh.text.Color c) {

    if (c == null) {
      return null;
    }

    switch (c) {
      case red: return Color.RED;
      case black: return Color.BLACK;
      case blue: return Color.BLUE;
      case cyan: return Color.CYAN;
      case green: return Color.GREEN;
      case magenta: return Color.MAGENTA;
      case white: return Color.WHITE;
      case yellow: return Color.YELLOW;
      default: return null;
    }
  }

}
