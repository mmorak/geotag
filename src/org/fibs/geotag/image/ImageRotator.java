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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.fibs.geotag.data.ImageInfo;

/**
 * A utility class that knows how EXIF Orientation tags work and rotates (and
 * mirrors) images accordingly.
 * 
 * @author Andreas Schneider
 * 
 */

public class ImageRotator {
  
  /** The image to be rotated. */
  private BufferedImage image;

  /** Contains the EXIF data determining which way to rotate. */
  private ImageInfo imageInfo;

  /**
   * Create a rotator for an image with given EXIF data.
   * 
   * @param image
   *          The image to be rotated
   * @param imageInfo
   *          The {@link ImageInfo} containing the orientation information
   */
  public ImageRotator(BufferedImage image, ImageInfo imageInfo) {
    this.image = image;
    this.imageInfo = imageInfo;
  }

  /**
   * Creates an {@link AffineTransform} performing the image correction as
   * determined by the EXIF orientation information.
   * 
   * @return The {@link AffineTransform} to perform the image correction
   */
  private AffineTransform createTransformation() {
    // The Transformations as commented below don't all make sense to me,
    // but the do perform the correct image transformations.
    // create a default transformation - the identity transformation
    AffineTransform transform = new AffineTransform();
    Orientation orientation = Orientation.getOrientation(imageInfo);
    switch (orientation) {
      case NORMAL: // correct orientation
        break;
      case FLIP_LEFT_RIGHT: // flip left-right
        transform.scale(-1.0, 1.0);
        transform.translate(-image.getWidth(), 0);
        break;
      case ROTATE_180: // rotate 180 degrees
        transform.quadrantRotate(2);
        transform.translate(-image.getWidth(), -image.getHeight());
        break;
      case FLIP_UP_DOWN: // flip upside-down
        transform.scale(1.0, -1.0);
        transform.translate(0, -image.getHeight());
        break;
      case ROTATE_90_CLOCKWISE_FLIP_LEFT_RIGHT: // rotate 90 degrees clockwise, then flip left-right
        transform.quadrantRotate(1);
        transform.scale(1.0, -1.0);
        break;
      case ROTATE_90_CLOCKWISE: // rotate 90 degrees clockwise
        transform.quadrantRotate(1);
        transform.translate(0, -image.getHeight());
        break;
      case FLIP_UP_DOWN_ROTATE_90_ANTICLOCK: // flip upside-down, rotate 90 degrees left
        transform.scale(1.0, -1.0);
        transform.quadrantRotate(1);
        transform.translate(-image.getWidth(), -image.getHeight());
        break;
      case ROTATE_90_ANTICLOCK: // rotate 90 degrees left
        transform.quadrantRotate(-1);
        transform.translate(-image.getWidth(), 0);
        break;
      default:
        // there are no other values
        break;
    }
    return transform;
  }

  /**
   * Perform the required image transformation to match the EXIF data.
   * 
   * @return The transformed image
   */
  public BufferedImage rotate() {
    // get the AffineTransform that does the job
    AffineTransform transform = createTransformation();
    // save work if there is no work to be done
    if (transform.isIdentity()) {
      return image;
    }
    // determine the width and height of the new image
    int newImageWidth = image.getWidth();
    int newImageHeight = image.getHeight();
    // considering that the aspect of the image might change
    if (Orientation.getOrientation(imageInfo).changesAspect()) {
      newImageWidth = image.getHeight();
      newImageHeight = image.getWidth();
    }
    // draw original image to new image object
    BufferedImage newImage = new BufferedImage(newImageWidth, newImageHeight,
        BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics2D = newImage.createGraphics();
    graphics2D.drawImage(image, transform, null);
    return newImage;
  }
}
