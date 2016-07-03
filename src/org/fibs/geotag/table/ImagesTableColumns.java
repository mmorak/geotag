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

package org.fibs.geotag.table;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.util.Units;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * An enumeration of the columns in the table.
 * 
 * @author Andreas Schneider
 * 
 */
public final class ImagesTableColumns {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(ImagesTableColumns.class);
  
  /**
   * hide constructor.
   */
  private ImagesTableColumns() {
    // hide constructor
  }

  /**
   * An enum for the columns available.
   */
  public enum COLUMN {
    /** The column for the image name. */
    IMAGE_NAME,
    /** The column for the GPS Date/time. */
    GPS_DATE,
    /**
     * The column for the time difference between camera (local) time and GPS
     * (GMT) time.
     */
    TIME_OFFSET,
    /** The column for the camera date. */
    CAMERA_DATE,
    /** The column for the latitude. */
    LATITUDE,
    /** The column for the longitude. */
    LONGITUDE,
    /** THe column for the altitude. */
    ALTITUDE,
    /** The column for the direction. */
    DIRECTION,
    /** The column for the location name. */
    LOCATION_NAME,
    /** The column for the city name. */
    CITY_NAME,
    /** The column for the province/state etc name. */
    PROVINCE_NAME,
    /** The column for the country name. */
    COUNTRY_NAME,
    /** The column for the user comment */
    USER_COMMENT
  }

  /**
   * @param column
   * @return A description for the column (used for tooltips).
   */
  public static String getDescription(COLUMN column) {
    switch (column) {
      case IMAGE_NAME:
        return i18n.tr("The name of the image file"); //$NON-NLS-1$
      case GPS_DATE:
        return i18n.tr("The GPS time in GMT for this image."); //$NON-NLS-1$
      case TIME_OFFSET:
        return i18n.tr("The difference between camera time and GPS time"); //$NON-NLS-1$
      case CAMERA_DATE:
        return i18n.tr("The time recorded by the camera for this image"); //$NON-NLS-1$
      case LATITUDE:
        return i18n.tr("The latitude (in degrees) associated with this image"); //$NON-NLS-1$
      case LONGITUDE:
        return i18n.tr("The longitude (in degrees) associated with this image"); //$NON-NLS-1$
      case ALTITUDE:
        Units.ALTITUDE unit = Units.ALTITUDE.values()[Settings.get(
            SETTING.ALTITUDE_UNIT, 0)];
        String unitAbbreviation = Units.getAbbreviation(unit);
        String description = String.format(i18n
            .tr("The altitude (in %1$s) associated with this image"), //$NON-NLS-1$
            unitAbbreviation);
        return description;
      case DIRECTION:
        return i18n.tr("Image direction in degrees"); //$NON-NLS-1$
      case LOCATION_NAME:
        return i18n.tr("Location name"); //$NON-NLS-1$
      case CITY_NAME:
        return i18n.tr("The city name"); //$NON-NLS-1$
      case PROVINCE_NAME:
        return i18n.tr("Province/State name"); //$NON-NLS-1$
      case COUNTRY_NAME:
        return i18n.tr("Country name"); //$NON-NLS-1$
      case USER_COMMENT:
        return i18n.tr("Description"); //$NON-NLS-1$
      default:
        return null;
    }
  }
}
