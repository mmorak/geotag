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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.StringTokenizer;

import org.fibs.geotag.Messages;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.ui.ImagesTableModel;

/**
 * A class writing the location information for an image to the EXIF data of the
 * image file
 * 
 * @author Andreas Schneider
 * 
 */
public class ExifWriterTask extends BackgroundTask<ImageInfo> {

  /** the table model to be informed about changes */
  private ImagesTableModel imagesTableModel;

  /** the list of images to be updated */
  private List<ImageInfo> imageInfos;

  /** Keep track of progress */
  private int currentProgress = 0;

  /**
   * @param imagestableModel
   * @param imageInfos
   */
  public ExifWriterTask(ImagesTableModel imagestableModel,
      List<ImageInfo> imageInfos) {
    this.imagesTableModel = imagestableModel;
    this.imageInfos = imageInfos;
  }

  /**
   * this does the work of writing the EXIF data by calling exiftool
   * 
   * @param imageInfo
   * @return True if the exiftool process was called successfully
   */
  public boolean write(ImageInfo imageInfo) {
    boolean success = false;
    // first we build the command
    List<String> command = new ArrayList<String>();
    // the command name
    command.add("exiftool"); //$NON-NLS-1$
    // option -n: -n write values as numbers instead of words
    command.add("-n"); //$NON-NLS-1$
    // option -s: use tag names instead of descriptions
    command.add("-s"); //$NON-NLS-1$
    // the GPSVersion needs to be set to 2.2.0.0
    command.add("-GPSVersionID=2 2 0 0"); //$NON-NLS-1$
    // the latitude
    double latitude = Double.parseDouble(imageInfo.getGPSLatitude());
    command.add("-GPSLatitudeRef=" + (latitude >= 0.0 ? 'N' : 'S')); //$NON-NLS-1$
    command.add("-GPSLatitude=" + Math.abs(latitude)); //$NON-NLS-1$
    // the longitude
    double longitude = Double.parseDouble(imageInfo.getGPSLongitude());
    command.add("-GPSLongitudeRef=" + (longitude >= 0.0 ? 'E' : 'W')); //$NON-NLS-1$
    command.add("-GPSLongitude=" + Math.abs(longitude)); //$NON-NLS-1$
    // the altitude
    double altitude = Double.parseDouble(imageInfo.getGPSAltitude());
    command.add("-GPSAltitudeRef=" + (altitude >= 0.0 ? '0' : '1')); //$NON-NLS-1$
    command.add("-GPSAltitude=" + Math.abs(altitude)); //$NON-NLS-1$
    // the map datum is always set to WGS-84
    command.add("-GPSMapDatum=WGS-84"); //$NON-NLS-1$
    // and finally we set the GPS date and time
    String gpsDatetime = imageInfo.getGPSDateTime();
    if (gpsDatetime != null) {
      // split the time into the date and the time
      StringTokenizer tokenizer = new StringTokenizer(gpsDatetime); // default
      // delimiters
      // are OK
      // here
      if (tokenizer.countTokens() == 2) {
        // that's what we expect
        String date = tokenizer.nextToken();
        String time = tokenizer.nextToken();
        command.add("-GPSDateStamp=" + date); //$NON-NLS-1$
        command.add("-GPSTimeStamp=" + time); //$NON-NLS-1$
      }
    }
    command.add(imageInfo.getPath());
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      Process process = processBuilder.redirectErrorStream(true).start();
      // now start a thread that reads the input stream of the process and
      // writes it to stdout
      final InputStream inputStream = process.getInputStream();
      new Thread() {
        @Override
        public void run() {
          try {
            for (;;) {
              int b = inputStream.read();
              if (b == -1) {
                break; // lost stream - process probably gone
              }
              System.out.write(b);
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }.start();
      // we wait for the process to finish
      process.waitFor();
      success = true;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return success;
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
      if (terminate) {
        break;
      }
      currentProgress++;
      setProgressMessage();
      if (write(imageInfo) == true) {
        // write went OK...
        imagesUpdated++;
        // the location data source is now the image
        imageInfo.setSource(DATA_SOURCE.IMAGE);
        // tell the GUI about the change
        publish(imageInfo);
      }
    }
    String result = null;
    if (imagesUpdated == 1) {
      result = Messages.getString("ExifWriterTask.OneImageUpdated"); //$NON-NLS-1$
    } else {
      try {
        result = String.format(Messages
            .getString("ExifWriterTask.ImagesUpdatedFormat"), imagesUpdated); //$NON-NLS-1$
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
