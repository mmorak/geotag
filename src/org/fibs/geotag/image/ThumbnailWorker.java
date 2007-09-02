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

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import org.fibs.geotag.Settings;
import org.fibs.geotag.data.ImageInfo;

/**
 * A class to load a thumbnail image as a {@link SwingWorker}
 * 
 * @author Andreas Schneider
 * 
 */
public class ThumbnailWorker extends SwingWorker<Void, ImageInfo> {

  /**
   * Default for the longest size of thumbnails - in case we don't have a
   * preference yet
   */
  private static final int DEFAULT_THUMBNAIL_SIZE = 150;

  /** the {@link ImageInfo} to load the thumbnail for */
  private ImageInfo imageInfo;

  /**
   * @param imageInfo
   *          The {@link ImageInfo} to load the thumbnail for
   */
  public ThumbnailWorker(ImageInfo imageInfo) {
    this.imageInfo = imageInfo;
  }

  /**
   * Determine how to generate a thumbnail and generate it Does nothing if the
   * thumbnail already exists
   */
  @Override
  protected Void doInBackground() throws Exception {
    if (imageInfo.getThumbnail() == null) {
      File imagefile = new File(imageInfo.getPath());
      boolean success = false;
      if (ImageFileFilter.isJpegFile(imagefile)) {
        success = createJpegThumbnail();
      }
      if (success) {
        publish(imageInfo);
      }
    }
    return null;
  }

  /**
   * create a Jpeg thumbnail
   * 
   * @return True if successful
   */
  private boolean createJpegThumbnail() {
    try {
      // next we try an get the image data
      BufferedImage originalImage = ImageIO.read(new File(imageInfo.getPath()));
      // and adjust for rotation according to EXIF data
      BufferedImage rotatedImage = (new ImageRotator(originalImage, imageInfo)
          .rotate());
      // note the image size
      imageInfo.setWidth(rotatedImage.getWidth());
      imageInfo.setHeight(rotatedImage.getHeight());
      // now we create a thumbnail image
      BufferedImage thumbImage = ThumbnailGenerator.createThumbnailImage(
          rotatedImage, Settings.getInt(Settings.THUMBNAIL_SIZE,
              DEFAULT_THUMBNAIL_SIZE));
      ImageIcon imageIcon = new ImageIcon(thumbImage);
      imageInfo.setThumbnail(imageIcon);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

}
