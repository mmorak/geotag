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

package org.fibs.geotag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Andreas Schneider
 * 
 */
public final class Settings {

  /**
   * hide constructor.
   */
  private Settings() {
    // hide constructor
  }

  /**
   * The valid settings know to the program.
   */
  public enum SETTING {
    /** Preferences key for window top left x coordinate. */
    MAIN_WINDOW_X,
    /** Preferences key for window top left y coordinate. */
    MAIN_WINDOW_Y,
    /** Preferences key for window height. */
    MAIN_WINDOW_HEIGHT,
    /** Preferences key for window width. */
    MAIN_WINDOW_WIDHTH,
    /** Preferences key for the preview height. */
    PREVIEW_HEIGHT,
    /** Preference key for thumbnail size (longest side in pixels) . */
    THUMBNAIL_SIZE,
    /** Preferences key for showing thumbnails in tooltips. */
    TUMBNAILS_IN_TOOLTIPS,
    /** Preference key for storing the last single file the user chose. */
    LAST_FILE_OPENED,
    /** Preference key for storing the last directory of images the user added. */
    LAST_DIRECTORY_OPENED,
    /** Preference key for the last file filter used in the file chooser*/
    LAST_FILE_FILTER_SELECTED,
    /** Preference key for storing the last gpx file the user opened. */
    LAST_GPX_FILE_OPENED,
    /** Preference key for storing the path to the exiftool executable. */
    EXIFTOOL_PATH,
    /** Preferences key for additional exiftool arguments. */
    EXIFTOOL_ARGUMENTS,
    /** Preference key for storing the path to the GPSBabel executable. */
    GPSBABEL_PATH,
    /** Preferences key for the protocol used by GPSBabel (the argument for -i). */
    GPSBABEL_PROTOCOL,
    /** Preferences key for the GPSBabel device (the argument for -f). */
    GPSBABEL_DEVICE,
    /** Preferences key for the path to the dcraw executable. */
    DCRAW_PATH,
    /** Preference key for always writing locations to XMP files. */
    XMP_FILES_ONLY,
    /** Preference key for creation of backup upon updating a file. */
    CREATE_BACKUPS,
    /** Preferences key for the image name column width. */
    IMAGE_NAME_WIDTH,
    /** Preferences key for the GPS time column width. */
    GPS_DATE_WIDTH,
    /** Preferences key for the time offset column width. */
    TIME_OFFSET_WIDTH,
    /** Preferences key for the camera date column width. */
    CAMERA_DATE_WIDTH,
    /** Preferences key for the latitude column width. */
    LATITUDE_WIDTH,
    /** Preferences key for the longitude column width. */
    LONGITUDE_WIDTH,
    /** Preferences key for the altitude column width. */
    ALTITUDE_WIDTH,
    /** Preferences key for the direction column width. */
    DIRECTION_WIDTH,
    /** Preferences key for the location name column width. */
    LOCATION_NAME_WIDTH,
    /** Preferences key for the city name column width. */
    CITY_NAME_WIDTH,
    /** Preferences key for the province name column width. */
    PROVINCE_NAME_WIDTH,
    /** Preferences key for the country name column width. */
    COUNTRY_NAME_WIDTH,
    /** Preferences key for user comment column width */
    USER_COMMENT_WIDTH,
    /** Preferences key for the image name column position. */
    IMAGE_NAME_POSITION,
    /** Preferences key for the GPS time column position. */
    GPS_DATE_POSITION,
    /** Preferences key for the time offset column position. */
    TIME_OFFSET_POSITION,
    /** Preferences key for the camera date column position. */
    CAMERA_DATE_POSITION,
    /** Preferences key for the latitude column position. */
    LATITUDE_POSITION,
    /** Preferences key for the longitude column position. */
    LONGITUDE_POSITION,
    /** Preferences key for the altitude column position. */
    ALTITUDE_POSITION,
    /** Preferences key for the direction column position. */
    DIRECTION_POSITION,
    /** Preferences key for the location name column position. */
    LOCATION_NAME_POSITION,
    /** Preferences key for the city name column position. */
    CITY_NAME_POSITION,
    /** Preferences key for the province name column position. */
    PROVINCE_NAME_POSITION,
    /** Preferences key for the country name column position. */
    COUNTRY_NAME_POSITION,
    /** Preferences key for user comment column position */
    USER_COMMENT_POSITION,
    /** Preferences key for the font used. */
    FONT,
    /** Preferences key for the last zoom level used in Google Maps. */
    LAST_GOOGLE_MAPS_ZOOM_LEVEL,
    /** Preferences key for the last map type used in Google Maps. */
    LAST_GOOGLE_MAPS_MAP_TYPE,
    /** Preferences key for the last latitude set from Google Maps. */
    LAST_GOOGLE_MAPS_LATITUDE,
    /** Preferences key for the last longitude set from Google Maps. */
    LAST_GOOGLE_MAPS_LONGITUDE,
    /** Preferences key for the tracks to be draw on Google Maps. */
    GOOGLE_MAP_SHOW_TRACKS,
    /** Preferences key for showing wikipedia marks on the map. */
    GOOGLE_MAP_SHOW_WIKIPEDIA,
    /** Preferences key for Google Maps mouse wheel zooming. */
    GOOGLE_MAPS_MOUSE_WHEEL_ZOOM,
    /** Preferences key for status of menu on Google Map. */
    GOOGLE_MAPS_MENU_OPEN,
    /** Preferences key for checking for new versions. */
    CHECK_FOR_NEW_VERSION,
    /** Preferences key for the browser name - empty string for default. */
    BROWSER,
    /** Preferences key for the last KML/KMZ file saved. */
    GOOGLEEARTH_LAST_FILE_SAVED,
    /** Preferences key for the path to the Googleearth binary */
    GOOGLE_EARTH_PATH,
    /** Preferences key for storing thumbnail images in KMZ files or not. */
    KMZ_STORE_THUMBNAILS,
    /**
     * Preferences key for KML image directory. Leave blank for files system
     * path.
     */
    KML_IMAGE_PATH,
    /** Preferences key for the icon used in KML files */
    KML_ICON_URL,
    /** Preferences key for KML description header */
    KML_DESCRIPTION_HEADER,
    /** Preferences key for KML description footer */
    KML_DESCRIPTION_FOOTER,
    /** Preferences key for the geonames URL */
    GEONAMES_URL,
    /** Preferences key for using geonames search radius. */
    GEONAMES_USE_RADIUS,
    /** Preferences key for geonames search radius in km. */
    GEONAMES_RADIUS,
    /**
     * Preferences key for number of locations to find (maxRows parameter in
     * query).
     */
    GEONAMES_MAX_ROWS,
    /** Preferences key to enable/disable Wikipedia location name search. */
    GEONAMES_USE_WIKIPEDIA,
    /** Preferences key for number of Wikipedia entries to retrieve if enabled. */
    GEONAMES_WIKIPEDIA_ENTRIES,
    /** Preferences key for using an override language for geonames queries. */
    GEONAMES_OVERRIDE_LANGUAGE,
    /** Preferences key for the overriding language in geonames queries. */
    GEONAMES_LANGUAGE,
    /** Preferences key for distance unit (km, mi, nmi). */
    DISTANCE_UNIT,
    /** Preferences key for altitude unit (m, ft). */
    ALTITUDE_UNIT,
    /** Preferences key formatting latitudes/longitudes. */
    COORDINATES_FORMAT,
    /** Preferences key for proxy type. */
    PROXY_TYPE,
    /** Preferences key for proxy host/port address. */
    PROXY_ADDRESS,
    /** Preferences key for allowing coordinates from the clipboard. */
    CLIPBOARD_ENABLED,
    /** Preferences key for the order of latitude and longitude. */
    CLIPBOARD_LATITUDE_FIRST,
    /** Preferences key for the letters representing north. */
    CLIPBOARD_NORTH,
    /** Preferences key for the letters representing south. */
    CLIPBOARD_SOUTH,
    /** Preferences key for the letters representing east. */
    CLIPBOARD_EAST,
    /** Preferences key for the letters representing west. */
    CLIPBOARD_WEST,
    /** Preferences key for the last used time zone */
    LAST_USED_TIMEZONE,
    /** Preferences key for number of clicks to edit */
    CLICKS_TO_EDIT,
    /** Preferences key for file types that are supported by reading only XMP files */
    FILE_TYPES_SUPPORTED_BY_XMP;


