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

package org.fibs.geotag.exif;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.UpdateCameraDate;
import org.fibs.geotag.data.UpdateCityName;
import org.fibs.geotag.data.UpdateCountryName;
import org.fibs.geotag.data.UpdateGPSAltitude;
import org.fibs.geotag.data.UpdateGPSDateTime;
import org.fibs.geotag.data.UpdateGPSImgDirection;
import org.fibs.geotag.data.UpdateGPSLatitude;
import org.fibs.geotag.data.UpdateGPSLongitude;
import org.fibs.geotag.data.UpdateLocationName;
import org.fibs.geotag.data.UpdateProvinceName;
import org.fibs.geotag.data.UpdateUserComment;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.image.FileTypes;
import org.fibs.geotag.util.Units.ALTITUDE;

/**
 * @author Andreas Schneider
 * 
 */
public class ExiftoolReader implements ExifReader {

  /** exiftag error lines start with this text. */
  private static final String ERROR_TAG = "Error:"; //$NON-NLS-1$

  /** exiftool DateTimeOriginal output lines start with this text. */
  private static final String DATE_TIME_ORIGINAL_TAG = "DateTimeOriginal: "; //$NON-NLS-1$

  /** exiftool CreateTag output lines start with this text. */
  private static final String CREATE_DATE_TAG = "CreateDate: "; //$NON-NLS-1$

  /** exiftool GPSLatitude output lines start with this text. */
  private static final String GPS_LATITUDE_TAG = "GPSLatitude: "; //$NON-NLS-1$

  /** exiftool GPSLongitude output lines start with this text. */
  private static final String GPS_LONGITUDE_TAG = "GPSLongitude: "; //$NON-NLS-1$

  /** exiftool GPSAltitude output lines start with this text. */
  private static final String GPS_ALTITUDE_TAG = "GPSAltitude: "; //$NON-NLS-1$

  /** exiftool GPSImgDirection output lines start with this text. */
  private static final String GPS_IMG_DIRECTION_TAG = "GPSImgDirection: "; //$NON-NLS-1$

  /** exiftool GPSDatetime output lines start with this text. */
  private static final String GPS_DATE_TIME_TAG = "GPSDateTime: "; //$NON-NLS-1$

  /**
   * exiftool XMP:GPSTimeStamp output lines start with this text. (only up to
   * exiftool 7.03)
   */
  private static final String GPS_TIME_STAMP_TAG = "GPSTimeStamp: "; //$NON-NLS-1$

  /** exiftool Orientation output lines start with this text. */
  private static final String ORIENTATION_TAG = "Orientation: "; //$NON-NLS-1$
  
  /** exiftool ImageWidth output lines start with this text */
  private static final String IMAGE_WIDTH_TAG = "ImageWidth: "; //$NON-NLS-1$
  
  /** exiftool ImageHeight output lines start with this text */
  private static final String IMAGE_HEIGHT_TAG = "ImageHeight: "; //$NON-NLS-1$
  
  /** exiftool EXIF Location output lines start with this text. */
  private static final String LOCATION_TAG = "ContentLocationName: "; //$NON-NLS-1$
  
  /** exiftool Sub-Location lines start with this text */
  private static final String SUB_LOCATION_TAG = "Sub-location: "; //$NON-NLS-1$

  /** exiftool XMP Location output lines start with this text. */
  private static final String LOCATION_TAG_2 = "Location: "; //$NON-NLS-1$

  /** exiftool City output lines start with this text. */
  private static final String CITY_TAG = "City: "; //$NON-NLS-1$

  /** exiftool Country output lines start with this text. */
  private static final String COUNTRY_TAG = "Country-PrimaryLocationName: "; //$NON-NLS-1$

  /** exiftool XMP Country output lines start with this text. */
  private static final String COUNTRY_XMP_TAG = "Country: "; //$NON-NLS-1$

  /** exiftool Province output lines start with this text. */
  private static final String PROVINCE_TAG = "Province-State: "; //$NON-NLS-1$

  /** exiftool XMP state output lines start with this text. */
  private static final String STATE_TAG = "State: "; //$NON-NLS-1$
  
  /** exiftool UserCo,ment lines start with this text. */
  private static final String USER_COMMENT_TAG = "UserComment: "; //$NON-NLS-1$

