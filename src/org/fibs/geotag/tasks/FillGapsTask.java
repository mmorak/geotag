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
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.track.TrackMatcher;
import org.fibs.geotag.ui.ImagesTableModel;

/**
 * A background task for filling gaps between image locations
 * 
 * @author Andreas Schneider
 * 
 */
public class FillGapsTask extends UndoableBackgroundTask<ImageInfo> {

  /** The {@link TrackMatcher} used to match image to coordinates */
  private TrackMatcher trackMatcher;

  /** The table */
  private ImagesTableModel imagesTableModel;

  /** A counter for keeping crack of progress */
  private int currentProgress = 0;

  /** The images to be matched */
  private List<ImageInfo> images;

  /**
   * Create a new matching task for a list of images
   * 
   * @param group
   * @param name
   * @param trackMatcher
   * @param imagesTableModel
   * @param images
   */
  public FillGapsTask(String group, String name, TrackMatcher trackMatcher,
      ImagesTableModel imagesTableModel, List<ImageInfo> images) {
    super(group, name);
    this.trackMatcher = trackMatcher;
    this.imagesTableModel = imagesTableModel;
    this.images = images;
  }

  /**
   * @see javax.swing.SwingWorker#doInBackground()
   */
  @SuppressWarnings("boxing")
  @Override
  protected String doInBackground() throws Exception {
    int gapsFilled = 0;
    for (ImageInfo imageInfo : images) {
      if (terminate) {
        break;
      }
      // we catch all Exceptions - we don't want this to terminate without
      // notice
      try {
        // get the ImageInfo
        currentProgress++;
        setProgressMessage();
        if (imageInfo.hasLocation() == false) {
          // no coordinates yet... fill the gap
          // tell trackMatcher that exact matches are not required
          if (trackMatcher.match(imageInfo, false) == true) {
            gapsFilled++;
            publish(imageInfo);
          }
        }
      } catch (RuntimeException e) {
        // catch all RuntimeExceptions
        e.printStackTrace();
      }
    }
    String result = null;
    if (gapsFilled == 0) {
      result = Messages.getString("FillGapsTask.FilledNoGaps"); //$NON-NLS-1$
    } else if (gapsFilled == 1) {
      result = Messages.getString("FillGapsTask.FilledOneGap"); //$NON-NLS-1$
    } else {
      try {
        result = String.format(Messages
            .getString("FillGapsTask.FilledGapsFormat"), gapsFilled); //$NON-NLS-1$
      } catch (IllegalFormatException e) {
        e.printStackTrace();
      }
    }
    return result;
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
   * @see org.fibs.geotag.tasks.BackgroundTask#getCurrentProgress()
   */
  @Override
  public int getCurrentProgress() {
    return currentProgress;
  }

  /**
   * @see javax.swing.SwingWorker#process(java.util.List)
   */
  @Override
  protected void process(List<ImageInfo> imageInfos) {
    for (ImageInfo imageInfo : imageInfos) {
      int imageRow = imagesTableModel.getRow(imageInfo);
      imagesTableModel.fireTableRowsUpdated(imageRow, imageRow);
    }
  }
}