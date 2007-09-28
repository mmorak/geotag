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

import org.fibs.geotag.util.FileUtil;

/**
 * An enumeration of image file types
 * 
 * @author Andreas Schneider
 * 
 */
// TODO rename to FileTypes
public enum FileTypes {
  /** Image file type for unknown files */
  UNKOWN,
  /** Image file type JPEG */
  JPEG,
  /** Image file type RAW (EXIF data read only) */
  RAW_READ_ONLY,
  /** Image file type RAW (EXIF data read and write) */
  RAW_READ_WRITE,
  /** Image file type TIFF */
  TIFF,
  /** Image file type XMP */
  XMP;

  /**
   * Determine the type of an image file
   * 
   * @param file
   *          The file
   * @return The type
   */
  public static FileTypes fileType(File file) {
    if (isJpegFile(file)) {
      return JPEG;
    } else if (isReadWriteRawFile(file)) {
      return RAW_READ_WRITE;
    } else if (isReadOnlyRawFile(file)) {
      return RAW_READ_ONLY;
    } else if (isTiffFile(file)) {
      return TIFF;
    } else if (isXmpFile(file)) {
      return XMP;
    }
    return UNKOWN;
  }

  /**
   * Check if a file is a JPEG file.
   * 
   * @param file
   *          The file to be checked
   * @return True if the file name has a JPEG extension
   */
  private static boolean isJpegFile(File file) {
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
   * Check if a file is a TIFF file.
   * 
   * @param file
   *          The file to be checked
   * @return True if the file name has a TIFF extension
   */
  private static boolean isTiffFile(File file) {
    if (file.isDirectory()) {
      return false;
    }
    String extension = FileUtil.getExtension(file);
    if (extension != null) {
      if (extension.equals("tif") || //$NON-NLS-1$
          extension.equals("tiff")) { //$NON-NLS-1$
        return true;
      }
    }
    return false;
  }

  /**
   * Check if a file is a RAW file with EXIF read/write
   * 
   * @param file
   *          The file to be checked
   * @return True if the file name has a raw file extension
   */
  private static boolean isReadWriteRawFile(File file) {
    if (file.isDirectory()) {
      return false;
    }
    String extension = FileUtil.getExtension(file);
    if (extension != null) {
      // Canon RAW 2 format
      if (extension.equals("cr2") || //$NON-NLS-1$
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
          // Panasonic RAW format
          extension.equals("raw")) { //$NON-NLS-1$
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
  private static boolean isReadOnlyRawFile(File file) {
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
  private static boolean isXmpFile(File file) {
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

}
