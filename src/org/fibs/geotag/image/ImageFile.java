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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A class encapsulating image files and how their image data is read.
 * 
 * @author Andreas Schneider
 * 
 */
public abstract class ImageFile {
  /** the actual image file. */
  private File file;

  /**
   * Constructor with package visibility. Use Factory to construct.
   * 
   * @param file
   */
  ImageFile(File file) {
    this.file = file;
  }

  /**
   * @return The BufferedImage read from the image file
   * @throws IOException
   */
  public abstract BufferedImage read() throws IOException;

  /**
   * @return the file
   */
  protected File getFile() {
    return file;
  }
}
