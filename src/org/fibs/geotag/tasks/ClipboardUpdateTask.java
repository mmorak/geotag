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
import org.fibs.geotag.data.UpdateGPSAltitude;
import org.fibs.geotag.data.UpdateGPSLatitude;
import org.fibs.geotag.data.UpdateGPSLongitude;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.external.ClipboardUpdate;
import org.fibs.geotag.util.Units.ALTITUDE;

/**
 * @author Andreas Schneider
 * 
 */
public class ClipboardUpdateTask extends UndoableBackgroundTask<ImageInfo> {

  /** the {@link ClipboardUpdate}s to be applied. */
  private List<ClipboardUpdate> clipboardUpdates;

  /** the images the update should be applied to. */
  private List<ImageInfo> images;

  /**
   * @param name
   * @param clipboardUpdates
   * @param images
   */
  public ClipboardUpdateTask(String name,
      List<ClipboardUpdate> clipboardUpdates, List<ImageInfo> images) {
    super(null, name); // has no group name
    this.clipboardUpdates = clipboardUpdates;
    this.images = images;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getCurrentProgress()
   */
  @Override
  public int getCurrentProgress() {
    // this is so fast, we don't bother showing progress
    return 1;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getMaxProgress()
   */
  @Override
  public int getMaxProgress() {
    return images.size();
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
    // apply only the last clipboard update - there is usually always actually)
    // only one
    ClipboardUpdate clipboardUpdate = clipboardUpdates.get(clipboardUpdates
        .size() - 1);
    for (ImageInfo imageInfo : images) {
      new UpdateGPSLatitude(imageInfo, (new Double(clipboardUpdate
          .getLatitude())).toString(), DATA_SOURCE.CLIPBOARD);
      new UpdateGPSLongitude(imageInfo, (new Double(clipboardUpdate
          .getLongitude())).toString(), DATA_SOURCE.CLIPBOARD);
      // leave the altitude untouched, unless it is not set yet,
      // in which case we set it to zero
      if (imageInfo.getGpsAltitude() == null) {
        new UpdateGPSAltitude(imageInfo, (new Double(0.0)).toString(),
            DATA_SOURCE.CLIPBOARD, ALTITUDE.METRES);
      }
      publish(imageInfo);
    }
    return getPresentationName();
  }

}
