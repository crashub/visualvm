package org.crsh.visualvm.ui;

import org.crsh.text.Style;
import org.crsh.visualvm.CrashSwingController;
import org.crsh.visualvm.Theme;
import org.crsh.visualvm.context.ExecuteProcessContext;
import org.crsh.visualvm.listener.TransferFocusListener;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class ContentPanel extends JTextPane {

  private final CrashSwingController controller;

  private StyledDocument doc;

  public ContentPanel(CrashSwingController controller, Font font, javax.swing.border.Border border) {

    //
    this.controller = controller;
    this.doc = getStyledDocument();

    //
    setAutoscrolls(true);
    setBorder(border);
    setFont(font);
    setEditable(false);
    setDocument(new DefaultStyledDocument());
    
    //
    addMouseListener(new TransferFocusListener(controller));

  }

  public JScrollPane asScrollable() {
    JScrollPane scrollPane =  new JScrollPane(this);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    return scrollPane;
  }

  public int getWidthInChar() {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
    int charWidth = metrics.charWidth('a');
    int charNumber = getWidth() / charWidth;
    return charNumber - 5; // 5 handle the margin.
  }

  public int getHeightInChar() {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
    int charHeight = metrics.getHeight() ;
    int charNumber = (getParent().getHeight() - 40) / charHeight; // 30 px for the input
    return charNumber;
  }
  
  public void append(String content) {
    append(content, null, doc);
  }

  public void appendTypedCommand(String content) {
    append("\n\n" + controller.getPrompt() + content + "\n\n", null, doc);
  }

  public void append(java.util.List<ExecuteProcessContext.ResultOuput> output) {
    reloadContent(output, doc);
  }

  public void reloadContent(java.util.List<ExecuteProcessContext.ResultOuput> output, StyledDocument doc) {

    for (ExecuteProcessContext.ResultOuput o : output) {
      append(o.value, o.style, doc);
    }
    setDocument(doc);
    this.doc = doc;
    setCaretPosition(doc.getLength());
  }

  public void append(String content, Style style, StyledDocument document) {

    MutableAttributeSet attributes = null;
    if (style == null) {
      attributes = new SimpleAttributeSet();
    } else {
      attributes = buildTextAttribute(style);
    }

    try {
      document.insertString(document.getLength(), content, attributes);
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

  public MutableAttributeSet buildTextAttribute(Style style) {

    //
    if (style == null) {
      throw new NullPointerException();
    }

    //
    if (style == Style.reset || !(style instanceof Style.Composite)) {
      return new SimpleAttributeSet();
    }

    //
    Style.Composite composite = (Style.Composite) style;
    MutableAttributeSet attributes = new SimpleAttributeSet();
    Color fg = mapColor(composite.getForeground());
    Color bg = mapColor(composite.getBackground());

    //
    if (fg != null) {
      StyleConstants.setForeground(attributes, fg);
    }

    //
    if (bg != null) {
      StyleConstants.setBackground(attributes, bg);
    }

    //
    if (composite.getBold() != null) {
      StyleConstants.setBold(attributes, composite.getBold());
    }

    //
    if (composite.getUnderline() != null) {
      StyleConstants.setUnderline(attributes, composite.getUnderline());
    }

    //
    return attributes;

  }

  private Color mapColor(org.crsh.text.Color c) {

    if (c == null) {
      return null;
    }

    Theme theme = controller.getTheme();

    switch (c) {
      case red: return theme.red();
      case black: return theme.black();
      case blue: return theme.blue();
      case cyan: return theme.cyan();
      case green: return theme.green();
      case magenta: return theme.magenta();
      case white: return theme.white();
      case yellow: return theme.yellow();
      default: return null;
    }
  }

}
