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
import org.fibs.geotag.data.UpdateCityName;
import org.fibs.geotag.data.UpdateCountryName;
import org.fibs.geotag.data.UpdateLocationName;
import org.fibs.geotag.data.UpdateProvinceName;
import org.fibs.geotag.data.UpdateUserComment;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.table.ImagesTableColumns.COLUMN;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author Andreas Schneider
 * 
 */
public class ManualEditTask extends UndoableBackgroundTask<ImageInfo> {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(ManualEditTask.class);

  /** The image whose data will be set. */
  private ImageInfo imageInfo;

  /** The column that was edited. */
  private COLUMN column;

  /** The new value. */
  private String newValue;

  /**
   * @param name
   * @param imageInfo
   * @param column
   * @param newValue
   */
  public ManualEditTask(String name, ImageInfo imageInfo, COLUMN column,
      String newValue) {
    super(i18n.tr("Manual edit"), name); //$NON-NLS-1$
    this.imageInfo = imageInfo;
    this.column = column;
    this.newValue = newValue;
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
      if (newValue.length() == 0) {
        // empty string set to null
        newValue = null;
      }
      switch (column) {
        case IMAGE_NAME:
        case GPS_DATE:
        case TIME_OFFSET:
        case CAMERA_DATE:
        case LATITUDE:
        case LONGITUDE:
        case ALTITUDE:
        case DIRECTION:
          break;
        case LOCATION_NAME:
          new UpdateLocationName(imageInfo, newValue, DATA_SOURCE.MANUAL);
          break;
        case CITY_NAME:
          new UpdateCityName(imageInfo, newValue, DATA_SOURCE.MANUAL);
          break;
        case PROVINCE_NAME:
          new UpdateProvinceName(imageInfo, newValue, DATA_SOURCE.MANUAL);
          break;
        case COUNTRY_NAME:
          new UpdateCountryName(imageInfo, newValue, DATA_SOURCE.MANUAL);
          break;
        case USER_COMMENT:
          new UpdateUserComment(imageInfo, newValue, DATA_SOURCE.MANUAL);
          break;
        default:
          break;
      }
      publish(imageInfo);
    }
    return String.format(
        i18n.tr("%1$s edited."), getName()); //$NON-NLS-1$
  }

}
