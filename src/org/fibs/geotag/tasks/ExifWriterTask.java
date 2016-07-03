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
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.exif.ExifWriter;
import org.fibs.geotag.table.ImagesTableModel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A class writing the location information for an image to the EXIF data of the
 * image file.
 * 
 * @author Andreas Schneider
 * 
 */
public class ExifWriterTask extends BackgroundTask<ImageInfo> {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(ExifWriterTask.class);

  /** the table model to be informed about changes. */
  private ImagesTableModel imagesTableModel;

  /** the list of images to be updated. */
  private List<ImageInfo> imageInfos;

  /** Keep track of progress. */
  private int currentProgress = 0;

  /**
   * @param name
   * @param imagestableModel
   * @param imageInfos
   */
  public ExifWriterTask(String name, ImagesTableModel imagestableModel,
      List<ImageInfo> imageInfos) {
    super(name);
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
    int imagesUpdated = 0;
    for (ImageInfo imageInfo : imageInfos) {
      if (interruptRequested()) {
        break;
      }
      currentProgress++;
      setProgressMessage();
      try {
        ExifWriter exifWriter = new ExifWriter();
        if (exifWriter.write(imageInfo)) {
          // write went OK...
          imagesUpdated++;
          // the location data source is now the image
          imageInfo.setSource(DATA_SOURCE.IMAGE);
          // tell the GUI about the change
          publish(imageInfo);
        }
      } catch (RuntimeException e) {
        // catch all runtime exceptions - no need to terminate early
        e.printStackTrace();
      }
    }
    String result = null;
    if (imagesUpdated == 1) {
      result = i18n.tr("One image updated."); //$NON-NLS-1$
    } else {
      try {
        result = String.format(i18n
            .tr("%d images updated."), imagesUpdated); //$NON-NLS-1$
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
