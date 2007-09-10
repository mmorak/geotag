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

package org.fibs.geotag;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Andreas Schneider
 * 
 */
public class Settings {

  /**
   * The valid settings know to the program
   */
  public enum SETTING {
    /** Preferences key for window top left x coordinate */
    MAIN_WINDOW_X,
    /** Preferences key for window top left y coordinate */
    MAIN_WINDOW_Y,
    /** Preferences key for window height */
    MAIN_WINDOW_HEIGHT,
    /** Preferences key for window width */
    MAIN_WINDOW_WIDHTH,
    /** Preferences key for the preview height */
    PREVIEW_HEIGHT,
    /** Preference key for thumbnail size */
    THUMBNAIL_SIZE,
    /** Preference key for storing the last single file the user chose */
    LAST_FILE_OPENED,
    /** Preference key for storing the last directory of images the user added */
    LAST_DIRECTORY_OPENED,
    /** Preference key for storing the last gpx file the user opened */
    LAST_GPX_FILE_OPENED,
    /** Preference key for storing the path to the exiftool executable */
    EXIFTOOL_PATH,
    /** Preferences key for additional exiftool arguments */
    EXIFTOOL_ARGUMENTS,
    /** Preference key for storing the path to the GPSBabel executable */
    GPSBABEL_PATH,
    /** Preferences key for the protocol used by GPSBabel (the argument for -i) */
    GPSBABEL_PROTOCOL,
    /** Preferences key for the GPSBabel device (the argument for -f) */
    GPSBABEL_DEVICE,
    /** Preferences key for the path to the dcraw executable */
    DCRAW_PATH,
    /** Preference key for always writing locations to XMP files */
    XMP_FILES_ONLY,
    /** Preferences key for the image name column width */
    IMAGE_NAME_WIDTH,
    /** Preferences key for the GPS time column width */
    GPS_DATE_WIDTH,
    /** Preferences key for the time offset column width */
    TIME_OFFSET_WIDTH,
    /** Preferences key for the camera date column width */
    CAMERA_DATE_WIDTH,
    /** Preferences key for the latitude column width */
    LATITUDE_WIDTH,
    /** Preferences key for the longitude column width */
    LONGITUDE_WIDTH,
    /** Preferences key for the altitude column width */
    ALTITUDE_WIDTH,
    /** Preferences key for the image name column position */
    IMAGE_NAME_POSITION,
    /** Preferences key for the GPS time column position */
    GPS_DATE_POSITION,
    /** Preferences key for the time offset column position */
    TIME_OFFSET_POSITION,
    /** Preferences key for the camera date column position */
    CAMERA_DATE_POSITION,
    /** Preferences key for the latitude column position */
    LATITUDE_POSITION,
    /** Preferences key for the longitude column position */
    LONGITUDE_POSITION,
    /** Preferences key for the altitude column position */
    ALTITUDE_POSITION,
    /** Preferences key for the font used */
    FONT,
    /** Preferences key for the last zoom level used in Google Maps */
    LAST_GOOGLE_MAPS_ZOOM_LEVEL,
    /** Preferences key for the tracks to be draw on Google Maps */
    GOOGLE_MAP_TRACKS_CHOICE;

    /**
     * For the actual key I prefer lower case keys with dots
     * 
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      return name().toLowerCase().replace('_', '.');
    }
  }

  /** The preferences for the main package */
  private static Preferences preferences = Preferences
      .userNodeForPackage(Geotag.class);

  /**
   * Flush (save) the settings
   */
  public static void flush() {
    try {
      preferences.flush();
    } catch (BackingStoreException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param key
   *          The name of the setting
   * @param defaultValue
   *          The default value
   * @return The value of the setting
   */
  public static int get(SETTING key, int defaultValue) {
    return preferences.getInt(key.toString(), defaultValue);
  }

  /**
   * @param key
   * @param value
   */
  public static void put(SETTING key, int value) {
    preferences.putInt(key.toString(), value);
  }

  /**
   * @param key
   * @param defaultValue
   * @return The value
   */
  public static String get(SETTING key, String defaultValue) {
    return preferences.get(key.toString(), defaultValue);
  }

  /**
   * @param key
   * @param value
   */
  public static void put(SETTING key, String value) {
    preferences.put(key.toString(), value);
  }

  /**
   * @param key
   * @param defaultValue
   * @return The value
   */
  public static boolean get(SETTING key, boolean defaultValue) {
    return preferences.getBoolean(key.toString(), defaultValue);
  }

  /**
   * @param key
   * @param value
   */
  public static void put(SETTING key, boolean value) {
    preferences.putBoolean(key.toString(), value);
  }
}
