/**
 * Geotag
 * Copyright (C) 2007,2008 Andreas Schneider
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

import org.fibs.geotag.Messages;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.UpdateGPSAltitude;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;

/**
 * @author Andreas Schneider
 * 
 */
public class EditAltitudeTask extends UndoableBackgroundTask<ImageInfo> {

  /** The image whose altitude will be set. */
  private ImageInfo imageInfo;

  /** The new altitude. */
  private String newAltitude;

  /** The source of the update. */
  private DATA_SOURCE dataSource;

  /**
   * @param name
   * @param imageInfo
   * @param newAltitude
   * @param dataSource
   */
  public EditAltitudeTask(String name, ImageInfo imageInfo, String newAltitude,
      DATA_SOURCE dataSource) {
    super(null, name); // has no group name
    this.imageInfo = imageInfo;
    this.newAltitude = newAltitude;
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
      if (newAltitude.length() == 0) {
        // empty string - no direction
        new UpdateGPSAltitude(imageInfo, null, dataSource);
      } else {
        new UpdateGPSAltitude(imageInfo, newAltitude, dataSource);
      }
      publish(imageInfo);
    }
    return Messages.getString("EditAltitudeTask.AltitudeEdited"); //$NON-NLS-1$
  }

}
