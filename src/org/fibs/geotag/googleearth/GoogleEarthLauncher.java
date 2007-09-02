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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.fibs.geotag.Geotag;
import org.fibs.geotag.Messages;
import org.fibs.geotag.data.ImageInfo;

import com.google.earth.kml._2.AltitudeModeEnum;
import com.google.earth.kml._2.FolderType;
import com.google.earth.kml._2.KmlType;
import com.google.earth.kml._2.LinkType;
import com.google.earth.kml._2.LookAtType;
import com.google.earth.kml._2.NetworkLinkType;
import com.google.earth.kml._2.ObjectFactory;
import com.google.earth.kml._2.RefreshModeEnum;
import com.google.earth.kml._2.ViewRefreshModeEnum;

/**
 * Bare Bones Googleearth Launch for Java<br>
 * Based on com.centerkey.utils.BareBonesBrowserLaunch
 * 
 * @author Dem Pilafian
 * @author Andreas Schneider
 * @version 1.5, December 10, 2005
 */
public class GoogleEarthLauncher {

  /** Error message to display */
  private static final String errorMessage = Messages
      .getString("Launcher.ErrorAttemptingToLaunch"); //$NON-NLS-1$

  /** The last image launched into Google Earth will receive updates */
  private static ImageInfo lastImageLauched;

  /**
   * Launch Google Earth for a given image info
   * 
   * @param imageInfo
   */
  public static void launch(ImageInfo imageInfo) {
    lastImageLauched = imageInfo;
    double latitude = 51.0 + 28.0 / 60 + 38.0 / 3600;
    double longitude = 0.0;
    double altitude = 100;
    if (imageInfo.hasLocation()) {
      try {
        latitude = Double.parseDouble(imageInfo.getGPSLatitude());
        longitude = Double.parseDouble(imageInfo.getGPSLongitude());
        altitude = Double.parseDouble(imageInfo.getGPSAltitude());
      } catch (NumberFormatException e) {
        e.printStackTrace();
      }
    }
    // first we create a KML file
    // get the correct context
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(KmlType.class
          .getPackage().getName());
      // we need a factory to create objects
      ObjectFactory factory = new ObjectFactory();
      // the KML element at the top
      KmlType kml = factory.createKmlType();
      // it contains one Folder
      FolderType folder = factory.createFolderType();
      folder.setName(Geotag.NAME + ' '
          + Messages.getString("GoogleEarthLauncher.Folder")); //$NON-NLS-1$
      folder.setOpen(new Boolean(true));
      LookAtType lookAt = factory.createLookAtType();
      lookAt.setLatitude(new Double(latitude));
      lookAt.setLongitude(new Double(longitude));
      lookAt.setAltitude(new Double(altitude));
      lookAt.setAltitudeMode(AltitudeModeEnum.ABSOLUTE);
      // add an extra 100 meters altitude
      lookAt.setRange(new Double(0));
      folder.setLookAt(lookAt);
      // The folder might contain another folder with images
      // FolderType subFolder = factory.createFolderType();
      // subFolder.setName("Images");
      // subFolder.setOpen(new Boolean(false));
      // //sub folder contains a place mark
      // PlacemarkType placeMark = factory.createPlacemarkType();
      // // the place mark is not visible
      // placeMark.setVisibility(new Boolean(false));
      // placeMark.setName("Invisible place");
      // StyleType style = factory.createStyleType();
      // IconStyleType iconStyle = factory.createIconStyleType();
      // IconStyleIconType icon = factory.createIconStyleIconType();
      // iconStyle.setIcon(icon);
      // style.setIconStyle(iconStyle);
      // placeMark.getStyleSelector().add(factory.createStyle(style));
      //      
      // PointType point = factory.createPointType();
      // List<String> coordinates = point.getCoordinates();
      //
      // coordinates.add(longitude + "," + latitude); //$NON-NLS-1$
      // placeMark.setGeometry(factory.createPoint(point));
      //      
      // subFolder.getFeature().add(factory.createPlacemark(placeMark));
      //      
      // folder.getFeature().add(factory.createFolder(subFolder));

      // The folder also contains a network link
      NetworkLinkType networkLink = factory.createNetworkLinkType();
      networkLink.setName(Geotag.NAME + ' '
          + Messages.getString("GoogleEarthLauncher.Link")); //$NON-NLS-1$
      networkLink.setOpen(new Boolean(true));
      networkLink.setVisibility(new Boolean(true));
      LinkType link = factory.createLinkType();
      link.setRefreshMode(RefreshModeEnum.ON_CHANGE);
      // link.setRefreshInterval(new Float(5));
      link.setHref("http://127.0.0.1:4321/kml/geotag.kml"); //$NON-NLS-1$
      link.setViewRefreshMode(ViewRefreshModeEnum.ON_STOP);
      link.setViewRefreshTime(new Float(0));
      link.setViewFormat("longitude=[lookatLon]&latitude=[lookatLat]"); //$NON-NLS-1$
      networkLink.setLink(link);
      folder.getFeature().add(factory.createNetworkLink(networkLink));
      kml.setFeature(factory.createFolder(folder));
      // That's the entire KML created, now we need a file to write it to
      File file = new File(Geotag.NAME + ".kml"); //$NON-NLS-1$
      // a better choice is a temporary file
      try {
        file = File.createTempFile(Geotag.NAME, ".kml"); //$NON-NLS-1$
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        OutputStream outputStream = new FileOutputStream(file);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE); //$NON-NLS-1$
        // finally we write the whole kml to the output stream
        marshaller.marshal(factory.createKml(kml), outputStream);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      openKmlFile(file);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }

  /**
   * Opens the specified KML file in Google Earth
   * 
   * @param file
   *          The KML file
   */
  public static void openKmlFile(File file) {
    String osName = System.getProperty("os.name"); //$NON-NLS-1$
    try {
      if (osName.startsWith("Mac OS")) { //$NON-NLS-1$
        Class<?> fileMgr = Class.forName("com.apple.eio.FileManager"); //$NON-NLS-1$
        Method openURL = fileMgr.getDeclaredMethod("openURL", //$NON-NLS-1$
            new Class[] { String.class });
        openURL.invoke(null, new Object[] { file.getPath() });
      } else if (osName.startsWith("Windows")) //$NON-NLS-1$
        Runtime.getRuntime().exec(
            "rundll32 url.dll,FileProtocolHandler " + file.getPath()); //$NON-NLS-1$
      else { // assume Unix or Linux
        String executable = "googleearth"; //$NON-NLS-1$
        if (Runtime.getRuntime().exec(new String[] { "which", executable }) //$NON-NLS-1$
            .waitFor() != 0) {
          throw new Exception(Messages.getString("Launcher.CouldNotFind")); //$NON-NLS-1$
        }
        Runtime.getRuntime().exec(new String[] { executable, file.getPath() });
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, errorMessage + ":\n" //$NON-NLS-1$
          + e.getLocalizedMessage());
    }
  }

  /**
   * @return the lastImageLauched
   */
  public static ImageInfo getLastImageLauched() {
    return lastImageLauched;
  }

}
