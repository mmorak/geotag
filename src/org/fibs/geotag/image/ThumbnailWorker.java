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

import javax.swing.SwingWorker;

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.ImageInfo.THUMBNAIL_STATUS;

/**
 * A class to load a thumbnail image as a {@link SwingWorker}.
 * 
 * @author Andreas Schneider
 * 
 */
public class ThumbnailWorker extends SwingWorker<Void, ImageInfo> {

  /** the {@link ImageInfo} to load the thumbnail for. */
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
   * thumbnail already exists.
   */
  @Override
  protected Void doInBackground() throws Exception {
    // only start loading it once.. no need to do it again
    // if status is loading, failed or available
    if (imageInfo.getThumbNailStatus() == THUMBNAIL_STATUS.UNKNOWN) {
      if (ThumbnailGenerator.loadThumbnail(imageInfo)) {
        publish(imageInfo);
      }
    }
    return null;
  }

}
