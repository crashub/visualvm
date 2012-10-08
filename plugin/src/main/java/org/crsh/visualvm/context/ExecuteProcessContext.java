package org.crsh.visualvm.context;

import org.crsh.shell.ShellProcessContext;
import org.crsh.shell.ShellResponse;
import org.crsh.text.Chunk;
import org.crsh.text.Style;
import org.crsh.text.Text;
import org.crsh.visualvm.CrashView;

import javax.swing.*;
import java.io.IOException;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class ExecuteProcessContext implements ShellProcessContext {

  private final CrashView view;
  private final JScrollPane scrollPane;
  private final int width;
  
  private Style style;

  public ExecuteProcessContext(int width, CrashView view, JScrollPane scrollPane) {

    if (view == null) {
      throw new NullPointerException();
    }

    if (scrollPane == null) {
      throw new NullPointerException();
    }

    this.width = width;
    this.view = view;
    this.scrollPane = scrollPane;

  }

  public int getWidth() {
    return width;
  }

  public String getProperty(String name) {
    return null;
  }

  public String readLine(String msg, boolean echo) {
    return null;
  }

  public void write(Chunk chunk) throws NullPointerException, IOException {

    if (chunk instanceof Text) {
      CharSequence seq = ((Text) chunk).getText();
      if (seq.length() > 0) {
        view.append(seq.toString(), style);
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
      }
    } else if (chunk instanceof Style) {
      style = (Style) chunk;
      
    }
  }

  public void flush() {}
  public void end(ShellResponse response) {}

}
