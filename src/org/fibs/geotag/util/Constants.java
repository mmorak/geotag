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
 * @author Andreas Schneider
 *
 */
public final class Constants {
  /** Hide constructor. */
  public Constants() {
    // hidden
  }
  
  /** One second contains 1000 milliseconds. */
  public static final long ONE_SECOND_IN_MILLIS = 1000;
  
  /** One minute contains 60 * 1000 milliseconds. */
  public static final long ONE_MINUTE_IN_MILLIS = 60 * ONE_SECOND_IN_MILLIS;
  
  /** One kilobyte. */
  public static final int ONE_K = 1024;
  
  /** Degrees in a circle. */
  public static final double FULL_CIRCLE_DEGREES = 360.0;
  
  /** Minutes in one degree. */
  public static final double MINUTES_PER_DEGREE = 60.0;
  
  /** Seconds in one minute. */
  public static final double SECONDS_PER_MINUTE = 60.0;
  
  /** Seconds in one degree. */
  public static final double SECONDS_PER_DEGREE = SECONDS_PER_MINUTE * MINUTES_PER_DEGREE;
  
  /** Minutes in one hour. */
  public static final double MINUTES_PER_HOUR = 60.0;
  
  /** Seconds in one hour. */
  public static final double SECONDS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE;
  
  /**
   * The average great circle radius in metres.
   * We approximate the earth's shape by a sphere.
   */
  public static final long AVERAGE_GREAT_CIRCLE_RADIUS_METRES = 6372795;

}
