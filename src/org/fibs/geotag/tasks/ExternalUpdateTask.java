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

package org.fibs.geotag.tasks;

import org.fibs.geotag.data.EditGPSAltitude;
import org.fibs.geotag.data.EditGPSImgDirection;
import org.fibs.geotag.data.EditGPSLatitude;
import org.fibs.geotag.data.EditGPSLongitude;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.external.ExternalUpdate;

/**
 * @author Andreas Schneider
 * 
 */
public class ExternalUpdateTask extends UndoableBackgroundTask<ImageInfo> {

  /** the {@link ExternalUpdate} to be applied */
  private ExternalUpdate externalUpdate;

  /**
   * @param name
   * @param externalUpdate
   */
  public ExternalUpdateTask(String name, ExternalUpdate externalUpdate) {
    super(null, name); // has no group name
    this.externalUpdate = externalUpdate;
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
    ImageInfo imageInfo = ImageInfo.getImageInfo(externalUpdate
        .getImageNumber());
    if (imageInfo != null) {
      new EditGPSLatitude(imageInfo, (new Double(externalUpdate.getLatitude()))
          .toString(), ImageInfo.DATA_SOURCE.MAP);
      new EditGPSLongitude(imageInfo,
          (new Double(externalUpdate.getLongitude())).toString(),
          ImageInfo.DATA_SOURCE.MAP);
      // leave the altitude untouched, unless it is not set yet,
      // in which case we set it to zero
      if (imageInfo.getGPSAltitude() == null) {
        new EditGPSAltitude(imageInfo, (new Double(0.0)).toString(),
            ImageInfo.DATA_SOURCE.MAP);
      }
      if (externalUpdate.getDirection() != Double.NaN) {
        new EditGPSImgDirection(imageInfo, (new Double(externalUpdate
            .getDirection()).toString()));
      }
      publish(imageInfo);
    }
    return getPresentationName();
  }

}
