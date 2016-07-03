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

/**
 * @author Andreas Schneider
 * 
 */
public final class ImageFileFactory {
  /**
   * hide constructor.
   */
  private ImageFileFactory() {
    // hide constructor
  }
  /**
   * Return an instance of an image file.
   * 
   * @param file
   *          The actual file
   * @return An ImageFile object or null if file is not a recognised image file
   */
  public static ImageFile createImageFile(File file) {
    switch (FileTypes.fileType(file)) {
      case JPEG:
        return new JpegImageFile(file);
      case TIFF:
        return new TiffImageFile(file);
      case RAW_READ_ONLY:
      case RAW_READ_WRITE:
      case CUSTOM_FILE_WITH_XMP:
        return new RawImageFile(file);
      case XMP:
      case UNKOWN:
        return null;
      default:
        return null;
    }
  }
}