    /**
     * For the actual key I prefer lower case keys with dots.
     * 
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      return name().toLowerCase().replace('_', '.');
    }
  }

  /**
   * An interface to be be implemented by anyone who wants to be notified
   * whenever a setting changes
   */
  public static interface SettingsListener {
    /**
     * Called whenever a setting changes. The new value can then be retrieved
     * via one of the Settings.get() methods.
     * 
     * @param setting
     *          The setting that has changed.
     */
    public void settingChanged(SETTING setting);
  }

  /** Here each setting has a (maybe null) list of listeners */
  private static Map<SETTING, List<SettingsListener>> listeners = new HashMap<SETTING, List<SettingsListener>>();

  /**
   * Add a listener for a setting
   * 
   * @param setting
   *          The setting to be listened for
   * @param listener
   *          The listener
   */
  public static void addListener(SETTING setting, SettingsListener listener) {
    List<SettingsListener> listenerList = listeners.get(setting);
    if (listenerList == null) {
      listenerList = new ArrayList<SettingsListener>();
      listeners.put(setting, listenerList);
    }
    listenerList.add(listener);
  }

  /**
   * Remove a listener for a setting
   * 
   * @param setting
   * @param listener
   */
  public static void removeListener(SETTING setting, SettingsListener listener) {
    List<SettingsListener> listenerList = listeners.get(setting);
    if (listenerList != null) {
      listenerList.remove(listener);
    }
  }

