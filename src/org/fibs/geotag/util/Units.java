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

package org.fibs.geotag.util;

import org.fibs.geotag.Messages;

/**
 * A class encapsulation distance units. Nautical miles left out on purpose
 * until someone wants them.
 * 
 * @author Andreas Schneider
 * 
 */
public class Units {
  /**
   * An enumeration of the distance units we support
   */
  public enum DISTANCE {
    /** Kilometres */
    KILOMETRES,
    /** Statute miles */
    MILES;
  }

  /**
   * An enumeration of the altitude units we support
   */
  public enum ALTITUDE {
    /** metres */
    METRES,
    /** feet */
    FEET
  }

  /** the long names of the distance units */
  private static final String[] distanceDisplayNames = {
      Messages.getString("Units.Kilometers"), //$NON-NLS-1$
      Messages.getString("Units.Miles") //$NON-NLS-1$
  };

  /** Abbreviations for the distance units */
  private static final String[] distanceAbbreviations = {
      Messages.getString("Units.km"), //$NON-NLS-1$
      Messages.getString("Units.mi") //$NON-NLS-1$
  };

  /** the long names of the altitude units */
  private static final String[] altitudeDisplayNames = {
      Messages.getString("Units.Metres"), //$NON-NLS-1$
      Messages.getString("Units.Feet") //$NON-NLS-1$
  };

  /** Abbreviations for the distance units */
  private static final String[] altitudeAbbreviations = {
      Messages.getString("Units.m"), //$NON-NLS-1$
      Messages.getString("Units.ft") //$NON-NLS-1$
  };

  /** An imperial mile in kilometres */
  private static final double MILE = 1.609344;

  /** A foot in meters */
  private static final double FOOT = 0.3048;

  /**
   * @param distance
   * @param unit
   * @return the distance in kilometres
   */
  private static double toKilometres(double distance, DISTANCE unit) {
    switch (unit) {
      case KILOMETRES:
        return distance;
      case MILES:
        return distance * MILE;
    }
    return distance;
  }

  /**
   * @param kilometres
   * @param unit
   * @return the kilometres distance in the requested unit
   */
  private static double fromKilometres(double kilometres, DISTANCE unit) {
    switch (unit) {
      case KILOMETRES:
        return kilometres;
      case MILES:
        return kilometres / MILE;
    }
    return kilometres;
  }

  /**
   * @param altitude
   * @param unit
   * @return the altitude in metres
   */
  private static double toMetres(double altitude, ALTITUDE unit) {
    switch (unit) {
      case METRES:
        return altitude;
      case FEET:
        return altitude * FOOT;
    }
    return altitude;
  }

  /**
   * @param metres
   * @param unit
   * @return the metres altitude in the requested unit
   */
  private static double fromMetres(double metres, ALTITUDE unit) {
    switch (unit) {
      case METRES:
        return metres;
      case FEET:
        return metres / FOOT;
    }
    return metres;
  }

  /**
   * Convert distances from on unit to another
   * 
   * @param distance
   * @param from
   * @param to
   * @return the converted distance
   */
  public static double convert(double distance, DISTANCE from, DISTANCE to) {
    double kilometres = toKilometres(distance, from);
    return fromKilometres(kilometres, to);
  }

  /**
   * Convert altitudes from one unit to another
   * 
   * @param altitude
   * @param from
   * @param to
   * @return the converted altitude
   */
  public static double convert(double altitude, ALTITUDE from, ALTITUDE to) {
    double metres = toMetres(altitude, from);
    return fromMetres(metres, to);
  }

  /**
   * @param unit
   * @return The display name for a distance unit
   */
  public static String getDisplayName(DISTANCE unit) {
    return distanceDisplayNames[unit.ordinal()];
  }

  /**
   * @param unit
   * @return The display name for an altitude unit
   */
  public static String getDisplayName(ALTITUDE unit) {
    return altitudeDisplayNames[unit.ordinal()];
  }

  /**
   * @return All available display names
   */
  public static String[] getDistanceUnitNames() {
    return distanceDisplayNames;
  }

  /**
   * @return All available display names
   */
  public static String[] getAltitudeUnitNames() {
    return altitudeDisplayNames;
  }

  /**
   * @param unit
   * @return the abbreviation for a distance unit
   */
  public static String getAbbreviation(DISTANCE unit) {
    return distanceAbbreviations[unit.ordinal()];
  }

  /**
   * @param unit
   * @return the abbreviation for an altitude unit
   */
  public static String getAbbreviation(ALTITUDE unit) {
    return altitudeAbbreviations[unit.ordinal()];
  }
}