  /**
   * Create the exiftool command line arguments.
   * 
   * @return The arguments as an array of strings
   */
  private String[] exifToolArguments() {
    List<String> args = new ArrayList<String>();
    // -S generates short output
    args.add("-S"); //$NON-NLS-1$
    // -n generates numeric output (not descriptive)
    args.add("-n"); //$NON-NLS-1$
    // retrieve the CreateDate first */
    args.add("-CreateDate"); //$NON-NLS-1$
    // then retrieve the DateTimeOriginal to override it
    args.add("-DateTimeOriginal"); //$NON-NLS-1$
    // retrieve the latitude
    args.add("-GPSLatitude"); //$NON-NLS-1$
    // retrieve the longitude
    args.add("-GPSLongitude"); //$NON-NLS-1$
    // retrieve the altitude
    args.add("-GPSAltitude"); //$NON-NLS-1$
    // retrieve the direction
    args.add("-GPSImgDirection"); //$NON-NLS-1$
    args.add("-GPSImgDirectionRef"); //$NON-NLS-1$
    // retrieve the GPS date/time
    args.add("-GPSDateTime"); //$NON-NLS-1$
    // retrieve the image orientation, width and height
    args.add("-Orientation"); //$NON-NLS-1$
    args.add("-ImageWidth"); //$NON-NLS-1$
    args.add("-ImageHeight"); //$NON-NLS-1$
    // retrieve IPTC image location
    args.add("-ContentLocationName"); //$NON-NLS-1$
    // retrieve sub-location (seems better choice than content location)
    args.add("-Sub-Location"); //$NON-NLS-1$
    // retrieve IPTC city name
    args.add("-City"); //$NON-NLS-1$
    // retrieve IPTC country
    args.add("-Country-PrimaryLocationName"); //$NON-NLS-1$
    // retrieve IPTC province/state
    args.add("-Province-State"); //$NON-NLS-1$
    // retrieve user comment
    args.add("-UserComment"); //$NON-NLS-1$
    return args.toArray(new String[0]);
  }

  /**
   * Create the exiftool XMP command line arguments.
   * 
   * @return The arguments as an array of strings
   */
  private String[] exifToolXmpArguments() {
    List<String> args = new ArrayList<String>();
    // -S generates short output
    args.add("-S"); //$NON-NLS-1$
    // -n generates numeric output (not descriptive)
    args.add("-n"); //$NON-NLS-1$
    // date time information in XMP file overrides data from image Exif
    //args.add("-XMP:DateTimeOriginal"); //$NON-NLS-1$
    // retrieve the latitude
    args.add("-GPSLatitude"); //$NON-NLS-1$
    // retrieve the longitude
    args.add("-GPSLongitude"); //$NON-NLS-1$
    // retrieve the altitude
    args.add("-GPSAltitude"); //$NON-NLS-1$
    // retrieve the GPS date/time - this changed in version 7.04 of exiftool
    if ("7.04".compareTo(Exiftool.getVersion()) > 0) { //$NON-NLS-1$
      // old behaviour
      args.add("-XMP:GPSTimeStamp"); //$NON-NLS-1$
    } else {
      // new argument since 7.04
      args.add("-XMP:GPSDateTime"); //$NON-NLS-1$
    }
    // retrieve the direction
    args.add("-XMP:GPSImgDirection"); //$NON-NLS-1$
    args.add("-XMP:GPSImgDirectionRef"); //$NON-NLS-1$
    // retrieve the image orientation width and height
    args.add("-XMP:Orientation"); //$NON-NLS-1$
    args.add("-XMP:ImageWidth"); //$NON-NLS-1$
    args.add("-XMP:ImageHeight"); //$NON-NLS-1$
    // retrieve the image location
    args.add("-XMP:Location"); //$NON-NLS-1$
    // retrieve XMP City
    args.add("-XMP:City"); //$NON-NLS-1$
    // retrieve XMP Country
    args.add("-XMP:Country"); //$NON-NLS-1$
    // retrieve XMP state
    args.add("-XMP:State"); //$NON-NLS-1$
    // retrieve XMP user comment
    args.add("-XMP:UserComment"); //$NON-NLS-1$

    return args.toArray(new String[0]);
  }

