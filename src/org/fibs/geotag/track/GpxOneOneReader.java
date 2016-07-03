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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.topografix.gpx._1._0.Gpx;
import com.topografix.gpx._1._0.Gpx.Trk;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg.Trkpt;
import com.topografix.gpx._1._0.ObjectFactory;
import com.topografix.gpx._1._1.GpxType;
import com.topografix.gpx._1._1.TrkType;
import com.topografix.gpx._1._1.TrksegType;
import com.topografix.gpx._1._1.WptType;

/**
 * A class reading GPX 1.1 files.
 * 
 * @author Andreas Schneider
 * 
 */
public final class GpxOneOneReader {

  /**
   * hide constructor.
   */
  private GpxOneOneReader() {
    // hide constructor
  }

  /**
   * @param file
   *          The file to be read
   * @return the data contained as a {@link Gpx} object
   */
  public static Gpx read(File file) {
    try {
      FileInputStream fileInputStream = new FileInputStream(file);
      GpxType gpxType = read(fileInputStream);
      // now convert the read data to GPX 1.0 format
      Gpx gpx = convert(gpxType);
      fileInputStream.close();
      return gpx;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
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
   * Convert GPX 1.1 data to GPX 1.0.
   * 
   * @param gpxType
   *          the data read from a GPX 1.1 file
   * @return The data in GPX 1.0 format
   */
  private static Gpx convert(GpxType gpxType) {
    ObjectFactory objectFactoryOneZero = new ObjectFactory();
    Gpx gpx = objectFactoryOneZero.createGpx();
    List<Trk> tracksOneZero = gpx.getTrk();
    for (TrkType trackOneOne : gpxType.getTrk()) {
      Trk trackOneZero = objectFactoryOneZero.createGpxTrk();
      tracksOneZero.add(trackOneZero);
      trackOneZero.setName(trackOneOne.getName());
      trackOneZero.setNumber(trackOneOne.getNumber());
      List<Trkseg> segmentsOneZero = trackOneZero.getTrkseg();
      for (TrksegType segmentOneOne : trackOneOne.getTrkseg()) {
        Trkseg segmentOneZero = objectFactoryOneZero.createGpxTrkTrkseg();
        segmentsOneZero.add(segmentOneZero);
        List<Trkpt> trackpointsOneZero = segmentOneZero.getTrkpt();
        for (WptType trkptOneOne : segmentOneOne.getTrkpt()) {
          Trkpt trkptOneZero = objectFactoryOneZero.createGpxTrkTrksegTrkpt();
          trackpointsOneZero.add(trkptOneZero);
          trkptOneZero.setCmt(trkptOneOne.getCmt());
          trkptOneZero.setDesc(trkptOneOne.getDesc());
          trkptOneZero.setLat(trkptOneOne.getLat());
          trkptOneZero.setLon(trkptOneOne.getLon());
          trkptOneZero.setEle(trkptOneOne.getEle());
          trkptOneZero.setName(trkptOneOne.getName());
          trkptOneZero.setTime(trkptOneOne.getTime());
        }
      }
    }
    System.out.println(gpx.getTrk().size());
    return gpx;
  }

}
