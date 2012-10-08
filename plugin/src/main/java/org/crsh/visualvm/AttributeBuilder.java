package org.crsh.visualvm;

import org.crsh.text.Decoration;
import org.crsh.text.Style;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class AttributeBuilder {

  public MutableAttributeSet build(Style style) {

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
