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

package org.fibs.geotag.util;

/**
 * A class that represents Airy's meridian. Used as an educational default :-)
 * 
 * @author Andreas Schneider
 * 
 */
public class Airy {
  /** Airy latitude degrees. */
  private static final double DEGREES = 51.0;

  /** Airy latitude minutes. */
  private static final double MINUTES = 28.0;

  /** Airy latitude seconds. */
  private static final double SECONDS = 38.0;

  /** Latitude of the cross hair in Airy's telescope. */
  public static final double LATITUDE = DEGREES + MINUTES
      / Constants.MINUTES_PER_DEGREE + SECONDS / Constants.SECONDS_PER_DEGREE;

  /** Longitude of the cross hair in Airy's telescope. */
  public static final double LONGITUDE = 0.0;
}
