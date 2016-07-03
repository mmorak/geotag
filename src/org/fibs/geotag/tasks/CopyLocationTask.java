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
import org.fibs.geotag.data.UpdateGPSLatitude;
import org.fibs.geotag.data.UpdateGPSLongitude;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.util.Units.ALTITUDE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * a background task for copying the location from one image to others.
 * 
 * @author Andreas Schneider
 * 
 */
public class CopyLocationTask extends UndoableBackgroundTask<ImageInfo> {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(CopyLocationTask.class);

  /** keep track of current progress. */
  private int currentProgress = 0;

  /** The table model. */
  private ImagesTableModel imagesTableModel;

  /** The source of the location data. */
  private ImageInfo source;

  /** The list of images receiving a new location. */
  private List<ImageInfo> targets;

  /**
   * create a background task to copy a location to other images.
   * 
   * @param group
   * @param name
   * @param imagesTableModel
   * @param source
   * @param targets
   */
  public CopyLocationTask(String group, String name,
      ImagesTableModel imagesTableModel, ImageInfo source,
      List<ImageInfo> targets) {
    super(group, name);
    this.source = source;
    this.targets = targets;
    this.imagesTableModel = imagesTableModel;
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
    return targets.size();
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
    for (ImageInfo target : targets) {
      if (interruptRequested()) {
        break;
      }
      currentProgress++;
      try {
        setProgressMessage();
        new UpdateGPSLatitude(target, source.getGpsLatitude(),
            ImageInfo.DATA_SOURCE.COPIED);
        new UpdateGPSLongitude(target, source.getGpsLongitude(),
            ImageInfo.DATA_SOURCE.COPIED);
        // The altitude is retrieved in metres
        new UpdateGPSAltitude(target, source.getGpsAltitude(),
            ImageInfo.DATA_SOURCE.COPIED, ALTITUDE.METRES);
        publish(target);
      } catch (RuntimeException e) {
        // catch all Runtime Exceptions, so the task doesn't terminate
        e.printStackTrace();
      }
    }
    String result = null;
    if (currentProgress == 1) {
      result = i18n.tr("Location copied to one image."); //$NON-NLS-1$
    } else {
      try {
        result = String
            .format(
                i18n.tr("Location copied to %d images."), currentProgress); //$NON-NLS-1$
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
  protected void process(List<ImageInfo> images) {
    for (ImageInfo imageInfo : images) {
      int imageRow = imagesTableModel.getRow(imageInfo);
      imagesTableModel.fireTableRowsUpdated(imageRow, imageRow);
    }
  }
}
