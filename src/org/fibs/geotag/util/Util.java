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

import java.util.ArrayList;
import java.util.List;

/**
 * A class for small convenience methods that are used in several places
 * 
 * @author Andreas Schneider
 * 
 */
public class Util {

  /**
   * Given the start and end values of an interval and a point in between, this
   * calculates a ratio of where in the interval the point in between lies, so
   * that start + ratio * intervalSize = point in between
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
    // TODO: Decide if this should throw exceptions
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
    double distance = 6372795 * Math.atan2(dividend, divisor);
    return distance;
  }

  /**
   * Return the square of number
   * 
   * @param x
   *          The number
   * @return Its square
   */
  public static double square(double x) {
    return x * x;
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

}
