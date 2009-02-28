/**
 * Geotag
 * Copyright (C) 2007-2009 Andreas Schneider
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
  
  /**
   * the possible image orientations as defined by EXIF.
   */
  private enum Orientation {
    /** Normal image orientation. */
    NORMAL,
    /** Flip image left-right. */
    FLIP_LEFT_RIGHT,
    /** Rotate image 180 degrees. */
    ROTATE_180,
    /** Flip image upside down. */
    FLIP_UP_DOWN,
    /** Rotate image 90 degrees clockwise, then flip left-right. */
    ROTATE_90_CLOCKWISE_FLIP_LEFT_RIGHT,
    /** Rotate image 90 degrees clockwise. */
    ROTATE_90_CLOCKWISE,
    /** Flip image upside-down, then rotate 80 degrees anti-clockwise. */
    FLIP_UP_DOWN_ROTATE_90_ANTICLOCK,
    /** Rotate image 90 degrees anti-clockwise. */
    ROTATE_90_ANTICLOCK;
    /**
     * @param exifvalue The orientation as an integer as defined by EXIF
     * @return The Orientation enum constant representing it.
     */
    static Orientation getOrientation(int exifvalue) {
      return Orientation.values()[exifvalue - 1];
    }
  }
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
   * The orientation is stored in the {@link ImageInfo} as a String. This
   * methods converts it to an Integer.
   * 
   * @return The image orientation between 1 and 8
   */
  private int getOrientationAsInt() {
    int orientation = 1;
    if (imageInfo != null) {
      try {
        orientation = Integer.parseInt(imageInfo.getOrientation());
      } catch (Exception e) {
        // just use the default
      }
    }
    return orientation;
  }

  /**
   * Easy test if the rotation swaps the width and height of the image.
   * 
   * @return True if the image aspect changes
   */
  private boolean changesAspect() {
    // only value 5 to 8 change the aspect of the image
    Orientation orientation = Orientation.getOrientation(getOrientationAsInt()); getOrientationAsInt();
    switch (orientation) {
      case NORMAL:
      case FLIP_LEFT_RIGHT:
      case ROTATE_180:
      case FLIP_UP_DOWN:
        return false;
      case ROTATE_90_CLOCKWISE_FLIP_LEFT_RIGHT:
      case ROTATE_90_CLOCKWISE:
      case FLIP_UP_DOWN_ROTATE_90_ANTICLOCK:
      case ROTATE_90_ANTICLOCK:
        return true;
      default:
        return false;    
    }
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
    Orientation orientation = Orientation.getOrientation(getOrientationAsInt());
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
    if (changesAspect()) {
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
