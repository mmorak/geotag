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
import org.fibs.geotag.table.ImagesTableModel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A background task for removing images from the table.
 * 
 * @author Andreas Schneider
 * 
 */
public class RemoveImagesTask extends UndoableBackgroundTask<ImageInfo> {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(RemoveImagesTask.class);

  /** keep track of current progress. */
  private int currentProgress = 0;

  /** The table model. */
  private ImagesTableModel imagesTableModel;

  /** The list of images to be removed. */
  private List<ImageInfo> imageInfos;

  /**
   * create a background task to remove a list of images from the table.
   * 
   * @param group
   * @param name
   * @param imagesTableModel
   * @param imageInfos
   */
  public RemoveImagesTask(String group, String name,
      ImagesTableModel imagesTableModel, List<ImageInfo> imageInfos) {
    super(group, name);
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
      if (interruptRequested()) {
        break;
      }
      currentProgress++;
      setProgressMessage();
      try {
        publish(imageInfo);
      } catch (RuntimeException e) {
        // catch all RuntimeExceptions
        e.printStackTrace();
      }
    }
    String result = null;
    if (currentProgress == 1) {
      result = i18n.tr("One image removed"); //$NON-NLS-1$
    } else {
      try {
        result = String
            .format(
                i18n.tr("%d images removed"), currentProgress); //$NON-NLS-1$
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
      int row = imagesTableModel.getRow(imageInfo);
      imagesTableModel.removeRow(row);
    }
    imagesTableModel.fireTableDataChanged();
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

  /**
   * @see org.fibs.geotag.tasks.UndoableBackgroundTask#redo()
   */
  @Override
  public void undo() {
    // Needs to call super
    super.undo();
    for (ImageInfo imageInfo : imageInfos) {
      imagesTableModel.addImageInfo(imageInfo);
    }
    imagesTableModel.fireTableDataChanged();
  }

  /**
   * @see org.fibs.geotag.tasks.UndoableBackgroundTask#undo()
   */
  @Override
  public void redo() {
    // Needs to call super
    super.redo();
    for (ImageInfo imageInfo : imageInfos) {
      int row = imagesTableModel.getRow(imageInfo);
      imagesTableModel.removeRow(row);
    }
    imagesTableModel.fireTableDataChanged();
  }
}
