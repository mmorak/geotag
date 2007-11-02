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
public class Distance {
  /**
   * An enumeration of the units we support
   */
  public enum UNIT {
    /** Kilometres */
    KILOMETRES,
    /** Statute miles */
    MILES;
  }

  /** the long names of the distance units */
  private static final String[] displayNames = {
      Messages.getString("Distance.Kilometers"), //$NON-NLS-1$
      Messages.getString("Distance.Miles") //$NON-NLS-1$
  };

  /** Abbreviations for the distance units */
  private static final String[] displayAbbreviations = {
      Messages.getString("Distance.km"), //$NON-NLS-1$
      Messages.getString("Distance.mi") //$NON-NLS-1$
  };

  /** An imperial mile in kilometres */
  private static final double MILE = 1.609344;

  /**
   * @param distance
   * @param unit
   * @return the distance in kilometres
   */
  private static double toKilometres(double distance, UNIT unit) {
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
  private static double fromKilometres(double kilometres, UNIT unit) {
    switch (unit) {
      case KILOMETRES:
        return kilometres;
      case MILES:
        return kilometres / MILE;
    }
    return kilometres;
  }

  /**
   * Convert distances from on unit to another
   * 
   * @param distance
   * @param from
   * @param to
   * @return the converted distance
   */
  public static double convert(double distance, UNIT from, UNIT to) {
    double kilometres = toKilometres(distance, from);
    return fromKilometres(kilometres, to);
  }

  /**
   * @param unit
   * @return The display name for a distance unit
   */
  public static String getDisplayName(UNIT unit) {
    return displayNames[unit.ordinal()];
  }

  /**
   * @return All available display names
   */
  public static String[] getDisplayNames() {
    return displayNames;
  }

  /**
   * @param unit
   * @return the abbreviation for a distance unit
   */
  public static String getDisplayAbbreviation(UNIT unit) {
    return displayAbbreviations[unit.ordinal()];
  }
}
