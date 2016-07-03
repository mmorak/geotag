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

package org.fibs.geotag.image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.exif.Exiftool;
import org.fibs.geotag.util.FileUtil;

/**
 * An enumeration of image file types.
 * 
 * @author Andreas Schneider
 * 
 */
public enum FileTypes {
  /** Image file type for unknown files. */
  UNKOWN,
  /** Image file type JPEG. */
  JPEG,
  /** Image file type RAW (EXIF data read only). */
  RAW_READ_ONLY,
  /** Image file type RAW (EXIF data read and write). */
  RAW_READ_WRITE,
  /** Image file type TIFF. */
  TIFF,
  /** Image file type XMP. */
  XMP,
  /** Image file with XMP file (user specified) */
  CUSTOM_FILE_WITH_XMP;

  /**
   * Determine the type of an image file.
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
    } else if (isCustomFileWithXmpFile(file)) {
      return FileTypes.CUSTOM_FILE_WITH_XMP;
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
   * Check if a file is a RAW file with EXIF read/write.
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
          extension.equals("raw") || //$NON-NLS-1$
          // Canon RAW format. Can't write GPS data to it,
          // but XMP files can be used for that purpose.
          extension.equals("crw")) { //$NON-NLS-1$
        return true;
      }
      if (extension.equals("rw2")) { //$NON-NLS-1$
        // Panasonic RW2 files are read/write supported since Exiftool 7.73
        if (Exiftool.getVersion() != null && Exiftool.getVersion().compareTo("7.73") >=0) { //$NON-NLS-1$
          return true;
        }
      }
      if (extension.equals("nrw")) { //$NON-NLS-1$
        // Nikon NRW files are read/write supported since Exiftool 7.90 (or thereabouts)
        if (Exiftool.getVersion() != null && Exiftool.getVersion().compareTo("7.90") >=0) { //$NON-NLS-1$
          return true;
        }
      }
      if (extension.equals("srw")) { //$NON-NLS-1$
        // Samsung SRW files are read/write supported since Exiftool 8.13
        if (Exiftool.getVersion() != null && Exiftool.getVersion().compareTo("8.13") >=0) { //$NON-NLS-1$
          return true;
        }
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
   * Check if a file is an XMP file.
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
  
  /**
   * Check if a file is of a type specified by the user
   * as not being supported, but having an XMP file to read
   * and write the data from and to.
   * @param file The file to be checked
   * @return True if the file is a custom file with XMP sidecar
   */
  private static boolean isCustomFileWithXmpFile(File file) {
    if (file.isDirectory()) {
      return false;
    }
    String extension = FileUtil.getExtension(file);
    List<String> fileTypes = getFileTypesSupportedByXmp();
    for (String fileType : fileTypes) {
      if (fileType.toLowerCase().equals(extension)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Return a list of user specified custom file extensions.
   * For the vast majority of users this should be an empty list
   * @return The (possibly empty) list of file types supported by 
   * XMP sidecars
   */
  public static  List<String> getFileTypesSupportedByXmp() {
    List<String> fileTypesSupportedByXmp = new ArrayList<String>();
    // Get the user setting
    String fileTypesSetting = Settings.get(SETTING.FILE_TYPES_SUPPORTED_BY_XMP, ""); //$NON-NLS-1$
    // Tokenize the setting
    // We allow spaces and commas as separators. We also allow dots in case the user
    // specifies for example 'img' files by '.img'
    StringTokenizer tokenizer = new StringTokenizer(fileTypesSetting, " ,."); //$NON-NLS-1$
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      fileTypesSupportedByXmp.add(token);
    }
    return fileTypesSupportedByXmp;
  }

}
