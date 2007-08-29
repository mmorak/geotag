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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * this class is used to generate image thumbnails
 * 
 * @author Andreas Schneider
 * 
 */
public class ThumbnailGenerator {
  /**
   * Create a thumbnail image
   * 
   * @param originalImage
   *          The image to be thumbnailed
   * @param longestSide
   *          The length of the longest side of the thumbnail image
   * @return The thumbnail
   */
  public static BufferedImage createThumbnailImage(BufferedImage originalImage,
      int longestSide) {
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();
    double scaleFactor = 1.0;
    if (width > height) {
      scaleFactor = (double) longestSide / (double) width;
    } else {
      scaleFactor = (double) longestSide / (double) height;
    }
    AffineTransform transform = new AffineTransform();
    transform.scale(scaleFactor, scaleFactor);
    int thumbnailWidth = (int) (width * scaleFactor);
    int thumbnailHeight = (int) (height * scaleFactor);
    // draw original image to thumbnail image object and
    // scale/transform it to the new size on-the-fly
    BufferedImage thumbImage = new BufferedImage(thumbnailWidth,
        thumbnailHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics2D = thumbImage.createGraphics();
    graphics2D.drawImage(originalImage, transform, null);
    return thumbImage;
  }
}
