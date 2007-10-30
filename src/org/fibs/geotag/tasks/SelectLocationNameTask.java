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

package org.fibs.geotag.tasks;

import java.util.List;

import org.fibs.geotag.Messages;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.UpdateCountryName;
import org.fibs.geotag.data.UpdateLocationName;
import org.fibs.geotag.data.UpdateProvinceName;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.geonames.Location;
import org.fibs.geotag.geonames.WikipediaLocation;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.util.Util;

/**
 * @author Andreas Schneider
 * 
 */
public class SelectLocationNameTask extends UndoableBackgroundTask<ImageInfo> {

  /** The image whose location names will be set */
  private ImageInfo imageInfo;

  /** The new location */
  private Location location;

  /** The source of the update */
  private DATA_SOURCE dataSource;

  /** the table model to inform about changes */
  private ImagesTableModel imagesTableModel;

  /**
   * @param name
   * @param imagesTableModel
   * @param imageInfo
   * @param location
   * @param dataSource
   */
  public SelectLocationNameTask(String name, ImagesTableModel imagesTableModel,
      ImageInfo imageInfo, Location location, DATA_SOURCE dataSource) {
    super(null, name); // has no group name
    this.imagesTableModel = imagesTableModel;
    this.imageInfo = imageInfo;
    this.location = location;
    this.dataSource = dataSource;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getCurrentProgress()
   */
  @Override
  public int getCurrentProgress() {
    return 1;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getMaxProgress()
   */
  @Override
  public int getMaxProgress() {
    return 1;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getMinProgress()
   */
  @Override
  public int getMinProgress() {
    return 1;
  }

  /**
   * @see javax.swing.SwingWorker#doInBackground()
   */
  @Override
  protected String doInBackground() throws Exception {
    if (location != null) {
      if (location instanceof WikipediaLocation
          && !Util.sameContent(imageInfo.getLocationName(), location.getName())) {
        // Wikipedia locations only set the location name, not province or
        // country
        new UpdateLocationName(imageInfo, location.getName(), dataSource);
        publish(imageInfo);
      } else if (!Util.sameContent(imageInfo.getLocationName(), location
          .getName())
          || !Util.sameContent(imageInfo.getProvinceName(), location
              .getProvince())
          || !Util.sameContent(imageInfo.getCountryName(), location
              .getCountryName())) {
        new UpdateLocationName(imageInfo, location.getName(), dataSource);
        new UpdateProvinceName(imageInfo, location.getProvince(), dataSource);
        new UpdateCountryName(imageInfo, location.getCountryName(), dataSource);
        publish(imageInfo);
      }
    }
    return Messages.getString("SelectLocationNameTask.NewLocationNameSelected"); //$NON-NLS-1$
  }

  /**
   * @see javax.swing.SwingWorker#process(java.util.List)
   */
  @Override
  protected void process(List<ImageInfo> images) {
    for (ImageInfo image : images) {
      int imageRow = imagesTableModel.getRow(image);
      imagesTableModel.fireTableRowsUpdated(imageRow, imageRow);
    }
  }

}
