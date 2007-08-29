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

package org.fibs.geotag.googleearth;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.fibs.geotag.Geotag;

import com.google.earth.kml._2.BalloonStyleType;
import com.google.earth.kml._2.DocumentType;
import com.google.earth.kml._2.FeatureType;
import com.google.earth.kml._2.KmlType;
import com.google.earth.kml._2.ObjectFactory;
import com.google.earth.kml._2.PlacemarkType;
import com.google.earth.kml._2.PointType;
import com.google.earth.kml._2.StyleSelectorType;
import com.google.earth.kml._2.StyleType;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Class to handle HttpRequests for the Webserver
 * 
 * @author Andreas Schneider
 * 
 */
public class KmlRequestHandler implements HttpHandler {

  /** HTTP response OK */
  private static final int RESPONSE_OK = 200;

  /** the latitude parameter */
  private static final String LATITUDE = "latitude"; //$NON-NLS-1$

  /** the longitude parameter */
  private static final String LONGITUDE = "longitude"; //$NON-NLS-1$

  /** ID for the balloon style */
  private static final String BALLOON_STYLE = "balloonStyle"; //$NON-NLS-1$

  /**
   * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
   */
  public void handle(HttpExchange exchange) throws IOException {
    // only handle GET requests
    String requestMethod = exchange.getRequestMethod();
    if ("GET".equalsIgnoreCase(requestMethod)) { //$NON-NLS-1$
      // what is the request?
      URI uri = exchange.getRequestURI();
      System.out.println(uri.getPath());
      System.out.println(uri.getQuery());
      // find out the data coming in from google earth
      // store the query in a Query hash map
      Query query = new Query(uri.getQuery());
      // find the latitude
      double latitude = 51.5;
      if (query.get(LATITUDE) != null) {
        latitude = Double.parseDouble(query.get(LATITUDE));
      }
      // find the longitude
      double longitude = 0.0;
      if (query.get(LONGITUDE) != null) {
        longitude = Double.parseDouble(query.get(LONGITUDE));
      }
      // Set headers for response
      Headers responseHeaders = exchange.getResponseHeaders();
      // set the response content type to KML
      responseHeaders.set(
          "Content-Type", "application/vnd.google-earth.kml+xml"); //$NON-NLS-1$ //$NON-NLS-2$
      // Mark the response as OK
      exchange.sendResponseHeaders(RESPONSE_OK, 0);

      // Get response body - so we can send an answer
      OutputStream responseBody = exchange.getResponseBody();

      // send the KML response
      writeKml(latitude, longitude, responseBody);

      responseBody.close();
    }
  }

  /**
   * Send a response back to Google Earth
   * 
   * @param latitude
   * @param longitude
   * @param outputStream
   */
  private void writeKml(double latitude, double longitude,
      OutputStream outputStream) {
    try {
      // get the correct context
      JAXBContext jaxbContext = JAXBContext.newInstance(KmlType.class
          .getPackage().getName());
      // we need a factory to create objects
      ObjectFactory factory = new ObjectFactory();
      // the KML element at the top
      KmlType kml = factory.createKmlType();
      // contains one Document
      DocumentType document = factory.createDocumentType();
      // with several features
      List<JAXBElement<? extends FeatureType>> documentFeatures = document
          .getFeature();

      // first we define a style
      StyleType style = factory.createStyleType();
      style.setId(BALLOON_STYLE);

      BalloonStyleType balloonStyle = factory.createBalloonStyleType();
      // TODO - the next bit needs tweaking to do something useful
      String balloonText = "<b>$[name]</b><br/>"; //$NON-NLS-1$
      balloonText += "<a href='http://127.0.0.1:8080/images/test.jpg'>Sample link</a><br>"; //$NON-NLS-1$
      balloonText += "Click<a href='http://127.0.0.1:8080/geotag/accept.html'><b>here</b></a> to send coordinates to " + Geotag.NAME; //$NON-NLS-1$
      balloonStyle.setText(balloonText);
      style.setBalloonStyle(balloonStyle);

      List<JAXBElement<? extends StyleSelectorType>> styleSelectors = document
          .getStyleSelector();
      styleSelectors.add(factory.createStyle(style));
      // The place mark
      PlacemarkType placemark = factory.createPlacemarkType();
      placemark.setName(Geotag.NAME);
      placemark.setStyleUrl('#' + BALLOON_STYLE);

      PointType point = factory.createPointType();
      List<String> coordinates = point.getCoordinates();

      coordinates.add(longitude + "," + latitude); //$NON-NLS-1$
      placemark.setGeometry(factory.createPoint(point));
      documentFeatures.add(factory.createPlacemark(placemark));
      kml.setFeature(factory.createDocument(document));
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE); //$NON-NLS-1$
      // finally we write the whole kml to the output stream
      marshaller.marshal(factory.createKml(kml), outputStream);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    KmlRequestHandler handler = new KmlRequestHandler();
    handler.writeKml(51.5, 0.0, System.out);
  }

}
