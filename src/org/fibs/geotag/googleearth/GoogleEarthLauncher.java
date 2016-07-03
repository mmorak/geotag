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
import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.util.Airy;
import org.fibs.geotag.util.ClassLoaderUtil;
import org.fibs.geotag.util.OperatingSystem;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
 * Bare Bones Googleearth Launch for Java.<br>
 * Based on com.centerkey.utils.BareBonesBrowserLaunch
 * 
 * @author Dem Pilafian
 * @author Andreas Schneider
 * @version 1.5, December 10, 2005
 */
public final class GoogleEarthLauncher {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(GoogleEarthLauncher.class);

  /**
   * hide constructor.
   */
  private GoogleEarthLauncher() {
    // hide constructor
  }

  /** Error message to display. */
  private static final String ERROR_MESSAGE = i18n
      .tr("Error attempting to launch Google Earth"); //$NON-NLS-1$

  /** The last image launched into Google Earth will receive updates. */
  private static ImageInfo lastImageLauched;

  /**
   * Launch Google Earth for a given image info.
   * 
   * @param imageInfo
   */
  public static void launch(ImageInfo imageInfo) {
    lastImageLauched = imageInfo;
    double latitude = Airy.LATITUDE;
    double longitude = Airy.LONGITUDE;
    final double defaultAltitude = 100;
    double altitude = defaultAltitude;
    if (imageInfo.hasLocation()) {
      try {
        latitude = Double.parseDouble(imageInfo.getGpsLatitude());
        longitude = Double.parseDouble(imageInfo.getGpsLongitude());
        altitude = Double.parseDouble(imageInfo.getGpsAltitude());
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
          + i18n.tr("Folder")); //$NON-NLS-1$
      folder.setOpen(Boolean.valueOf(true));
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
          + i18n.tr("Link")); //$NON-NLS-1$
      networkLink.setOpen(Boolean.valueOf(true));
      networkLink.setVisibility(Boolean.valueOf(true));
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
        file.deleteOnExit();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        OutputStream outputStream = new FileOutputStream(file);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE); //$NON-NLS-1$
        // finally we write the whole kml to the output stream
        marshaller.marshal(factory.createKml(kml), outputStream);
        outputStream.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      openKmlFile(file);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }

  /**
   * Opens the specified KML file in Google Earth.
   * 
   * @param file
   *          The KML file
   */
  public static void openKmlFile(File file) {
    try {
      if (OperatingSystem.isMacOS()) {
        Class<?> fileMgr = ClassLoaderUtil.getClassForName("com.apple.eio.FileManager"); //$NON-NLS-1$
        Method openURL = fileMgr.getDeclaredMethod("openURL", //$NON-NLS-1$
            new Class[] { String.class });
        openURL.invoke(null, new Object[] { file.getPath() });
      } else if (OperatingSystem.isWindows()) {
        Runtime.getRuntime().exec(
            "rundll32 url.dll,FileProtocolHandler " + file.getPath()); //$NON-NLS-1$
      } else { // assume Unix or Linux
        String executable = Settings.get(SETTING.GOOGLE_EARTH_PATH, "googleearth"); //$NON-NLS-1$
        if (Runtime.getRuntime().exec(new String[] { "which", executable }) //$NON-NLS-1$
            .waitFor() != 0) {
          throw new Exception(String.format(i18n.tr("Could not find '%1$s' on $PATH"), executable)); //$NON-NLS-1$
        }
        Runtime.getRuntime().exec(new String[] { executable, file.getPath() });
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, ERROR_MESSAGE + ":\n" //$NON-NLS-1$
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
