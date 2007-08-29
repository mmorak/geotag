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

package org.fibs.geotag.ui;

/**
 * An enumeration of the columns in the table
 * 
 * @author Andreas Schneider
 * 
 */
public enum ImagesTableColumns {
  /** The column for the image name */
  IMAGE_NAME_COLUMN,
  /** The column for the GPS Date/time */
  GPS_DATE_COLUMN,
  /**
   * The column for the time difference between camera (local) time and GPS
   * (GMT) time
   */
  TIME_OFFSET_COLUMN,
  /** The column for EXIF CreateDate */
  CREATE_DATE_COLUMN,
  /** The column for the latitude */
  LATITUDE_COLUMN,
  /** The column for the longitude */
  LONGITUDE_COLUMN,
  /** THe column for the altitude */
  ALTITUDE_COLUMN
}
