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
  
  // A couple of constants to keep checkstyle happy
  /** Width. */
  private static final int WIDTH = 4000;
  
  /** height. */
  private static final int HEIGHT = 3000;
  
  /** the diagonal according to Pythagoras. */
  private static final int DIAGONAL = 5000;
  
  /** smallest latitude. */
  private static final double SOUTH = 51.0;
  
  /** largest latitude. */
  private static final double NORTH = 52.0;
  
  /** smallest longitude. */
  private static final double EAST = -1.0;
  
  /** largest longitude. */
  private static final double WEST = 1.0;
  
  /** Acceptable error. */
  private static final double DELTA = 0.1;
  
  /**
   * 
   */
  public void testPixeldistance() {
    ObjectFactory objectFactory = new ObjectFactory();
    // we have a map 4000x3000 pixels in size
    // the distance between opposite corners is 5000 pixels
    int width = WIDTH;
    int height = HEIGHT;
    BoundsType mapBounds = objectFactory.createBoundsType();
    // the map is near Greenwich
    mapBounds.setMinlat(new BigDecimal(SOUTH));
    mapBounds.setMaxlat(new BigDecimal(NORTH));
    mapBounds.setMinlon(new BigDecimal(EAST));
    mapBounds.setMaxlon(new BigDecimal(WEST));
    // here are two points at opposite corners
    // first the top left
    Trkpt point1 = objectFactory.createGpxTrkTrksegTrkpt();
    point1.setLat(new BigDecimal(NORTH));
    point1.setLon(new BigDecimal(EAST));
    // then the bottom right
    Trkpt point2 = objectFactory.createGpxTrkTrksegTrkpt();
    point2.setLat(new BigDecimal(SOUTH));
    point2.setLon(new BigDecimal(WEST));
    // now calulate the distance in pixels
    double distanceInPixels = BoundsTypeUtil.pixelDistance(point1, point2, mapBounds, width, height);
    assertEquals(DIAGONAL, distanceInPixels, DELTA);
  }
}
