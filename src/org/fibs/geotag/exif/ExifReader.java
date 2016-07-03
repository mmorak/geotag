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

package org.fibs.geotag.exif;

import java.io.File;

import org.fibs.geotag.data.ImageInfo;

/**
 * An interface implemented by classes reading Exif data from files.
 * 
 * @author Andreas Schneider
 * 
 */
public interface ExifReader {
  /**
   * Read EXIf info from the given file and create an {@link ImageInfo} object
   * containing it. If an {@link ImageInfo} object for the file already exists,
   * the reader must update it and not create a new one.
   * 
   * @param file
   *          The image file to be read
   * @param reuseImageInfo
   *          Store results in this ImageInfo, if it is not null
   * @return The {@link ImageInfo} containg the EXIF data
   */
  ImageInfo readExifData(File file, ImageInfo reuseImageInfo);
}
