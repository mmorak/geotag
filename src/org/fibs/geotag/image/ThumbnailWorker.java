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
import org.fibs.geotag.data.ImageInfo.THUMBNAIL_STATUS;
import org.fibs.geotag.dcraw.Dcraw;

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
    // only start loading it once.. no need to do it again
    // if status is loading, failed or available
    if (imageInfo.getThumbNailStatus() == THUMBNAIL_STATUS.UNKNOWN) {
      imageInfo.setThumbNailStatus(THUMBNAIL_STATUS.LOADING);
      File imagefile = new File(imageInfo.getPath());
      boolean success = false;
      if (ImageFileFilter.isJpegFile(imagefile)) {
        success = createJpegThumbnail();
      } else if (ImageFileFilter.isRawFile(imagefile)) {
        success = createRawThumbnail();
      }
      if (success) {
        publish(imageInfo);
        imageInfo.setThumbNailStatus(THUMBNAIL_STATUS.AVAILABLE);
      } else {
        // sometimes retrying seems to be necessary.
        // Ideally we'd like to set this to STATUS.FAILED
        imageInfo.setThumbNailStatus(THUMBNAIL_STATUS.UNKNOWN);
      }
    }
    return null;
  }

  /**
   * @param originalImage
   */
  private void createThumbnail(BufferedImage originalImage) {
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
    imageInfo.setThumbNailStatus(THUMBNAIL_STATUS.AVAILABLE);
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
      createThumbnail(originalImage);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Use dcraw to extract a thumbnail from a RAW image
   * 
   * @return True if and only if a thumbnail could be create
   */
  private boolean createRawThumbnail() {
    try {
      BufferedImage originalImage = Dcraw.getEmbeddedImage(new File(imageInfo
          .getPath()));
      if (originalImage != null) {
        createThumbnail(originalImage);
        return true;
      }
      System.out.println("No thumbnail for " + imageInfo.getPath()); //$NON-NLS-1$
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
    return false;
  }

}
