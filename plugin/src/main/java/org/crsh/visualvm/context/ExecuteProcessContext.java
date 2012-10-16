package org.crsh.visualvm.context;

import org.crsh.shell.ShellProcessContext;
import org.crsh.shell.ShellResponse;
import org.crsh.text.Chunk;
import org.crsh.text.Style;
import org.crsh.text.Text;
import org.crsh.visualvm.CrashSwingController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class ExecuteProcessContext implements ShellProcessContext {

  private final CrashSwingController controller;

  private final List<ResultOuput> buffer;
  private boolean canceled;

  private Style style;

  public ExecuteProcessContext(CrashSwingController controller) {

    if (controller == null) {
      throw new NullPointerException();
    }

    this.controller = controller;
    this.buffer = new ArrayList<ResultOuput>();

  }

  public int getWidth() {
    return controller.getWidth();
  }

  public String getProperty(String name) {
    return null;
  }

  public String readLine(String msg, boolean echo) {
    return null;
  }

  public void write(Chunk chunk) throws NullPointerException, IOException {

    if (controller.isWaiting()) {
      controller.setWaiting(false);
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
    if (!canceled) {
      for (ResultOuput output : buffer) {
        controller.append(output.value, output.style);
      }
    }
  }

  public void end(ShellResponse response) {
    controller.setWaiting(false);
  }

  public void cancel() {
    canceled = true;
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
