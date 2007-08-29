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
import java.io.IOException;
import java.io.InputStream;

import org.fibs.geotag.data.EditGPSAltitude;
import org.fibs.geotag.data.EditGPSDateTime;
import org.fibs.geotag.data.EditGPSLatitude;
import org.fibs.geotag.data.EditGPSLongitude;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.ui.ImagesTableModel;

/**
 * An implementation of ExifReaderTask using exiftool to do the job. This is
 * only left to show that reading EXIF data can be implemented in different
 * ways. Using the MetadataExtractor library is almost twice as fast. this task
 * is not undo-able yet. This class doesn't work! It ignores the EXIF fields
 * indicating East/west and North/South. I still leave it in the source code as
 * an example how to use exiftool.
 * 
 * @author Andreas Schneider
 * 
 */
public class ExiftoolReaderTask extends ExifReaderTask {

  /** exiftool filename output lines start with this text */
  private static final String FILENAME_TAG = "======== "; //$NON-NLS-1$

  /** exiftool CreateTag output lines start with this text */
  private static final String CREATE_DATE_TAG = "CreateDate: "; //$NON-NLS-1$

  /** exiftool GPSLatitude output lines start with this text */
  private static final String GPS_LATITUDE_TAG = "GPSLatitude: "; //$NON-NLS-1$

  /** exiftool GPSLongitude output lines start with this text */
  private static final String GPS_LONGITUDE_TAG = "GPSLongitude: "; //$NON-NLS-1$

  /** exiftool GPSAltitude output lines start with this text */
  private static final String GPS_ALTITUDE_TAG = "GPSAltitude: "; //$NON-NLS-1$

  /** exiftool GPSDatetime output lines start with this text */
  private static final String GPS_DATE_TIME_TAG = "GPSDateTime: "; //$NON-NLS-1$

  /** exiftool Orientation output lines start with this text */
  private static final String ORIENTATION_TAG = "Orientation: "; //$NON-NLS-1$

  /**
   * An enum for the exiftool command line arguments (without the leading dash)
   * 
   * @author Andreas Schneider
   * 
   */
  private enum EXIFTOOL_ARGUMENTS {
    /** -S generates short output */
    S,
    /** -n generates numeric output (not descriptive) */
    n,
    /** retrieve the CreateDate */
    CreateDate,
    /** retrieve the latitude */
    GPSLatitude,
    /** retrieve the longitude */
    GPSLongitude,
    /** retrieve the altitude */
    GPSAltitude,
    /** retrieve the GPS date/time */
    GPSDateTime,
    /** retrieve the image orientation */
    Orientation
  }

  /**
   * Create this ExiftoolInfoReader
   * 
   * @param name
   * @param tableModel
   * @param files
   */
  public ExiftoolReaderTask(String name, ImagesTableModel tableModel,
      File[] files) {
    super(name, tableModel, files);
  }

  /**
   * Read EXIF data from a bunch of files and create {@link ImageInfo} objects
   * for them.
   */
  @Override
  protected int readExifData() {
    int imagesLoaded = 0;
    // First we build the command line
    String[] commandArray = new String[1 + EXIFTOOL_ARGUMENTS.values().length
        + files.length];
    commandArray[0] = "exiftool"; //$NON-NLS-1$
    for (int i = 0; i < EXIFTOOL_ARGUMENTS.values().length; i++) {
      EXIFTOOL_ARGUMENTS argument = EXIFTOOL_ARGUMENTS.values()[i];
      commandArray[1 + i] = "-" + argument.toString(); //$NON-NLS-1$
    }
    // add the filenames
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      commandArray[1 + EXIFTOOL_ARGUMENTS.values().length + i] = file.getPath();
    }
    try {
      Process process = Runtime.getRuntime().exec(commandArray, null, null);
      InputStream stream = process.getInputStream();
      imagesLoaded = readData(stream);
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return imagesLoaded;
  }

  /**
   * Read the output of exiftool and generate {@link ImageInfo} objects from it.
   * 
   * @param stream
   * @return number of images loaded
   */
  private int readData(InputStream stream) {
    // The ImageInfo object holding information about the current file
    ImageInfo currentImageInfo = null;
    // keep track of files processed (for the ProgressMonitor)
    int filesFound = 0;
    // we read the data into a array of bytes - this is its size
    int bufferSize = 256;
    // and this is the buffer itself
    byte[] buffer = new byte[bufferSize];
    // initially there are no bytes in the buffer
    int bytesInBuffer = 0;
    try {
      // forever (or until we break out of the loop
      for (;;) {
        // read one byte
        int b = stream.read();
        if (b == -1) {
          break; // end of stream - process has probably finished
        }
        // put the byte read into the buffer
        buffer[bytesInBuffer] = (byte) b;
        // there is now one more byte in the buffer
        bytesInBuffer++;
        if (b == '\n' || bytesInBuffer == bufferSize) {
          // end of line or buffer full
          // first remove trailing \r\n
          while (bytesInBuffer > 0
              && (buffer[bytesInBuffer - 1] == '\r' || buffer[bytesInBuffer - 1] == '\n')) {
            bytesInBuffer--;
          }
          // convert the buffer to text
          String text = new String(buffer, 0, bytesInBuffer);
          // if the text starts with FILENAME_TAG, this starts a new exiftool
          // section
          if (text.startsWith(FILENAME_TAG)) {
            // if there is currentImageInfo, we;re done with it
            // Just spotted a bug - the last ImageInfo is never published
            // but this class is just an exaple, so I don't really care
            if (currentImageInfo != null) {
              publish(currentImageInfo);
            }
            filesFound++;
            currentProgress = filesFound;
            setProgressMessage();
            // remove the leading FILENAME_TAG
            String filename = text.substring(FILENAME_TAG.length());
            // and create a new ImageInfo object for this image
            currentImageInfo = new ImageInfo(new File(filename));
          } else if (currentImageInfo != null) { // make sure we get a filename
            // line first
            // now compare with the other lines we are interested in and extract
            // information
            if (text.startsWith(CREATE_DATE_TAG)) {
              new EditGPSDateTime(currentImageInfo, text
                  .substring(CREATE_DATE_TAG.length()));
              // TODO: this should also set a reasonable GMT time
            } else if (text.startsWith(GPS_LATITUDE_TAG)) {
              new EditGPSLatitude(currentImageInfo, text
                  .substring(GPS_LATITUDE_TAG.length()),
                  ImageInfo.DATA_SOURCE.IMAGE);
            } else if (text.startsWith(GPS_LONGITUDE_TAG)) {
              new EditGPSLongitude(currentImageInfo, text
                  .substring(GPS_LONGITUDE_TAG.length()),
                  ImageInfo.DATA_SOURCE.IMAGE);
            } else if (text.startsWith(GPS_ALTITUDE_TAG)) {
              new EditGPSAltitude(currentImageInfo, text
                  .substring(GPS_ALTITUDE_TAG.length()),
                  ImageInfo.DATA_SOURCE.IMAGE);
            } else if (text.startsWith(GPS_DATE_TIME_TAG)) {
              new EditGPSDateTime(currentImageInfo, text
                  .substring(GPS_DATE_TIME_TAG.length()));
            } else if (text.startsWith(ORIENTATION_TAG)) {
              currentImageInfo.setOrientation(text.substring(ORIENTATION_TAG
                  .length()));
            } else {
              System.out.println(text);
            }
          }
          // The line has been handled - empty buffer and start all over again
          bytesInBuffer = 0;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return filesFound;
  }
}
