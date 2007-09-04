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

  /** The preferences for the main package */
  private static Preferences preferences = Preferences
      .userNodeForPackage(Geotag.class);

  /** Preferences key for window top left x coordinate */
  public static final String MAIN_WINDOW_X = "MainWindow.X"; //$NON-NLS-1$

  /** Preferences key for window top left y coordinate */
  public static final String MAIN_WINDOW_Y = "MainWindow.Y"; //$NON-NLS-1$

  /** Preferences key for window height */
  public static final String MAIN_WINDOW_HEIGHT = "MainWindow.Height"; //$NON-NLS-1$

  /** Preferences key for window width */
  public static final String MAIN_WINDOW_WIDHT = "MainWindow.Width"; //$NON-NLS-1$

  /** Preferences key for the preview height */
  public static final String PREVIEW_HEIGHT = "Preview.Height"; //$NON-NLS-1$

  /** Preference key for thumbnail size */
  public static final String THUMBNAIL_SIZE = "thumbnail.size"; //$NON-NLS-1$

  /** Preference key for storing the last single file the user chose */
  public static final String LAST_FILE_OPENED = "last_file_opened"; //$NON-NLS-1$

  /** Preference key for storing the last directory of images the user added */
  public static final String LAST_DIRECTORY_OPENED = "last_directory_opened"; //$NON-NLS-1$

  /** Preference key for storing the last gpx file the user opened */
  public static final String LAST_GPX_FILE_OPENED = "last_gpx_file_opened"; //$NON-NLS-1$

  /** Preference key for storing the path to the exiftool executable */
  public static final String EXIFTOOL_PATH = "exiftool.path"; //$NON-NLS-1$

  /** Preference key for storing the path to the GPSBabel executable */
  public static final String GPSBABEL_PATH = "gpsbabel.path"; //$NON-NLS-1$

  /** Preferences key for the protocol used by GPSBabel (the argument for -i) */
  public static final String GPSBABEL_PROTOCOL = "gpsbabl.prptocol"; //$NON-NLS-1$

  /** Preferences key for the GPSBabel device (the argument for -f) */
  public static final String GPSBABEL_DEVICE = "gpsbabel.device"; //$NON-NLS-1$

  /** Preferences key for the path to the dcraw executable */
  public static final String DCRAW_PATH = "dcraw.path"; //$NON-NLS-1$

  /** Preference key for always writing locations to XMP files */
  public static final String XMP_FILES_ONLY = "xmp.files.only"; //$NON-NLS-1$

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
  public static int getInt(String key, int defaultValue) {
    return preferences.getInt(key, defaultValue);
  }

  /**
   * @param key
   * @param value
   */
  public static void putInt(String key, int value) {
    preferences.putInt(key, value);
  }

  /**
   * @param key
   * @param defaultValue
   * @return The value
   */
  public static String get(String key, String defaultValue) {
    return preferences.get(key, defaultValue);
  }

  /**
   * @param key
   * @param value
   */
  public static void put(String key, String value) {
    preferences.put(key, value);
  }

  /**
   * @param key
   * @param defaultValue
   * @return The value
   */
  public static boolean getBoolean(String key, boolean defaultValue) {
    return preferences.getBoolean(key, defaultValue);
  }

  /**
   * @param key
   * @param value
   */
  public static void put(String key, boolean value) {
    preferences.putBoolean(key, value);
  }
}
