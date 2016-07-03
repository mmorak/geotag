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

package org.fibs.geotag.webserver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.fibs.geotag.track.TrackStore;
import org.fibs.geotag.util.BoundsTypeUtil;

import com.topografix.gpx._1._0.BoundsType;
import com.topografix.gpx._1._0.ObjectFactory;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg.Trkpt;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

/**
 * @author Andreas Schneider
 * 
 */
public class TracksHandler implements ContextHandler {

  /**
   * @see org.fibs.geotag.webserver.ContextHandler#serve(org.fibs.geotag.webserver.WebServer,
   *      java.lang.String, java.lang.String, java.util.Properties,
   *      java.util.Properties)
   */
  @SuppressWarnings("boxing")
  @Override
  public Response serve(WebServer server, String uri, String method,
      Properties header, Properties parms) {
    Double south = null;
    Double west = null;
    Double north = null;
    Double east = null;
    Integer width = null;
    Integer height = null;
    Enumeration<Object> parameters = parms.keys();
    while (parameters.hasMoreElements()) {
      String parameter = (String) parameters.nextElement();
      String value = parms.getProperty(parameter);
      if (parameter.equals("south")) { //$NON-NLS-1$
        south = new Double(Double.parseDouble(value));
      } else if (parameter.equals("west")) { //$NON-NLS-1$
        west = new Double(Double.parseDouble(value));
      } else if (parameter.equals("north")) { //$NON-NLS-1$
        north = new Double(Double.parseDouble(value));
      } else if (parameter.equals("east")) { //$NON-NLS-1$
        east = new Double(Double.parseDouble(value));
      } else if (parameter.equals("width")) { //$NON-NLS-1$
        width = Integer.valueOf(Integer.parseInt(value));
      } else if (parameter.equals("height")) { //$NON-NLS-1$
        height = Integer.valueOf(Integer.parseInt(value));
      }
    }
    ObjectFactory gpxObjectFactory = new ObjectFactory();
    if (south != null && west != null && north != null && east != null
        && width != null && height != null) {
      BoundsType mapBounds = gpxObjectFactory.createBoundsType();
      mapBounds.setMinlat(new BigDecimal(south));
      mapBounds.setMaxlat(new BigDecimal(north));
      mapBounds.setMinlon(new BigDecimal(west));
      mapBounds.setMaxlon(new BigDecimal(east));
      List<Trkseg> segments = new ArrayList<Trkseg>();
      for (Trkseg segment : TrackStore.getTrackStore()
          .getIntersectingTrackSegments(mapBounds)) {
        segments.add(segment);
      }
      // trim down the tracks to bare minimum
      List<Trkseg> filteredSegments = filterSegments(mapBounds, segments,
          width, height);
      // now that we have segments, create a response
      return server.xmlResponse(tracksToXml(filteredSegments));
    }
    return server.new Response(NanoHTTPD.HTTP_NOTFOUND,
        NanoHTTPD.MIME_PLAINTEXT, WebServer.FILE_NOT_FOUND);
  }

  /**
   * @param trackPoint
   * @param mapBounds
   * @return True if the traclPoint is within the mapBounds
   */
  private boolean isOnMap(Trkpt trackPoint, BoundsType mapBounds) {
    BigDecimal latitude = trackPoint.getLat();
    BigDecimal longitude = trackPoint.getLon();
    // check the scenarios where the track point is not on the map
    if (latitude.compareTo(mapBounds.getMinlat()) < 0) {
      // latitude is smaller that the smallest map latitude
      return false;
    }
    if (latitude.compareTo(mapBounds.getMaxlat()) > 0) {
      // latitude is bigger than biggest latitude on map
      return false;
    }
    // now the same check for longitudes
    if (longitude.compareTo(mapBounds.getMinlon()) < 0) {
      return false;
    }
    if (longitude.compareTo(mapBounds.getMaxlon()) > 0) {
      return false;
    }
    // no condition for being within the map violated
    return true;
  }

