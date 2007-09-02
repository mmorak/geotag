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

package org.fibs.geotag.exif;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.fibs.geotag.Settings;
import org.fibs.geotag.data.EditCreateDate;
import org.fibs.geotag.data.EditGPSAltitude;
import org.fibs.geotag.data.EditGPSDateTime;
import org.fibs.geotag.data.EditGPSLatitude;
import org.fibs.geotag.data.EditGPSLongitude;
import org.fibs.geotag.data.ImageInfo;

/**
 * @author Andreas Schneider
 * 
 */
public class ExiftoolReader implements ExifReader {

  /** exiftag error lines start with this text */
  private static final String ERROR_TAG = "Error:"; //$NON-NLS-1$

  /** exiftool DateTimeOriginal output lines start with this text */
  private static final String DATE_TIME_ORIGINAL_TAG = "DateTimeOriginal: "; //$NON-NLS-1$

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
   */
  private enum EXIFTOOL_ARGUMENTS {
    /** -S generates short output */
    S,
    /** -n generates numeric output (not descriptive) */
    n,
    /** retrieve the DateTimeOriginal first */
    DateTimeOriginal,
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
   * Read EXIF data from a file and create an {@link ImageInfo} object
   * 
   * @param file
   *          The file to be examined
   * @return The {@link ImageInfo} for that file
   */
  public ImageInfo readExifData(File file) {
    // First we build the command line
    List<String> command = new ArrayList<String>();
    command.add(Settings.get(Settings.EXIFTOOL_PATH, "exiftool")); //$NON-NLS-1$
    for (int i = 0; i < EXIFTOOL_ARGUMENTS.values().length; i++) {
      EXIFTOOL_ARGUMENTS argument = EXIFTOOL_ARGUMENTS.values()[i];
      command.add("-" + argument.toString()); //$NON-NLS-1$
    }
    // add the filename
    command.add(file.getPath());
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      Process process = processBuilder.redirectErrorStream(true).start();
      InputStream stream = process.getInputStream();
      ImageInfo imageInfo = readExifData(file, stream);
      stream.close();
      return imageInfo;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Read the output of exiftool and generate an {@link ImageInfo} object from
   * it.
   * 
   * @param file
   *          The file being examined by exiftool
   * @param stream
   *          The stream containing the output of exiftool
   * @return The {@link ImageInfo} for the file
   */
  private ImageInfo readExifData(File file, InputStream stream) {
    // The ImageInfo object holding information about the current file
    ImageInfo imageInfo = new ImageInfo(file);
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
          // now compare with the other lines we are interested in and extract
          // information
          if (text.startsWith(DATE_TIME_ORIGINAL_TAG)
              || text.startsWith(CREATE_DATE_TAG)) {
            // don't set it twice. Exiftool honours the order of its arguments
            // so it will first deliver the DateTimeOriginal and then other
            // values
            // DateTimeOriginal is the one we want most of all
            if (imageInfo.getCreateDate() == null) {
              String createDate;
              if (text.startsWith(DATE_TIME_ORIGINAL_TAG)) {
                createDate = text.substring(DATE_TIME_ORIGINAL_TAG.length());
              } else {
                // CreateDate is the only option left
                createDate = text.substring(CREATE_DATE_TAG.length());
              }
              new EditCreateDate(imageInfo, createDate);
              // we also set the GPS date to a good guess if it hasn't been
              // set yet.
              imageInfo.setGPSDateTime();
            }
          } else if (text.startsWith(GPS_LATITUDE_TAG)) {
            new EditGPSLatitude(imageInfo, text.substring(GPS_LATITUDE_TAG
                .length()), ImageInfo.DATA_SOURCE.IMAGE);
          } else if (text.startsWith(GPS_LONGITUDE_TAG)) {
            new EditGPSLongitude(imageInfo, text.substring(GPS_LONGITUDE_TAG
                .length()), ImageInfo.DATA_SOURCE.IMAGE);
          } else if (text.startsWith(GPS_ALTITUDE_TAG)) {
            new EditGPSAltitude(imageInfo, text.substring(GPS_ALTITUDE_TAG
                .length()), ImageInfo.DATA_SOURCE.IMAGE);
          } else if (text.startsWith(GPS_DATE_TIME_TAG)) {
            new EditGPSDateTime(imageInfo, text.substring(GPS_DATE_TIME_TAG
                .length()));
          } else if (text.startsWith(ORIENTATION_TAG)) {
            imageInfo.setOrientation(text.substring(ORIENTATION_TAG.length()));
          } else if (text.startsWith(ERROR_TAG)) {
            // throw exception with text, caught below
            throw new IllegalArgumentException(text);
          } else {
            System.out.println(text);
          }
          // The line has been handled - empty buffer and start all over again
          bytesInBuffer = 0;
        }
      }

    } catch (Exception e) {
      System.err.println(this.getClass().getName() + ": " + e.getMessage()); //$NON-NLS-1$
      return null;
    }
    return imageInfo;
  }

}
