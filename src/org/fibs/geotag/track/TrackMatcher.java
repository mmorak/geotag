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

import org.fibs.geotag.data.UpdateGPSAltitude;
import org.fibs.geotag.data.UpdateGPSLatitude;
import org.fibs.geotag.data.UpdateGPSLongitude;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.util.Util;

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
  // private Gpx gpx;
  // /**
  // * Add a list of tracks
  // *
  // * @param newTracks
  // */
  // public void addTracks(List<Trk> newTracks) {
  // tracks.addAll(newTracks);
  // }

  /**
   * Checks if the GMT time lies between the times of two track points
   * 
   * @param gmt
   * @param startPoint
   * @param endPoint
   * @return True if the track point interval contains the imageInfo time
   */
  private boolean isBetweenTrackPoints(Calendar gmt, Trkpt startPoint,
      Trkpt endPoint) {
    Calendar startTime = startPoint.getTime().toGregorianCalendar();
    Calendar endTime = endPoint.getTime().toGregorianCalendar();
    boolean matches = (startTime.compareTo(gmt) <= 0 && endTime.compareTo(gmt) >= 0);
    return matches;
  }

  /**
   * Try and find the location given an imageInfo The imageInfo's time must fall
   * within a track segment
   * @param timeGMT The GMT time we try to find in the tracks
   * @return The best match we coud find
   * 
   */
  public Match findMatch(Calendar timeGMT) {
    // no tracks - no can match...
    if ( ! TrackStore.getTrackStore().hasTracks()) {
      return null;
    }
    Match match = new Match();
    // we keep track of the track segments closest
    // to the image time, in case we don't find a proper interval match
    Trkpt lastPointBefore = null;
    Calendar lastPointBeforeTime = null;
    Trkpt firstPointAfter = null;
    Calendar firstPointAfterTime = null;
    // look at all the tracks
    for (Trk track : TrackStore.getTrackStore().getTracks()) {
      // look at all track segments
      List<Trkseg> segments = track.getTrkseg();
      for (Trkseg segment : segments) {
        // retrieve the track points of this segment
        List<Trkpt> trackPoints = segment.getTrkpt();

        // first we see if our candidate lies between the first and last
        // track point of this track segment
        if (trackPoints.size() > 1 && match.getMatchingSegment() == null) {
          Trkpt startPoint = trackPoints.get(0);
          Trkpt endPoint = trackPoints.get(trackPoints.size() - 1);
          if (isBetweenTrackPoints(timeGMT, startPoint, endPoint)) {
            // the image was taken in this segment - have a closer look
            // now we look at pairs of track points and see if their
            // time stamps make an interval containing the image time
            for (int i = 0; i < trackPoints.size() - 1; i++) {
              startPoint = trackPoints.get(i);
              endPoint = trackPoints.get(i + 1);
              if (isBetweenTrackPoints(timeGMT, startPoint,
                  endPoint)) {
                match.setMatchingSegment(segment);
                match.setPreviousPoint(startPoint);
                match.setNextPoint(endPoint);
                // no need to look at other trackpoints
                break;
              }
            }
            if (match.getMatchingSegment() != null) {
              // skip the rest of the for loop and continue with next segment
              continue;
            }
          }
        }

        // our image is not in this interval, or there are less than
        // two track points in the segment
        if (trackPoints.size() > 0) {
          Trkpt startPoint = trackPoints.get(0);
          Trkpt endPoint = trackPoints.get(trackPoints.size() - 1);
          Calendar startTime = startPoint.getTime().toGregorianCalendar();
          Calendar endTime = endPoint.getTime().toGregorianCalendar();
          if (startTime.compareTo(timeGMT) >= 0) {
            // start time is after or exactly the same as image time
            if (firstPointAfter == null || firstPointAfterTime == null
                || firstPointAfterTime.after(startTime)) {
              firstPointAfter = startPoint;
              firstPointAfterTime = startTime;
              match.setPreviousSegment(segment);
            }
          }
          if (endTime.compareTo(timeGMT) <= 0) {
            // end time is before or exactly the same as image time
            if (lastPointBefore == null || lastPointBeforeTime == null
                || lastPointBeforeTime.before(endTime)) {
              lastPointBefore = endPoint;
              lastPointBeforeTime = endTime;
              match.setNextSegment(segment);
            }
          }
        }
      }
    }
    if (match.getMatchingSegment() != null) {
      return match;
    }
    if (lastPointBefore != null && firstPointAfter != null) {
      match.setPreviousPoint(lastPointBefore);
      match.setNextPoint(firstPointAfter);
      return match;
    }
    return null;
  }

  /**
   * Calculate the coordinates and store them
   * 
   * @param imageInfo
   *          The ImageInfo that will get new coordinates
   * @param match The match determined by the matcher
   */
  public void performMatch(ImageInfo imageInfo, Match match) {
    Trkpt startPoint = match.getPreviousPoint();
    Trkpt endPoint = match.getNextPoint();
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
      new UpdateGPSLatitude(imageInfo, Double.toString(latitude),
          ImageInfo.DATA_SOURCE.TRACK);
      new UpdateGPSLongitude(imageInfo, Double.toString(longitude),
          ImageInfo.DATA_SOURCE.TRACK);
      new UpdateGPSAltitude(imageInfo, Double.toString(altitude),
          ImageInfo.DATA_SOURCE.TRACK);
    }
  }

  /**
   * A class holding the information found out by the matcher
   */
  public class Match {
    /** The latest segment before the requested time not containing it */
    private Trkseg previousSegment;

    /** The earliest segment after the requested time not containing it */
    private Trkseg nextSegment;

    /** The matching segment */
    private Trkseg matchingSegment;

    /** The last track point before the requested time*/
    private Trkpt previousPoint;

    /** the next track point after the requested time */
    private Trkpt nextPoint;

    /**
     * @return the previousSegment
     */
    public Trkseg getPreviousSegment() {
      return previousSegment;
    }

    /**
     * @param previousSegment
     *          the previousSegment to set
     */
    public void setPreviousSegment(Trkseg previousSegment) {
      this.previousSegment = previousSegment;
    }

    /**
     * @return the nextSegment
     */
    public Trkseg getNextSegment() {
      return nextSegment;
    }

    /**
     * @param nextSegment
     *          the nextSegment to set
     */
    public void setNextSegment(Trkseg nextSegment) {
      this.nextSegment = nextSegment;
    }

    /**
     * @return the matchingSegment
     */
    public Trkseg getMatchingSegment() {
      return matchingSegment;
    }

    /**
     * @param matchingSegment
     *          the matchingSegment to set
     */
    public void setMatchingSegment(Trkseg matchingSegment) {
      this.matchingSegment = matchingSegment;
    }

    /**
     * @return the previousPoint
     */
    public Trkpt getPreviousPoint() {
      return previousPoint;
    }

    /**
     * @param previousPoint
     *          the previousPoint to set
     */
    public void setPreviousPoint(Trkpt previousPoint) {
      this.previousPoint = previousPoint;
    }

    /**
     * @return the nextPoint
     */
    public Trkpt getNextPoint() {
      return nextPoint;
    }

    /**
     * @param nextPoint
     *          the nextPoint to set
     */
    public void setNextPoint(Trkpt nextPoint) {
      this.nextPoint = nextPoint;
    }
  }

}
