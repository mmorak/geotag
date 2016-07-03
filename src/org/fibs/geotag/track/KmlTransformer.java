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

import java.util.List;

import org.fibs.geotag.Geotag;

import com.google.earth.kml._2.DocumentType;
import com.google.earth.kml._2.FolderType;
import com.google.earth.kml._2.KmlType;
import com.google.earth.kml._2.LineStringType;
import com.google.earth.kml._2.LineStyleType;
import com.google.earth.kml._2.ObjectFactory;
import com.google.earth.kml._2.PlacemarkType;
import com.google.earth.kml._2.StyleType;
import com.topografix.gpx._1._0.Gpx;
import com.topografix.gpx._1._0.Gpx.Trk;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg.Trkpt;

/**
 * A class for GPX->KML transformations.
 * 
 * @author Andreas Schneider
 * 
 */
public class KmlTransformer {
  /**
   * Take a GPX object and generate a KML representation of it.
   * 
   * @param gpx The Gpx object to transform
   * @return The KmlType object
   */
  public KmlType transform(Gpx gpx) {
    ObjectFactory objectFactory = new ObjectFactory();
    KmlType kmlType = objectFactory.createKmlType();
    DocumentType document = objectFactory.createDocumentType();
    document.setName(Geotag.NAME);
    StyleType style = objectFactory.createStyleType();
    style.setId("linestyle"); //$NON-NLS-1$
    LineStyleType lineStyle = objectFactory.createLineStyleType();
    final byte[] colour = new byte[] { 0x64, (byte) 0xee, (byte) 0xee, 0x17 };
    lineStyle.setColor(colour);
    final Float linewidth = new Float(6);
    lineStyle.setWidth(linewidth);
    style.setLineStyle(lineStyle);
    document.getStyleSelector().add(objectFactory.createStyle(style));
    for (Trk track : gpx.getTrk()) {
      FolderType tracksFolder = objectFactory.createFolderType();
      tracksFolder.setName(track.getName());
      PlacemarkType placemark = objectFactory.createPlacemarkType();
      placemark.setName("Path"); //$NON-NLS-1$
      placemark.setStyleUrl("#linestyle"); //$NON-NLS-1$
      LineStringType lineString = objectFactory.createLineStringType();
      lineString.setTessellate(Boolean.valueOf(true));
      List<String> coordinates = lineString.getCoordinates();
      for (Trkseg segment : track.getTrkseg()) {
        for (Trkpt trackPoint : segment.getTrkpt()) {
          String string = trackPoint.getLon() + "," + trackPoint.getLat() + "," //$NON-NLS-1$ //$NON-NLS-2$
              + trackPoint.getEle();
          coordinates.add(string);
        }
      }
      placemark.setGeometry(objectFactory.createLineString(lineString));
      tracksFolder.getFeature().add(objectFactory.createPlacemark(placemark));
      document.getFeature().add(objectFactory.createFolder(tracksFolder));
    }
    kmlType.setFeature(objectFactory.createDocument(document));
    return kmlType;
  }

}
