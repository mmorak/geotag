/**
 * Geotag
 * Copyright (C) 2007-2015 Andreas Schneider
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

import java.io.File;

import junit.framework.TestCase;

/**
 * @author Andreas Schneider
 *
 */
public class FileUtilTest extends TestCase {
  /**
   * 
   */
  @SuppressWarnings("nls")
  public void testGetExtension() {
    File file = new File("test.TXT");
    String extension = FileUtil.getExtension(file);
    assertEquals("txt", extension);
  }
}
