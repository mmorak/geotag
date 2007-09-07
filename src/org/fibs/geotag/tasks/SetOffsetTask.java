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
import org.fibs.geotag.data.EditGPSDateTime;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.table.ImagesTableModel;

/**
 * a background task for changing the time offset for a list of images
 * 
 * @author Andreas Schneider
 * 
 */
public class SetOffsetTask extends UndoableBackgroundTask<ImageInfo> {

  /** The offset to give the images */
  private int offset;

  /** keep track of current progress */
  private int currentProgress = 0;

  /** The table model */
  private ImagesTableModel imagesTableModel;

  /** The list of images receiving a new offset */
  private List<ImageInfo> imageInfos;

  /**
   * create a background task to set the time offset for a list of images
   * 
   * @param group
   * @param name
   * @param imagesTableModel
   * @param offset
   * @param imageInfos
   */
  public SetOffsetTask(String group, String name,
      ImagesTableModel imagesTableModel, int offset, List<ImageInfo> imageInfos) {
    super(group, name);
    this.offset = offset;
    this.imageInfos = imageInfos;
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
    for (ImageInfo imageInfo : imageInfos) {
      if (terminate) {
        break;
      }
      currentProgress++;
      setProgressMessage();
      try {
        String gmtDate = ImageInfo.subtractOffset(imageInfo.getCameraDate(),
            offset);
        new EditGPSDateTime(imageInfo, gmtDate);
        publish(imageInfo);
      } catch (RuntimeException e) {
        // catch all RuntimeExceptions
        e.printStackTrace();
      }
    }
    String result = null;
    if (currentProgress == 1) {
      result = Messages.getString("SetOffsetTask.CopiedTimeToOne"); //$NON-NLS-1$
    } else {
      try {
        result = String.format(Messages
            .getString("SetOffsetTask.ImagesCopiedFormat"), currentProgress); //$NON-NLS-1$
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

  /**
   * @see javax.swing.SwingWorker#done()
   */
  @Override
  protected void done() {
    // the order of row may have changed
    // This is safe here, as we're back on the event thread
    imagesTableModel.sortRows();
    super.done();
  }
}
