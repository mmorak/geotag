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

import junit.framework.TestCase;

/**
 * @author andreas
 * 
 */
public class Ascii2NativeTest extends TestCase {
  /**
   * test case
   */
  public void testConversion() {
    String nat = "\u00E4\u00F6\u00FCABC"; //$NON-NLS-1$
    assertEquals(6, nat.length());
    String asc = Ascii2Native.native2Ascii(nat);
    System.out.println(asc);
    assertEquals("\\u00E4\\u00F6\\u00FCABC", asc); //$NON-NLS-1$
    String back = Ascii2Native.ascii2Native(asc);
    System.out.println(back);
    assertEquals(nat, back);
  }
}
