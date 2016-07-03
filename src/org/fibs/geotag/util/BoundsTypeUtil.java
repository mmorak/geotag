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

import com.topografix.gpx._1._0.BoundsType;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg.Trkpt;

/**
 * @author Andreas Schneider
 * 
 */
public final class BoundsTypeUtil {

  /**
   * hide constructor.
   */
  private BoundsTypeUtil() {
    // hide constructor
  }

  /**
   * @param point1
   * @param point2
   * @param mapBounds
   * @param mapWidth
   *          in pixels
   * @param mapHeight
   *          in pixels
   * @return the distance in pixels the points would have on (or off) the map
   */
  public static double pixelDistance(Trkpt point1, Trkpt point2,
      BoundsType mapBounds, int mapWidth, int mapHeight) {
    double mapLatitudeRange = mapBounds.getMaxlat().doubleValue()
        - mapBounds.getMinlat().doubleValue();
    double mapLongitudeRange = mapBounds.getMaxlon().doubleValue()
        - mapBounds.getMinlon().doubleValue();
    double pixelsPerDegreeLatitude = mapHeight / mapLatitudeRange;
    double pixelsPerDegreeLongitude = mapWidth / mapLongitudeRange;
    double latitudeDistance = Math.abs(point1.getLat().doubleValue()
        - point2.getLat().doubleValue());
    double longitudeDistance = Math.abs(point1.getLon().doubleValue()
        - point2.getLon().doubleValue());
    double latitudePixelDistance = latitudeDistance * pixelsPerDegreeLatitude;
    double longitudePixelDistance = longitudeDistance
        * pixelsPerDegreeLongitude;
    double pixelDistance = Math.sqrt(Util.square(latitudePixelDistance)
        + Util.square(longitudePixelDistance));
    return pixelDistance;
  }

  /**
   * @param bounds1
   * @param bounds2
   * @return True if the the two bounds intersect
   */
  public static boolean intersect(BoundsType bounds1, BoundsType bounds2) {
    // there are four ways the rectangles don't intersect
    boolean dontIntersect =
    // bottom of first is above top of second
    bounds1.getMinlat().compareTo(bounds2.getMaxlat()) > 0
    // bottom of second is above top of first
        || bounds2.getMinlat().compareTo(bounds1.getMaxlat()) > 0
        // left of first is greater than right of second
        || bounds1.getMinlon().compareTo(bounds2.getMaxlon()) > 0
        // left of second is greater than right of first
        || bounds2.getMinlon().compareTo(bounds1.getMaxlon()) > 0;
    // they intersect if the don't don't intersect
    return !dontIntersect;
  }
}
