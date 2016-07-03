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

package org.fibs.geotag.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.Icon;

/**
 * A triangular icon
 * 
 * @author andreas
 */
public class ArrowIcon implements Icon {

  /**
   * Which way should the arrow point?
   */
  public static enum Orientation {
    /** point down */
    DOWN,
    /** point right */
    RIGHT
  }

  /** Size in pixels of the icon */
  private int size;

  /** Orientation of the arrow */
  private Orientation orientation;

  /**
   * Create an ArrowIcon
   * 
   * @param size
   *          the size in pixels of the icon
   * @param orientation
   *          The orientation of the arrow/triangle
   */
  public ArrowIcon(int size, Orientation orientation) {
    this.size = size;
    this.orientation = orientation;
  }

  /**
   * @see javax.swing.Icon#getIconHeight()
   */
  @Override
  public int getIconHeight() {
    return size;
  }

  /**
   * @see javax.swing.Icon#getIconWidth()
   */
  @Override
  public int getIconWidth() {
    // TODO Auto-generated method stub
    return size;
  }

  /**
   * Here goes the actual work of painting the icon
   * 
   * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int,
   *      int)
   */
  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
    // 50% black
    g.setColor(new Color(0x80000000, true));
    Polygon triangle = new Polygon();
    switch (orientation) {
      case DOWN:
        triangle.addPoint(x, y);
        triangle.addPoint(x + size, y);
        triangle.addPoint(x + size / 2, y + size);
        break;
      case RIGHT:
        triangle.addPoint(x, y);
        triangle.addPoint(x, y + size);
        triangle.addPoint(x + size, y + size / 2);
        break;
     default:
       // nothing to be done
       break;
    }
    g.fillPolygon(triangle);
  }

}
