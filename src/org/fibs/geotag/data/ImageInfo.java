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

package org.fibs.geotag.data;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.swing.ImageIcon;

/**
 * A class holding the information we have for an image
 * 
 * @author Andreas Schneider
 * 
 */
public class ImageInfo implements Comparable<ImageInfo> {

  /**
   * An enumeration for the source of the location data
   * 
   * @author Andreas Schneider
   * 
   */
  public enum DATA_SOURCE {
    /** No location data available */
    NONE,
    /** Location data was read from the image's EXIF data */
    IMAGE,
    /** Location data was input manually */
    MANUAL,
    /** Location data was received from a map */
    MAP,
    /** Location data was interpolated from neighbouring data */
    INTERPOLATED,
    /** Location data was computed from a track file */
    TRACK,
    /** Location was copied from another image */
    COPIED
  }

  /**
   * An enumeration for the availability of thumbnail images
   * 
   * @author Andreas Schneider
   * 
   */
  public enum THUMBNAIL_STATUS {
    /** Haven't tried to get a thumbnail yet */
    UNKNOWN,
    /** Thumnail loading is currently under way */
    LOADING,
    /** No thumbnail available - No need to try again */
    FAI1LED,
    /** Found a thumbnail and stored it */
    AVAILABLE,
  }

  /** A HashMap containing the data. We use the file path as the key */
  private static HashMap<String, ImageInfo> values = new HashMap<String, ImageInfo>();

  /** The date format pattern used */
  private static final String DATE_FORMAT_PATTERN = "yyyy:MM:dd HH:mm:ss"; //$NON-NLS-1$

