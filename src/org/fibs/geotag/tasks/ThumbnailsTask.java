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

package org.fibs.geotag.tasks;

import java.util.List;

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.ImageInfo.THUMBNAIL_STATUS;
import org.fibs.geotag.image.ThumbnailGenerator;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A class loading a number of thumbnail images and giving feedback.
 * 
 * @author Andreas Schneider
 * 
 */
public class ThumbnailsTask extends BackgroundTask<ImageInfo> {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(ThumbnailsTask.class);

  /** the list of images to be updated. */
  private List<ImageInfo> imageInfos;

  /** Keep track of progress. */
  private int currentProgress = 0;

  /**
   * @param name
   * @param imageInfos
   */
  public ThumbnailsTask(String name, List<ImageInfo> imageInfos) {
    super(name);
    this.imageInfos = imageInfos;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getCurrentProgress()
   */
  @Override
  public int getCurrentProgress() {
    return currentProgress;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getMaxProgress()
   */
  @Override
  public int getMaxProgress() {
    return imageInfos.size();
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getMinProgress()
   */
  @Override
  public int getMinProgress() {
    return 1;
  }

  /**
   * @see javax.swing.SwingWorker#doInBackground()
   */
  @Override
  protected String doInBackground() throws Exception {
    for (ImageInfo imageInfo : imageInfos) {
      if (interruptRequested()) {
        break;
      }
      currentProgress++;
      setProgressMessage(i18n
          .tr("Generating thumbnails:")); //$NON-NLS-1$
      // only start loading it once.. no need to do it again
      // if status is loading, failed or available
      if (imageInfo.getThumbNailStatus() == THUMBNAIL_STATUS.UNKNOWN) {
        if (ThumbnailGenerator.loadThumbnail(imageInfo)) {
          publish(imageInfo);
        }
      }
    }
    String result = i18n.tr("Thumbnails generated."); //$NON-NLS-1$
    return result;
  }

}
