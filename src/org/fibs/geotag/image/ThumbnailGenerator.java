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
import java.io.File;

import javax.swing.ImageIcon;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.ImageInfo.THUMBNAIL_STATUS;

/**
 * This class is used to generate image thumbnails.
 * 
 * @author Andreas Schneider
 * 
 */
public final class ThumbnailGenerator {

  /**
   * hide constructor.
   */
  private ThumbnailGenerator() {
    // hide constructor
  }

  /**
   * Create a thumbnail image.
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

  /**
   * Load the thumbnail for an image.
   * 
   * @param imageInfo
   * @return true if thumbnail could be created
   */
  public static boolean loadThumbnail(ImageInfo imageInfo) {
    try {
      imageInfo.setThumbNailStatus(THUMBNAIL_STATUS.LOADING);
      File file = new File(imageInfo.getPath());
      ImageFile imageFile = ImageFileFactory.createImageFile(file);
      if (imageFile != null) {
        BufferedImage originalImage = imageFile.read();
        if (originalImage != null) {
          BufferedImage rotatedImage = (new ImageRotator(originalImage,
              imageInfo).rotate());
          // note the image size
          imageInfo.setWidth(rotatedImage.getWidth());
          imageInfo.setHeight(rotatedImage.getHeight());
          // now we create a thumbnail image
          BufferedImage thumbImage = ThumbnailGenerator.createThumbnailImage(
              rotatedImage, Settings.get(SETTING.THUMBNAIL_SIZE,
                  Settings.DEFAULT_THUMBNAIL_SIZE));
          ImageIcon imageIcon = new ImageIcon(thumbImage);
          imageInfo.setThumbnail(imageIcon);
          imageInfo.setThumbNailStatus(THUMBNAIL_STATUS.AVAILABLE);
          return true;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    imageInfo.setThumbNailStatus(THUMBNAIL_STATUS.UNKNOWN);
    return false;
  }
}
