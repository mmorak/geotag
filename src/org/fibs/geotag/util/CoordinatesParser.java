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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;

/**
 * A class to extract coordinates from text using regular expressions.
 * 
 * @author Andreas Schneider
 * 
 */
public class CoordinatesParser {

  /** How many minutes there are in a degree. */
  static final double MINUTES_PER_DEGREE = 60.0;

  /** How many seconds there are in a degree. */
  static final double SECONDS_PER_DEGREE = 60.0 * MINUTES_PER_DEGREE;

  /** Regular expression for floating point (or integer) numbers. */
  static final String FLOAT = "(\\-?\\d+([,\\.]\\d+)?)"; //$NON-NLS-1$

  /** A pre-compiled pattern for float (or integer) numbers. */
  static final Pattern FLOAT_PATTERN = Pattern.compile(FLOAT);

  /** Regular expression for zero or more white space characters. */
  static final String SPACES = "(\\s*)"; //$NON-NLS-1$

  /** Regular expression for the degree symbol (or substitutes). */
  static final String DEGREE_MARK = "([Dd\\" + Unicode.DEGREE_SYMBOL + "])"; //$NON-NLS-1$ //$NON-NLS-2$

  /** Regular expression for the minute mark (or substitutes). */
  static final String MINUTE_MARK = "([Mm'" + Unicode.SINGLE_PRIME_MARK + "])"; //$NON-NLS-1$ //$NON-NLS-2$

  /** Regular expression for the second mark (or substitutes). */
  static final String SECOND_MARK = "([Ss\"" + Unicode.DOUBLE_PRIME_MARK + "])"; //$NON-NLS-1$ //$NON-NLS-2$

  /** Regular expression for degrees. */
  static final String DEGREES = '(' + FLOAT + DEGREE_MARK + SPACES + ')';

  /** Regular expression for minutes. */
  static final String MINUTES = '(' + FLOAT + MINUTE_MARK + SPACES + ')';

  /** Regular expression for seconds. */
  static final String SECONDS = '(' + FLOAT + SECOND_MARK + SPACES + ')';

  /** Regular expression for coordinates (with optional minutes and seconds). */
  static final String COORDINATES = DEGREES + MINUTES + '?' + SECONDS + '?';

  /** Regular expression for the letters representing north. */
  static String north;

  /** Regular expression for the letters representing south. */
  static String south;

  /** Regular expression for the letters representing west. */
  static String west;

  /** Regular expression for the letters representing east. */
  static String east;

  /** Regular expression for north or south. */
  static String northSouth;

  /** Regular expression for east or west. */
  static String eastWest;

  /** Regular expression for one of north, south, east or west. */
  static String northSouthEastWest;

  /** Regular expression for latitudes. */
  static String latitude;

  /** Regular expression for longitudes. */
  static String longitude;

  /** Regular expression for latitude or longitude. */
  static String latitudeLongitude;

  /** the matcher used to perform the matching. */
  private Matcher matcher;

  /**
   * @param text
   *          The text to be analysed
   */
  public CoordinatesParser(String text) {
    setupRegularExpressions();
    Pattern pattern = Pattern.compile(latitudeLongitude);
    matcher = pattern.matcher(text);
  }

  /**
   * @return The next coordinate if available, or Double.NaN
   */
  public double nextCoordinate() {
    double coordinate = Double.NaN;
    // ask the matcher if there is another match
    if (matcher.find()) {
      // there is a match, retrieve it and remove leading/trailing white space
      String match = matcher.group().trim();
      // we try and retrieve up to 3 numbers from the match
      Matcher floatMatcher = FLOAT_PATTERN.matcher(match);
      if (floatMatcher.find()) {
        String degrees = floatMatcher.group().replace(',', '.');
        coordinate = Double.parseDouble(degrees);
        if (floatMatcher.find()) {
          String minutes = floatMatcher.group().replace(',', '.');
          coordinate += Double.parseDouble(minutes) / MINUTES_PER_DEGREE;
          if (floatMatcher.find()) {
            String seconds = floatMatcher.group().replace(',', '.');
            coordinate += Double.parseDouble(seconds) / SECONDS_PER_DEGREE;
          }
        }
      }
      if (!Double.isNaN(coordinate)) {
        Matcher signMatcher = Pattern.compile(northSouthEastWest)
            .matcher(match);
        if (signMatcher.find()) {
          String sign = signMatcher.group();
          // is it south or west?
          // TODO: understand why there can be an empty match
          if (sign.length() > 0) {
            if (Settings.get(SETTING.CLIPBOARD_SOUTH, Coordinates.SOUTH)
                .indexOf(sign) >= 0
                || Settings.get(SETTING.CLIPBOARD_WEST, Coordinates.WEST)
                    .indexOf(sign) >= 0) {
              coordinate = -coordinate;
            }
          }
        }
      }
    }
    return coordinate;
  }

  /**
   * Set up the regular expressions that can depend on user settings.
   */
  private void setupRegularExpressions() {
    north = '[' + Settings.get(SETTING.CLIPBOARD_NORTH, Coordinates.NORTH) + ']';
    south = '[' + Settings.get(SETTING.CLIPBOARD_SOUTH, Coordinates.SOUTH) + ']';
    west = '[' + Settings.get(SETTING.CLIPBOARD_WEST, Coordinates.WEST) + ']';
    east = '[' + Settings.get(SETTING.CLIPBOARD_EAST, Coordinates.EAST) + ']';

    northSouth = '(' + north + '|' + south + ')';
    eastWest = '(' + east + '|' + west + ')';

    northSouthEastWest = '(' + northSouth + '|' + eastWest + ')';

    latitude = '(' + northSouth + '?' + SPACES + COORDINATES + SPACES
        + northSouth + "?)"; //$NON-NLS-1$
    longitude = '(' + eastWest + '?' + SPACES + COORDINATES + SPACES + eastWest
        + "?)"; //$NON-NLS-1$

    latitudeLongitude = '(' + northSouthEastWest + '?' + SPACES + COORDINATES
        + SPACES + northSouthEastWest + "?)"; //$NON-NLS-1$
  }
}
