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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

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
import org.fibs.geotag.util.Constants;
import org.fibs.geotag.util.Units.ALTITUDE;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.iptc.IptcDirectory;

/**
 * @author Andreas Schneider
 * 
 */
public class MetadataExtractorReader implements ExifReader {

  /**
   * A convenience method to convert an array of three {@link Rational}s to a
   * latitude/longitude. Can throw an ArrayIndexOutOfBoundsException.
   * 
   * @param rationals
   *          The array of rationals
   * @return The value in degrees
   */
  private double rationalsToDegrees(Rational[] rationals) {
    double degrees = 0.0;
    // very elaborate, but apparently Adobe Lightroom saves 0 denominators 
    if (rationals[0].getDenominator() != 0) {
      degrees += rationals[0].doubleValue();
    }
    if (rationals[1].getDenominator() != 0) {
      degrees += rationals[1].doubleValue() / Constants.MINUTES_PER_DEGREE;
    }
    if (rationals[2].getDenominator() != 0) {
      degrees += rationals[2].doubleValue() / Constants.SECONDS_PER_DEGREE;
    }
    return degrees;
  }

  /**
   * @param file
   *          The file being examined
   * @param reuseImageInfo
   *          reuse this ImageInfo if not null, otherwise create a new one
   * @return The {@link ImageInfo} for that file
   */
  @Override
  // because we use the pre Java 5 MetadataExtractor API
  public ImageInfo readExifData(File file, ImageInfo reuseImageInfo) {
    // process all files
    ImageInfo imageInfo = reuseImageInfo;
    if (imageInfo == null) {
      // get or create create an ImageInfo object
      imageInfo = ImageInfo.getImageInfo(file.getPath());
      if (imageInfo == null) {
        imageInfo = new ImageInfo(file);
      }
    }
    try {
      Metadata metadata = JpegMetadataReader.readMetadata(file);
      // iterate through metadata directories
      @SuppressWarnings("rawtypes")
      Iterator directories = metadata.getDirectoryIterator();
      while (directories.hasNext()) {
        Directory directory = (Directory) directories.next();
        // we're interested in two directories:
        if (directory.getName().equals("Exif")) { //$NON-NLS-1$
          readExifDirectory(file, imageInfo, directory);
        } else if (directory.getName().equals("GPS")) { //$NON-NLS-1$
          readGpsDirectory(imageInfo, directory);
        } else if (directory.getName().equals("Iptc")) { //$NON-NLS-1$
          readIptcDirectory(imageInfo, directory);
        }
      }
      // that's all the information we were able to retrieve,
      // publish the result
      return imageInfo;
    } catch (JpegProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @param imageInfo
   * @param directory
   */
  private void readIptcDirectory(ImageInfo imageInfo, Directory directory) {
    try {
      String location = directory.getString(IptcDirectory.TAG_CITY);
      new UpdateCityName(imageInfo, location, DATA_SOURCE.IMAGE);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      String country = directory
          .getString(IptcDirectory.TAG_COUNTRY_OR_PRIMARY_LOCATION);
      new UpdateCountryName(imageInfo, country, DATA_SOURCE.IMAGE);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      String province = directory
          .getString(IptcDirectory.TAG_PROVINCE_OR_STATE);
      new UpdateProvinceName(imageInfo, province, DATA_SOURCE.IMAGE);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      if (directory.containsTag(IptcDirectory.TAG_CONTENT_LOCATION_NAME)) {
        String location = directory
            .getString(IptcDirectory.TAG_CONTENT_LOCATION_NAME);
        new UpdateLocationName(imageInfo, location, DATA_SOURCE.IMAGE);
      } else if (directory.containsTag(IptcDirectory.TAG_SUB_LOCATION)) {
          String location = directory
          .getString(IptcDirectory.TAG_SUB_LOCATION);
      new UpdateLocationName(imageInfo, location, DATA_SOURCE.IMAGE);    	  
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @param imageInfo
   * @param directory
   */
  private void readGpsDirectory(ImageInfo imageInfo, Directory directory) {
    // we're also interested in the GPS directory.
    // Here we try to retrieve several tags, each attempt
    // surrounded by try/catch blocks catching all exceptions
    try {
      if (directory.containsTag(GpsDirectory.TAG_GPS_ALTITUDE)) {
        // first we retrieve the altitude, stored as one Rational
        Rational rational = directory
            .getRational(GpsDirectory.TAG_GPS_ALTITUDE);
        // convert to a double
        double altitude = rational.doubleValue();
        int ref = directory.getInt(GpsDirectory.TAG_GPS_ALTITUDE_REF);
        if (ref == 1) { // this is not the character '1'
          altitude = -altitude;
        }
        // and finally to string used to update the ImageInfo
        // exif altitudes are always in metres
        new UpdateGPSAltitude(imageInfo, Double.toString(altitude),
            ImageInfo.DATA_SOURCE.IMAGE, ALTITUDE.METRES);
      }
    } catch (Exception e) {
      // catch all Exceptions
      e.printStackTrace();
    }
    try {
      // the image direction is stored as one Rational
      if (directory.containsTag(GpsDirectory.TAG_GPS_IMG_DIRECTION)) {
        Rational rational = directory
            .getRational(GpsDirectory.TAG_GPS_IMG_DIRECTION);
        // convert to a double
        double direction = rational.doubleValue();
        // and update
        new UpdateGPSImgDirection(imageInfo, Double.toString(direction),
            DATA_SOURCE.IMAGE);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      if (directory.containsTag(GpsDirectory.TAG_GPS_LATITUDE)) {
        // The latitude is retrieved as an array of three rationals
        Rational[] rationals = directory
            .getRationalArray(GpsDirectory.TAG_GPS_LATITUDE);
        // converted into degrees
        double latitude = rationalsToDegrees(rationals);
        // and stored
        char ref = (char) directory
            .getByteArray(GpsDirectory.TAG_GPS_LATITUDE_REF)[0];
        if (ref == 'S') {
          latitude = -latitude;
        }
        new UpdateGPSLatitude(imageInfo, Double.toString(latitude),
            ImageInfo.DATA_SOURCE.IMAGE);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      if (directory.containsTag(GpsDirectory.TAG_GPS_LONGITUDE)) {
        // the same procedure for the longitude
        Rational[] rationals = directory
            .getRationalArray(GpsDirectory.TAG_GPS_LONGITUDE);
        double longitude = rationalsToDegrees(rationals);
        char ref = (char) directory
            .getByteArray(GpsDirectory.TAG_GPS_LONGITUDE_REF)[0];
        if (ref == 'W') {
          longitude = -longitude;
        }
        new UpdateGPSLongitude(imageInfo, Double.toString(longitude),
            ImageInfo.DATA_SOURCE.IMAGE);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    // The time is strangely stored as sRationals as well, one
    // each for hours, minutes and seconds
    String time = null;
    try {
      if (directory.containsTag(GpsDirectory.TAG_GPS_TIME_STAMP)) {
        Rational[] rationals = directory
            .getRationalArray(GpsDirectory.TAG_GPS_TIME_STAMP);
        // we convert them to integers
        int hours = rationals[0].intValue();
        int minutes = rationals[1].intValue();
        int seconds = rationals[2].intValue();
        // format them as a String
        time = String
            .format(
                "%02d:%02d:%02d", Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)); //$NON-NLS-1$
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    // The date id stored as a simple String
    String date = null;
    try {
      if (directory.containsTag(GpsDirectory.TAG_GPS_DATE_STAMP)) {
        date = directory.getString(GpsDirectory.TAG_GPS_DATE_STAMP);
        // I've come across images that store the date separated by dashes
        // rather than colons as specified by EXIF
        date = date.replace('-', ':');
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    // if we were able to retrieve both date and time
    if (date != null && time != null) {
      // we update the ImageInfo
      new UpdateGPSDateTime(imageInfo, date + ' ' + time);
    }
  }

  /**
   * @param file
   * @param imageInfo
   * @param directory
   */
  private void readExifDirectory(File file, ImageInfo imageInfo,
      Directory directory) {
    // The Exif directory should contain the camera date of the image
    // It's found in the DatetimeOriginal Tag and can be retrieved
    // as a simple string
    String cameraDate = directory
        .getString(ExifDirectory.TAG_DATETIME_ORIGINAL);
    if (cameraDate == null) {
      // If DateTimeOriginal is not found, we settle for DateTimeDigitized
      // which should be the same for digital cameras
      cameraDate = directory.getString(ExifDirectory.TAG_DATETIME_DIGITIZED);
    }
    if (cameraDate == null) {
      // Still nothing, try the DateTime tag
      cameraDate = directory.getString(ExifDirectory.TAG_DATETIME);
    }
    if (cameraDate == null) {
      // as a last resort we use the file date
      long lastModified = file.lastModified();
      // create a DateFormat for the local time zone
      DateFormat dateFormat = new SimpleDateFormat(ImageInfo
          .getDateFormatPattern());
      Date date = new Date(lastModified);
      cameraDate = dateFormat.format(date);
    }
    // update the imageinfo/exifdata
    new UpdateCameraDate(imageInfo, cameraDate);
    // we also set the GPS date to a good guess if it hasn't been
    // set yet.
    imageInfo.setGpsDateTime();
    // the orientation of the image is also stored here
    // catch all exceptions
    try {
      if (directory.containsTag(ExifDirectory.TAG_ORIENTATION)) {
        int orientation = directory.getInt(ExifDirectory.TAG_ORIENTATION);
        imageInfo.setOrientation(Integer.toString(orientation));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      if (directory.containsTag(ExifDirectory.TAG_EXIF_IMAGE_WIDTH)) {
        int width = directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_WIDTH);
        imageInfo.setWidth(width);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      if (directory.containsTag(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT)) {
        int height = directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT);
        imageInfo.setHeight(height);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      if (directory.containsTag(ExifDirectory.TAG_USER_COMMENT)) {
        String comment = directory.getDescription(ExifDirectory.TAG_USER_COMMENT);
        new UpdateUserComment(imageInfo, comment, ImageInfo.DATA_SOURCE.IMAGE);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
