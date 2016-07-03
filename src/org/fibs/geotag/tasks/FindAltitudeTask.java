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

import java.util.IllegalFormatException;
import java.util.List;

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.UpdateGPSAltitude;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.geonames.AltitudeHandler;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.util.Units;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A class finding altitudes for images.
 * 
 * @author Andreas Schneider
 * 
 */
public class FindAltitudeTask extends UndoableBackgroundTask<ImageInfo> {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(FindAltitudeTask.class);

  /** the table model to be informed about changes. */
  private ImagesTableModel imagesTableModel;

  /** the list of images to be updated. */
  private List<ImageInfo> imageInfos;

  /** Keep track of progress. */
  private int currentProgress = 0;

  /**
   * @param group
   * @param name
   * @param imagestableModel
   * @param imageInfos
   */
  public FindAltitudeTask(String group, String name,
      ImagesTableModel imagestableModel, List<ImageInfo> imageInfos) {
    super(group, name);
    this.imagesTableModel = imagestableModel;
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
  @SuppressWarnings("boxing")
  @Override
  protected String doInBackground() throws Exception {
    int altitudesFound = 0;
    for (ImageInfo imageInfo : imageInfos) {
      if (interruptRequested()) {
        break;
      }
      currentProgress++;
      setProgressMessage();
      try {
        AltitudeHandler altitudeHandler = new AltitudeHandler(imageInfo
            .getGpsLatitude(), imageInfo.getGpsLongitude());
        // update the altitude
        String altitude = altitudeHandler.getAltitude();
        if (altitude != null) {
          // That's the special value for "sea level" / "unknown":
          if (!altitude.startsWith("-32768")) { //$NON-NLS-1$
            new UpdateGPSAltitude(imageInfo, altitude, DATA_SOURCE.GEONAMES,
                Units.ALTITUDE.METRES);
            altitudesFound++;
          }
          // tell the GUI about the change
          publish(imageInfo);
        }
      } catch (RuntimeException e) {
        // catch all runtime exceptions - no need to terminate early
        e.printStackTrace();
      }
    }
    String result = null;
    if (altitudesFound == 1) {
      result = i18n.tr("One altitude found"); //$NON-NLS-1$
    } else {
      try {
        result = String.format(i18n
            .tr("%d altitudes found"), //$NON-NLS-1$
            altitudesFound);
      } catch (IllegalFormatException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  /**
   * @see javax.swing.SwingWorker#process(java.util.List)
   */
  @Override
  protected void process(List<ImageInfo> chunks) {
    for (ImageInfo imageInfo : chunks) {
      int imageRow = imagesTableModel.getRow(imageInfo);
      imagesTableModel.fireTableRowsUpdated(imageRow, imageRow);
    }
  }

}
