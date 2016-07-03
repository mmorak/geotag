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

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.UpdateGPSLongitude;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author Andreas Schneider
 * 
 */
public class EditLongitudeTask extends UndoableBackgroundTask<ImageInfo> {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(EditLongitudeTask.class);

  /** The image whose longitude will be set. */
  private ImageInfo imageInfo;

  /** The new longitude. */
  private String newLongitude;

  /** The source of the update. */
  private DATA_SOURCE dataSource;

  /**
   * @param name
   * @param imageInfo
   * @param newLongitude
   * @param dataSource
   */
  public EditLongitudeTask(String name, ImageInfo imageInfo,
      String newLongitude, DATA_SOURCE dataSource) {
    super(null, name); // has no group name
    this.imageInfo = imageInfo;
    this.newLongitude = newLongitude;
    this.dataSource = dataSource;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getCurrentProgress()
   */
  @Override
  public int getCurrentProgress() {
    return 1;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getMaxProgress()
   */
  @Override
  public int getMaxProgress() {
    return 1;
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
    if (imageInfo != null) {
      if (newLongitude.length() == 0) {
        // empty string - no direction
        new UpdateGPSLongitude(imageInfo, null, dataSource);
      } else {
        new UpdateGPSLongitude(imageInfo, newLongitude, dataSource);
      }
      publish(imageInfo);
    }
    return i18n.tr("Longitude edited"); //$NON-NLS-1$
  }

}
