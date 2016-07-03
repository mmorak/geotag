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

import java.io.File;

/**
 * A utility class dealing with files.
 * 
 * @author Andreas Schneider
 * 
 */
public final class FileUtil {
  /**
   * hide constructor.
   */
  private FileUtil() {
    // hide constructor
  }
  
  /**
   * Determine the extension of a file.
   * 
   * @param file
   *          The file to be examined
   * @return The extension if found, null otherwise
   */
  public static String getExtension(File file) {
    return getExtension(file.getName());
  }

  /**
   * Determine the extension of a file name.
   * 
   * @param fileName
   *          the name of the file
   * @return The extension if found, null otherwise
   */
  private static String getExtension(String fileName) {
    int index = fileName.lastIndexOf('.');
    String extension = null;
    if (index > 0 && index < fileName.length() - 1) {
      extension = fileName.substring(index + 1).toLowerCase();
    }
    return extension;
  }

  /**
   * Create a file name where the file's extension is replaced.
   * 
   * @param fileName
   *          The original file name
   * @param replacement
   *          The new extesion
   * @return The file name with a new extension, or null if not possible
   */
  public static String replaceExtension(String fileName, String replacement) {
    String extension = getExtension(fileName);
    if (extension == null) {
      return null;
    }
    String basename = fileName.substring(0, fileName.length()
        - extension.length());
    return basename + replacement;
  }

}