  /**
   * This is where we filter the tracks. There is no need to send parts of the
   * tracks that are off screen and we also want to avoid sending consecutive
   * track points that at the current zoom factor are too close together.
   * 
   * @param mapBounds
   * @param segments
   * @param mapWidth
   * @param mapHeight
   * @return the filtered list of tracks
   */
  private List<Trkseg> filterSegments(BoundsType mapBounds,
      List<Trkseg> segments, int mapWidth, int mapHeight) {
    int numberUnfiltered = 0;
    int numberFiltered = 0;
    List<Trkseg> filteredList = new ArrayList<Trkseg>();
    ObjectFactory gpxObjectFactory = new ObjectFactory();
    // loop over all tracks
    for (Trkseg segment : segments) {
      Trkseg filteredSegment = gpxObjectFactory.createGpxTrkTrkseg();
      // go through the track points
      // we want to add the last point off the map to the track
      // to get a line that starts off the map if possible
      Trkpt lastPointOffMap = null;
      // we need to compare the pixel distance to the last point on the map
      Trkpt lastPointOnMap = null;
      // we need to keep track of the last point added to see how close it is to
      // the current one
      Trkpt lastPointAdded = null;
      for (Trkpt trackPoint : segment.getTrkpt()) {
        numberUnfiltered++;
        if (isOnMap(trackPoint, mapBounds)) {
          lastPointOnMap = trackPoint;
          // if we got here from a point off the map we use that point
          if (lastPointOffMap != null) {
            filteredSegment.getTrkpt().add(lastPointOffMap);
            lastPointAdded = lastPointOffMap;
            // don't add that point again
            lastPointOffMap = null;
          }
          // this point is on the map, but it might me too close to the last
          // point added
          final int tooClose = 10;
          if (lastPointAdded == null
              || BoundsTypeUtil.pixelDistance(lastPointAdded, trackPoint,
                  mapBounds, mapWidth, mapHeight) > tooClose) {
            filteredSegment.getTrkpt().add(trackPoint);
            lastPointAdded = trackPoint;
          }
        } else {
          // point is not on map
          lastPointOffMap = trackPoint;
          if (lastPointOnMap != null) {
            // this is the first off map point after one or more on map points -
            // keep it
            filteredSegment.getTrkpt().add(trackPoint);
            // don't add any more off map points
            lastPointOnMap = null;
          }
        }
      }
      // only add the segment to the list if its not empty after all the
      // filtering
      if (filteredSegment.getTrkpt().size() > 0) {
        filteredList.add(filteredSegment);
        numberFiltered += filteredSegment.getTrkpt().size();
      }
    }
    System.out.println("Filter: " + numberUnfiltered + "->" + numberFiltered); //$NON-NLS-1$ //$NON-NLS-2$
    return filteredList;
  }

  /**
   * Convert a list of segments to a string in XML format.
   * 
   * @param segments
   *          the list of segments
   * @return The XML representation of the track segment list
   */
  private String tracksToXml(List<Trkseg> segments) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<tracks>\n"); //$NON-NLS-1$
    @SuppressWarnings("unused")
    int trackPoints = 0;
    for (Trkseg trkseg : segments) {
      trackPoints += trkseg.getTrkpt().size();
      stringBuilder.append(" <track>\n"); //$NON-NLS-1$
      for (Trkpt trackPoint : trkseg.getTrkpt()) {
        stringBuilder.append("  <point latitude=\""); //$NON-NLS-1$
        stringBuilder.append(trackPoint.getLat().toString());
        stringBuilder.append("\" longitude=\""); //$NON-NLS-1$
        stringBuilder.append(trackPoint.getLon().toString());
        // we could specify colour and opacity here as well, but not yet
        stringBuilder.append("\"/>\n"); //$NON-NLS-1$
      }
      stringBuilder.append(" </track>\n"); //$NON-NLS-1$
    }
    stringBuilder.append("</tracks>"); //$NON-NLS-1$
    return stringBuilder.toString();
  }

}
