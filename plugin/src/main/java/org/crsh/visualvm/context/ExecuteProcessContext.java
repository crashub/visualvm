package org.crsh.visualvm.context;

import org.crsh.shell.ShellProcessContext;
import org.crsh.shell.ShellResponse;
import org.crsh.text.CLS;
import org.crsh.text.Chunk;
import org.crsh.text.Style;
import org.crsh.text.Text;
import org.crsh.visualvm.CrashSwingController;

import javax.swing.text.DefaultStyledDocument;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class ExecuteProcessContext implements ShellProcessContext {

  private final CrashSwingController controller;

  private final List<ResultOuput> buffer;
  private boolean cleared;
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
    return controller.getContentWidth();
  }

  public int getHeight() {
    return controller.getContentHeight();
  }

  public boolean takeAlternateBuffer() {
    controller.snapshot();
    return true;
  }

  public boolean releaseAlternateBuffer() {
    controller.restore();
    return true;
  }

  public String getProperty(String name) {
    return null;
  }

  public String readLine(String msg, boolean echo) {
    return null;
  }

  public void flush() {
    if (!canceled) {
      if (cleared) {
        controller.reloadContent(Collections.unmodifiableList(buffer), new DefaultStyledDocument());
        cleared = false;
      } else {
        controller.append(Collections.unmodifiableList(buffer));
      }
      buffer.clear();
    }
  }

  public void end(ShellResponse response) {

    if (response instanceof ShellResponse.UnknownCommand) {
      ResultOuput output = new ResultOuput(response.getMessage(), null);
      controller.append(Arrays.asList(output));
    }

    if (controller.isWaiting()) {
      controller.setWaiting(false);
    }
    
    controller.inputEnable();
    controller.inputFocus();
  }

  public void provide(Chunk chunk) throws IOException {

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
    } else if (chunk instanceof CLS) {
      cleared = true;
    }

  }

  public Class<Chunk> getConsumedType() {
    return Chunk.class;
  }

  public void cancel() {
    canceled = true;
  }

  public class ResultOuput {

    public final String value;
    public final Style style;

    ResultOuput(String value, Style style) {
      this.value = value;
      this.style = style;
    }

  }

}
