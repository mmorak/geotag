/**
 * Geotag
 * Copyright (C) 2007,2008 Andreas Schneider
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

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.exif.ExifReader;
import org.fibs.geotag.exif.Exiftool;
import org.fibs.geotag.exif.ExiftoolReader;
import org.fibs.geotag.exif.MetadataExtractorReader;
import org.fibs.geotag.i18n.Messages;
import org.fibs.geotag.image.FileTypes;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.util.FileUtil;

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
   * data.
   */
  private ImagesTableModel tableModel;

  /** The files to be examined for their EXIf data. */
  private File[] files;

  /** keep track of the progress. */
  private int currentProgress = 0;

  /** Keep a list of image infos so we can undo and redo. */
  private List<ImageInfo> images = new ArrayList<ImageInfo>();

  /**
   * Create a new {@link ExifReaderTask}.
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
   * Determine how to read the exif data and read it.
   * 
   * @param file
   *          The file to be examined
   * @return The {@link ImageInfo} for the file
   */
  private ImageInfo readExifData(File file) {
    ImageInfo result = null;
    ExifReader exifReader = null;
    switch (FileTypes.fileType(file)) {
      case JPEG:
        exifReader = new MetadataExtractorReader();
        break;
      case RAW_READ_ONLY:
      case RAW_READ_WRITE:
      case TIFF:
        exifReader = new ExiftoolReader();
        break;
      case UNKOWN:
      case XMP:
        break;
      default:
        break;
    }
    if (exifReader != null) {
      result = exifReader.readExifData(file, null);
      // now check if there is an XMP sidecar file for our file
      String xmpFileName = FileUtil.replaceExtension(file.getPath(), "xmp"); //$NON-NLS-1$
      if (xmpFileName != null) {
        File xmpFile = new File(xmpFileName);
        if (xmpFile.exists()) {
          System.out.println(xmpFileName);
          exifReader = new ExiftoolReader();
          result = exifReader.readExifData(xmpFile, result);
        }
      }
    }
    return result;
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
    // long start = System.currentTimeMillis();
    // we split our files into two groups:
    // Files that need to be handled individually and files
    // that can be handled by exiftool in one go:
    List<File> handleOneByOne = new ArrayList<File>();
    List<File> handleInOneGo = new ArrayList<File>();
    for (File file : files) {
      switch (FileTypes.fileType(file)) {
        case JPEG:
          // Jpeg as best best handled by the MetadataExtractor
          // on a per file basis
          handleOneByOne.add(file);
          break;
        case RAW_READ_ONLY:
        case RAW_READ_WRITE:
        case TIFF:
          // how those files are handled depends on the presence of
          // XMP sidecar files.
          String xmpFileName = FileUtil.replaceExtension(file.getPath(), "xmp"); //$NON-NLS-1$
          if (xmpFileName != null) {
            File xmpFile = new File(xmpFileName);
            if (xmpFile.exists()) {
              handleOneByOne.add(file);
            } else {
              handleInOneGo.add(file);
            }
          }
          break;
        case UNKOWN:
        case XMP:
          break;
        default:
          break;
      }
    }
    // System.out.println("One by one: "+handleOneByOne.size());
    // System.out.println("In one go: "+handleInOneGo.size());
    ImageInfo imageInfo;
    int imagesPublished = 0;
    int imagesFailed = 0;
    for (File file : files) {
      if (interruptRequested()) {
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
        } else {
          imagesFailed++;
        }
      } catch (RuntimeException e) {
        // catch all Runtime Exceptions - don't let the task die
        imagesFailed++;
        e.printStackTrace();
      }
    }
    StringBuilder result = new StringBuilder();
    if (imagesPublished == 1) {
      result.append(Messages.getString("ExifReaderTask.OneImageLoaded")); //$NON-NLS-1$
    } else {
      try {
        result.append(String.format(Messages
            .getString("ExifReaderTask.ImagesLoadedFormat"), imagesPublished)); //$NON-NLS-1$
      } catch (IllegalFormatException e) {
        e.printStackTrace();
      }
    }
    if (imagesFailed > 0 && !Exiftool.isAvailable()) {
      result.append(Messages.getString("ExifReaderTask.ExiftoolNotFound")); //$NON-NLS-1$
    }
    // long end = System.currentTimeMillis();
    // System.out.println("Read files: "+(end-start)/1000.0);
    return result.toString();
  }

  /**
   * @see javax.swing.SwingWorker#process(java.util.List)
   */
  @Override
  protected void process(List<ImageInfo> chunks) {
    for (ImageInfo imageInfo : chunks) {
      int row = tableModel.getRow(imageInfo);
      if (row == -1) {
        images.add(imageInfo);
        tableModel.addImageInfo(imageInfo);
      } else {
        tableModel.fireTableRowsUpdated(row, row);
      }
    }
  }

  /**
   * @see org.fibs.geotag.tasks.UndoableBackgroundTask#redo()
   */
  @Override
  public void redo() {
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
  public void undo() {
    // Needs to call super
    super.undo();
    for (ImageInfo imageInfo : images) {
      int row = tableModel.getRow(imageInfo);
      tableModel.removeRow(row);
    }
    tableModel.fireTableDataChanged();
  }

}
