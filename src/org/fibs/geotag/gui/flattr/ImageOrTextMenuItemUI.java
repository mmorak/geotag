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
package org.fibs.geotag.gui.flattr;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.plaf.basic.BasicMenuItemUI;

/**
 * A custom MenuItemUI that displays the image if available or
 * the alternativ text, but not both.
 * @author andreas
 */
public class ImageOrTextMenuItemUI extends BasicMenuItemUI {
  
  /** The image to be painted instead of the text */
  private ImageIcon backgroundImage;
  
  /**
   * @param backgroundImage The initial image (can be null)
   */
  public ImageOrTextMenuItemUI(ImageIcon backgroundImage) {
    this.backgroundImage = backgroundImage;
  }
  
  /**
   * @param backgroundImage An update to the image to be painted
   */
  public void setBackgroundImage(ImageIcon backgroundImage) {
    this.backgroundImage = backgroundImage;
  }
  
  /**
   * We draw the image instead of the text, so this is the correct method to override
   * @see javax.swing.plaf.basic.BasicMenuItemUI#paintText(java.awt.Graphics, javax.swing.JMenuItem, java.awt.Rectangle, java.lang.String)
   */
  @Override
  protected void paintText(Graphics g, JMenuItem theMenuItem, Rectangle rectangle, String text) {
    if (this.backgroundImage != null) {
      // draw the image if it's not null
      g.drawImage(this.backgroundImage.getImage(), rectangle.x, rectangle.y, backgroundImage.getIconWidth(), backgroundImage.getIconHeight(), theMenuItem);
    } else {
      // draw the text otherwise
      super.paintText(g, theMenuItem, rectangle, text);
    }
  }
}
