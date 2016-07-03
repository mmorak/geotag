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

import java.util.Collections;
import java.util.IllegalFormatException;
import java.util.List;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.UpdateCityName;
import org.fibs.geotag.data.UpdateCountryName;
import org.fibs.geotag.data.UpdateLocationName;
import org.fibs.geotag.data.UpdateProvinceName;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.geonames.Location;
import org.fibs.geotag.geonames.LocationHandler;
import org.fibs.geotag.geonames.WikipediaHandler;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.util.Util;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A class finding location names for images.
 * 
 * @author Andreas Schneider
 * 
 */
public class LocationNamesTask extends UndoableBackgroundTask<ImageInfo> {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(LocationNamesTask.class);

  /** the table model to be informed about changes. */
  private ImagesTableModel imagesTableModel;

  /** the list of images to be updated. */
  private List<ImageInfo> imageInfos;

  /** Keep track of progress. */
  private int currentProgress = 0;

  /**
   * @param group
   * @param name
   * @param imagestableModel
   * @param imageInfos
   */
  public LocationNamesTask(String group, String name,
      ImagesTableModel imagestableModel, List<ImageInfo> imageInfos) {
    super(group, name);
    this.imagesTableModel = imagestableModel;
    this.imageInfos = imageInfos;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getCurrentProgress()
   */
  @Override
  public int getCurrentProgress() {
    return currentProgress;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getMaxProgress()
   */
  @Override
  public int getMaxProgress() {
    return imageInfos.size();
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
  @SuppressWarnings("boxing")
  @Override
  protected String doInBackground() throws Exception {
    int namesFound = 0;
    for (ImageInfo imageInfo : imageInfos) {
      if (interruptRequested()) {
        break;
      }
      currentProgress++;
      setProgressMessage();
      try {
        LocationHandler locationHandler = new LocationHandler(imageInfo
            .getGpsLatitude(), imageInfo.getGpsLongitude());
        List<Location> locations = locationHandler.getLocations();
        if (Settings.get(SETTING.GEONAMES_USE_WIKIPEDIA, false)) {
          WikipediaHandler wikipediaHandler = new WikipediaHandler(imageInfo
              .getGpsLatitude(), imageInfo.getGpsLongitude());
          // TODO for all wikipedia locations we need to determine country and
          // province - geonames.org say they will add this soon (at least
          // the country)
          locations.addAll(wikipediaHandler.getLocations());
        }
        if (locations.size() > 0) {
          Collections.sort(locations);
          imageInfo.setNearbyLocations(locations);
          // we go through the locations and fill the fields with the closest
          // macth
          boolean locationNeeded = true;
          boolean cityNeeded = true;
          boolean provinceNeeded = true;
          boolean countryNeeded = true;
          boolean updated = false;
          for (Location location : locations) {
            // if the first location we find is a populated place we fill in the
            // city, but leave the location blank
            if (cityNeeded && location.getName() != null
                && location.getName().length() > 0) {
              if (location.isPopulatedPlace()) {
                if (!Util.sameContent(location.getName(), imageInfo
                    .getCityName())) {
                  new UpdateCityName(imageInfo, location.getName(),
                      DATA_SOURCE.GEONAMES);
                  updated = true;
                }
                // if we updated or not - we have a city name
                cityNeeded = false;
                // and we don't need a location name
                locationNeeded = false;
              }
            }
            if (locationNeeded && location.getName() != null
                && location.getName().length() > 0) {
              if (!Util.sameContent(location.getName(), imageInfo
                  .getLocationName())) {
                new UpdateLocationName(imageInfo, location.getName(),
                    DATA_SOURCE.GEONAMES);
                updated = true;
              }
              // we have a location name now
              // we still want the nearest populated place
              locationNeeded = false;
            }
            if (provinceNeeded && location.getProvince() != null
                && location.getProvince().length() > 0) {
              if (!Util.sameContent(location.getProvince(), imageInfo
                  .getProvinceName())) {
                new UpdateProvinceName(imageInfo, location.getProvince(),
                    DATA_SOURCE.GEONAMES);
                updated = true;
              }
              provinceNeeded = false;
            }
            if (countryNeeded && location.getCountryName() != null
                && location.getCountryName().length() > 0) {
              if (!Util.sameContent(location.getCountryName(), imageInfo
                  .getCountryName())) {
                new UpdateCountryName(imageInfo, location.getCountryName(),
                    DATA_SOURCE.GEONAMES);
                updated = true;
              }
              countryNeeded = false;
            }
          }
          if (updated) {
            namesFound++;
          }
        }
        // tell the GUI about the change
        publish(imageInfo);
      } catch (RuntimeException e) {
        // catch all runtime exceptions - no need to terminate early
        e.printStackTrace();
      }
    }
    String result = null;
    if (namesFound == 1) {
      result = i18n.tr("One location name found"); //$NON-NLS-1$
    } else {
      try {
        result = String.format(i18n
            .tr("%d location names found"), //$NON-NLS-1$
            namesFound);
      } catch (IllegalFormatException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  /**
   * @see javax.swing.SwingWorker#process(java.util.List)
   */
  @Override
  protected void process(List<ImageInfo> chunks) {
    for (ImageInfo imageInfo : chunks) {
      int imageRow = imagesTableModel.getRow(imageInfo);
      imagesTableModel.fireTableRowsUpdated(imageRow, imageRow);
    }
  }

}
