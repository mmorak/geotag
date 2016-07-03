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
public class WikipediaHandler extends DefaultHandler {

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
  public WikipediaHandler(String latitude, String longitude) {
    try {
      // Create a SAX 2 parser
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      // This object is the context handler
      xmlReader.setContentHandler(this);
      // Build the request
      GeonamesService service = new GeonamesService(GeonamesService.FIND_NEARBY_WIKIPEDIA);
      service.addParameter("lat", latitude); //$NON-NLS-1$
      service.addParameter("lng", longitude); //$NON-NLS-1$
      // how many entries should we retrieve
      int maxRows = Settings.get(SETTING.GEONAMES_WIKIPEDIA_ENTRIES,
          Settings.GEONAMES_DEFAULT_WIKIPEDIA_ENTRIES);
      service.addParameter("maxRows", maxRows); //$NON-NLS-1$
      // finally the language
      String language = Locale.getDefault().getLanguage();
      if (Settings.get(SETTING.GEONAMES_OVERRIDE_LANGUAGE, false)) {
        language = Settings.get(SETTING.GEONAMES_LANGUAGE, ""); //$NON-NLS-1$
      }
      service.addParameter("lang", language); //$NON-NLS-1$
      String url = service.buildURL();
      URL request = new URL(url);
      URLConnection connection = request.openConnection(Proxies.getProxy());
      // 30 second time out:
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
        if (tag == ELEMENTS.entry) {
          // a new location
          currentLocation = new WikipediaLocation();
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
          case entry:
            // finished with one location
            locations.add(currentLocation);
            // System.out.println(currentLocation);
            currentLocation = null;
            break;
          case title:
            currentLocation.setName(currentValue.toString());
            break;
          case lat:
            currentLocation.setLatitude(currentValue.toString());
            break;
          case lng:
            currentLocation.setLongitude(currentValue.toString());
            break;
          case distance:
            currentLocation.setDistance(Double.parseDouble(currentValue
                .toString()));
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
    /** Name of element containing an entry. */
    entry,
    /** Name of element giving the title. */
    title,
    /** Element specifying latitude. */
    lat,
    /** Element specifying longitude. */
    lng,
    /** Element specifying the distance from the requested latitude/longitude. */
    distance
  }
}
