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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.exif.Exiftool;
import org.fibs.geotag.table.ImagesTable;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.tasks.ExifWriterTask;
import org.fibs.geotag.tasks.TaskExecutor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author andreas
 * 
 */
@SuppressWarnings("serial")
public class SaveLocationsMenu extends JMenu implements ActionListener,
    MenuConstants {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(SaveLocationsMenu.class);

  /** The menu item used to save the location of an image. */
  private JMenuItem saveOneLocationItem;

  /** The menu item used to save new locations of selected images. */
  private JMenuItem saveSelectedLocationsItem;

  /** The menu item used to save new locations for all images. */
  private JMenuItem saveAllLocationsItem;

  /** The current image -can be null */
  private ImageInfo currentImage;

  /** The table model of the images table */
  private ImagesTableModel tableModel;

  /** The currently selected rows in the table */
  private int[] selectedRows;

  /**
   * This menu is always added, possibly disabled. Some people had difficulties
   * spotting it when available.
   * 
   * @param backgroundTask
   *          true if a background task is running
   * @param imagesTable
   *          the main images table
   * @param currentImage
   *          the image under the mouse pointer - can be null
   */
  public SaveLocationsMenu(boolean backgroundTask, ImagesTable imagesTable,
      ImageInfo currentImage) {
    super(SAVE_LOCATIONS);
    populate(backgroundTask, imagesTable, currentImage);
  }

  /**
   * (Re-)populate the menu. Repopulating the menu is necessary if this menu is
   * not created on the go every time (image table context menu) but is static
   * (like inside the File menu)
   * 
   * @param backgroundTask
   * @param imagesTable
   * @param theCurrentImage
   */
  public void populate(boolean backgroundTask, ImagesTable imagesTable,
      ImageInfo theCurrentImage) {
    removeAll();
    this.currentImage = theCurrentImage;
    this.tableModel = (ImagesTableModel) imagesTable.getModel();
    this.selectedRows = imagesTable.getSelectedRows();

    boolean itemEnabled;
    boolean menuEnabled;
    // enable if exiftool is available
    setEnabled(Exiftool.isAvailable());
    menuEnabled = false;

    if (currentImage != null) {
      saveOneLocationItem = new JMenuItem(SAVE_THIS_LOCATION);
      // enabled if there is no background task, the image has a location that's
      // not coming from the image itself
      itemEnabled = !backgroundTask && currentImage.hasNewLocation();
      menuEnabled |= itemEnabled;
      saveOneLocationItem.setEnabled(itemEnabled);
      saveOneLocationItem.addActionListener(this);
      this.add(saveOneLocationItem);
    }

    saveSelectedLocationsItem = new JMenuItem(SAVE_SELECTED_LOCATIONS);
    // enable if there is no background task, there is a selection of images
    // and at least one image in the selection has a new location
    itemEnabled = false;
    for (int index = 0; index < selectedRows.length; index++) {
      if (tableModel.getImageInfo(selectedRows[index]).hasNewLocation()) {
        itemEnabled = !backgroundTask;
        break;
      }
    }
    menuEnabled |= itemEnabled;
    saveSelectedLocationsItem.setEnabled(itemEnabled);
    saveSelectedLocationsItem.addActionListener(this);
    this.add(saveSelectedLocationsItem);

    saveAllLocationsItem = new JMenuItem(SAVE_ALL_LOCATIONS);
    // Using accelerators needs more work.
    // We need to use proper AbstractActions - a major change.
    // if (currentImage == null) {
    // saveAllLocationsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
    // Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    // }
    // enable if there is no background task and there is at least one image
    // that has a new location
    itemEnabled = false;
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      if (tableModel.getImageInfo(index).hasNewLocation()) {
        itemEnabled = !backgroundTask;
        break;
      }
    }
    menuEnabled |= itemEnabled;
    saveAllLocationsItem.setEnabled(itemEnabled);
    saveAllLocationsItem.addActionListener(this);
    this.add(saveAllLocationsItem);

    this.setEnabled(menuEnabled);
  }

  /**
   * Save the EXIF data to all images in the list.
   * 
   * @param images
   */
  private void saveLocations(List<ImageInfo> images) {
    TaskExecutor
        .execute(new ExifWriterTask(
            i18n.tr("Save new locations"), tableModel, images)); //$NON-NLS-1$
  }

  /**
   * Save the EXIF data to this image.
   */
  private void saveOneLocation() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(currentImage);
    saveLocations(images);
  }

  /**
   * Save the EXIF data to all selected images.
   */
  private void saveSelectedLocations() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      ImageInfo candidate = tableModel.getImageInfo(selectedRows[index]);
      if (candidate.hasNewLocation()) {
        images.add(candidate);
      }
    }
    saveLocations(images);
  }

  /**
   * Save the EXIF data to all images.
   */
  private void saveAllLocations() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      ImageInfo candidate = tableModel.getImageInfo(index);
      if (candidate.hasNewLocation()) {
        images.add(candidate);
      }
    }
    saveLocations(images);
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == saveOneLocationItem) {
      saveOneLocation();
    } else if (event.getSource() == saveSelectedLocationsItem) {
      saveSelectedLocations();
    } else if (event.getSource() == saveAllLocationsItem) {
      saveAllLocations();
    }
  }

}
