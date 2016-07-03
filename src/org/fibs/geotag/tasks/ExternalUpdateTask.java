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

import org.fibs.geotag.data.UpdateGPSAltitude;
import org.fibs.geotag.data.UpdateGPSImgDirection;
import org.fibs.geotag.data.UpdateGPSLatitude;
import org.fibs.geotag.data.UpdateGPSLongitude;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.external.ExternalUpdate;
import org.fibs.geotag.util.Airy;
import org.fibs.geotag.util.Util;
import org.fibs.geotag.util.Units.ALTITUDE;

/**
 * @author Andreas Schneider
 * 
 */
public class ExternalUpdateTask extends UndoableBackgroundTask<ImageInfo> {

  /** Assume locations are identical if they are closer than this in meters. */
  private static final double LOCATION_CLOSE = 0.1;
  
  /** the {@link ExternalUpdate} to be applied. */
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
      System.out.println(Util.greatCircleDistance(externalUpdate.getLatitude(),
          externalUpdate.getLongitude(), Airy.LATITUDE, Airy.LONGITUDE));
      // first the case that the location is exactly our default (within 10cm)
      if (Util.greatCircleDistance(externalUpdate.getLatitude(), externalUpdate
          .getLongitude(), Airy.LATITUDE, Airy.LONGITUDE) < LOCATION_CLOSE) {
        new UpdateGPSLatitude(imageInfo, null, DATA_SOURCE.NONE);
        new UpdateGPSLongitude(imageInfo, null, DATA_SOURCE.NONE);
        new UpdateGPSAltitude(imageInfo, null, DATA_SOURCE.NONE, ALTITUDE.METRES);
      } else {
        new UpdateGPSLatitude(imageInfo, (new Double(externalUpdate
            .getLatitude())).toString(), DATA_SOURCE.MAP);
        new UpdateGPSLongitude(imageInfo, (new Double(externalUpdate
            .getLongitude())).toString(), DATA_SOURCE.MAP);
        // leave the altitude untouched, unless it is not set yet,
        // in which case we set it to zero
        if (imageInfo.getGpsAltitude() == null) {
          new UpdateGPSAltitude(imageInfo, (new Double(0.0)).toString(),
              DATA_SOURCE.MAP, ALTITUDE.METRES);
        }
        if (!Double.isNaN(externalUpdate.getDirection())) {
          new UpdateGPSImgDirection(imageInfo, (new Double(externalUpdate
              .getDirection()).toString()), DATA_SOURCE.MAP);
        }
      }
      publish(imageInfo);
    }
    return getPresentationName();
  }

}
