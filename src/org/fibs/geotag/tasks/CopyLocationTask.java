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

import java.util.IllegalFormatException;
import java.util.List;

import org.fibs.geotag.Messages;
import org.fibs.geotag.data.EditGPSAltitude;
import org.fibs.geotag.data.EditGPSLatitude;
import org.fibs.geotag.data.EditGPSLongitude;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.ui.ImagesTableModel;

/**
 * a background task for copying the location from one image to others
 * 
 * @author Andreas Schneider
 * 
 */
public class CopyLocationTask extends UndoableBackgroundTask<ImageInfo> {

  /** keep track of current progress */
  private int currentProgress = 0;

  /** The table model */
  private ImagesTableModel imagesTableModel;

  /** The source of the location data */
  private ImageInfo source;

  /** The list of images receiving a new location */
  private List<ImageInfo> targets;

  /**
   * create a background task to copy a location to other images
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
      if (terminate) {
        break;
      }
      currentProgress++;
      try {
        setProgressMessage();
        new EditGPSLatitude(target, source.getGPSLatitude(),
            ImageInfo.DATA_SOURCE.COPIED);
        new EditGPSLongitude(target, source.getGPSLongitude(),
            ImageInfo.DATA_SOURCE.COPIED);
        new EditGPSAltitude(target, source.getGPSAltitude(),
            ImageInfo.DATA_SOURCE.COPIED);
        publish(target);
      } catch (RuntimeException e) {
        // catch all Runtime Exceptions, so the task doesn't terminate
        e.printStackTrace();
      }
    }
    String result = null;
    if (currentProgress == 1) {
      result = Messages.getString("CopyLocationTask.LocationCopiedToOne"); //$NON-NLS-1$
    } else {
      try {
        result = String
            .format(
                Messages.getString("CopyLocationTask.LocationsCopiedFormat"), currentProgress); //$NON-NLS-1$
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
