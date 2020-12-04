/**
 * Geotag
 * Copyright (C) 2007-2014 Andreas Schneider
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

import org.fibs.geotag.dcraw.Dcraw;

/**
 * @author Andreas Schneider
 * 
 */
public class RawImageFile extends ImageFile {

  /**
   * Constructor with package visibility. Use Factory to construct.
   * 
   * @param file
   */
  RawImageFile(File file) {
    super(file);
  }

  /**
   * @see org.fibs.geotag.image.ImageFile#read()
   */
  @Override
  public BufferedImage read() throws IOException {
    return Dcraw.getEmbeddedImage(getFile());
  }

}
