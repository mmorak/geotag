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
    return isJpegFile(file) || isRawFile(file);
  }

  /**
   * Check if a file is a JPEG file.
   * 
   * @param file
   *          The file to be checked
   * @return True if the file name has a JPEG extension
   */
  public static boolean isJpegFile(File file) {
    if (file.isDirectory()) {
      return false;
    }
    String extension = getExtension(file);
    if (extension != null) {
      if (extension.equals("jpeg") || //$NON-NLS-1$
          extension.equals("jpg")) { //$NON-NLS-1$
        return true;
      }
    }
    return false;
  }

  /**
   * Check if a file is a RAW file
   * 
   * @param file
   *          The file to be checked
   * @return True if the file name has a raw file extension
   */
  public static boolean isRawFile(File file) {
    if (file.isDirectory()) {
      return false;
    }
    String extension = getExtension(file);
    if (extension != null) {
      if (extension.equals("cr2") || // Canon RAW 2 format //$NON-NLS-1$
          //extension.equals("crw") || // Canon RAW Camera Image File Format - not working //$NON-NLS-1$
          extension.equals("dng") || // Digital Negative //$NON-NLS-1$
          extension.equals("erf") || // Epson RAW Format //$NON-NLS-1$
          extension.equals("mef") || // Mamiya RAW format //$NON-NLS-1$
          extension.equals("mrw") || // Minolta RAW Format //$NON-NLS-1$
          extension.equals("nef") || // Nikon (RAW) Electronic Format //$NON-NLS-1$
          extension.equals("orf") || // Olympus RAW Format //$NON-NLS-1$
          extension.equals("pef") || // Pentax (RAW) Electronic Format //$NON-NLS-1$
          extension.equals("raw")) { // Panasonic RAW format //$NON-NLS-1$
        
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
    return Messages.getString("ImageFileFilter.ImageFiles"); //$NON-NLS-1$
  }

  /**
   * Determine the extension of a file
   * 
   * @param file
   *          The file to be examined
   * @return The extension if found, null otherwise
   */
  private static String getExtension(File file) {
    String extension = null;
    String fileName = file.getName();
    int index = fileName.lastIndexOf('.');

    if (index > 0 && index < fileName.length() - 1) {
      extension = fileName.substring(index + 1).toLowerCase();
    }
    return extension;
  }
}
