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
import org.fibs.geotag.util.FileUtil;

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
    String extension = FileUtil.getExtension(file);
    if (extension != null) {
      if (extension.equals("jpeg") || //$NON-NLS-1$
          extension.equals("jpg") || //$NON-NLS-1$
          extension.equals("thm")) { //$NON-NLS-1$
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
    String extension = FileUtil.getExtension(file);
    if (extension != null) {
      // Sony Alpha RAW format - read only
      if (extension.equals("arw") || //$NON-NLS-1$
          // Canon RAW 2 format
          extension.equals("cr2") || //$NON-NLS-1$
          // Kodak Digital Camera RAW - read only
          extension.equals("dcr") || //$NON-NLS-1$
          // Digital Negative
          extension.equals("dng") || //$NON-NLS-1$
          // Epson RAW Format
          extension.equals("erf") || //$NON-NLS-1$
          // Mamiya RAW format
          extension.equals("mef") || //$NON-NLS-1$
          // Minolta RAW Format
          extension.equals("mrw") || //$NON-NLS-1$
          // Nikon (RAW) Electronic Format
          extension.equals("nef") || //$NON-NLS-1$
          // Olympus RAW Format
          extension.equals("orf") || //$NON-NLS-1$
          // Pentax (RAW) Electronic Format
          extension.equals("pef") || //$NON-NLS-1$
          // FujiFilm RAW Format - read only
          extension.equals("raf") || //$NON-NLS-1$
          // Panasonic RAW format
          extension.equals("raw") || //$NON-NLS-1$
          // Sony RAW 2 format - read only
          extension.equals("sr2") || //$NON-NLS-1$
          // Sony RAW Format - read only
          extension.equals("srf") || //$NON-NLS-1$
          // Sigma/Foveon RAW format - read only
          extension.equals("x3f")) { //$NON-NLS-1$
        return true;
      }
    }
    return false;
  }

  /**
   * @param file
   *          The file to be checked
   * @return True, if the RAW file is read only in exiftool
   */
  public static boolean isReadOnlyRawFile(File file) {
    if (file.isDirectory()) {
      return false;
    }
    String extension = FileUtil.getExtension(file);
    if (extension != null) {
      // Sony Alpha RAW format - read only
      if (extension.equals("arw") || //$NON-NLS-1$
          // Kodak Digital Camera RAW - read only
          extension.equals("dcr") || //$NON-NLS-1$
          // FujiFilm RAW Format - read only
          extension.equals("raf") || //$NON-NLS-1$
          // Sony RAW 2 format - read only
          extension.equals("sr2") || //$NON-NLS-1$
          // Sony RAW Format - read only
          extension.equals("srf") || //$NON-NLS-1$
          // Sigma/Foveon RAW format - read only
          extension.equals("x3f")) { //$NON-NLS-1$
        return true;
      }
    }
    return false;
  }

  /**
   * Check if a file is an XMP file
   * 
   * @param file
   *          The file to be checked
   * @return True if the file name has an xmp file extension
   */
  public static boolean isXmpFile(File file) {
    if (file.isDirectory()) {
      return false;
    }
    String extension = FileUtil.getExtension(file);
    if (extension != null) {
      if (extension.equals("xmp")) { //$NON-NLS-1$
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

}
