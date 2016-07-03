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
package org.fibs.geotag.util;

import java.awt.Font;

import junit.framework.TestCase;

/**
 * @author Andreas Schneider
 *
 */
public class FontUtilTest extends TestCase {
  /**
   * 
   */
  @SuppressWarnings("nls")
  public void testFontIds() {
    final int fontSize = 12;
    Font font = new Font("Dialog", Font.PLAIN, fontSize);
    String fontID = FontUtil.fontToID(font);
    assertEquals("Dialog-12", fontID);
    font = FontUtil.fontFromID(fontID);
    assertEquals("Dialog", font.getFamily());
    assertEquals(fontSize, font.getSize());
  }
}
