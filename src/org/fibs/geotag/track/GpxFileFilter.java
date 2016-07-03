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

package org.fibs.geotag.track;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.fibs.geotag.util.FileUtil;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A class to determine if a file is a GPX file or not.
 * 
 * @author Andreas Schneider
 * 
 */
public class GpxFileFilter extends FileFilter implements java.io.FileFilter {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(GpxFileFilter.class);

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
    return isGpxFile(file);
  }

  /**
   * Check if a file is an image file. For the tim being we only support jpeg
   * files.
   * 
   * @param file
   *          The file to be checked
   * @return True if the file name has an image extension
   */
  public static boolean isGpxFile(File file) {
    if (file.isDirectory()) {
      return false;
    }
    String extension = FileUtil.getExtension(file);
    if (extension != null) {
      if (extension.equals("gpx")) { //$NON-NLS-1$
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
    return i18n.tr("GPX files"); //$NON-NLS-1$
  }
}
