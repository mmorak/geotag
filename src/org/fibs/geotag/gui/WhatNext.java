/**
 * Geotag
 * Copyright (C) 2007-2009 Andreas Schneider
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

import javax.swing.JOptionPane;

import org.fibs.geotag.Geotag;
import org.fibs.geotag.dcraw.Dcraw;
import org.fibs.geotag.exif.Exiftool;
import org.fibs.geotag.gpsbabel.GPSBabel;
import org.fibs.geotag.i18n.Messages;
import org.fibs.geotag.image.FileTypes;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.track.TrackStore;
import org.fibs.geotag.util.Util;

/**
 * A class that tries to generate suggestions what to do next.
 * 
 * @author Andreas Schneider
 * 
 */
public final class WhatNext {
  /**
   * hide constructor.
   */
  private WhatNext() {
    // hide constructor
  }

  /** the maximum line length. */
  private static final int MAX_LINE_LENGTH = 60;

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

    // build the final message
    StringBuilder message = new StringBuilder("<html>"); //$NON-NLS-1$
    List<String> lines;
    for (String suggestion : suggestions) {
      lines = Util.splitString(suggestion, MAX_LINE_LENGTH);
      for (String line : lines) {
        message.append(line).append("<br>"); //$NON-NLS-1$
      }
      message.append("<br>"); //$NON-NLS-1$
    }
    message.append("</html"); //$NON-NLS-1$
    JOptionPane.showMessageDialog(parentComponent, message.toString(), Messages
        .getString("MainWindow.WhatNext"), //$NON-NLS-1$
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
      String text = String.format(Messages
          .getString("WhatNext.SuggestFindingDcrawFormat"), Messages //$NON-NLS-1$
          .getString("MainWindow.File"), Messages //$NON-NLS-1$
          .getString("MainWindow.Settings")); //$NON-NLS-1$
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
      String text = String.format(Messages
          .getString("WhatNext.SuggestFindingGPSBabelFormat"), Messages //$NON-NLS-1$
          .getString("MainWindow.File"), Messages //$NON-NLS-1$
          .getString("MainWindow.Settings")); //$NON-NLS-1$
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
      String text = String.format(Messages
          .getString("WhatNext.SuggestFindingExiftoolFormat"), Messages //$NON-NLS-1$
          .getString("MainWindow.File"), Messages //$NON-NLS-1$
          .getString("MainWindow.Settings")); //$NON-NLS-1$
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
      String text = String.format(Messages
          .getString("WhatNext.SuggestLocationNamesFormat"), //$NON-NLS-1$
          Messages.getString("ImagesTablePopupMenu.LocationNames")); //$NON-NLS-1$
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
      String text = String.format(Messages
          .getString("WhatNext.SuggestSavingFormat"), //$NON-NLS-1$
          Messages.getString("ImagesTablePopupMenu.SaveNewLocations")); //$NON-NLS-1$
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
      String text = String.format(Messages
          .getString("WhatNext.SuggestFillingGapsFormat"), //$NON-NLS-1$
          Messages.getString("ImagesTablePopupMenu.FillGaps"), //$NON-NLS-1$
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
      String text = String.format(Messages
          .getString("WhatNext.SuggestMatchTracksFormat"), //$NON-NLS-1$
          Messages.getString("ImagesTableModel.Offset"), //$NON-NLS-1$
          Messages.getString("ImagesTablePopupMenu.MatchTracks")); //$NON-NLS-1$
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
      String text = String.format(Messages
          .getString("WhatNext.SuggestShowOnMapFormat"), //$NON-NLS-1$
          Messages.getString("ImagesTablePopupMenu.ShowOnMap")); //$NON-NLS-1$
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
      String text = String.format(Messages
          .getString("WhatNext.SuggestCheckingTimesFormat"), //$NON-NLS-1$
          Messages.getString("ImagesTableModel.Offset"), //$NON-NLS-1$
          Messages.getString("ImagesTablePopupMenu.SelectCorrectTimeForImage")); //$NON-NLS-1$
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
              Messages.getString("WhatNext.SuggestOpeningTrackFormat"), //$NON-NLS-1$
              Messages.getString("MainWindow.File"), Messages.getString("MainWindow.LoadTracksFromFile")); //$NON-NLS-1$ //$NON-NLS-2$
      suggestions.add(text);

      if (gpsbabelAvailable) {
        text = String.format(Messages
            .getString("WhatNext.SuggestLoadingFromGPSFormat"), //$NON-NLS-1$
            Messages.getString("MainWindow.File"), Messages //$NON-NLS-1$
                .getString("MainWindow.LoadTrackFromGPS")); //$NON-NLS-1$
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
              Messages.getString("WhatNext.SuggestLoadingImagesFormat"), //$NON-NLS-1$
              Messages.getString("MainWindow.File"), Messages.getString("MainWindow.AddDirectory")); //$NON-NLS-1$ //$NON-NLS-2$
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
      String text = String.format(Messages
          .getString("WhatNext.SuggestRemovingImagesFormat"), //$NON-NLS-1$
          Messages.getString("ImagesTablePopupMenu.RemoveImages")); //$NON-NLS-1$
      suggestions.add(text);
    }
  }

}
