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
import org.fibs.geotag.data.UpdateGPSImgDirection;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.util.Constants;
import org.fibs.geotag.util.Util;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author Andreas Schneider
 * 
 */
public class EditDirectionTask extends UndoableBackgroundTask<ImageInfo> {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(EditDirectionTask.class);

  /** The image whose direction will be set. */
  private ImageInfo imageInfo;

  /** The new direction. */
  private String newDirection;

  /** The source of the update. */
  private DATA_SOURCE dataSource;

  /**
   * @param name
   * @param imageInfo
   * @param newDirection
   * @param dataSource
   */
  public EditDirectionTask(String name, ImageInfo imageInfo,
      String newDirection, DATA_SOURCE dataSource) {
    super(null, name); // has no group name
    this.imageInfo = imageInfo;
    this.newDirection = newDirection;
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
      if (newDirection.length() == 0) {
        // empty string - no direction
        new UpdateGPSImgDirection(imageInfo, null, dataSource);
      } else {
        // see if the user entered a cardinal direction (e.g NNW)
        double direction = Util.degreesFromCardinalDirection(newDirection);
        if (Double.isNaN(direction)) {
          // no... should be number
          try {
            direction = Double.parseDouble(newDirection);
            // must be between 0 (inclusive) and 360 (exclusive)
            if (direction < 0.0 || direction >= Constants.FULL_CIRCLE_DEGREES) {
              direction = Double.NaN;
            }
          } catch (RuntimeException e) {
            direction = Double.NaN;
          }
        }
        if (!Double.isNaN(direction)) {
          new UpdateGPSImgDirection(imageInfo, Double.toString(direction),
              dataSource);
        }
      }
      publish(imageInfo);
    }
    return i18n.tr("Direction edited"); //$NON-NLS-1$
  }

}
