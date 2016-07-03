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

package org.fibs.geotag.gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.googleearth.GoogleEarthLauncher;
import org.fibs.geotag.googleearth.GoogleearthFileFilter;
import org.fibs.geotag.gui.MainWindow;
import org.fibs.geotag.image.ThumbnailWorker;
import org.fibs.geotag.table.ImagesTable;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.tasks.GoogleEarthExportTask;
import org.fibs.geotag.tasks.TaskExecutor;
import org.fibs.geotag.tasks.ThumbnailsTask;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author andreas
 * 
 */
@SuppressWarnings("serial")
public class GoogleEarthMenu extends JMenu implements ActionListener,
    MenuConstants {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(GoogleEarthMenu.class);

  /** The menu item used to show an image location in Google Earth. */
  private JMenuItem showInGoogleEarthItem;

  /** The menu item used to export a single image to a KML/KMZ file. */
  private JMenuItem exportOneImageToKmlItem;

  /** The menu item used to export selected images to a KML/KMZ file. */
  private JMenuItem exportSelectedToKmlItem;

  /**
   * The menu item used to export all images with locations to a KML/KMZ file.
   */
  private JMenuItem exportAllToKmlItem;

  /** The image currently under the cursor */
  ImageInfo currentImage;

  /** The model of the table containing the images */
  private ImagesTableModel tableModel;

  /** the currently selected images */
  private int[] selectedRows;

  /**
   * @param backgroundTask
   * @param imagesTable
   * @param currentImage
   */
  public GoogleEarthMenu(boolean backgroundTask, ImagesTable imagesTable,
      ImageInfo currentImage) {
    super(GOOGLEEARTH);
    this.currentImage = currentImage;
    this.tableModel = (ImagesTableModel) imagesTable.getModel();
    this.selectedRows = imagesTable.getSelectedRows();

    showInGoogleEarthItem = new JMenuItem(SHOW_IN_GOOGLEEARTH);
    boolean enabled = true; // we can always do this safely
    showInGoogleEarthItem.setEnabled(enabled);
    showInGoogleEarthItem.addActionListener(this);
    this.add(showInGoogleEarthItem);

    exportOneImageToKmlItem = new JMenuItem(EXPORT_THIS);
    // enable if there is no background task this image has a location
    enabled = !backgroundTask && currentImage.hasLocation();
    exportOneImageToKmlItem.setEnabled(enabled);
    exportOneImageToKmlItem.addActionListener(this);
    this.add(exportOneImageToKmlItem);

    exportSelectedToKmlItem = new JMenuItem(EXPORT_SELECTED);
    // enable if there is no background task and there is a
    // selection containing at least on image with location.
    enabled = false;
    for (int index = 0; index < selectedRows.length; index++) {
      if (tableModel.getImageInfo(selectedRows[index]).hasLocation()) {
        enabled = !backgroundTask;
        break;
      }
    }
    exportSelectedToKmlItem.setEnabled(enabled);
    exportSelectedToKmlItem.addActionListener(this);
    this.add(exportSelectedToKmlItem);

    exportAllToKmlItem = new JMenuItem(EXPORT_ALL);
    // enable if there is no background task and there is at least one image
    // that has a location
    enabled = false;
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      if (tableModel.getImageInfo(index).hasLocation()) {
        enabled = !backgroundTask;
        break;
      }
    }
    exportAllToKmlItem.setEnabled(enabled);
    exportAllToKmlItem.addActionListener(this);
    this.add(exportAllToKmlItem);
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == showInGoogleEarthItem) {
      showInGoogleEarth();
    } else if (event.getSource() == exportOneImageToKmlItem) {
      exportOneToKml();
    } else if (event.getSource() == exportSelectedToKmlItem) {
      exportSelectedToKml();
    } else if (event.getSource() == exportAllToKmlItem) {
      exportAllToKml();
    }
  }

  /**
   * Launch Google Earth to show the location.
   */
  private void showInGoogleEarth() {
    // make sure there is a thumbnail - this won't create the
    // thumbnail again, if it already exists.
    ThumbnailWorker worker = new ThumbnailWorker(currentImage) {
      @Override
      protected void done() {
        GoogleEarthLauncher.launch(currentImage);
      }
    };
    TaskExecutor.execute(worker);
  }

  /**
   * Export a single image to a KML/KMZ file.
   */
  private void exportOneToKml() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(currentImage);
    exportToKml(images);
  }

  /**
   * Export images with locations from a selection to a KML/KMZ file.
   */
  private void exportSelectedToKml() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      ImageInfo candidate = tableModel.getImageInfo(selectedRows[index]);
      if (candidate.hasLocation()) {
        images.add(candidate);
      }
    }
    exportToKml(images);
  }

  /**
   * Export all images with a location to a KML/KMZ file.
   */
  private void exportAllToKml() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      ImageInfo candidate = tableModel.getImageInfo(index);
      if (candidate.hasLocation()) {
        images.add(candidate);
      }
    }
    exportToKml(images);
  }

  /**
   * Export a list of images to a KML/KMZ file.
   * 
   * @param images
   */
  private void exportToKml(final List<ImageInfo> images) {
    JFileChooser chooser = new JFileChooser();
    String lastFile = Settings.get(SETTING.GOOGLEEARTH_LAST_FILE_SAVED, null);
    if (lastFile != null) {
      File file = new File(lastFile);
      if (file.exists() && file.getParentFile() != null) {
        chooser.setCurrentDirectory(file.getParentFile());
      }
    }
    GoogleearthFileFilter fileFilter = new GoogleearthFileFilter();
    chooser.setFileFilter(fileFilter);
    chooser.setMultiSelectionEnabled(false);

    JFrame parentFrame = MainWindow.getMainWindow(GoogleEarthMenu.this);
    if (chooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
      try {
        File outputFile = chooser.getSelectedFile();
        if (!fileFilter.accept(outputFile)) {
          // not a kml/kmz file selected - add .kml suffix
          outputFile = new File(chooser.getSelectedFile().getPath() + ".kml"); //$NON-NLS-1$
        }
        if (outputFile.exists()) {
          // TODO decide if these messages should get their own class
          String title = i18n.tr("File exists"); //$NON-NLS-1$
          String message = String
              .format(
                  i18n.tr("Overwrite existing file %s?"), outputFile.getName()); //$NON-NLS-1$
          if (JOptionPane.showConfirmDialog(parentFrame, message, title,
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
            return;
          }
        }
        // do we need to create thumbnail images?
        if (GoogleearthFileFilter.isKmzFile(outputFile)
            && Settings.get(SETTING.KMZ_STORE_THUMBNAILS, false)) {
          // the output file is kmz and we need to store thumbnails
          // use a ThumbnailsTask and generate KML/KMZ when done
          final File file = outputFile;
          TaskExecutor.execute(new ThumbnailsTask(i18n
              .tr("Generate thumbnails"), images) { //$NON-NLS-1$
                @Override
                public void done() {
                  exportToKml(images, file);
                }
              });

        } else {
          exportToKml(images, outputFile);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Finally - export images to a file.
   * 
   * @param images
   * @param file
   */
  void exportToKml(List<ImageInfo> images, File file) {
    TaskExecutor.execute(new GoogleEarthExportTask(i18n
        .tr("Export for Google Earth"), images, file)); //$NON-NLS-1$
    Settings.put(SETTING.GOOGLEEARTH_LAST_FILE_SAVED, file.getPath());
  }
}
