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

package org.fibs.geotag.geonames;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A class sending a request to genames.org and parsing the response
 * 
 * @author Andreas Schneider
 * 
 */
public class WikipediaHandler extends DefaultHandler {

  
  /***/
  @SuppressWarnings("unused")
  private ELEMENTS lastElementStarted = null;
  /***/
  private StringBuilder currentValue = null;

  /**
   * @param latitude
   * @param longitude
   */
  public WikipediaHandler(String latitude, String longitude) {
    try {
      // Create a SAX 2 parser
      XMLReader xr = XMLReaderFactory.createXMLReader();
      // This object is the context handler
      xr.setContentHandler(this);
      // Build the request
      URL versionFile = new URL(
          "http://ws.geonames.org/findNearbyWikipedia?lat="+latitude+"&lng="+longitude); //$NON-NLS-1$ //$NON-NLS-2$
      InputStream inputStream = versionFile.openStream();
      xr.parse(new InputSource(inputStream));
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
   * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(String namespaceURI, String localName, String qName,
      Attributes attr) throws SAXException {
    // we only look at the localName.. geonames.org doesn't use attributes
    currentValue = null;
    for (ELEMENTS tag : ELEMENTS.values()) {
      if (tag.toString().equals(localName)) {
        lastElementStarted = tag;
        currentValue = new StringBuilder();
        System.out.println(tag);
        break;
      }
    }
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(String namespaceURI, String localName, String qName)
      throws SAXException {
    if (currentValue != null) {
      System.out.println(currentValue.toString());
      currentValue = null;
    }
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
   * The XML elements we are interested in.
   * Specified exactly as the appear in the XML
   */
  enum ELEMENTS  {
    /** Entries are enclosed in a 'geonames' element */
    geonames,
    /** Name of element containing an entry */
    entry,
    /** Name of element specifying the language */
    lang,
    /** Name of element giving the title */
    title,
    /** Element specifying latitude */
    lat,
    /** Element specifying longitude */
    lng,
    /** Element giving the Wikipedia URL */
    wikipediaUrl,
    /** Element specifying the distance from the requested latitude/longitude */
    distance
  }
}