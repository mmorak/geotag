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

package org.fibs.geotag.table;

import org.fibs.geotag.Messages;
import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.util.Units;

/**
 * An enumeration of the columns in the table
 * 
 * @author Andreas Schneider
 * 
 */
public class ImagesTableColumns {

  /**
   * An enum for the columns available
   */
  public enum COLUMN {
    /** The column for the image name */
    IMAGE_NAME,
    /** The column for the GPS Date/time */
    GPS_DATE,
    /**
     * The column for the time difference between camera (local) time and GPS
     * (GMT) time
     */
    TIME_OFFSET,
    /** The column for the camera date */
    CAMERA_DATE,
    /** The column for the latitude */
    LATITUDE,
    /** The column for the longitude */
    LONGITUDE,
    /** THe column for the altitude */
    ALTITUDE,
    /** The column for the direction */
    DIRECTION,
    /** The column for the location name */
    LOCATION_NAME,
    /** The column for the province/state etc name */
    PROVINCE_NAME,
    /** The column for the country name */
    COUNTRY_NAME
  }

  /**
   * @param column
   * @return A description for the column (used for tooltips)
   */
  public static String getDescription(COLUMN column) {
    switch (column) {
      case IMAGE_NAME:
        return Messages.getString("ImagesTableColumns.TooltipImageName"); //$NON-NLS-1$
      case GPS_DATE:
        return Messages.getString("ImagesTableColumns.TooltipGpsDate"); //$NON-NLS-1$
      case TIME_OFFSET:
        return Messages.getString("ImagesTableColumns.TooltipTimeOffset"); //$NON-NLS-1$
      case CAMERA_DATE:
        return Messages.getString("ImagesTableColumns.TooltipCameraDate"); //$NON-NLS-1$
      case LATITUDE:
        return Messages.getString("ImagesTableColumns.TooltipLatitude"); //$NON-NLS-1$
      case LONGITUDE:
        return Messages.getString("ImagesTableColumns.TooltipLongitude"); //$NON-NLS-1$
      case ALTITUDE:
        Units.ALTITUDE unit = Units.ALTITUDE.values()[Settings.get(
            SETTING.ALTITUDE_UNIT, 0)];
        String unitAbbreviation = Units.getAbbreviation(unit);
        String description = String.format(Messages
            .getString("ImagesTableColumns.TooltipAltitudeFormat"), //$NON-NLS-1$
            unitAbbreviation);
        return description;
      case DIRECTION:
        return Messages.getString("ImagesTableColumns.TooltipDirection"); //$NON-NLS-1$
      case LOCATION_NAME:
        return Messages.getString("ImagesTableColumns.LocationName"); //$NON-NLS-1$
      case PROVINCE_NAME:
        return Messages.getString("ImagesTableColumns.ProvinceStateName"); //$NON-NLS-1$
      case COUNTRY_NAME:
        return Messages.getString("ImagesTableColumns.CountryName"); //$NON-NLS-1$
    }
    return null;
  }
}
