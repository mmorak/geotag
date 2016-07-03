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
import java.util.StringTokenizer;

/**
 * A class with utility methods for Fonts.
 * 
 * @author Andreas Schneider
 * 
 */

public final class FontUtil {

  /**
   * hide constructor.
   */
  private FontUtil() {
    // hide constructor
  }
  
  /**
   * Create an id for the given font.
   * 
   * @param font
   * @return Font id of form Family-Size
   */
  public static String fontToID(Font font) {
    return font.getFamily() + '-' + font.getSize();
  }

  /**
   * Create a Font given an Id created by fontToID().
   * 
   * @param id
   * @return The Font
   */
  public static Font fontFromID(String id) {
    Font font = null;
    try {
      StringTokenizer tokenizer = new StringTokenizer(id, "-"); //$NON-NLS-1$
      String family = tokenizer.nextToken();
      int size = Integer.parseInt(tokenizer.nextToken());
      font = new Font(family, Font.PLAIN, size);
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
    return font;
  }
}
