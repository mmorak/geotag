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

package org.fibs.geotag.webserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.fibs.geotag.Messages;
import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.track.TrackMatcher;
import org.fibs.geotag.track.TrackMatcher.Match;

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

  /** The choices we have for displaying tracks */
  public static final String[] GOOGLE_MAP_TRACK_CHOICES = {
    Messages.getString("TracksHandler.None"), //$NON-NLS-1$
    Messages.getString("TracksHandler.MatchingSegment"), //$NON-NLS-1$
    Messages.getString("TracksHandler.MatchingSegmentAndNeighbours") //$NON-NLS-1$
    //"All tracks"
  };
  /** The MIME type for XML files */
  private static final String XML_MIME_TYPE = "application/xml"; //$NON-NLS-1$

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
    Double width = null;
    Double height = null;
    Integer image = null;
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
        width = new Double(Double.parseDouble(value));
      } else if (parameter.equals("height")) { //$NON-NLS-1$
        height = new Double(Double.parseDouble(value));
      } else if (parameter.equals("image")) { //$NON-NLS-1$
        image = new Integer(Integer.parseInt(value));
      }
    }
    ObjectFactory gpxObjectFactory = new ObjectFactory();
    BoundsType bounds = gpxObjectFactory.createBoundsType();
    if (south != null && west != null && north != null && east != null
        && width != null && height != null) {
      int tracksChoice = Settings.get(SETTING.GOOGLE_MAP_TRACKS_CHOICE, 1);
      List<Trkseg> segments = new ArrayList<Trkseg>();
      ImageInfo imageInfo = ImageInfo.getImageInfo(image);
      Match match = null;
      switch (tracksChoice) {
        case 0: // none
          break;
        case 1: // matching track only
          match = new TrackMatcher().findMatch(imageInfo.getTimeGMT());
          if (match != null) {
            if (match.getMatchingSegment() != null) {
              segments.add(match.getMatchingSegment());
            }
          }
          break;
        case 2: // Matching track and two closest
          match = new TrackMatcher().findMatch(imageInfo.getTimeGMT());
          if (match != null) {
            if (match.getMatchingSegment() != null) {
              segments.add(match.getMatchingSegment());
            }
            if (match.getPreviousSegment() != null) {
              segments.add(match.getPreviousSegment());
            }
            if (match.getNextSegment() != null) {
              segments.add(match.getNextSegment());
            }
          }
      }
      bounds.setMinlat(new BigDecimal(south));
      bounds.setMaxlat(new BigDecimal(north));
      bounds.setMinlon(new BigDecimal(west));
      bounds.setMaxlon(new BigDecimal(east));
      
      
      // get track segments whose bounds intersect with the map bounds
      

      // now that we have segments, write to out
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try {
        writeTracks(segments, byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
            byteArrayOutputStream.toByteArray());
        return server.new Response(NanoHTTPD.HTTP_OK, XML_MIME_TYPE,
            byteArrayInputStream);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return server.new Response(NanoHTTPD.HTTP_NOTFOUND,
        NanoHTTPD.MIME_PLAINTEXT, WebServer.FILE_NOT_FOUND);
  }

  /**
   * Write a list of segments to an ouput stream in XML format
   * 
   * @param segments
   *          the list of segments
   * @param stream
   *          The output stream
   * @throws IOException
   */
  private void writeTracks(List<Trkseg> segments, OutputStream stream)
      throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<tracks>\n"); //$NON-NLS-1$
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
    stream.write(stringBuilder.toString().getBytes());
  }

}