  /**
   * Read EXIF data from a file and create an {@link ImageInfo} object.
   * 
   * @param file
   *          The file to be examined
   * @param reuseImageInfo
   *          Reuse this ImageInfo if not null, create new one otherwise
   * @return The {@link ImageInfo} for that file
   */
  @Override
  public ImageInfo readExifData(File file, ImageInfo reuseImageInfo) {
    ImageInfo imageInfo = reuseImageInfo;
    // First we build the command line
    List<String> command = new ArrayList<String>();
    command.add(Settings.get(SETTING.EXIFTOOL_PATH, "exiftool")); //$NON-NLS-1$
    String[] arguments = exifToolArguments();
    if (FileTypes.fileType(file) == FileTypes.XMP) {
      arguments = exifToolXmpArguments();
    }
    for (String argument : arguments) {
      command.add(argument);
    }
    // add the filename
    command.add(file.getPath());
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      Process process = processBuilder.redirectErrorStream(true).start();
      // process.waitFor();
      InputStream stream = process.getInputStream();
      // InputStream stream = new FileInputStream(outFile);
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
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream,
        Charset.forName("UTF-8"))); //$NON-NLS-1$
    try {
      // forever (or until we break out of the loop
      for (;;) {
        // read one line
        String line = reader.readLine();

        if (line == null) {
          break; // end of stream - process has probably finished
        }
        // convert the buffer to text
        String text = line;
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
          imageInfo.setGpsDateTime();
        } else if (text.startsWith(GPS_LATITUDE_TAG)) {
          String latitude = text.substring(GPS_LATITUDE_TAG.length());
          // Exiftool translates undef() as "0" in numeric mode
          if (! "0".equals(latitude)) { //$NON-NLS-1$
            new UpdateGPSLatitude(imageInfo, latitude, ImageInfo.DATA_SOURCE.IMAGE);
          }
        } else if (text.startsWith(GPS_LONGITUDE_TAG)) {
          String longitude = text.substring(GPS_LONGITUDE_TAG.length());
          // Exiftool translates undef() as "0" in numeric mode
          if (! "0".equals(longitude)) { //$NON-NLS-1$
            new UpdateGPSLongitude(imageInfo, longitude, ImageInfo.DATA_SOURCE.IMAGE);
          }
        } else if (text.startsWith(GPS_ALTITUDE_TAG)) {
          String altitude = text.substring(GPS_ALTITUDE_TAG.length());
          if (! "undef".equals(altitude)) { //$NON-NLS-1$
            // altitudes in exif data are in metres
            new UpdateGPSAltitude(imageInfo, altitude, ImageInfo.DATA_SOURCE.IMAGE, ALTITUDE.METRES);
          }
        } else if (text.startsWith(GPS_IMG_DIRECTION_TAG)) {
          String direction = text.substring(GPS_IMG_DIRECTION_TAG.length());
          if (! "undef".equals(direction)) { //$NON-NLS-1$
            new UpdateGPSImgDirection(imageInfo, direction, DATA_SOURCE.IMAGE);
          }
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
        } else if (text.startsWith(IMAGE_WIDTH_TAG)) {
          try {
            int width = Integer.parseInt(text.substring(IMAGE_WIDTH_TAG.length()));
            imageInfo.setWidth(width);
          } catch (NumberFormatException e) {
            e.printStackTrace();
          }
        } else if (text.startsWith(IMAGE_HEIGHT_TAG)) {
          try {
            int height = Integer.parseInt(text.substring(IMAGE_HEIGHT_TAG.length()));
            imageInfo.setHeight(height);
          } catch (NumberFormatException e) {
            e.printStackTrace();
          }
        } else if (text.startsWith(LOCATION_TAG)) {
          new UpdateLocationName(imageInfo, text.substring(LOCATION_TAG
              .length()), DATA_SOURCE.IMAGE);
        } else if (text.startsWith(SUB_LOCATION_TAG)) {
            new UpdateLocationName(imageInfo, text.substring(SUB_LOCATION_TAG
                    .length()), DATA_SOURCE.IMAGE);
        } else if (text.startsWith(LOCATION_TAG_2)) {
          new UpdateLocationName(imageInfo, text.substring(LOCATION_TAG_2
              .length()), DATA_SOURCE.IMAGE);
        } else if (text.startsWith(CITY_TAG)) {
          new UpdateCityName(imageInfo, text.substring(CITY_TAG.length()),
              DATA_SOURCE.IMAGE);
        } else if (text.startsWith(COUNTRY_TAG)) {
          new UpdateCountryName(imageInfo,
              text.substring(COUNTRY_TAG.length()), DATA_SOURCE.IMAGE);
        } else if (text.startsWith(COUNTRY_XMP_TAG)) {
          new UpdateCountryName(imageInfo, text.substring(COUNTRY_XMP_TAG
              .length()), DATA_SOURCE.IMAGE);
        } else if (text.startsWith(PROVINCE_TAG)) {
          new UpdateProvinceName(imageInfo, text.substring(PROVINCE_TAG
              .length()), DATA_SOURCE.IMAGE);
        } else if (text.startsWith(STATE_TAG)) {
          new UpdateProvinceName(imageInfo, text.substring(STATE_TAG.length()),
              DATA_SOURCE.IMAGE);
        } else if (text.startsWith(USER_COMMENT_TAG)) {
          new UpdateUserComment(imageInfo, text.substring(USER_COMMENT_TAG.length()), 
              DATA_SOURCE.IMAGE);
        } else if (text.startsWith(ERROR_TAG)) {
          // throw exception with text, caught below
          throw new IllegalArgumentException(text);
        } else {
          System.out.println(text);
        }
      }
      reader.close();
    } catch (Throwable e) {
      System.err.println(this.getClass().getName() + ": " + e.getMessage()); //$NON-NLS-1$
      return null;
    }
    return imageInfo;
  }

}
