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

import java.io.File;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.fibs.geotag.Messages;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.exif.ExifReader;
import org.fibs.geotag.exif.ExiftoolReader;
import org.fibs.geotag.exif.MetadataExtractorReader;
import org.fibs.geotag.image.ImageFileFilter;
import org.fibs.geotag.ui.ImagesTableModel;

/**
 * This class handles reading EXIF data from image files. We call exiftool for
 * this purpose. this task is not undoable.
 * 
 * @author Andreas Schneider
 * 
 */
public class ExifReaderTask extends UndoableBackgroundTask<ImageInfo> {

  /**
   * The <code>ImagesTableModel</code> that needs to be told about new EXIF
   * data
   */
  private ImagesTableModel tableModel;

  /** The files to be examined for their EXIf data */
  protected File[] files;

  /** keep track of the progress */
  protected int currentProgress = 0;

  /** Keep a list of image infos so we can undo and redo */
  List<ImageInfo> images = new ArrayList<ImageInfo>();

  /**
   * Create a new {@link ExifReaderTask}
   * 
   * @param name
   *          The name of the task
   * @param tableModel
   *          The {@link ImagesTableModel} used to store the EXIF data
   * @param files
   *          The files (possibly) containing the EXIF data
   */
  public ExifReaderTask(String name, ImagesTableModel tableModel, File[] files) {
    super(null, name);
    this.tableModel = tableModel;
    this.files = files;
  }

  /**
   * Determine how to read the exif data and read it
   * 
   * @param file
   *          The file to be examined
   * @return The {@link ImageInfo} for the file
   */
  private ImageInfo readExifData(File file) {
    ExifReader exifReader = null;
    if (ImageFileFilter.isJpegFile(file)) {
      exifReader = new MetadataExtractorReader();
    } else if (ImageFileFilter.isRawFile(file)) {
      exifReader = new ExiftoolReader();
    }
    if (exifReader != null) {
      return exifReader.readExifData(file);
    }
    return null;
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
    return files.length;
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
    ImageInfo imageInfo;
    int imagesPublished = 0;
    for (File file : files) {
      if (terminate) {
        break;
      }
      // keep track of progress
      currentProgress++;
      // give feedback via the ProgressBar
      setProgressMessage();
      try {
        imageInfo = readExifData(file);
        if (imageInfo != null) {
          publish(imageInfo);
          imagesPublished++;
        }
      } catch (RuntimeException e) {
        // catch all Runtime Exceptions - don't let the task die
        e.printStackTrace();
      }
    }
    String result = null;
    if (imagesPublished == 1) {
      result = Messages.getString("ExifReaderTask.OneImageLoaded"); //$NON-NLS-1$
    } else {
      try {
        result = String.format(Messages
            .getString("ExifReaderTask.ImagesLoadedFormat"), imagesPublished); //$NON-NLS-1$
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
      images.add(imageInfo);
      tableModel.addImageInfo(imageInfo);
    }
  }

  /**
   * @see org.fibs.geotag.tasks.UndoableBackgroundTask#redo()
   */
  @Override
  public void redo() throws CannotRedoException {
    // Needs to call super
    super.redo();
    for (ImageInfo imageInfo : images) {
      tableModel.addImageInfo(imageInfo);
    }
    tableModel.fireTableDataChanged();
  }

  /**
   * @see org.fibs.geotag.tasks.UndoableBackgroundTask#undo()
   */
  @Override
  public void undo() throws CannotUndoException {
    // Needs to call super
    super.undo();
    for (ImageInfo imageInfo : images) {
      int row = tableModel.getRow(imageInfo);
      tableModel.removeRow(row);
    }
    tableModel.fireTableDataChanged();
  }

}
