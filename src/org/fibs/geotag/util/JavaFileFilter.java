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

import javax.swing.filechooser.FileFilter;

/**
 * A file filter for directories and dot-java files.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("nls")
// This is for developers only - no need to translate
public class JavaFileFilter extends FileFilter implements java.io.FileFilter {

  /**
   * Accept directories and dot-java files.
   * 
   * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
   */
  @Override
  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;
    }
    return isJavaFile(file);
  }

  /**
   * Check if a file is a java file.
   * 
   * @param file
   *          The file to be checked
   * @return True if the file is a directory or ends in dot-java
   */
  public static boolean isJavaFile(File file) {
    if (file.isDirectory()) {
      return false;
    }
    String extension = FileUtil.getExtension(file);
    if (extension != null) {
      if (extension.equals("java")) {
        return true;
      }
    }
    return false;
  }

  /**
   * @see javax.swing.filechooser.FileFilter#getDescription()
   */
  @Override
  public String getDescription() {
    return "Java files";
  }

}
