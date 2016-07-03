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

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.fibs.geotag.Geotag;
import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.util.Airy;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.earth.kml._2.DocumentType;
import com.google.earth.kml._2.FeatureType;
import com.google.earth.kml._2.FolderType;
import com.google.earth.kml._2.IconStyleIconType;
import com.google.earth.kml._2.IconStyleType;
import com.google.earth.kml._2.KmlType;
import com.google.earth.kml._2.ObjectFactory;
import com.google.earth.kml._2.PlacemarkType;
import com.google.earth.kml._2.PointType;
import com.google.earth.kml._2.StyleSelectorType;
import com.google.earth.kml._2.StyleType;
import com.google.earth.kml._2.TimeStampType;

/**
 * A class for exporting to KML/KMZ files.
 * 
 * @author Andreas Schneider
 * 
 */
public class KmlExporter {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(KmlExporter.class);

  /** We need the style name more than once. */
  private static final String STYLE = "Photo"; //$NON-NLS-1$

  /** The name of the images folder displayed in Google Earth. */
  private static final String KML_FOLDER = i18n
      .tr("Images"); //$NON-NLS-1$

  /** The name of the images folder within KMZ (zip) files. */
  private static final String KMZ_IMAGE_FOLDER = "images"; //$NON-NLS-1$

  /** The {@link ImageInfo}s exported. */
  private List<ImageInfo> imageInfos;

  /**
   * Constructor.
   * 
   * @param imageInfos
   *          ImageInfos to export
   */
  public KmlExporter(List<ImageInfo> imageInfos) {
    this.imageInfos = imageInfos;
  }