  /** Defines how date/time is formatted in EXIF terms */
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
      DATE_FORMAT_PATTERN);

  // don't forget to set the time zone for the DateFormat as well
  static {
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
  }

  /** we keep track of number of instances created */
  private static int instancesCreated = 0;

  /** This acts as short unique ID for this image info */
  private int sequenceNumber;

  /** The image filename including path */
  private String path;

  /** The image filename without the path */
  private String name;

  /** A thumbnail of the image */
  private ImageIcon thumbnail;

  /** Ehat do we know about availability of a thumbnail image */
  private THUMBNAIL_STATUS thumbNailStatus = THUMBNAIL_STATUS.UNKNOWN;

  /** The image width in pixels */
  private int width;

  /** The image height in pixels */
  private int height;

  /** The CameraDate entry */
  private String cameraDate;

  /** THE GPSLatitude EXIF entry */
  private String GPSLatitude;

  /** The GPSLongitude EXIF entry */
  private String GPSLongitude;

  /** The GPSAltitude EXIF entry */
  private String GPSAltitude;
  
  /** Th eGPSImgDirection EXIF entry */
  private String GPSImgDirection;

  /** The GPSDateTime EXIF entry */
  private String GPSDateTime;

  /** The Orientation EXIF entry */
  private String orientation;
  
  /** Where does the location data come from */
  private DATA_SOURCE source = DATA_SOURCE.NONE;

  /** The correct time as a {@link Calendar} for convenience */
  private Calendar exactTimeGMT = Calendar.getInstance(TimeZone
      .getTimeZone("GMT")); //$NON-NLS-1$

  /** A list of instances, used to retrieve an instance by sequenceNumber */
  private static List<ImageInfo> instances = new ArrayList<ImageInfo>();

  /**
   * Construct an image info object for an image
   * 
   * @param file
   *          The file containing the image
   */
  public ImageInfo(File file) {
    // first we create an id for this ImageInfo;
    instancesCreated++;
    sequenceNumber = instancesCreated;
    instances.add(this);
    // first some trivial information about the image
    this.name = file.getName();
    this.path = file.getPath();
    values.put(path, this);
  }

  /**
   * Utility method. Calculate the difference between local time and GMT time .
   * The offset is calculated such that GMT time plus offset equals local time
   * 
   * @param gmtDateString
   * @param localDateString
   * @return The time difference in seconds
   */
  public static int calculateOffset(String gmtDateString, String localDateString) {
    int offset = 0;
    if (gmtDateString != null && localDateString != null) {
      try {
        Date gmtDate = dateFormat.parse(gmtDateString);
        Date localDate = dateFormat.parse(localDateString);

        offset = (int) ((localDate.getTime() - gmtDate.getTime()) / 1000.0);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    return offset;
  }

  /**
   * A convenience method to take a String in local time and return a string
   * with the offset subtracted.
   * 
   * @param localDateString
   * @param offset
   * @return The adjusted time as a string
   */
  public static String subtractOffset(String localDateString, int offset) {
    try {
      Date date = dateFormat.parse(localDateString);
      date.setTime(date.getTime() - offset * 1000);
      return dateFormat.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return new String(); // empty string
  }

  /**
   * @param imageInfo
   *          The ImageInfo to compare to
   * @return the comparison result
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(ImageInfo imageInfo) {
    // the GPSDateTime shouldn't be null, but we check anyway
    if (getGPSDateTime() != null && imageInfo.getGPSDateTime() != null) {
      return getGPSDateTime().compareTo(imageInfo.getGPSDateTime());
    }
    // if that fails, compare the name
    return getName().compareTo(imageInfo.getName());
  }

  /**
   * @return the sequenceNumber
   */
  public int getSequenceNumber() {
    return sequenceNumber;
  }

  /**
   * Retrieve an {@link ImageInfo} instance from its sequenceNumber
   * 
   * @param sequenceNumer
   *          The sequenceNumber of the {@link ImageInfo} wanted
   * @return The {@link ImageInfo} with the given sequenceNumber
   */
  public static ImageInfo getImageInfo(int sequenceNumer) {
    /* sequence numbers start at 1, array indices start at 0 */
    return instances.get(sequenceNumer - 1);
  }

  /**
   * Return the ImageInfo for a file
   * 
   * @param filePath
   *          The file's path
   * @return The ImageInfo for that file, or null if not found
   */
  public static ImageInfo getImageInfo(String filePath) {
    return values.get(filePath);
  }

  /**
   * Checks if there is a location available for this image
   * 
   * @return True if we have location information for this image
   */
  public boolean hasLocation() {
    return source != DATA_SOURCE.NONE;
  }

  /**
   * Checks if there is a location available for this image that's not derived
   * from the image's EXIF data
   * 
   * @return True if there is a new location available
   */
  public boolean hasNewLocation() {
    return (source != DATA_SOURCE.NONE) && (source != DATA_SOURCE.IMAGE);
  }

  /**
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * @param path
   *          the path to set
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the thumbNailStatus
   */
  public THUMBNAIL_STATUS getThumbNailStatus() {
    return thumbNailStatus;
  }

  /**
   * @param thumbNailStatus
   *          the thumbNailStatus to set
   */
  public void setThumbNailStatus(THUMBNAIL_STATUS thumbNailStatus) {
    this.thumbNailStatus = thumbNailStatus;
  }

  /**
   * @return the thumbnail
   */
  public ImageIcon getThumbnail() {
    return thumbnail;
  }

  /**
   * @param thumbnail
   *          the thumbnail to set
   */
  public void setThumbnail(ImageIcon thumbnail) {
    this.thumbnail = thumbnail;
  }

  /**
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * @param height
   *          the height to set
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @param width
   *          the width to set
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * @return The difference between camera (local) time and GPS time (GMT).
   *         Returns <code>Double.NaN</code> if the difference is undefined.
   */
  public int getOffset() {
    return calculateOffset(getGPSDateTime(), getCameraDate());
  }

  /**
   * @return The time difference as a string (always with a leading sign)
   */
  public String getOffsetString() {
    return getOffsetString(getOffset());
  }

  /**
   * Create a string with a leading sign that represents this time difference
   * 
   * @param offset
   *          the offset to be converted into a string
   * @return The String representation of the offset value
   */
  public static String getOffsetString(int offset) {
    boolean negative = offset < 0.;
    int absOffset = Math.abs(offset);
    int hours = (absOffset / (60 * 60));
    absOffset -= hours * 60 * 60;
    int minutes = (absOffset / 60);
    int seconds = (absOffset - minutes * 60);
    hours *= negative ? -1 : 1;
    return String
        .format(
            "%+d:%02d:%02d", new Integer(hours), new Integer(minutes), new Integer(seconds)); //$NON-NLS-1$
  }

  /**
   * @return the cameraDate
   */
  public String getCameraDate() {
    return cameraDate;
  }

  /**
   * This has package visibility to force the use of undo-able edits
   * 
   * @param cameraDate
   *          the camera date to set
   */
  void setCameraDate(String cameraDate) {
    this.cameraDate = cameraDate;
  }

  /**
   * @return the gPSLatitude
   */
  public String getGPSLatitude() {
    return GPSLatitude;
  }

  /**
   * This has package visibility to force the use of undo-able edits
   * 
   * @param latitude
   *          the gPSLatitude to set
   * @param source
   *          Where the position information comes from
   */
  void setGPSLatitude(String latitude, DATA_SOURCE source) {
    this.GPSLatitude = latitude;
    this.source = source;
  }

  /**
   * @return the gPSLongitude
   */
  public String getGPSLongitude() {
    return GPSLongitude;
  }

  /**
   * This has package visibility to force the use of undo-able edits
   * 
   * @param longitude
   *          the gPSLongitude to set
   * @param source
   *          Where the position data comes from
   */
  void setGPSLongitude(String longitude, DATA_SOURCE source) {
    this.GPSLongitude = longitude;
    this.source = source;
  }

  /**
   * @return the gPSAltitude
   */
  public String getGPSAltitude() {
    return GPSAltitude;
  }

  /**
   * This has package visibility to force the use of undo-able edits
   * 
   * @param altitude
   *          the gPSAltitude to set
   * @param source
   *          Where the position data comes from
   */
  void setGPSAltitude(String altitude, DATA_SOURCE source) {
    GPSAltitude = altitude;
    this.source = source;
  }
  
  /**
   * This has package visibility to force the use of undo-able edits
   * 
   * @return the GPS image direction
   */
  public String getGPSImgDirection() {
    return GPSImgDirection;
  }
  
  /**
   * @param direction The direction to set
   */
  void setGPSImgDirection(String direction) {
    GPSImgDirection = direction;
  }

  /**
   * @return the gPSDateTime
   */
  public String getGPSDateTime() {
    return GPSDateTime;
  }

  /**
   * This has package visibility to force use of undo-able edits
   * 
   * @param dateTime
   *          the gPSDateTime to set
   */
  void setGPSDateTime(String dateTime) {
    this.GPSDateTime = dateTime;
    if (dateTime != null) {
      try {
        exactTimeGMT.setTime(dateFormat.parse(dateTime));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Set the GPSDateTime from the camera time, adjusting for the local time
   * zone. Only do this if the GPSDateTime is unknown, but the camera time
   * isn't. this can have public visibility as it uses an undo-able edit.
   */
  public void setGPSDateTime() {
    if (GPSDateTime == null && cameraDate != null) {
      // create a DateFormat for the local time zone
      DateFormat format = new SimpleDateFormat(ImageInfo.getDateFormatPattern());
      try {
        // parse the date as local time zone
        Date date = format.parse(cameraDate);
        // and format it back to GMT
        format.setTimeZone(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
        String gmtTime = format.format(date);
        // update
        new EditGPSDateTime(this, gmtTime);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @return the GPS time as a calendar
   */
  public Calendar getTimeGMT() {
    return exactTimeGMT;
  }

  /**
   * @return the orientation
   */
  public String getOrientation() {
    return orientation;
  }

  /**
   * @param orientation
   *          the orientation to set
   */
  public void setOrientation(String orientation) {
    this.orientation = orientation;
  }

  /**
   * @return the source
   */
  public DATA_SOURCE getSource() {
    return source;
  }

  /**
   * @param source
   */
  public void setSource(DATA_SOURCE source) {
    this.source = source;
  }

  /**
   * Make the date format pattern available to the outside world
   * 
   * @return The standard date format pattern
   */
  public static String getDateFormatPattern() {
    return DATE_FORMAT_PATTERN;
  }

  /**
   * Make the date format available to the outside world. Can be quite useful
   * for debugging.
   * 
   * @return The standard GMT date format used in this class
   */
  public static SimpleDateFormat getDateFormat() {
    return dateFormat;
  }
}
