package org.crsh.visualvm.context;

import org.crsh.shell.ShellProcessContext;
import org.crsh.shell.ShellResponse;
import org.crsh.text.Chunk;
import org.crsh.text.Style;
import org.crsh.text.Text;
import org.crsh.visualvm.CrashView;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class ExecuteProcessContext implements ShellProcessContext {

  private final CrashView view;
  private final int width;
  private final List<ResultOuput> buffer;
  
  private Style style;

  public ExecuteProcessContext(CrashView view) {

    if (view == null) {
      throw new NullPointerException();
    }

    this.view = view;
    this.width = view.getWidth();
    this.buffer = new ArrayList<ResultOuput>();
    
    this.view.setWaiting(true);

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

    if (view.isWaiting()) {
      view.setWaiting(false);
    }

    if (chunk instanceof Text) {
      CharSequence seq = ((Text) chunk).getText();
      if (seq.length() > 0) {
        buffer.add(new ResultOuput(seq.toString(), style));
      }
    } else if (chunk instanceof Style) {
      style = (Style) chunk;
      
    }
  }

  public void flush() {
    for (ResultOuput output : buffer) {
      view.append(output.value, output.style);
    }
  }

  public void end(ShellResponse response) {
    view.setWaiting(false);
  }

  class ResultOuput {

    private final String value;
    private final Style style;

    ResultOuput(String value, Style style) {
      this.value = value;
      this.style = style;
    }

  }

}
