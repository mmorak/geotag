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

package org.fibs.geotag.track;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.topografix.gpx._1._1.GpxType;
import com.topografix.gpx._1._1.TrkType;
import com.topografix.gpx._1._1.TrksegType;
import com.topografix.gpx._1._1.WptType;
import com.topografix.gpx._1._0.Gpx;
import com.topografix.gpx._1._0.ObjectFactory;
import com.topografix.gpx._1._0.Gpx.Trk;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg.Trkpt;

/**
 * A class reading GPX 1.1 files.
 * 
 * @author Andreas Schneider
 * 
 */
public final class Gpx1_1Reader {

  /**
   * hide constructor.
   */
  private Gpx1_1Reader() {
    // hide constructor
  }

  /**
   * @param file
   *          The file to be read
   * @return the data contained as a {@link Gpx} object
   */
  public static Gpx read(File file) {
    try {
      GpxType gpxType = read(new FileInputStream(file));
      // now convert the read data to GPX 1.0 format
      return convert(gpxType);
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
  @SuppressWarnings("unchecked")
  public static GpxType read(InputStream inputStream) {
    GpxType gpx = null;
    try {
      // create a JAXB context for GPX files
      JAXBContext jaxbContext = JAXBContext
          .newInstance("com.topografix.gpx._1._1"); //$NON-NLS-1$
      // create an Unmarshaller for the context
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      // unmarshall (read) the file
      JAXBElement<GpxType> element = (JAXBElement<GpxType>) unmarshaller
          .unmarshal(inputStream);
      gpx = element.getValue();
      // That's all.
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return gpx;
  }

  /**
   * Convert GPX 1.1 data to GPX 1.0
   * 
   * @param gpxType
   *          the data read from a GPX 1.1 file
   * @return The data in GPX 1.0 format
   */
  private static Gpx convert(GpxType gpxType) {
    ObjectFactory objectFactory1_0 = new ObjectFactory();
    Gpx gpx = objectFactory1_0.createGpx();
    List<Trk> tracks1_0 = gpx.getTrk();
    for (TrkType track1_1 : gpxType.getTrk()) {
      Trk track1_0 = objectFactory1_0.createGpxTrk();
      tracks1_0.add(track1_0);
      track1_0.setName(track1_1.getName());
      track1_0.setNumber(track1_1.getNumber());
      List<Trkseg> segments1_0 = track1_0.getTrkseg();
      for (TrksegType segment1_1 : track1_1.getTrkseg()) {
        Trkseg segment1_0 = objectFactory1_0.createGpxTrkTrkseg();
        segments1_0.add(segment1_0);
        List<Trkpt> trackpoints1_0 = segment1_0.getTrkpt();
        for (WptType trkpt1_1 : segment1_1.getTrkpt()) {
          Trkpt trkpt1_0 = objectFactory1_0.createGpxTrkTrksegTrkpt();
          trackpoints1_0.add(trkpt1_0);
          trkpt1_0.setCmt(trkpt1_1.getCmt());
          trkpt1_0.setDesc(trkpt1_1.getDesc());
          trkpt1_0.setLat(trkpt1_1.getLat());
          trkpt1_0.setLon(trkpt1_1.getLon());
          trkpt1_0.setEle(trkpt1_1.getEle());
          trkpt1_0.setName(trkpt1_1.getName());
          trkpt1_0.setTime(trkpt1_1.getTime());
        }
      }
    }
    System.out.println(gpx.getTrk().size());
    return gpx;
  }

}
