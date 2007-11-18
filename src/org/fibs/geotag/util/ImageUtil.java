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

package org.fibs.geotag.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * A class with image utility methods.
 * 
 * @author Andreas Schneider
 * 
 */
public final class ImageUtil {
  /**
   * hide constructor.
   */
  private ImageUtil() {
    // hide constructor
  }
  /**
   * Create a BufferedImage from an Image.
   * 
   * @param image
   *          The original image
   * @return the buffered image
   */
  public static BufferedImage bufferImage(Image image) {
    BufferedImage bufferedImage = null;
    int imageWidth = image.getWidth(null);
    int imageHeight = image.getHeight(null);
    if (imageWidth > 0 && imageHeight > 0) {
      bufferedImage = new BufferedImage(imageWidth, imageHeight,
          BufferedImage.TYPE_INT_RGB);
      Graphics g = bufferedImage.getGraphics();
      g.drawImage(image, 0, 0, null);
    }
    return bufferedImage;
  }
}
