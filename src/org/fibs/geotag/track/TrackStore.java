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

package org.fibs.geotag.track;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fibs.geotag.util.BoundsTypeUtil;

import com.topografix.gpx._1._0.BoundsType;
import com.topografix.gpx._1._0.Gpx;
import com.topografix.gpx._1._0.ObjectFactory;
import com.topografix.gpx._1._0.Gpx.Trk;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg.Trkpt;

/**
 * A class that stores the tracks we have loaded or downloaded from GPS
 * 
 * @author Andreas Schneider
 * 
 */
public class TrackStore {

  /** The only TrackStore ever created */
  private static final TrackStore trackStore = new TrackStore();

  /** Here we store the bounds of each track segment */
  private Map<Trkseg, BoundsType> segmentBounds = new HashMap<Trkseg, BoundsType>();

  /** A Gpx object containing all the tracks we know */
  private Gpx gpx;

  /** A list containing the track segments */
  private List<Trkseg> segmentList = null;

  /**
   * A private constructor
   */
  private TrackStore() {
    // private
  }

  /**
   * @return The only TrackStore instance
   */
  public static TrackStore getTrackStore() {
    return trackStore;
  }

  /**
   * Add the contents of a GPX file to the tracks
   * 
   * @param newGpx
   */
  public void addGPX(Gpx newGpx) {
    if (gpx == null) {
      gpx = newGpx;
    } else {
      gpx.getTrk().addAll(newGpx.getTrk());
    }
    // store the segments in a list
    segmentList = new ArrayList<Trkseg>();
    for (Trk track : gpx.getTrk()) {
      for (Trkseg segment : track.getTrkseg()) {
        segmentList.add(segment);
      }
    }
    createTrackSegmentBounds(newGpx);
  }

  /**
   * @param newGpx
   */
  private void createTrackSegmentBounds(Gpx newGpx) {
    // unfortunately GPX only defines a bounds for the entire file, not per
    // track
    ObjectFactory objectFactory = new ObjectFactory();
    for (Trk track : newGpx.getTrk()) {
      for (Trkseg segment : track.getTrkseg()) {
        if (segment.getTrkpt().size() > 0) {
          // we calculate bounds for each track segment
          BoundsType bounds = objectFactory.createBoundsType();
          for (Trkpt trackpoint : segment.getTrkpt()) {
            BigDecimal latitude = trackpoint.getLat();
            BigDecimal longitude = trackpoint.getLon();
            if (bounds.getMinlat() == null
                || latitude.compareTo(bounds.getMinlat()) < 0) {
              bounds.setMinlat(latitude);
            }
            if (bounds.getMaxlat() == null
                || latitude.compareTo(bounds.getMaxlat()) > 0) {
              bounds.setMaxlat(latitude);
            }
            if (bounds.getMinlon() == null
                || longitude.compareTo(bounds.getMinlon()) < 0) {
              bounds.setMinlon(longitude);
            }
            if (bounds.getMaxlon() == null
                || longitude.compareTo(bounds.getMaxlon()) > 0) {
              bounds.setMaxlon(longitude);
            }
          }
          // now that we know the bounds of the track we can store them
          segmentBounds.put(segment, bounds);
        }
      }
    }
  }

  /**
   * Check if we know any tracks
   * 
   * @return True if we do
   */
  public boolean hasTracks() {
    return gpx != null && gpx.getTrk().size() > 0;
  }

  /**
   * @return the Gpx object containing the tracks
   */
  public Gpx getGpx() {
    return gpx;
  }

  /**
   * @return The list of tracks
   */
  public List<Trk> getTracks() {
    if (gpx != null) {
      return gpx.getTrk();
    }
    return null;
  }

  /**
   * @return The list of track segments
   */
  public List<Trkseg> getTrackSegments() {
    return segmentList;
  }

  /**
   * @param segment
   *          A track segment in the store
   * @return The bounds of the segment, if available
   */
  BoundsType getSegmentBounds(Trkseg segment) {
    return segmentBounds.get(segment);
  }

  /**
   * Given the bounds (of a map) this method returns all stored track segments
   * whose bounds intersect the map bounds.
   * 
   * @param mapBounds
   *          The bounds of a map
   * @return All intersecting segments or an empty list
   */
  public List<Trkseg> getIntersectingTrackSegments(BoundsType mapBounds) {
    List<Trkseg> result = new ArrayList<Trkseg>();
    if (gpx != null) {
      for (Trk track : gpx.getTrk()) {
        for (Trkseg segment : track.getTrkseg()) {
          BoundsType bounds = segmentBounds.get(segment);
          if (bounds != null) {
            if (BoundsTypeUtil.intersect(mapBounds, bounds)) {
              // part or all of segment might be on map
              result.add(segment);
            }
          }
        }
      }
    }
    return result;
  }
}
