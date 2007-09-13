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

import java.math.BigDecimal;

import com.topografix.gpx._1._0.BoundsType;
import com.topografix.gpx._1._0.ObjectFactory;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg.Trkpt;

import junit.framework.TestCase;

/**
 * @author Andreas Schneider
 *
 */
public class BoundsTypeUtilTest extends TestCase {
  /**
   * 
   */
  public void testPixeldistance() {
    ObjectFactory objectFactory = new ObjectFactory();
    // we have a map 4000x3000 pixels in size
    // the distance between opposite corners is 5000 pixels
    int width = 4000;
    int height = 3000;
    BoundsType mapBounds = objectFactory.createBoundsType();
    // the map is near Greenwich
    mapBounds.setMinlat(new BigDecimal(51.0));
    mapBounds.setMaxlat(new BigDecimal(52.0));
    mapBounds.setMinlon(new BigDecimal(-1.0));
    mapBounds.setMaxlon(new BigDecimal(1.0));
    // here are two points at opposite corners
    // first the top left
    Trkpt point1 = objectFactory.createGpxTrkTrksegTrkpt();
    point1.setLat(new BigDecimal(52.0));
    point1.setLon(new BigDecimal(-1.0));
    // then the bottom right
    Trkpt point2 = objectFactory.createGpxTrkTrksegTrkpt();
    point2.setLat(new BigDecimal(51.0));
    point2.setLon(new BigDecimal(1.0));
    // now calulate the distance in pixels
    double distanceInPixels = BoundsTypeUtil.pixelDistance(point1, point2, mapBounds, width, height);
    assertEquals(5000.0, distanceInPixels, 0.1);
  }
}
