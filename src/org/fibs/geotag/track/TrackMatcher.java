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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.UpdateGPSAltitude;
import org.fibs.geotag.data.UpdateGPSLatitude;
import org.fibs.geotag.data.UpdateGPSLongitude;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.util.Constants;
import org.fibs.geotag.util.Util;
import org.fibs.geotag.util.Units.ALTITUDE;

import com.topografix.gpx._1._0.ObjectFactory;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg.Trkpt;

/**
 * This class matches the known tracks with time stamps from images.
 * 
 * @author Andreas Schneider
 * 
 */
public class TrackMatcher {
	/** Number of matches performed - used for speed measurements. */
	@SuppressWarnings("unused")
  private int matchesPerformed = 0;

	/** Total time used to match tracks - used for speed measurements. */
	@SuppressWarnings("unused")
  private double totalTime = 0.0;

	/** We keep track of how many track segments don't need looking at. */
	private int startIndex = 0;

	/**
	 * Try and find the location given an imageInfo The imageInfo's time must
	 * fall within a track segment.
	 * 
	 * @param timeGMT
	 *            The GMT time we try to find in the tracks
	 * @return The best match we could find
	 * 
	 */
	public Match findMatch(Calendar timeGMT) {
		// no tracks - no can match...
		if (!TrackStore.getTrackStore().hasTracks()) {
			return null;
		}
		// for the binary search performed later we need a track point
		// to be used as a search key
		ObjectFactory objectFactory = new ObjectFactory();
		Trkpt searchKey = objectFactory.createGpxTrkTrksegTrkpt();
		// this track point needs to hold the time we are looking for
		XMLGregorianCalendar xmlTimeGMT = null;
		try {
			xmlTimeGMT = DatatypeFactory.newInstance().newXMLGregorianCalendar(
					(GregorianCalendar) timeGMT);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		searchKey.setTime(xmlTimeGMT);
		long start = System.currentTimeMillis();
		Match match = new Match();
		// we keep track of the track segments closest
		// to the image time, in case we don't find a proper interval match
		Trkpt lastPointBefore = null;
		Calendar lastPointBeforeTime = null;
		Trkpt firstPointAfter = null;
		// look at all the track segments
		List<Trkseg> trackSegments = TrackStore.getTrackStore()
				.getTrackSegments();
		// for (Trkseg segment : TrackStore.getTrackStore().getTrackSegments())
		// {
		for (int segmentIndex = startIndex; segmentIndex < trackSegments.size(); segmentIndex++) {
			Trkseg segment = trackSegments.get(segmentIndex);
			// retrieve the track points of this segment
			List<Trkpt> trackPoints = segment.getTrkpt();

			// first we see if our candidate lies between the first and last
			// track point of this track segment
			if (trackPoints.size() > 1 && match.getMatchingSegment() == null) {
				// we use binary search now
				// note that we already know that the point lies within the
				// segment
				// so the search will not return -1 or trackPoints.size()
				int greaterOrEqual = Collections.binarySearch(trackPoints,
						searchKey, new TrackPointComparator());
				// first test if track point is within the segment
				if (greaterOrEqual != -1
						&& greaterOrEqual != -trackPoints.size() - 1) {
					// next time round start searching from this segment
					startIndex = segmentIndex;
					if (greaterOrEqual < 0) {
						// search result is (-(insertion point) - 1)
						// insertion point is defined as:
						// The index of the first element greater than the key,
						// or list.size() if all elements in the list are less
						// than the
						// specified key
						greaterOrEqual = -(greaterOrEqual + 1);
					} else if (greaterOrEqual == 0) {
						// exact match for first track point
						greaterOrEqual = 1;
					}
					match.setMatchingSegment(segment);
					match.setPreviousPoint(trackPoints.get(greaterOrEqual - 1));
					match.setNextPoint(trackPoints.get(greaterOrEqual));
				}
			}

			// our image is not in this interval, or there are less than
			// two track points in the segment
			if (match.getMatchingSegment() == null && trackPoints.size() > 0) {
				Trkpt startPoint = trackPoints.get(0);
				Trkpt endPoint = trackPoints.get(trackPoints.size() - 1);
				Calendar startTime = startPoint.getTime().toGregorianCalendar();
				Calendar endTime = endPoint.getTime().toGregorianCalendar();
				if (startTime.compareTo(timeGMT) >= 0) {
					// start time is after or exactly the same as image time
					firstPointAfter = startPoint;
					// at this time we know that searching any further will
					// not give any more information about this track point
					// as the following segments should be later than this one
					// and this segment is already later than the track point
					break;
				}
				if (endTime.compareTo(timeGMT) <= 0) {
					// end time is before or exactly the same as image time
					if (lastPointBefore == null || lastPointBeforeTime == null
							|| lastPointBeforeTime.before(endTime)) {
						lastPointBefore = endPoint;
						lastPointBeforeTime = endTime;
					}
					startIndex = segmentIndex;
				}
			}
		}
		long end = System.currentTimeMillis();
		matchesPerformed++;
		totalTime += (end - start) / (double) Constants.ONE_SECOND_IN_MILLIS;
		// System.out.println("This :"+(end-start)/1000.0+" Average: " +
		// totalTime /
		// matchesPerformed+" Total: "+totalTime);
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
	 * Calculate the coordinates and store them.
	 * 
	 * @param imageInfo
	 *            The ImageInfo that will get new coordinates
	 * @param match
	 *            The match determined by the matcher
	 */
	public void performMatch(ImageInfo imageInfo, Match match) {
		Trkpt startPoint = match.getPreviousPoint();
		Trkpt endPoint = match.getNextPoint();
		Calendar startTime = startPoint.getTime().toGregorianCalendar();
		Calendar endTime = endPoint.getTime().toGregorianCalendar();
		// we found the two readings before and after the image was
		// taken (or an exact match)
		// where in the segment lies our time
		double ratio = Util.calculateRatio(startTime.getTimeInMillis(),
				imageInfo.getTimeGMT().getTimeInMillis(), endTime
						.getTimeInMillis());
		// now we apply this ratio to interpolate the position
		double startLatitude = startPoint.getLat().doubleValue();
		double startLongitude = startPoint.getLon().doubleValue();
		double startAltitude = 0.0;
		if (startPoint.getEle() != null) {
			startAltitude = startPoint.getEle().doubleValue();
		}
		double endLatitude = endPoint.getLat().doubleValue();
		double endLongitude = endPoint.getLon().doubleValue();
		double endAltitude = 0.0;
		if (endPoint.getEle() != null) {
			endAltitude = endPoint.getEle().doubleValue();
		}
		double latitude = Util.applyRatio(startLatitude, endLatitude, ratio);
		double longitude = Util.applyRatio(startLongitude, endLongitude, ratio);
		double altitude = Util.applyRatio(startAltitude, endAltitude, ratio);
		// only store the result in the imageInfo if they are different
		// from before
		boolean update = imageInfo.getSource() != DATA_SOURCE.IMAGE;
		if (!update) {
			// the current coordinates come from the image
			try {
				double oldLatitude = Double.parseDouble(imageInfo
						.getGpsLatitude());
				double oldLongitude = Double.parseDouble(imageInfo
						.getGpsLongitude());
				double distance = Util.greatCircleDistance(latitude, longitude,
						oldLatitude, oldLongitude);
				if (distance > 1.0) {
					// more than one meter - update is valid
					update = true;
				}
			} catch (Exception e) {
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
			// altitudes are internally stored in metres
			new UpdateGPSAltitude(imageInfo, Double.toString(altitude),
					ImageInfo.DATA_SOURCE.TRACK, ALTITUDE.METRES);
		}
	}

	/**
	 * A class holding the information found out by the matcher.
	 */
	public static class Match {
		/** The matching segment. */
		private Trkseg matchingSegment;

		/** The last track point before the requested time. */
		private Trkpt previousPoint;

		/** the next track point after the requested time. */
		private Trkpt nextPoint;

		/**
		 * @return the matchingSegment
		 */
		public Trkseg getMatchingSegment() {
			return matchingSegment;
		}

		/**
		 * @param matchingSegment
		 *            the matchingSegment to set
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
		 *            the previousPoint to set
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
		 *            the nextPoint to set
		 */
		public void setNextPoint(Trkpt nextPoint) {
			this.nextPoint = nextPoint;
		}
	}

	/**
	 * Comparator for track points.
	 * 
	 * @author Andreas Schneider
	 * 
	 */
	static class TrackPointComparator implements Comparator<Trkpt>, Serializable {
		/***/
    private static final long serialVersionUID = 1L;

    /**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Trkpt point1, Trkpt point2) {
			Calendar time1 = point1.getTime().toGregorianCalendar();
			Calendar time2 = point2.getTime().toGregorianCalendar();
			return time1.compareTo(time2);
		}
	}

}
