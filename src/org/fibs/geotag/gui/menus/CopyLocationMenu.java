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
import org.fibs.geotag.table.ImagesTable;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.tasks.CopyLocationTask;
import org.fibs.geotag.tasks.TaskExecutor;

/**
 * @author andreas
 * 
 */
@SuppressWarnings("serial")
public class CopyLocationMenu extends JMenu implements MenuConstants,
    ActionListener {

  /** The menu item used to copy the location to the previous image. */
  private JMenuItem copyLocationToPreviousItem;

  /** The menu item used to copy the location to the next image. */
  private JMenuItem copyLocationToNextItem;

  /** The menu item used to copy the location to selected images. */
  private JMenuItem copyLocationToSelectedItem;

  /** The menu item used to copy the location to all images. */
  private JMenuItem copyLocationToAllItem;

  /** The rows selected in the table */
  private int[] selectedRows;

  /** The table'smodel */
  private ImagesTableModel tableModel;

  /** the image under the mouse cursor */
  private ImageInfo currentImage;

  /**
   * @param backgroundTask
   * @param imagesTable
   * @param currentImage
   */
  public CopyLocationMenu(boolean backgroundTask, ImagesTable imagesTable,
      ImageInfo currentImage) {
    super(COPY_LOCATION);
    this.tableModel = (ImagesTableModel) imagesTable.getModel();
    this.currentImage = currentImage;
    int row = tableModel.getRow(currentImage);
    selectedRows = imagesTable.getSelectedRows();

    boolean itemEnabled;
    @SuppressWarnings("unused")
    boolean menuEnabled;
    menuEnabled = false;

    copyLocationToPreviousItem = new JMenuItem(COPY_LOCATION_PREVIOUS);
    // Enable if there is no background task and this image has a location and
    // is not the first image
    itemEnabled = !backgroundTask && currentImage.hasLocation() && row > 0;
    menuEnabled |= itemEnabled;
    copyLocationToPreviousItem.setEnabled(itemEnabled);
    copyLocationToPreviousItem.addActionListener(this);
    this.add(copyLocationToPreviousItem);

    copyLocationToNextItem = new JMenuItem(COPY_LOCATION_NEXT);
    // Enable if there is no background task and this image has a location and
    // is not the last image
    itemEnabled = !backgroundTask && currentImage.hasLocation()
        && row < tableModel.getRowCount() - 1;
    menuEnabled |= itemEnabled;
    copyLocationToNextItem.setEnabled(itemEnabled);
    copyLocationToNextItem.addActionListener(this);
    this.add(copyLocationToNextItem);

    copyLocationToSelectedItem = new JMenuItem(COPY_LOCATION_SELECTED);
    // Enable if there is no background task and this image has a location and
    // there is a selection
    itemEnabled = !backgroundTask && currentImage.hasLocation()
        && selectedRows.length > 0;
    menuEnabled |= itemEnabled;
    copyLocationToSelectedItem.setEnabled(itemEnabled);
    copyLocationToSelectedItem.addActionListener(this);
    this.add(copyLocationToSelectedItem);

    copyLocationToAllItem = new JMenuItem(COPY_LOCATION_ALL);
    // Enable if there is no background task and this image has a location
    itemEnabled = !backgroundTask && currentImage.hasLocation();
    menuEnabled |= itemEnabled;
    copyLocationToAllItem.setEnabled(itemEnabled);
    copyLocationToAllItem.addActionListener(this);
    this.add(copyLocationToAllItem);
  }

  /**
   * Copy the location to the previous image.
   */
  private void copyLocationToPrevious() {
    int row = tableModel.getRow(currentImage);
    if (row > 0) {
      List<ImageInfo> imageList = new ArrayList<ImageInfo>();
      imageList.add(tableModel.getImageInfo(row - 1));
      TaskExecutor.execute(new CopyLocationTask(COPY_LOCATION,
          COPY_LOCATION_PREVIOUS, tableModel, currentImage, imageList));
    }
  }

  /**
   * Copy the location to the next image.
   */
  private void copyLocationToNext() {
    int row = tableModel.getRow(currentImage);
    if (row < tableModel.getRowCount() - 1) {
      List<ImageInfo> imageList = new ArrayList<ImageInfo>();
      imageList.add(tableModel.getImageInfo(row + 1));
      TaskExecutor.execute(new CopyLocationTask(COPY_LOCATION,
          COPY_LOCATION_NEXT, tableModel, currentImage, imageList));
    }
  }

  /**
   * Copy the location to all selected images.
   */
  private void copyLocationToSelected() {
    List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      imageList.add(tableModel.getImageInfo(selectedRows[index]));
    }
    TaskExecutor.execute(new CopyLocationTask(COPY_LOCATION,
        COPY_LOCATION_SELECTED, tableModel, currentImage, imageList));
  }

  /**
   * Copy the location to all images.
   */
  private void copyLocationToAll() {
    List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    for (int iindex = 0; iindex < tableModel.getRowCount(); iindex++) {
      imageList.add(tableModel.getImageInfo(iindex));
    }
    TaskExecutor.execute(new CopyLocationTask(COPY_LOCATION, COPY_LOCATION_ALL,
        tableModel, currentImage, imageList));
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == copyLocationToPreviousItem) {
      copyLocationToPrevious();
    } else if (event.getSource() == copyLocationToNextItem) {
      copyLocationToNext();
    } else if (event.getSource() == copyLocationToSelectedItem) {
      copyLocationToSelected();
    } else if (event.getSource() == copyLocationToAllItem) {
      copyLocationToAll();
    }
  }

}