  /**
   * Write KML to an output stream.
   * 
   * @param outputStream
   * @param kmz
   *          True if a KMZ file is written, false if KML
   */
  public void write(OutputStream outputStream, boolean kmz) {
    try {
      // get the correct context
      JAXBContext jaxbContext = JAXBContext.newInstance(KmlType.class
          .getPackage().getName());
      // we need a factory to create objects
      ObjectFactory factory = new ObjectFactory();
      // the KML element at the top
      KmlType kml = factory.createKmlType();
      // contains one Document
      DocumentType document = factory.createDocumentType();
      document.setName(Geotag.NAME);
      // with several features
      List<JAXBElement<? extends FeatureType>> documentFeatures = document
          .getFeature();
      // first we define a style
      StyleType style = factory.createStyleType();
      style.setId(STYLE);
      IconStyleType iconStyle = factory.createIconStyleType();

      IconStyleIconType icon = factory.createIconStyleIconType();
      String iconURL = Settings.get(SETTING.KML_ICON_URL, Settings.KML_DEFAULT_ICON_URL);
      if ("".equals(iconURL)) { //$NON-NLS-1$
        iconURL = Settings.KML_DEFAULT_ICON_URL;
        Settings.put(SETTING.KML_ICON_URL, iconURL);
        Settings.flush();
      }
      icon.setHref(iconURL);
      iconStyle.setIcon(icon);
      style.setIconStyle(iconStyle);

      List<JAXBElement<? extends StyleSelectorType>> styleSelectors = document
          .getStyleSelector();
      styleSelectors.add(factory.createStyle(style));
      // now a folder for the placemarks
      FolderType folder = factory.createFolderType();
      folder.setName(KML_FOLDER);
      folder.setOpen(Boolean.valueOf(false));
      List<JAXBElement<? extends FeatureType>> folderFeatures = folder
          .getFeature();
      for (ImageInfo imageInfo : imageInfos) {
        // default image path is the file system path of the image
        String imagePath = imageInfo.getPath();
        // user can specify a different directory via settings
        String imageDirectory = Settings.get(SETTING.KML_IMAGE_PATH, ""); //$NON-NLS-1$
        if (imageDirectory.length() > 0) {
          imagePath = imageDirectory
          // make sure a file separator is added if necessary
              + (imageDirectory.endsWith(File.separator) ? "" : File.separator) //$NON-NLS-1$
              + imageInfo.getName();
        }
        PlacemarkType placemark = factory.createPlacemarkType();
        placemark.setName(imageInfo.getName());
        TimeStampType timeStamp = factory.createTimeStampType();
        Date date = imageInfo.getTimeGMT().getTime();
        // Is there a better way to get the ISO format?
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //$NON-NLS-1$
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
        timeStamp.setWhen(dateFormat.format(date));
        placemark.setTimePrimitive(factory.createTimeStamp(timeStamp));
        StringBuilder description = new StringBuilder();
        String header = Settings.get(SETTING.KML_DESCRIPTION_HEADER, ""); //$NON-NLS-1$
        if (header != null && header.length() > 0) {
          description.append(header);
        }
        // clicking on the image opens the image via imagePath
        description.append("<a href=\""); //$NON-NLS-1$
        description.append(imagePath);
        // the image has two possible sources:
        description.append("\"><img src=\""); //$NON-NLS-1$
        if (kmz && Settings.get(SETTING.KMZ_STORE_THUMBNAILS, false)) {
          // image is stored in the KMZ file
          description.append(KMZ_IMAGE_FOLDER);
          description.append(File.separator);
          description.append(imageInfo.getName());
          description
              .append("\" width=\"" + imageInfo.getThumbnail().getIconWidth() + "\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
          // the imagePath is used
          description.append(imagePath);
                    description.append("\" width=\"" ); //$NON-NLS-1$
                    description.append(Settings.get(SETTING.THUMBNAIL_SIZE, Settings.DEFAULT_THUMBNAIL_SIZE));
                    description.append("\"/>"); //$NON-NLS-1$
        }
        description.append("</a>"); //$NON-NLS-1$
        String userComment = imageInfo.getUserComment();
        if (userComment != null && ! "".equals(userComment)) { //$NON-NLS-1$
          // We have a user comment
          description.append("<br>").append(userComment); //$NON-NLS-1$
        }
        String locString = getLocationString(imageInfo);
        if (locString != null) {
          description.append("<br>").append(locString); //$NON-NLS-1$
        }
        String footer = Settings.get(SETTING.KML_DESCRIPTION_FOOTER, ""); //$NON-NLS-1$
        if (footer != null && footer.length() > 0) {
          description.append(footer);
        }
        placemark.setDescription(description.toString());
        PointType point = factory.createPointType();
        double latitude = Airy.LATITUDE;
        double longitude = Airy.LONGITUDE;
        try {
          latitude = Double.parseDouble(imageInfo.getGpsLatitude());
          longitude = Double.parseDouble(imageInfo.getGpsLongitude());
        } catch (NumberFormatException e) {
          //
        }
        List<String> coordinates = point.getCoordinates();
        coordinates.add(longitude + "," + latitude); //$NON-NLS-1$
        placemark.setGeometry(factory.createPoint(point));
        placemark.setStyleUrl('#' + STYLE);
        folderFeatures.add(factory.createPlacemark(placemark));
      }
      documentFeatures.add(factory.createFolder(folder));

      kml.setFeature(factory.createDocument(document));
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE); //$NON-NLS-1$
      // finally we write the whole kml to the output stream
      marshaller.marshal(factory.createKml(kml), outputStream);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }

    /**
     * Builds a location description if possible
     * @param imageInfo
     * @return The location string or null
     */
    private static String getLocationString( ImageInfo imageInfo ) {
        StringBuilder location = new StringBuilder();
        addLocationItem( location, imageInfo.getLocationName() );
        addLocationItem( location, imageInfo.getCityName() );
        addLocationItem( location, imageInfo.getProvinceName() );
        addLocationItem( location, imageInfo.getCountryName() );
        if ( location.length() > 0 ) {
          return location.toString();
        }
        return null;
      }
    
      /**
       * Adds a location item to a StringBuilder. Also makes
       * sure to insert commas where appropriate.
       * @param builder
       * @param item
       */
      private static void addLocationItem( StringBuilder builder, String item ) {
        if ( item == null )
          return;
    
        if ( builder.length() > 0 ) {
          builder.append(", "); //$NON-NLS-1$
        }
        builder.append( item );
      }
    

  /**
   * Write image data to KML file.
   * 
   * @param file
   * @throws IOException
   */
  public void writeKml(File file) throws IOException {
    FileOutputStream outputStream = new FileOutputStream(file);
    write(outputStream, false);
    outputStream.close();
  }

  /**
   * Write image data to KMZ file.
   * 
   * @param file
   * @throws IOException
   */
  public void writeKmz(File file) throws IOException {
    ZipEntry zipEntry = new ZipEntry(Geotag.NAME + ".kml"); //$NON-NLS-1$
    ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(
        file));
    zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);
    zipOutputStream.putNextEntry(zipEntry);
    write(zipOutputStream, true);
    zipOutputStream.closeEntry();
    if (Settings.get(SETTING.KMZ_STORE_THUMBNAILS, false)) {
      // store thumbnails in the KMZ file
      for (ImageInfo imageInfo : imageInfos) {
        ImageIcon thumbnail = imageInfo.getThumbnail();
        if (thumbnail != null) {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          try {
            ImageIO.write((RenderedImage) thumbnail.getImage(),
                "jpg", byteArrayOutputStream); //$NON-NLS-1$
            // Do not use File.separator here 
            // Googleearth only likes forward slashes
            zipEntry = new ZipEntry(KMZ_IMAGE_FOLDER + '/'
                + imageInfo.getName());
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(byteArrayOutputStream.toByteArray());
            zipOutputStream.closeEntry();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
    zipOutputStream.close();
  }

  /**
   * Write image data to a file. Determines KML or KMZ by looking at the file
   * extension.
   * 
   * @param file
   * @throws IOException
   */
  public void write(File file) throws IOException {
    if (GoogleearthFileFilter.isKmlFile(file)) {
      writeKml(file);
    } else if (GoogleearthFileFilter.isKmzFile(file)) {
      writeKmz(file);
    } else {
      throw new IOException("Neither KML nor KMZ " + file.getPath()); //$NON-NLS-1$
    }
  }
}
