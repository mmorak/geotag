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

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A class for small convenience methods that are used in several places.
 * 
 * @author Andreas Schneider
 * 
 */
public final class Util {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(Util.class);
  
  /**
   * hide constructor.
   */
  private Util() {
    // hide constructor
  }  

  /**
   * Given the start and end values of an interval and a point in between, this
   * calculates a ratio of where in the interval the point in between lies, so
   * that start + ratio * intervalSize = point in between.
   * 
   * @param start
   *          The start of the interval
   * @param target
   *          The point inside the interval
   * @param end
   *          The end point of the interval
   * @return The ration
   */
  public static double calculateRatio(double start, double target, double end) {
    return (target - start) / (end - start);
  }

  /**
   * @param start
   *          A start value
   * @param end
   *          An end value
   * @param ratio
   *          A ratio (between zero and one)
   * @return The value ratio*100 percent between start and end
   */
  public static double applyRatio(double start, double end, double ratio) {
    return start + ratio * (end - start);
  }

  /**
   * Calculate the great circle distance between two points on earth's surface.
   * We're ignoring the altitude and hope not too many parachuters with camera
   * and GPS use the program. The formula is found on <a
   * href="http://en.wikipedia.org/wiki/Great-circle_distance"> Wikipedia</a>
   * 
   * @param latitude1
   *          Latitude of the first point in degrees
   * @param longitude1
   *          Longitude of the first point in degrees
   * @param latitude2
   *          Latitude of the second point in degrees
   * @param longitude2
   *          Longitude of the second point in degrees
   * @return The distance in meters between the points
   */
  public static double greatCircleDistance(double latitude1, double longitude1,
      double latitude2, double longitude2) {
    // first convert to radians
    double lat1 = Math.toRadians(latitude1);
    double lon1 = Math.toRadians(longitude1);
    double lat2 = Math.toRadians(latitude2);
    double lon2 = Math.toRadians(longitude2);
    // now calculate the difference in longitude
    double deltaLon = lon2 - lon1;
    double term1 = square(Math.cos(lat2) * Math.sin(deltaLon));
    double term2 = square(Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
        * Math.cos(lat2) * Math.cos(deltaLon));
    double dividend = Math.sqrt(term1 + term2);
    double divisor = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
        * Math.cos(lat2) * Math.cos(deltaLon);
    double distance = Constants.AVERAGE_GREAT_CIRCLE_RADIUS_METRES * Math.atan2(dividend, divisor);
    return distance;
  }

  /**
   * Return the square of number.
   * 
   * @param x
   *          The number
   * @return Its square
   */
  public static double square(double x) {
    return x * x;
  }

  /**
   * Calculate 10 to the power of a number Could call Math.pow, but that would
   * be a waste of CPU resources.
   * 
   * @param number
   *          An integer
   * @return 10 to the power of that integer
   */
  public static long powerOf10(int number) {
    long result = 1;
    final int ten = 10;
    for (int i = 1; i <= number; i++) {
      result *= ten;
    }
    return result;
  }

  /**
   * Split a line of text in lines no longer than maxLength characters. this
   * routine assumes that there are no words longer than maxLength.
   * 
   * @param stringToSplit
   *          the line of text to split
   * @param maxLength
   *          the maximum length of each resulting line of text
   * @return A List of strings representing the split text
   */
  public static List<String> splitString(String stringToSplit, int maxLength) {
    String text = stringToSplit;
    List<String> lines = new ArrayList<String>();
    while (text.length() > maxLength) {
      int spaceAt = maxLength - 1;
      // the text is too long.
      // find the last space before the maxLength
      for (int i = maxLength - 1; i > 0; i--) {
        if (Character.isWhitespace(text.charAt(i))) {
          spaceAt = i;
          break;
        }
      }
      lines.add(text.substring(0, spaceAt));
      text = text.substring(spaceAt + 1);
    }
    lines.add(text);
    return lines;
  }
  
