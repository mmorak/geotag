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

package org.fibs.geotag.googleearth;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.fibs.geotag.util.FileUtil;

/**
 * A class to determine if a file is a KML/KMZ file or not.
 * 
 * @author Andreas Schneider
 * 
 */
public class GoogleearthFileFilter extends FileFilter implements
    java.io.FileFilter {

  /**
   * Accept directories and GPX files.
   * 
   * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
   */
  @Override
  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;
    }
    return isKmlKmzFile(file);
  }

  /**
   * Check if a file is a KML/KMZ file.
   * 
   * @param file
   *          The file to be checked
   * @return True if the file name has an KML/KMZ extension
   */
  public static boolean isKmlKmzFile(File file) {
    if (file.isDirectory()) {
      return false;
    }
    String extension = FileUtil.getExtension(file);
    if (extension != null) {
      if (isKmlFile(file) || isKmzFile(file)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if a file is a KML file.
   * 
   * @param file
   *          The file to be checked
   * @return True if the file name has a KML extension
   */
  public static boolean isKmlFile(File file) {
    if (file.isDirectory()) {
      return false;
    }
    String extension = FileUtil.getExtension(file);
    if (extension != null) {
      if (extension.equals("kml")) { //$NON-NLS-1$
        return true;
      }
    }
    return false;
  }

  /**
   * Check if a file is a KMZ file.
   * 
   * @param file
   *          The file to be checked
   * @return True if the file name has a KMZ extension
   */
  public static boolean isKmzFile(File file) {
    if (file.isDirectory()) {
      return false;
    }
    String extension = FileUtil.getExtension(file);
    if (extension != null) {
      if (extension.equals("kmz")) { //$NON-NLS-1$
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
    return "Googleearth files"; //$NON-NLS-1$
  }

}
