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

package org.fibs.geotag.gui;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.fibs.geotag.Geotag;
import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.dcraw.Dcraw;
import org.fibs.geotag.exif.Exiftool;
import org.fibs.geotag.gpsbabel.GPSBabel;
import org.fibs.geotag.image.FileTypes;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.track.TrackStore;
import org.fibs.geotag.util.BrowserLauncher;
import org.fibs.geotag.util.LocaleUtil;
import org.fibs.geotag.util.Util;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A class that tries to generate suggestions what to do next.
 * 
 * @author Andreas Schneider
 * 
 */
public final class WhatNext {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(WhatNext.class);
  
  /**
   * hide constructor.
   */
  private WhatNext() {
    // hide constructor
  }

  /** the maximum line length. */
  private static final int MAX_LINE_LENGTH = 60;
  
  /** Either "->" or the correct Unicode character */
  private static final String RIGHT_ARROW = "\u2192"; //$NON-NLS-1$

  /**
   * This method tries to see where in the work flow we are and gives the use a
   * suggestion, what to do next.
   * 
   * @param parentComponent
   *          The parent component
   * @param tableModel
   *          The table model
   */
  public static void helpWhatNext(Component parentComponent,
      ImagesTableModel tableModel) {
    
    boolean exiftoolAvailable = Exiftool.isAvailable();
    boolean gpsbabelAvailable = GPSBabel.isAvailable();
    boolean dcrawAvailable = Dcraw.isAvailable();
    boolean imagesAvailable = (tableModel.getRowCount() > 0);
    boolean tracksAvailable = TrackStore.getTrackStore().hasTracks();
    boolean imagesWithNewLocationAvailable = false;
    for (int row = 0; row < tableModel.getRowCount(); row++) {
      if (tableModel.getImageInfo(row).hasNewLocation()) {
        imagesWithNewLocationAvailable = true;
        break;
      }
    }
    boolean imagesWithLocationAvailable = false;
    for (int row = 0; row < tableModel.getRowCount(); row++) {
      if (tableModel.getImageInfo(row).hasLocation()) {
        imagesWithLocationAvailable = true;
        break;
      }
    }
    boolean rawImagesAvailable = false;
    for (int row = 0; row < tableModel.getRowCount(); row++) {
      File file = new File(tableModel.getImageInfo(row).getPath());
      FileTypes fileType = FileTypes.fileType(file);
      if (fileType == FileTypes.RAW_READ_WRITE
          || fileType == FileTypes.RAW_READ_ONLY) {
        rawImagesAvailable = true;
        break;
      }
    }
    boolean gapsAvailable = false;
    if (imagesWithNewLocationAvailable) {
      for (int row = 0; row < tableModel.getRowCount(); row++) {
        if (!tableModel.getImageInfo(row).hasLocation()) {
          gapsAvailable = true;
          break;
        }
      }
    }
    boolean locationNamesFound = false;
    for (int row = 0; row < tableModel.getRowCount(); row++) {
      if (tableModel.getImageInfo(row).hasLocationName()) {
        locationNamesFound = true;
        break;
      }
    }
    boolean translationAvailable = LocaleUtil.translationAvailable(Locale.getDefault());
    List<String> suggestions = new ArrayList<String>();
    suggestFindingExiftool(exiftoolAvailable, suggestions);
    suggestLoadingImages(imagesAvailable, suggestions);
    suggestLoadingTracks(gpsbabelAvailable, tracksAvailable, suggestions);
    suggestCheckingTimes(imagesAvailable, suggestions);
    suggestShowOnMap(imagesAvailable, suggestions);
    suggestMatchingTracks(imagesAvailable, tracksAvailable, suggestions);
    suggestFillingGaps(gapsAvailable, suggestions);
    suggestSaving(imagesWithNewLocationAvailable, suggestions);
    suggestLocationNames(imagesWithLocationAvailable, locationNamesFound,
        suggestions);
    suggestFindingGPSBabel(gpsbabelAvailable, suggestions);
    suggestFindingDcraw(dcrawAvailable, rawImagesAvailable, suggestions);
    suggestRemovingImages(imagesAvailable, suggestions);
    suggestTranslation(translationAvailable, suggestions);

    // build the final message
    StringBuilder message = new StringBuilder("<html>"); //$NON-NLS-1$
    List<String> lines;
    for (String suggestion : suggestions) {
      lines = Util.splitHtmlString(suggestion, MAX_LINE_LENGTH);
      for (String line : lines) {
        message.append(line).append("<br>"); //$NON-NLS-1$
      }
      message.append("<br>"); //$NON-NLS-1$
    }
    message.append("</html"); //$NON-NLS-1$
    JEditorPane editorPane = new JEditorPane();
    editorPane.setEditable(false);
    editorPane.setContentType("text/html"); //$NON-NLS-1$
    editorPane.setText(message.toString());
    editorPane.setOpaque(false);
    editorPane.setFont(new JLabel().getFont());
    editorPane.addHyperlinkListener(new HyperlinkListener() {
      @Override
      public void hyperlinkUpdate(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          BrowserLauncher.openURL(Settings.get(SETTING.BROWSER, null), event
              .getURL().toString());
        }
      }
    });
    JOptionPane.showMessageDialog(parentComponent, editorPane, i18n
        .tr("What next?"), //$NON-NLS-1$
        JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Check if finding dcraw should be suggested and do so it if should.
   * 
   * @param dcrawAvailable
   * @param rawImagesAvailable
   * @param suggestions
   */
  private static void suggestFindingDcraw(boolean dcrawAvailable,
      boolean rawImagesAvailable, List<String> suggestions) {
    if (!dcrawAvailable && rawImagesAvailable) {
      String text = String.format(i18n
          .tr("The 'dcraw' program can't be found. It is needed to display previews of your RAW images. Select <b>%s %s %s</b> to find it."), i18n //$NON-NLS-1$
          .tr("File"), RIGHT_ARROW,i18n //$NON-NLS-1$
          .tr("Settings")); //$NON-NLS-1$
      suggestions.add(text);
    }
  }

  /**
   * Check if finding GPSBabel should be suggested and do so it if should.
   * 
   * @param gpsbabelAvailable
   * @param suggestions
   */
  private static void suggestFindingGPSBabel(boolean gpsbabelAvailable,
      List<String> suggestions) {
    if (!gpsbabelAvailable) {
      String text = String.format(i18n
          .tr("The 'GPSBabel' program can't be found. It is only needed if you want to load tracks directly from your GPS. Select <b>%s %s %s</b> to find it."), i18n //$NON-NLS-1$
          .tr("File"), RIGHT_ARROW, i18n //$NON-NLS-1$
          .tr("Settings")); //$NON-NLS-1$
      suggestions.add(text);
    }
  }

  /**
   * Check if finding Exiftool should be suggested and do so it if should.
   * 
   * @param exiftoolAvailable
   * @param suggestions
   */
  private static void suggestFindingExiftool(boolean exiftoolAvailable,
      List<String> suggestions) {
    if (!exiftoolAvailable) {
      String text = String.format(i18n
          .tr("The 'exiftool' program can't be found. It is needed to save coordinates  to your images. Select <b>%s %s %s</b> to find it."), i18n //$NON-NLS-1$
          .tr("File"), RIGHT_ARROW, i18n //$NON-NLS-1$
          .tr("Settings")); //$NON-NLS-1$
      suggestions.add(text);
    }
  }

  /**
   * Check if finding location names should be suggested and do so it if should.
   * 
   * @param imagesWithLocationAvailable
   * @param locationNamesFound
   * @param suggestions
   */
  private static void suggestLocationNames(boolean imagesWithLocationAvailable,
      boolean locationNamesFound, List<String> suggestions) {
    if (imagesWithLocationAvailable && !locationNamesFound) {
      String text = String.format(i18n
          .tr("Geotag can search for nearby place names for images that already have coordinates by using the <b>%1$s</b> menu."), //$NON-NLS-1$
          i18n.tr("Location names")); //$NON-NLS-1$
      suggestions.add(text);
    }
  }

  /**
   * Check if saving should be suggested and do so it if should.
   * 
   * @param imagesWithNewLocationAvailable
   * @param suggestions
   */
  private static void suggestSaving(boolean imagesWithNewLocationAvailable,
      List<String> suggestions) {
    if (imagesWithNewLocationAvailable) {
      String text = String.format(i18n
          .tr("You can save the locations of images listed in <b>bold</b> by using the <b>%1$s</b> menu. Note that this operation <b>cannot be un-done</b>!"), //$NON-NLS-1$
          i18n.tr("Save new locations")); //$NON-NLS-1$
      suggestions.add(text);
    }
  }

  /**
   * Check if filling gaps should be suggested and do so it if should.
   * 
   * @param gapsAvailable
   * @param suggestions
   */
  private static void suggestFillingGaps(boolean gapsAvailable,
      List<String> suggestions) {
    if (gapsAvailable) {
      String text = String.format(i18n
          .tr("Some images have coordinates and some don't. You can use the <b>%1$s</b> menu to make %2$s guess where images were taken."), //$NON-NLS-1$
          i18n.tr("Fill gaps"), //$NON-NLS-1$
          Geotag.NAME);
      suggestions.add(text);
    }
  }

  /**
   * Check if matching tracks to images should be suggested and do so it if
   * should.
   * 
   * @param imagesAvailable
   * @param tracksAvailable
   * @param suggestions
   */
  private static void suggestMatchingTracks(boolean imagesAvailable,
      boolean tracksAvailable, List<String> suggestions) {
    if (imagesAvailable && tracksAvailable) {
      String text = String.format(i18n
          .tr("If your happy with the time <b>%1$s</b> values, right click on any image and use the <b>%2$s</b> menu to match the GPS data to images."), //$NON-NLS-1$
          i18n.tr("Offset"), //$NON-NLS-1$
          i18n.tr("Find locations")); //$NON-NLS-1$
      suggestions.add(text);
    }
  }

  /**
   * Check if showing images on a map should be suggested and do so it if
   * should.
   * 
   * @param imagesAvailable
   * @param suggestions
   */
  private static void suggestShowOnMap(boolean imagesAvailable,
      List<String> suggestions) {
    if (imagesAvailable) {
      String text = String.format(i18n
          .tr("You can right click on an image and select <b>%1$s</b> to show the image location on a map in your browser and move the marker on the map to adjust the location."), //$NON-NLS-1$
          i18n.tr("Show on map")); //$NON-NLS-1$
      suggestions.add(text);
    }
  }

  /**
   * Check if checking images time stamps should be suggested and do so it if
   * should.
   * 
   * @param imagesAvailable
   * @param suggestions
   */
  private static void suggestCheckingTimes(boolean imagesAvailable,
      List<String> suggestions) {
    if (imagesAvailable) {
      // now for some interesting suggestions
      String text = String.format(i18n
          .tr("Make sure the time <b>%1$s</b> values are correct. It's best to right click on an image you are sure about (say an image of your GPS display) and choose <b>%2$s</b> from the menu bar, then select the exact time (and time zone) the image was taken."), //$NON-NLS-1$
          i18n.tr("Offset"), //$NON-NLS-1$
          i18n.tr("Set time of image")); //$NON-NLS-1$
      suggestions.add(text);
    }
  }

  /**
   * Check if loading tracks should be suggested and do so it if should.
   * 
   * @param gpsbabelAvailable
   * @param tracksAvailable
   * @param suggestions
   */
  private static void suggestLoadingTracks(boolean gpsbabelAvailable,
      boolean tracksAvailable, List<String> suggestions) {
    if (!tracksAvailable) {
      // no tracks loaded yet - suggest to do so
      String text = String
          .format(
              i18n.tr("Load a GPS track file. Select <b>%s %s %s</b> from the menu bar."), //$NON-NLS-1$
              i18n.tr("File"), RIGHT_ARROW, i18n.tr("Load tracks from file")); //$NON-NLS-1$ //$NON-NLS-2$
      suggestions.add(text);

      if (gpsbabelAvailable) {
        text = String.format(i18n
            .tr("You can also load tracks directly from your GPS. Select <b>%s %s %s</b>."), //$NON-NLS-1$
            i18n.tr("File"), RIGHT_ARROW, i18n //$NON-NLS-1$
                .tr("Load tracks from GPS")); //$NON-NLS-1$
        suggestions.add(text);
      }
    }
  }

  /**
   * Check if loading images should be suggested and do so it if should.
   * 
   * @param imagesAvailable
   * @param suggestions
   */
  private static void suggestLoadingImages(boolean imagesAvailable,
      List<String> suggestions) {
    if (!imagesAvailable) {
      // no images loaded yet - suggest doing so
      String text = String
          .format(
              i18n.tr("Load some image files. Select <b>%s %s %s</b> from the menu or add them by using drag-and-drop."), //$NON-NLS-1$
              i18n.tr("File"), RIGHT_ARROW, i18n.tr("Add images from directory")); //$NON-NLS-1$ //$NON-NLS-2$
      suggestions.add(text);
    }
  }

  /**
   * Check if images are available and suggest removing some of them.
   * 
   * @param imagesAvailable
   * @param suggestions
   */
  private static void suggestRemovingImages(boolean imagesAvailable,
      List<String> suggestions) {
    if (imagesAvailable) {
      // images are available - suggest removing some of them
      String text = String.format(i18n
          .tr("If there are too many images displayed, you can remove some of them using the  <b>%1$s</b> menu. They will be removed from the display, but not deleted."), //$NON-NLS-1$
          i18n.tr("Remove images")); //$NON-NLS-1$
      suggestions.add(text);
    }
  }
  
  /**
   * Suggest helping out by translating Geotag
   * @param translationAvailable
   * @param suggestions
   */
  private static void suggestTranslation(boolean translationAvailable, List<String> suggestions) {
    if (translationAvailable) {
      return;
    }
    StringBuilder builder =new StringBuilder();
    builder.append("Your language seems to be <b>"); //$NON-NLS-1$
    builder.append(Locale.getDefault().getDisplayName());
    builder.append("</b> and we don't have a translation for it. "); //$NON-NLS-1$
    builder.append("To find out how to help"); //$NON-NLS-1$
    builder.append( " <a href=\"http://geotag.sourceforge.net/?q=node/24\">click&nbsp;here</a>."); //$NON-NLS-1$
    suggestions.add(builder.toString());
  }

}