  /**
   * Notify all listeners for a setting, that the setting has changed
   * 
   * @param setting
   */
  private static void notifyListeners(SETTING setting) {
    List<SettingsListener> listenerList = listeners.get(setting);
    if (listenerList != null) {
      for (SettingsListener listener : listenerList) {
        listener.settingChanged(setting);
      }
    }
  }

  /** Default value for icon URL used in KML/KMZ exports */
  public static final String KML_DEFAULT_ICON_URL = "http://maps.google.com/mapfiles/kml/pal4/icon46.png"; //$NON-NLS-1$
  
  /** Default value for GEONAMES_URL */
  public static final String GEONAMES_DEFAULT_URL = "api.geonames.org"; //$NON-NLS-1$

  /** Default value for GEONAMES_RADIUS. */
  public static final int GEONAMES_DEFAULT_RADIUS = 5;

  /** Default value for GEONAMES_MAX_ROWS. */
  public static final int GEONAMES_DEFAULT_MAX_ROWS = 5;

  /** Maximum value for GEONAMES_MAX_ROWS. */
  public static final int GEONAMES_MAX_MAX_ROWS = 50;

  /** Default value for GEONAMES_WIKIPEDIA_ENTRIES. */
  public static final int GEONAMES_DEFAULT_WIKIPEDIA_ENTRIES = 3;

  /** Maximum value of GEONAMES_WIKIPEDIA_ENTRIES. */
  public static final int GEONAMES_MAX_WIKIPEDIA_ENTRIES = 50;

  /** Default value for the thumbnail size */
  public static final int DEFAULT_THUMBNAIL_SIZE = 150;

  /** Default value for clicks to edit */
  public static final int DEFAULT_CLICKS_TO_EDIT = 1;
  
  /** Value for Google Maps API version 2 */
  public static final String MAPS_API_2 = "2"; //$NON-NLS-1$
  
  /** Value for Google Maps API version 3 */
  public static final String MAPS_API_3 = "3"; //$NON-NLS-1$
  
  /** Values for Google Maps API versions */
  public static final String[] MAPS_API_VERSIONS = {MAPS_API_2, MAPS_API_3};
  
  /** The preferences for the main package. */
  private static Preferences preferences = Preferences
      .userNodeForPackage(Geotag.class);

  /**
   * Flush (save) the settings.
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
    notifyListeners(key);
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
    notifyListeners(key);
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
    notifyListeners(key);
  }
}
