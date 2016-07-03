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
package org.fibs.geotag.track;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.fibs.geotag.util.BoundsTypeUtil;

import com.topografix.gpx._1._0.BoundsType;
import com.topografix.gpx._1._0.Gpx;
import com.topografix.gpx._1._0.Gpx.Trk;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg;

/**
 * @author Andreas Schneider
 *
 */
public class TrackStoreTest extends TestCase {
  /**
   * 
   */
  public void testTrackStore() {
    // first test the empty track store
    assertNotNull(TrackStore.getTrackStore());
    assertFalse(TrackStore.getTrackStore().hasTracks());
    assertNull(TrackStore.getTrackStore().getGpx());
    assertNull(TrackStore.getTrackStore().getTracks());
    // now test putting some data into it - This needs a file called all.gpx in the resources directory
    InputStream stream = TrackStoreTest.class.getClassLoader().getResourceAsStream("all.gpx"); //$NON-NLS-1$
    assertNotNull(stream);
    Gpx gpx = GpxReader.read(stream);
    try {
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertNotNull(gpx);
    TrackStore.getTrackStore().addGPX(gpx);
    assertTrue(TrackStore.getTrackStore().hasTracks());
    assertNotNull(TrackStore.getTrackStore().getGpx());
    assertNotNull(TrackStore.getTrackStore().getTracks());
    // now test the intersects() Method
    BoundsType gpxBounds = gpx.getBounds();
    for (Trk track : TrackStore.getTrackStore().getTracks()) {
      for (Trkseg segment : track.getTrkseg()) {
        BoundsType segmentBounds = TrackStore.getTrackStore().getSegmentBounds(segment);
        if (segmentBounds != null) {
          assertTrue(BoundsTypeUtil.intersect(gpxBounds, segmentBounds));
        }
      }
    }
  }
}  
