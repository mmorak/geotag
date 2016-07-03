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
import org.fibs.geotag.tasks.MatchImagesTask;
import org.fibs.geotag.tasks.TaskExecutor;
import org.fibs.geotag.track.TrackStore;

/**
 * @author andreas
 * 
 */
@SuppressWarnings("serial")
public class MatchTracksMenu extends JMenu implements MenuConstants,
    ActionListener {

  /** The menu item used to apply the track data to one image. */
  private JMenuItem matchTrackToOneImageItem;

  /** The menu item used to apply the track data to selected images. */
  private JMenuItem matchTrackToSelectedImagesItem;

  /** The menu item used to apply the track data to all images. */
  private JMenuItem matchTrackToAllImagesItem;

  /** The table for the images */
  private ImagesTable imagesTable;

  /** The model for that table */
  private ImagesTableModel tableModel;

  /** The selected rows in the table */
  int[] selectedRows;

  /** The image under the mouse cursor */
  ImageInfo currentImage;

  /**
   * @param backgroundTask
   * @param imagesTable
   * @param currentImage
   * @param trackStore
   */
  public MatchTracksMenu(boolean backgroundTask, ImagesTable imagesTable,
      ImageInfo currentImage, TrackStore trackStore) {
    super(MATCH_TRACKS);
    boolean itemEnabled;
    @SuppressWarnings("unused")
    boolean menuEnabled;
    this.imagesTable = imagesTable;
    this.tableModel = (ImagesTableModel) imagesTable.getModel();
    this.currentImage = currentImage;
    selectedRows = imagesTable.getSelectedRows();
    menuEnabled = false;

    matchTrackToOneImageItem = new JMenuItem(MATCH_TRACK_THIS);
    // enable if there is no background task and tracks have been loaded
    itemEnabled = !backgroundTask && trackStore.hasTracks();
    menuEnabled |= itemEnabled;
    matchTrackToOneImageItem.setEnabled(itemEnabled);
    matchTrackToOneImageItem.addActionListener(this);
    this.add(matchTrackToOneImageItem);

    matchTrackToSelectedImagesItem = new JMenuItem(MATCH_TRACK_SELECTED);
    // enable if there is no background task, there are tracks and a selection
    itemEnabled = !backgroundTask && trackStore.hasTracks()
        && (selectedRows.length > 0);
    menuEnabled |= itemEnabled;
    matchTrackToSelectedImagesItem.setEnabled(itemEnabled);
    matchTrackToSelectedImagesItem.addActionListener(this);
    this.add(matchTrackToSelectedImagesItem);

    matchTrackToAllImagesItem = new JMenuItem(MATCH_TRACK_ALL);
    // enable if there is no background task and there are tracks available
    itemEnabled = !backgroundTask && trackStore.hasTracks();
    menuEnabled |= itemEnabled;
    matchTrackToAllImagesItem.setEnabled(itemEnabled);
    matchTrackToAllImagesItem.addActionListener(this);
    this.add(matchTrackToAllImagesItem);
  }

  /**
   * Use the TrackMatcher to find coordinates for one image.
   */
  private void matchTracksToOneImage() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(currentImage);
    TaskExecutor.execute(new MatchImagesTask(MATCH_TRACKS,
        MATCH_TRACK_SELECTED, imagesTable, images));
  }

  /**
   * Use the TrackMatcher to find coordinates for all selected images.
   */
  private void matchTracksToSelectedImages() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      images.add(tableModel.getImageInfo(selectedRows[index]));
    }
    TaskExecutor.execute(new MatchImagesTask(MATCH_TRACKS,
        MATCH_TRACK_SELECTED, imagesTable, images));
  }

  /**
   * Use the TrackMatcher to find coordinates for all images.
   */
  private void matchTracksToAllImages() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      images.add(tableModel.getImageInfo(index));
    }
    TaskExecutor.execute(new MatchImagesTask(MATCH_TRACKS, MATCH_TRACK_ALL,
        imagesTable, images));
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == matchTrackToOneImageItem) {
      matchTracksToOneImage();
    } else if (event.getSource() == matchTrackToSelectedImagesItem) {
      matchTracksToSelectedImages();
    } else if (event.getSource() == matchTrackToAllImagesItem) {
      matchTracksToAllImages();
    }
  }
}
