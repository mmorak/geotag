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

package org.fibs.geotag.googleearth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.external.ExternalUpdate;
import org.fibs.geotag.external.ExternalUpdateConsumer;
import org.fibs.geotag.util.Airy;
import org.fibs.geotag.webserver.ContextHandler;
import org.fibs.geotag.webserver.WebServer;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.earth.kml._2.BalloonStyleType;
import com.google.earth.kml._2.DocumentType;
import com.google.earth.kml._2.FeatureType;
import com.google.earth.kml._2.KmlType;
import com.google.earth.kml._2.ObjectFactory;
import com.google.earth.kml._2.PlacemarkType;
import com.google.earth.kml._2.PointType;
import com.google.earth.kml._2.StyleSelectorType;
import com.google.earth.kml._2.StyleType;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

/**
 * Class to handle HttpRequests for the Webserver.
 * 
 * @author Andreas Schneider
 * 
 */
public class KmlRequestHandler implements ContextHandler {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(KmlRequestHandler.class);

  /** Who to inform about external updates. */
  private ExternalUpdateConsumer parent;

  /** The correct mime type for kml files. */
  private static final String KML_MIME_TYPE = "application/vnd.google-earth.kml+xml"; //$NON-NLS-1$

  /** ID for the balloon style. */
  private static final String BALLOON_STYLE = "balloonStyle"; //$NON-NLS-1$

  /**
   * @param parent
   *          Who to inform about updates
   */
  public KmlRequestHandler(ExternalUpdateConsumer parent) {
    this.parent = parent;
  }

  /**
   * @see org.fibs.geotag.webserver.ContextHandler#serve(org.fibs.geotag.webserver.WebServer,
   *      java.lang.String, java.lang.String, java.util.Properties,
   *      java.util.Properties)
   */
  @Override
  public Response serve(WebServer server, String uri, String method,
      Properties header, Properties parameters) {
    double latitude = Airy.LATITUDE;
    double longitude = Airy.LONGITUDE;
    Enumeration<Object> keys = parameters.keys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      String value = parameters.getProperty(key);
      if (key.equals("latitude")) { //$NON-NLS-1$
        latitude = Double.parseDouble(value);
      } else if (key.equals("longitude")) { //$NON-NLS-1$
        longitude = Double.parseDouble(value);
      }
    }
    List<ExternalUpdate> externalUpdates = new ArrayList<ExternalUpdate>();
    ExternalUpdate externalUpdate = new ExternalUpdate(GoogleEarthLauncher
        .getLastImageLauched().getSequenceNumber(), latitude, longitude,
        Double.NaN);
    externalUpdates.add(externalUpdate);
    parent.processExternalUpdates(externalUpdates);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    writeKml(latitude, longitude, outputStream);
    byte[] kml = outputStream.toByteArray();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(kml);
    Response response = server.new Response(NanoHTTPD.HTTP_OK, KML_MIME_TYPE,
        inputStream);
    return response;
  }

  /**
   * Send a response back to Google Earth.
   * 
   * @param latitude
   * @param longitude
   * @param outputStream
   */
  private void writeKml(double latitude, double longitude,
      OutputStream outputStream) {
    ImageInfo lastImageLaunched = GoogleEarthLauncher.getLastImageLauched();
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
      String balloonText = "<center><b>$[name]</b>"; //$NON-NLS-1$
      if (lastImageLaunched.getThumbnail() != null) {
        balloonText += "<br/>"; //$NON-NLS-1$
        balloonText += "<img src=\"http://127.0.0.1:4321/images/"; //$NON-NLS-1$
        balloonText += Integer.toString(lastImageLaunched.getSequenceNumber());
        balloonText += ".jpg\" width=\""; //$NON-NLS-1$
        balloonText += lastImageLaunched.getThumbnail().getIconWidth();
        balloonText += "\" height=\""; //$NON-NLS-1$
        balloonText += lastImageLaunched.getThumbnail().getIconHeight();
        balloonText += "\">"; //$NON-NLS-1$
      }
      balloonText += "</center>"; //$NON-NLS-1$
      balloonStyle.setText(balloonText);
      style.setBalloonStyle(balloonStyle);

      List<JAXBElement<? extends StyleSelectorType>> styleSelectors = document
          .getStyleSelector();
      styleSelectors.add(factory.createStyle(style));
      // The place mark
      PlacemarkType placemark = factory.createPlacemarkType();
      placemark.setName(i18n.tr("Position")); //$NON-NLS-1$
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

}
