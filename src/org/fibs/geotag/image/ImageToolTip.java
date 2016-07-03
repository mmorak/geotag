/**
 * Geotag
 * Copyright (C) 2007-2016 Andreas Schneider
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fibs.geotag.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.plaf.basic.BasicToolTipUI;

/**
 * This class extends JToolTip so it displays an image instead of text.
 * 
 * @author Andreas Schneider
 */
@SuppressWarnings("serial")
public class ImageToolTip extends JToolTip {
  /** The image to be displayed in the tooltip. */
  private ImageIcon imageIcon;

  /** The custom UI used by this tooltip. */
  private ImageToolTipUI toolTipUi;

  /**
   * Construct an ImageToolTip.
   * 
   * @param imageIcon
   *          The imageIcon to be displayed in the tooltip
   * @param alternativeText
   *          The text to display if there is no image icon
   */
  public ImageToolTip(ImageIcon imageIcon, String alternativeText) {
    setTipText(alternativeText);
    this.imageIcon = imageIcon;
    this.toolTipUi = new ImageToolTipUI(imageIcon, getTipText());
    this.setUI(toolTipUi);
  }

  /**
   * @return the imageIcon
   */
  public ImageIcon getImageIcon() {
    return imageIcon;
  }

  /**
   * @param imageIcon
   *          the imageIcon to set
   */
  public void setImageIcon(ImageIcon imageIcon) {
    this.imageIcon = imageIcon;
    // Also tell the UI about the change
    toolTipUi.setImageIcon(imageIcon);
    invalidate();
  }
}

/**
 * Customise the BasicToolkitUI to draw the tool tip image.
 */
class ImageToolTipUI extends BasicToolTipUI {
  /** The imageIcon to be drawn. */
  private ImageIcon imageIcon;

  /** the text to display if there is no image. */
  private String text = ""; //$NON-NLS-1$

  /** the size in pixels of the space left around the text. */
  private static final int GAP = 2;

  /**
   * Construct the ImageToolTipUI.
   * 
   * @param imageIcon
   * @param text
   * 
   */
  public ImageToolTipUI(ImageIcon imageIcon, String text) {
    this.imageIcon = imageIcon;
    if (text != null) {
      this.text = text;
    }
  }

  /**
   * @see javax.swing.plaf.basic.BasicToolTipUI#paint(java.awt.Graphics,
   *      javax.swing.JComponent)
   */
  @Override
  public void paint(Graphics graphics, JComponent c) {
    if (graphics != null) {
      if (imageIcon != null) {
        graphics.drawImage(imageIcon.getImage(), 0, 0, c);
      } else {
        graphics.setColor(Color.BLACK);
        int ascent = graphics.getFontMetrics().getAscent();
        graphics.drawString(text, GAP, GAP + ascent);
      }
    }
  }

  /**
   * @see javax.swing.plaf.basic.BasicToolTipUI#getPreferredSize(javax.swing.JComponent)
   */
  @Override
  public Dimension getPreferredSize(JComponent c) {
    if (imageIcon != null) {
      return new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight());
    }
    int width = c.getFontMetrics(c.getFont()).stringWidth(text);
    int height = c.getFontMetrics(c.getFont()).getHeight();
    return new Dimension(width + GAP * 2, height + GAP * 2);
  }

  /**
   * @return the imageIcon
   */
  public ImageIcon getImageIcon() {
    return imageIcon;
  }

  /**
   * @param imageIcon
   *          the imageIcon to set
   */
  public void setImageIcon(ImageIcon imageIcon) {
    this.imageIcon = imageIcon;
  }
}
