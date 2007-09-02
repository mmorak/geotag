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

import java.util.Calendar;
import java.util.List;

import org.fibs.geotag.data.EditGPSAltitude;
import org.fibs.geotag.data.EditGPSLatitude;
import org.fibs.geotag.data.EditGPSLongitude;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.util.Util;

import com.topografix.gpx._1._0.Gpx;
import com.topografix.gpx._1._0.Gpx.Trk;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg.Trkpt;

/**
 * This class matches the known tracks with time stamps from images
 * 
 * @author Andreas Schneider
 * 
 */
public class TrackMatcher {
  /** The object containing all the tracks */
  private Gpx gpx;

  // /**
  // * Add a list of tracks
  // *
  // * @param newTracks
  // */
  // public void addTracks(List<Trk> newTracks) {
  // tracks.addAll(newTracks);
  // }

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
   * Checks if the time from an imageInfo lies between the times of two track
   * points
   * 
   * @param imageInfo
   * @param startPoint
   * @param endPoint
   * @return True if the track point interval contains the imageInfo time
   */
  private boolean isBetweenTrackPoints(ImageInfo imageInfo, Trkpt startPoint,
      Trkpt endPoint) {
    Calendar startTime = startPoint.getTime().toGregorianCalendar();
    Calendar endTime = endPoint.getTime().toGregorianCalendar();
    boolean matches = (startTime.compareTo(imageInfo.getTimeGMT()) <= 0 && endTime
        .compareTo(imageInfo.getTimeGMT()) >= 0);
    return matches;
  }

  /**
   * Try and find the location given an imageInfo The imageInfo's time must fall
   * within a track segment
   * 
   * @param imageInfo
   *          The imageInfo we want to match with our tracks
   * @param exact
   *          Only return a match if the image is inside a track segment
   * @return true if a match was found
   */
  public boolean match(ImageInfo imageInfo, boolean exact) {
    // we keep track of the track segments closest
    // to the image time, in case we don't find a proper interval match
    Trkpt lastPointBefore = null;
    Calendar lastPointBeforeTime = null;
    Trkpt firstPointAfter = null;
    Calendar firstPointAfterTime = null;
    // look at all the tracks
    for (Trk trk : gpx.getTrk()) {
      // look at all track segments
      List<Trkseg> trksegs = trk.getTrkseg();
      for (Trkseg trkseg : trksegs) {
        // retrieve the track points of this segment
        List<Trkpt> trkpts = trkseg.getTrkpt();
        // first we see if our candidate lies between the first and last
        // track point of this track segment
        if (trkpts.size() > 1) {
          Trkpt startPoint = trkpts.get(0);
          Trkpt endPoint = trkpts.get(trkpts.size() - 1);
          if (isBetweenTrackPoints(imageInfo, startPoint, endPoint)) {
            // the image was taken in this segment - have a closer look
            // now we look at pairs of track points and see if their
            // time stamps make an interval containing the image time
            for (int i = 0; i < trkpts.size() - 1; i++) {
              startPoint = trkpts.get(i);
              endPoint = trkpts.get(i + 1);
              if (isBetweenTrackPoints(imageInfo, startPoint, endPoint)) {
                performMatch(imageInfo, startPoint, endPoint);
                // return true, because we found a match
                return true;
              }
            }
          }
        }
        // our image is not in this interval, or there are less than
        // two track points in the segment
        if (trkpts.size() > 0) {
          Trkpt startPoint = trkpts.get(0);
          Trkpt endPoint = trkpts.get(trkpts.size() - 1);
          Calendar startTime = startPoint.getTime().toGregorianCalendar();
          Calendar endTime = endPoint.getTime().toGregorianCalendar();
          if (startTime.compareTo(imageInfo.getTimeGMT()) >= 0) {
            // start time is after or exactly the same as image time
            if (firstPointAfter == null || firstPointAfterTime == null
                || firstPointAfterTime.after(startTime)) {
              firstPointAfter = startPoint;
              firstPointAfterTime = startTime;
            }
          }
          if (endTime.compareTo(imageInfo.getTimeGMT()) <= 0) {
            // end time is before or exactly the same as image time
            if (lastPointBefore == null || lastPointBeforeTime == null
                || lastPointBeforeTime.before(endTime)) {
              lastPointBefore = endPoint;
              lastPointBeforeTime = endTime;
            }
          }
        }
      }
    }
    if (exact == false && lastPointBefore != null && firstPointAfter != null) {
      performMatch(imageInfo, lastPointBefore, firstPointAfter);
      return true;
    }
    return false;
  }

  /**
   * Calculate the coordinates and store them
   * 
   * @param imageInfo
   *          The ImageInfo that will get new coordinates
   * @param startPoint
   *          Track point before imageInfo
   * @param endPoint
   *          Track point after imageInfo
   */
  private void performMatch(ImageInfo imageInfo, Trkpt startPoint,
      Trkpt endPoint) {
    Calendar startTime = startPoint.getTime().toGregorianCalendar();
    Calendar endTime = endPoint.getTime().toGregorianCalendar();
    // we found the two readings before and after the image was
    // taken (or an exact match)
    // where in the segment lies our time
    double ratio = Util.calculateRatio(startTime.getTimeInMillis(), imageInfo
        .getTimeGMT().getTimeInMillis(), endTime.getTimeInMillis());
    // now we apply this ratio to interpolate the position
    double startLatitude = startPoint.getLat().doubleValue();
    double startLongitude = startPoint.getLon().doubleValue();
    double startAltitude = startPoint.getEle().doubleValue();
    double endLatitude = endPoint.getLat().doubleValue();
    double endLongitude = endPoint.getLon().doubleValue();
    double endAltitude = endPoint.getEle().doubleValue();
    double latitude = Util.applyRatio(startLatitude, endLatitude, ratio);
    double longitude = Util.applyRatio(startLongitude, endLongitude, ratio);
    double altitude = Util.applyRatio(startAltitude, endAltitude, ratio);
    // only store the result in the imageInfo if they are different
    // from before
    boolean update = imageInfo.getSource() != DATA_SOURCE.IMAGE;
    if (update == false) {
      // the current coordinates come from the image
      try {
        double oldLatitude = Double.parseDouble(imageInfo.getGPSLatitude());
        double oldLongitude = Double.parseDouble(imageInfo.getGPSLongitude());
        double distance = Util.greatCircleDistance(latitude, longitude,
            oldLatitude, oldLongitude);
        if (distance > 1.0) {
          // more than one meter - update is valid
          update = true;
        }
      } catch (NumberFormatException e) {
        e.printStackTrace();
        // the new values seem better than the old ones... update
        update = true;
      }
    }
    if (update) {
      new EditGPSLatitude(imageInfo, Double.toString(latitude),
          ImageInfo.DATA_SOURCE.TRACK);
      new EditGPSLongitude(imageInfo, Double.toString(longitude),
          ImageInfo.DATA_SOURCE.TRACK);
      new EditGPSAltitude(imageInfo, Double.toString(altitude),
          ImageInfo.DATA_SOURCE.TRACK);
    }
  }

  /**
   * @return the Gpx object containing the tracks
   */
  public Gpx getGpx() {
    return gpx;
  }
}
