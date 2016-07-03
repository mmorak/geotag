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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.fibs.geotag.Geotag;

import com.topografix.gpx._1._0.Gpx;

/**
 * A class writing GPX files.
 * 
 * @author Andreas Schneider
 * 
 */
public class GpxWriter implements TrackWriter {
  
  /**
   * @see org.fibs.geotag.track.TrackWriter#write(com.topografix.gpx._1._0.Gpx, java.io.File)
   */
  @Override
  public void write(Gpx gpx, File file) throws Exception {
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    write(gpx, fileOutputStream);
    fileOutputStream.close();
  }
  /**
   * @see org.fibs.geotag.track.TrackWriter#write(com.topografix.gpx._1._0.Gpx, java.io.OutputStream)
   */
  @Override
  public void write(Gpx gpx, OutputStream outputStream) throws Exception {
    JAXBContext jaxbContext = JAXBContext.newInstance(Gpx.class.getPackage()
        .getName());
    Marshaller marshaller = jaxbContext.createMarshaller();
    marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE); //$NON-NLS-1$
    marshaller
        .setProperty("jaxb.schemaLocation", //$NON-NLS-1$
            "http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd"); //$NON-NLS-1$
    gpx.setCreator(Geotag.NAME + ' ' + Geotag.WEBSITE);
    try {
      DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
      XMLGregorianCalendar now = datatypeFactory
          .newXMLGregorianCalendar(new GregorianCalendar());
      gpx.setTime(now);
    } catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }
    marshaller.marshal(gpx, outputStream);
    outputStream.close();
  }

}
