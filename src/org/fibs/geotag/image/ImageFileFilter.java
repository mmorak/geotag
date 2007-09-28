/**
 * Geotag
 * Copyright (C) 2007 Andreas Schneider
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

package org.fibs.geotag.image;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.fibs.geotag.Messages;

/**
 * A class to determine if a file is an image or not
 * 
 * @author Andreas Schneider
 * 
 */
public class ImageFileFilter extends FileFilter implements java.io.FileFilter {

  /**
   * Accept directories and image files
   * 
   * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
   */
  @Override
  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;
    }
    FileTypes fileType = FileTypes.fileType(file);
    switch (fileType) {
      case UNKOWN:
        return false;
      case JPEG:
        return true;
      case RAW_READ_ONLY:
        return true;
      case RAW_READ_WRITE:
        return true;
      case TIFF:
        return true;
      case XMP:
        return false;
    }
    return false;
  }

  /**
   * @see javax.swing.filechooser.FileFilter#getDescription()
   */
  @Override
  public String getDescription() {
    return Messages.getString("ImageFileFilter.ImageFiles"); //$NON-NLS-1$
  }

}