  /**
   * Split a line of text in lines no longer than maxLength characters. this
   * routine assumes that there are no words longer than maxLength.
   * 
   * @param stringToSplit
   *          the line of text to split
   * @param maxLength
   *          the maximum length of each resulting line of text
   * @return A List of strings representing the split text
   */
  public static List<String> splitHtmlString(String stringToSplit, int maxLength) {
    List<String> lines = new ArrayList<String>();
    boolean processingHtmlTag = false;
    StringBuilder word = new StringBuilder();
    StringBuilder line = new StringBuilder();
    for (int index=0; index < stringToSplit.length(); index++) {
      char character = stringToSplit.charAt(index);
      if (processingHtmlTag) {
        // always append - doesn't add to length
        word.append(character);
        switch (character) {
          case '>':
            processingHtmlTag = false;
            break;
          default:
        }
      } else {
        switch (character) {
          case '<':
            word.append(character);
            processingHtmlTag = true;
            break;
          case ' ':
            if (line.length() + word.length() + 1 < maxLength) {
              line.append(word.toString()).append(' ');
              word = new StringBuilder();
            } else {
              lines.add(line.toString());
              line = new StringBuilder();
              word.append(' ');
            }
            break;
          default:
            word.append(character);
        }
      }
    }
    if (word.length() > 0) {
      line.append(word.toString());
    }
    if (line.length() > 0) {
      lines.add(line.toString());
    }
    return lines;
  }

  /**
   * Given a cardinal direction (e.g. NNW) calculate the angle in degress
   * 
   * @param direction
   *          the cardinal direction as a string
   * @return The direction in degrees or Double.NaN
   */
  public static double degreesFromCardinalDirection(String direction) {
    String[] cardinalDirections = { i18n.tr("N"), //$NON-NLS-1$
        i18n.tr("NNE"), //$NON-NLS-1$
        i18n.tr("NE"), //$NON-NLS-1$
        i18n.tr("ENE"), //$NON-NLS-1$
        i18n.tr("E"), //$NON-NLS-1$
        i18n.tr("ESE"), //$NON-NLS-1$
        i18n.tr("SE"), //$NON-NLS-1$
        i18n.tr("SSE"), //$NON-NLS-1$
        i18n.tr("S"), //$NON-NLS-1$
        i18n.tr("SSW"), //$NON-NLS-1$
        i18n.tr("SW"), //$NON-NLS-1$
        i18n.tr("WSW"), //$NON-NLS-1$
        i18n.tr("W"), //$NON-NLS-1$
        i18n.tr("WNW"), //$NON-NLS-1$
        i18n.tr("NW"), //$NON-NLS-1$
        i18n.tr("NNW") //$NON-NLS-1$
    };
    for (int i = 0; i < cardinalDirections.length; i++) {
      if (direction.equalsIgnoreCase(cardinalDirections[i])) {
        return Constants.FULL_CIRCLE_DEGREES/ cardinalDirections.length * i;
      }
    }
    return Double.NaN;
  }

  /**
   * Utility method to check if one byte array starts with a specified sequence
   * of bytes.
   * 
   * @param array
   *          The array to check
   * @param prefix
   *          The prefix bytes to test for
   * @return true if the array starts with the bytes from the prefix
   */
  public static boolean startsWith(byte[] array, byte[] prefix) {
    if (array == prefix) {
      return true;
    }
    if (array == null || prefix == null) {
      return false;
    }
    int prefixLength = prefix.length;

    if (prefix.length > array.length) {
      return false;
    }

    for (int i = 0; i < prefixLength; i++) {
      if (array[i] != prefix[i]) {
        return false;
      }
    }

    return true;
  }

  /**
   * this treats null and the empty string as equal (i.e. same 'no content')
   * 
   * @param string1
   * @param string2
   * @return True if the two strings are equal
   */
  public static boolean sameContent(String string1, String string2) {
    String notNull1 = string1 == null ? "" : string1; //$NON-NLS-1$
    String notNull2 = string2 == null ? "" : string2; //$NON-NLS-1$
    return notNull1.equals(notNull2);
  }

}
