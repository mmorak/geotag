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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.topografix.gpx._1._0.Gpx;

/**
 * A class reading GPX files.
 * 
 * @author Andreas Schneider
 * 
 */
public final class GpxReader {
  
  /**
   * hide constructor.
   */
  private GpxReader() {
    // hide constructor
  }
  
  /**
   * @param file
   *          The file to be read
   * @return the data contained as a {@link Gpx} object
   */
  public static Gpx read(File file) {
    try {
      return read(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Read in a gpx from an InputStream.
   * 
   * @param inputStream
   *          The InputStream to be read
   * @return The data contained as a {@link Gpx} object
   */
  public static Gpx read(InputStream inputStream) {
    Gpx gpx = null;
    try {
      // create a JAXB context for GPX files
      JAXBContext jaxbContext = JAXBContext
          .newInstance("com.topografix.gpx._1._0"); //$NON-NLS-1$
      // create an Unmarshaller for the context
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      // unmarshall (read) the file
      gpx = (Gpx) unmarshaller.unmarshal(inputStream);
      // That's all.
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return gpx;
  }

}
