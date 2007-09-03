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

package org.fibs.geotag.ui;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.fibs.geotag.Geotag;
import org.fibs.geotag.Messages;
import org.fibs.geotag.dcraw.Dcraw;
import org.fibs.geotag.exif.Exiftool;
import org.fibs.geotag.gpsbabel.GPSBabel;
import org.fibs.geotag.image.ImageFileFilter;
import org.fibs.geotag.track.TrackMatcher;
import org.fibs.geotag.util.Util;

/**
 * A class that tries to generate suggestions what to do next
 * 
 * @author Andreas Schneider
 * 
 */
public class WhatNext {

  /**
   * This method tries to see where in the work flow we are and gives the use a
   * suggestion, what to do next
   * 
   * @param parentComponent
   * @param tableModel
   * @param trackMatcher
   */
  static void helpWhatNext(Component parentComponent,
      ImagesTableModel tableModel, TrackMatcher trackMatcher) {
    int maxLineLength = 60;
    boolean exiftoolAvailable = Exiftool.isAvailable();
    boolean gpsbabelAvailable = GPSBabel.isAvailable();
    boolean dcrawAvailable = Dcraw.isAvailable();
    boolean imagesAvailable = (tableModel.getRowCount() > 0);
    boolean tracksAvailable = trackMatcher.hasTracks();
    boolean imagesWithNewLocationAvailable = false;
    for (int row = 0; row < tableModel.getRowCount(); row++) {
      if (tableModel.getImageInfo(row).hasNewLocation()) {
        imagesWithNewLocationAvailable = true;
        break;
      }
    }
    boolean rawImagesAvailable = false;
    for (int row = 0; row < tableModel.getRowCount(); row++) {
      File file = new File(tableModel.getImageInfo(row).getPath());
      if (ImageFileFilter.isRawFile(file)) {
        rawImagesAvailable = true;
        break;
      }
    }
    boolean gapsAvailable = false;
    if (imagesWithNewLocationAvailable) {
      for (int row = 0; row < tableModel.getRowCount(); row++) {
        if (tableModel.getImageInfo(row).hasLocation() == false) {
          gapsAvailable = true;
          break;
        }
      }
    }
    List<String> suggestions = new ArrayList<String>();
    if (!imagesAvailable) {
      // no images loaded yet - suggest doing so
      String text = String
          .format(
              Messages.getString("WhatNext.SuggestLoadingImagesFormat"), //$NON-NLS-1$
              Messages.getString("MainWindow.File"), Messages.getString("MainWindow.AddDirectory")); //$NON-NLS-1$ //$NON-NLS-2$
      suggestions.add(text);
    }
    if (!tracksAvailable) {
      // no tracks loaded yet - suggest to do so
      String text = String
          .format(
              Messages.getString("WhatNext.SuggestOpeningTrackFormat"), //$NON-NLS-1$
              Messages.getString("MainWindow.File"), Messages.getString("MainWindow.OpenTrack")); //$NON-NLS-1$ //$NON-NLS-2$
      suggestions.add(text);

      if (gpsbabelAvailable) {
        text = String
            .format(
                Messages.getString("WhatNext.SuggestLoadingFromGPSFormat"), //$NON-NLS-1$
                Messages.getString("MainWindow.File"), Messages.getString("MainWindow.LoadTrackFromGPS")); //$NON-NLS-1$//$NON-NLS-2$
        suggestions.add(text);
      }
    }

    if (imagesAvailable) {
      // now for some interesting suggestions
      String text = String.format(Messages
          .getString("WhatNext.SuggestCheckingTimesFormat"), //$NON-NLS-1$
          Messages.getString("ImagesTableModel.Offset"), //$NON-NLS-1$
          Messages.getString("ImagesTablePopupMenu.SelectCorrectTimeForImage")); //$NON-NLS-1$
      suggestions.add(text);

      text = String.format(Messages
          .getString("WhatNext.SuggestShowOnMapFormat"), //$NON-NLS-1$
          Messages.getString("ImagesTablePopupMenu.ShowOnMap")); //$NON-NLS-1$
      suggestions.add(text);
    }

    if (imagesAvailable && tracksAvailable) {
      String text = String.format(Messages
          .getString("WhatNext.SuggestMatchTracksFormat"), //$NON-NLS-1$
          Messages.getString("ImagesTableModel.Offset"), //$NON-NLS-1$
          Messages.getString("ImagesTablePopupMenu.MatchTracks")); //$NON-NLS-1$
      suggestions.add(text);
    }

    if (gapsAvailable) {
      String text = String.format(Messages
          .getString("WhatNext.SuggestFillingGapsFormat"), //$NON-NLS-1$
          Messages.getString("ImagesTablePopupMenu.FillGaps"), //$NON-NLS-1$
          Geotag.NAME);
      suggestions.add(text);
    }

    if (imagesWithNewLocationAvailable) {
      String text = String.format(Messages
          .getString("WhatNext.SuggestSavingFormat"), //$NON-NLS-1$
          Messages.getString("ImagesTablePopupMenu.SaveNewLocations")); //$NON-NLS-1$
      suggestions.add(text);
    }

    if (!exiftoolAvailable) {
      String text = String
          .format(
              Messages.getString("WhatNext.SuggestFindingExiftoolFormat"), Messages.getString("MainWindow.File"), Messages.getString("MainWindow.Settings")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      suggestions.add(text);
    }

    if (!gpsbabelAvailable) {
      String text = String
          .format(
              Messages.getString("WhatNext.SuggestFindingGPSBabelFormat"), Messages.getString("MainWindow.File"), Messages.getString("MainWindow.Settings")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      suggestions.add(text);
    }

    if (!dcrawAvailable && rawImagesAvailable) {
      String text = String
          .format(
              Messages.getString("WhatNext.SuggestFindingDcrawFormat"), Messages.getString("MainWindow.File"), Messages.getString("MainWindow.Settings")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      suggestions.add(text);
    }

    // build the final message
    StringBuilder message = new StringBuilder("<html>"); //$NON-NLS-1$
    List<String> lines;
    for (String suggestion : suggestions) {
      lines = Util.splitString(suggestion, maxLineLength);
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

}
