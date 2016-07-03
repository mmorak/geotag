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

package org.fibs.geotag.geonames;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.util.Constants;
import org.fibs.geotag.util.Proxies;
import org.fibs.geotag.util.Units;
import org.fibs.geotag.util.Units.DISTANCE;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A class sending a request to genames.org and parsing the response.
 * 
 * @author Andreas Schneider
 * 
 */
public class LocationHandler extends DefaultHandler {

  /** A list of all locations retrieved. */
  private List<Location> locations = new ArrayList<Location>();

  /** The location we are currently parsing. */
  private Location currentLocation = null;

  /** Used to build the value of the element we are parsing. */
  private StringBuilder currentValue = null;

  /**
   * @param latitude
   * @param longitude
   */
  public LocationHandler(String latitude, String longitude) {
    try {
      // Create a SAX 2 parser
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      // This object is the context handler
      xmlReader.setContentHandler(this);
      // Build the request
      GeonamesService service = new GeonamesService(GeonamesService.FIND_NEARBY);
      service.addParameter("lat", latitude); //$NON-NLS-1$
      service.addParameter("lng", longitude); //$NON-NLS-1$
      service.addParameter("style", "FULL"); //$NON-NLS-1$ //$NON-NLS-2$
      boolean useRadius = Settings.get(SETTING.GEONAMES_USE_RADIUS, false);
      if (useRadius) {
        DISTANCE unit = Units.DISTANCE.values()[Settings.get(
            SETTING.DISTANCE_UNIT, 0)];
        int radius = Settings.get(SETTING.GEONAMES_RADIUS,
            Settings.GEONAMES_DEFAULT_RADIUS);
        double radiusKm = Units
            .convert(radius, unit, Units.DISTANCE.KILOMETRES);
        if (radiusKm != 0) {
          service.addParameter("radius", radiusKm); //$NON-NLS-1$
        }
      }
      int maxRows = Settings.get(SETTING.GEONAMES_MAX_ROWS,
          Settings.GEONAMES_DEFAULT_MAX_ROWS);
      service.addParameter("maxRows", maxRows); //$NON-NLS-1$
      // finally the language
      String language = Locale.getDefault().getLanguage();
      if (Settings.get(SETTING.GEONAMES_OVERRIDE_LANGUAGE, false)) {
        language = Settings.get(SETTING.GEONAMES_LANGUAGE, ""); //$NON-NLS-1$
      }
      service.addParameter("lang", language); //$NON-NLS-1$
      String url = service.buildURL();
      System.out.println(url);
      URL request = new URL(url);
      URLConnection connection = request.openConnection(Proxies.getProxy());
      // time out after 30 seconds - prevent hangs
      connection.setReadTimeout((int) Constants.ONE_MINUTE_IN_MILLIS / 2);
      InputStream inputStream = connection.getInputStream();
      xmlReader.parse(new InputSource(inputStream));
      inputStream.close();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler#startDocument()
   */
  @Override
  public void startDocument() throws SAXException {
    //
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler#endDocument()
   */
  @Override
  public void endDocument() throws SAXException {
    // do something
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
   *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(String namespaceURI, String localName, String qName,
      Attributes attr) throws SAXException {
    // we only look at the localName.. geonames.org doesn't use attributes
    currentValue = null;
    for (ELEMENTS tag : ELEMENTS.values()) {
      if (tag.toString().equals(localName)) {
        currentValue = new StringBuilder();
        // System.out.println(tag);
        if (tag == ELEMENTS.geoname) {
          // a new location
          currentLocation = new Location();
        }
        break;
      }
    }
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(String namespaceURI, String localName, String qName)
      throws SAXException {
    for (ELEMENTS tag : ELEMENTS.values()) {
      if (tag.toString().equals(localName)) {
        switch (tag) {
          case geonames:
            // nothing to be done
            break;
          case geoname:
            // finished with one location
            locations.add(currentLocation);
            currentLocation = null;
            break;
          case name:
            currentLocation.setName(currentValue.toString());
            break;
          case lat:
            currentLocation.setLatitude(currentValue.toString());
            break;
          case lng:
            currentLocation.setLongitude(currentValue.toString());
            break;
          case countryName:
            currentLocation.setCountryName(currentValue.toString());
            break;
          case adminName1:
            currentLocation.setProvince(currentValue.toString());
            break;
          case distance:
            // geonames sends the distance with a decimal point or a
            // decimal comma, depending on the language specified.
            // The replacement of comma with decimal point in all cases
            // might lead to problems for large distances.
            currentLocation.setDistance(Double.parseDouble(currentValue
                .toString().replace(',', '.')));
            break;
          case alternateNames:
            if (currentValue.toString().length() > 0) {
              currentLocation.setAlternateNames(currentValue.toString());
            }
            break;
          case fcodeName:
            currentLocation.setFeatureName(currentValue.toString());
            break;
          case fcl:
            currentLocation.setFeatureClass(currentValue.toString());
            break;
          default:
            break;
        }
      }
    }
    currentValue = null;
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
   */
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (currentValue != null) {
      currentValue.append(ch, start, length);
    }
  }

  /**
   * @return the locations
   */
  public List<Location> getLocations() {
    return locations;
  }

  /**
   * The XML elements we are interested in. Specified exactly as the appear in
   * the XML
   */
  enum ELEMENTS {
    /** Entries are enclosed in a 'geonames' element. */
    geonames,
    /** Name of element containing a geoname. */
    geoname,
    /** Element for location name. */
    name,
    /** Element specifying latitude. */
    lat,
    /** Element specifying longitude. */
    lng,
    /** Element for country name. */
    countryName,
    /** Element for province/state. */
    adminName1,
    /** Element specifying the distance from the requested latitude/longitude. */
    distance,
    /** element specifying alternative names for a place. */
    alternateNames,
    /** element for the feature code name. */
    fcodeName,
    /** element for feature class. */
    fcl
  }
}
