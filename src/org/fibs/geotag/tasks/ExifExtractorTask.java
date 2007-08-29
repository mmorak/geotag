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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import org.fibs.geotag.data.EditCreateDate;
import org.fibs.geotag.data.EditGPSAltitude;
import org.fibs.geotag.data.EditGPSDateTime;
import org.fibs.geotag.data.EditGPSLatitude;
import org.fibs.geotag.data.EditGPSLongitude;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.ui.ImagesTableModel;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.GpsDirectory;

/**
 * An implementation of ExifReaderTask using the MetadataExtractor API
 * 
 * @author Andreas Schneider
 * 
 */
public class ExifExtractorTask extends ExifReaderTask {

  /**
   * Create this ExiftoolInfoReader
   * 
   * @param name
   * @param tableModel
   * @param files
   */
  public ExifExtractorTask(String name, ImagesTableModel tableModel,
      File[] files) {
    super(name, tableModel, files);
  }

  /**
   * A convenience method to convert an array of three {@link Rational}s to a
   * latitude/longitude. Can throw an ArrayIndexOutOfBoundsException.
   * 
   * @param rationals
   *          The array of rationals
   * @return The value in degrees
   */
  private double rationalsToDegrees(Rational[] rationals) {
    return rationals[0].doubleValue() + rationals[1].doubleValue() / 60.0
        + rationals[2].doubleValue() / 3600.0;
  }

  /**
   * @see org.fibs.geotag.tasks.ExifReaderTask#readExifData()
   */
  @SuppressWarnings("unchecked")
  // because we use the pre Java 5 MetadataExtractor API
  @Override
  protected int readExifData() {
    // process all files
    int imagesPublished = 0;
    for (File file : files) {
      if (terminate) {
        break;
      }
      // create an ImageInfo object
      ImageInfo imageInfo = new ImageInfo(file);
      // keep track of progress - use protected variable from superclass
      currentProgress++;
      // give feedback via the ProgressBar
      setProgressMessage();
      try {
        Metadata metadata = JpegMetadataReader.readMetadata(file);
        // iterate through metadata directories
        Iterator directories = metadata.getDirectoryIterator();
        while (directories.hasNext()) {
          Directory directory = (Directory) directories.next();
          // we're interested in two directories:
          if (directory.getName().equals("Exif")) { //$NON-NLS-1$
            // The Exif directory should contain the createDate of the image
            // It's found in the DatetimeOriginal Tag and can be retrieved
            // as a simple string
            String createDate = directory
                .getString(ExifDirectory.TAG_DATETIME_ORIGINAL);
            // update the imageinfo/exifdata
            new EditCreateDate(imageInfo, createDate);
            // we also set the GPS date to a good guess if it hasn't been
            // set yet.
            if (imageInfo.getGPSDateTime() == null) {
              // create a DateFormat for the local time zone
              DateFormat dateFormat = new SimpleDateFormat(ImageInfo
                  .getDateFormatPattern());
              try {
                Date date = dateFormat.parse(createDate);
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
                String gmtTime = dateFormat.format(date);
                new EditGPSDateTime(imageInfo, gmtTime);
              } catch (ParseException e) {
                e.printStackTrace();
              }
            }
            // the orientation of the image is also stored here
            // catch all exceptions
            try {
              int orientation = directory.getInt(ExifDirectory.TAG_ORIENTATION);
              imageInfo.setOrientation(Integer.toString(orientation));
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else if (directory.getName().equals("GPS")) { //$NON-NLS-1$
            // we're also interested in the GPS directory.
            // Here we try to retrieve several tags, each attempt
            // surrounded by try/catch blocks catching all exceptions
            try {
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
              new EditGPSAltitude(imageInfo, Double.toString(altitude),
                  ImageInfo.DATA_SOURCE.IMAGE);
            } catch (Exception e) {
              // catch all Exceptions
              e.printStackTrace();
            }
            try {
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
              new EditGPSLatitude(imageInfo, Double.toString(latitude),
                  ImageInfo.DATA_SOURCE.IMAGE);
            } catch (Exception e) {
              e.printStackTrace();
            }
            try {
              // the same procedure for the longitude
              Rational[] rationals = directory
                  .getRationalArray(GpsDirectory.TAG_GPS_LONGITUDE);
              double longitude = rationalsToDegrees(rationals);
              char ref = (char) directory
                  .getByteArray(GpsDirectory.TAG_GPS_LONGITUDE_REF)[0];
              if (ref == 'W') {
                longitude = -longitude;
              }
              new EditGPSLongitude(imageInfo, Double.toString(longitude),
                  ImageInfo.DATA_SOURCE.IMAGE);
            } catch (Exception e) {
              e.printStackTrace();
            }
            // The time is strangely stored as sRationals as well, one
            // each for hours, minutes and seconds
            String time = null;
            try {
              Rational[] rationals = directory
                  .getRationalArray(GpsDirectory.TAG_GPS_TIME_STAMP);
              // we convert them to integers
              int hours = rationals[0].intValue();
              int minutes = rationals[1].intValue();
              int seconds = rationals[2].intValue();
              // format them as a String
              time = String
                  .format(
                      "%02d:%02d:%02d", new Integer(hours), new Integer(minutes), new Integer(seconds)); //$NON-NLS-1$
            } catch (Exception e) {
              e.printStackTrace();
            }
            // The date id stored as a simple String
            String date = null;
            try {
              date = directory.getString(GpsDirectory.TAG_GPS_DATE_STAMP);
            } catch (Exception e) {
              e.printStackTrace();
            }
            // if we were able to retrieve both date and time
            if (date != null && time != null) {
              // we update the ImageInfo
              new EditGPSDateTime(imageInfo, date + ' ' + time);
            }
          }
        }
        // that's all the information we were able to retrieve,
        // publish the result
        publish(imageInfo);
        imagesPublished++;
      } catch (JpegProcessingException e) {
        e.printStackTrace();
      }
    }
    return imagesPublished;
  }
}
