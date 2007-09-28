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
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.UpdateCameraDate;
import org.fibs.geotag.data.UpdateGPSAltitude;
import org.fibs.geotag.data.UpdateGPSDateTime;
import org.fibs.geotag.data.UpdateGPSImgDirection;
import org.fibs.geotag.data.UpdateGPSLatitude;
import org.fibs.geotag.data.UpdateGPSLongitude;
import org.fibs.geotag.image.FileTypes;

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

  /** exiftool GPSImgDirection output lines start with this text */
  private static final String GPS_IMG_DIRECTION_TAG = "GPSImgDirection: "; //$NON-NLS-1$

  /** exiftool GPSDatetime output lines start with this text */
  private static final String GPS_DATE_TIME_TAG = "GPSDateTime: "; //$NON-NLS-1$

  /** exiftool XMP:GPSTimeStamp output lines start with this text */
  private static final String GPS_TIME_STAMP_TAG = "GPSTimeStamp: "; //$NON-NLS-1$

  /** exiftool Orientation output lines start with this text */
  private static final String ORIENTATION_TAG = "Orientation: "; //$NON-NLS-1$

  /**
   * the exiftool command line arguments
   */
  private String[] exifToolArguments = new String[] { // -S generates short
  // output
      "-S", //$NON-NLS-1$
      // -n generates numeric output (not descriptive)
      "-n", //$NON-NLS-1$
      // retrieve the CreateDate first */
      "-CreateDate", //$NON-NLS-1$
      // then retrieve the DateTimeOriginal to override it
      "-DateTimeOriginal", //$NON-NLS-1$
      // retrieve the latitude
      "-GPSLatitude", //$NON-NLS-1$
      // retrieve the longitude
      "-GPSLongitude", //$NON-NLS-1$
      // retrieve the altitude
      "-GPSAltitude", //$NON-NLS-1$
      // retrieve the GPS date/time
      "-GPSDateTime", //$NON-NLS-1$
      // retrieve the image orientation
      "-Orientation" //$NON-NLS-1$
  };

  /**
   * the exiftool XMP command line arguments
   */
  private String[] exifToolXmpArguments = new String[] { // -S generates short
  // output
      "-S", //$NON-NLS-1$
      // -n generates numeric output (not descriptive)
      "-n", //$NON-NLS-1$
      // retrieve the latitude
      "-XMP:GPSLatitude", //$NON-NLS-1$
      // retrieve the longitude
      "-XMP:GPSLongitude", //$NON-NLS-1$
      // retrieve the altitude
      "-XMP:GPSAltitude", //$NON-NLS-1$
      // retrieve the GPS date/time
      "-XMP:GPSTimeStamp", //$NON-NLS-1$
      // retrieve the image orientation
      "-XMP:Orientation" //$NON-NLS-1$
  };

  /**
   * Read EXIF data from a file and create an {@link ImageInfo} object
   * 
   * @param file
   *          The file to be examined
   * @param reuseImageInfo
   *          Reuse this ImageInfo if not null, create new one otherwise
   * @return The {@link ImageInfo} for that file
   */
  public ImageInfo readExifData(File file, ImageInfo reuseImageInfo) {
    ImageInfo imageInfo = reuseImageInfo;
    // First we build the command line
    List<String> command = new ArrayList<String>();
    command.add(Settings.get(SETTING.EXIFTOOL_PATH, "exiftool")); //$NON-NLS-1$
    String[] arguments = exifToolArguments;
    if (FileTypes.fileType(file) == FileTypes.XMP) {
      arguments = exifToolXmpArguments;
    }
    for (String argument : arguments) {
      command.add(argument);
    }
    // add the filename
    command.add(file.getPath());
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      Process process = processBuilder.redirectErrorStream(true).start();
      InputStream stream = process.getInputStream();
      imageInfo = readExifData(file, stream, imageInfo);
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
   * @param reuseImageInfo
   *          Reuse this ImageInfo for results if not null
   * @return The {@link ImageInfo} for the file
   */
  private ImageInfo readExifData(File file, InputStream stream,
      ImageInfo reuseImageInfo) {
    // The ImageInfo object holding information about the current file
    ImageInfo imageInfo = reuseImageInfo;
    if (imageInfo == null) {
      imageInfo = ImageInfo.getImageInfo(file.getPath());
      if (imageInfo == null) {
        imageInfo = new ImageInfo(file);
      }
    }
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
            // Exiftool honours the order of arguments
            // So the CreateDate will come first and the
            // DateTimeOriginal second.
            String cameraDate;
            if (text.startsWith(DATE_TIME_ORIGINAL_TAG)) {
              cameraDate = text.substring(DATE_TIME_ORIGINAL_TAG.length());
            } else {
              // CreateDate is the only option left
              cameraDate = text.substring(CREATE_DATE_TAG.length());
            }
            new UpdateCameraDate(imageInfo, cameraDate);
            // we also set the GPS date to a good guess if it hasn't been
            // set yet.
            imageInfo.setGPSDateTime();
          } else if (text.startsWith(GPS_LATITUDE_TAG)) {
            new UpdateGPSLatitude(imageInfo, text.substring(GPS_LATITUDE_TAG
                .length()), ImageInfo.DATA_SOURCE.IMAGE);
          } else if (text.startsWith(GPS_LONGITUDE_TAG)) {
            new UpdateGPSLongitude(imageInfo, text.substring(GPS_LONGITUDE_TAG
                .length()), ImageInfo.DATA_SOURCE.IMAGE);
          } else if (text.startsWith(GPS_ALTITUDE_TAG)) {
            new UpdateGPSAltitude(imageInfo, text.substring(GPS_ALTITUDE_TAG
                .length()), ImageInfo.DATA_SOURCE.IMAGE);
          } else if (text.startsWith(GPS_IMG_DIRECTION_TAG)) {
            new UpdateGPSImgDirection(imageInfo, text
                .substring(GPS_IMG_DIRECTION_TAG.length()));
          } else if (text.startsWith(GPS_DATE_TIME_TAG)) {
            new UpdateGPSDateTime(imageInfo, text.substring(GPS_DATE_TIME_TAG
                .length()));
          } else if (text.startsWith(GPS_TIME_STAMP_TAG)) {
            // now this needs a little explanation. There are two different
            // GPSTimeStamp tags. One in the EXIF data which is the time only
            // and one in the XMP data which is date and time.
            // We never ask for the EXIF GPSTimeStamp, so when we see this
            // it has to be the XMP one
            // We also insist, that the GPS time is in GMT, i.e the
            // String we receive must end with Z
            if (text.endsWith("Z")) { //$NON-NLS-1$
              // drop the Z
              String gpsDateTime = text.substring(GPS_TIME_STAMP_TAG.length(),
                  text.length() - 1);
              new UpdateGPSDateTime(imageInfo, gpsDateTime);
            }
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
