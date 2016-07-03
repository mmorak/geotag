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

package org.fibs.geotag.tasks;

import java.util.List;

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.UpdateCityName;
import org.fibs.geotag.data.UpdateCountryName;
import org.fibs.geotag.data.UpdateLocationName;
import org.fibs.geotag.data.UpdateProvinceName;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.geonames.Location;
import org.fibs.geotag.geonames.WikipediaLocation;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.util.Util;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author Andreas Schneider
 * 
 */
public class SelectLocationNameTask extends UndoableBackgroundTask<ImageInfo> {

  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(SelectLocationNameTask.class);
  
  /** The image whose location names will be set. */
  private ImageInfo imageInfo;

  /** The new location. */
  private Location location;

  /** The actual string to be used as the location name. */
  private String actualName;

  /** The source of the update. */
  private DATA_SOURCE dataSource;

  /** the table model to inform about changes. */
  private ImagesTableModel imagesTableModel;

  /**
   * @param name
   * @param imagesTableModel
   * @param imageInfo
   * @param location
   * @param actualName
   * @param dataSource
   */
  public SelectLocationNameTask(String name, ImagesTableModel imagesTableModel,
      ImageInfo imageInfo, Location location, String actualName,
      DATA_SOURCE dataSource) {
    super(null, name); // has no group name
    this.imagesTableModel = imagesTableModel;
    this.imageInfo = imageInfo;
    this.location = location;
    this.actualName = actualName;
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
          && !Util.sameContent(imageInfo.getLocationName(), actualName)) {
        // Wikipedia locations only set the location name, not province or
        // country
        new UpdateLocationName(imageInfo, actualName, dataSource);
        publish(imageInfo);
      } else {
        // this is an actual location name from geonames.org.
        // two alternatives here:
        if (location.isPopulatedPlace()) {
          // for populated places we leave the location name alone
          if (!Util.sameContent(imageInfo.getCityName(), actualName)
              || !Util.sameContent(imageInfo.getProvinceName(), location
                  .getProvince())
              || !Util.sameContent(imageInfo.getCountryName(), location
                  .getCountryName())) {
            new UpdateCityName(imageInfo, actualName, dataSource);
            new UpdateProvinceName(imageInfo, location.getProvince(),
                dataSource);
            new UpdateCountryName(imageInfo, location.getCountryName(),
                dataSource);
            publish(imageInfo);
          }
        } else {
          // not a populated place - leave the city name alone
          if (!Util.sameContent(imageInfo.getLocationName(), actualName)
              || !Util.sameContent(imageInfo.getProvinceName(), location
                  .getProvince())
              || !Util.sameContent(imageInfo.getCountryName(), location
                  .getCountryName())) {
            new UpdateLocationName(imageInfo, actualName, dataSource);
            new UpdateProvinceName(imageInfo, location.getProvince(),
                dataSource);
            new UpdateCountryName(imageInfo, location.getCountryName(),
                dataSource);
            publish(imageInfo);
          }
        }
      }
    }
    return i18n.tr("New location name selected."); //$NON-NLS-1$
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
