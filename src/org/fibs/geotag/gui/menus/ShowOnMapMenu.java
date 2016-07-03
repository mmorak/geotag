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
import java.util.Locale;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.image.ThumbnailWorker;
import org.fibs.geotag.table.ImagesTable;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.tasks.TaskExecutor;
import org.fibs.geotag.tasks.ThumbnailsTask;
import org.fibs.geotag.util.Airy;
import org.fibs.geotag.util.BrowserLauncher;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author andreas
 * 
 */
@SuppressWarnings("serial")
public class ShowOnMapMenu extends JMenu implements ActionListener,
    MenuConstants {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(ShowOnMapMenu.class);

  /** the default Google zoom level. */
  private static final int DEFAULT_GOOGLE_ZOOM_LEVEL = 15;

  /** Text for menu. */
  private static final String SHOW_ON_MAP = i18n
      .tr("Show on map"); //$NON-NLS-1$

  /** Text for menu. */
  private static final String SHOW_ON_MAP_WITH_DIRECTION = i18n
      .tr("Show on map (with direction)"); //$NON-NLS-1$

  /** The menu item used to show an image location on a map. */
  private JMenuItem showOneOnMapItem;

  /** The menu item used to show selected image locations on a map. */
  private JMenuItem showSelectedOnMapItem;

  /** The menu item used to show all image locations on a map. */
  private JMenuItem showAllOnMapItem;

  /** The images table */
  private ImagesTable imagesTable;

  /** The image the mouse is over - might be null */
  private ImageInfo imageInfo;

  /** True if the image(s) should be shown with direction */
  private boolean withDirection;

  /**
   * @param backgroundTask
   * @param imagesTable
   * @param imageInfo
   * @param withDirection
   */
  public ShowOnMapMenu(boolean backgroundTask, ImagesTable imagesTable,
      ImageInfo imageInfo, boolean withDirection) {
    super(withDirection ? SHOW_ON_MAP_WITH_DIRECTION : SHOW_ON_MAP);

    this.imagesTable = imagesTable;
    this.imageInfo = imageInfo;
    this.withDirection = withDirection;

    showOneOnMapItem = new JMenuItem(SHOW_THIS_IMAGE);
    boolean enabled = true; // we can always do this safely
    showOneOnMapItem.setEnabled(enabled);
    showOneOnMapItem.addActionListener(this);
    this.add(showOneOnMapItem);

    showSelectedOnMapItem = new JMenuItem(SHOW_SELECTED_IMAGES);
    enabled = !backgroundTask && (imagesTable.getSelectedRows().length > 0);
    showSelectedOnMapItem.setEnabled(enabled);
    showSelectedOnMapItem.addActionListener(this);
    this.add(showSelectedOnMapItem);

    showAllOnMapItem = new JMenuItem(SHOW_ALL_IMAGES);
    enabled = !backgroundTask;
    showAllOnMapItem.setEnabled(enabled);
    showAllOnMapItem.addActionListener(this);
    this.add(showAllOnMapItem);
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == showOneOnMapItem) {
      // show the image location on a map
      showOneImageOnMap(withDirection);
    } else if (event.getSource() == showSelectedOnMapItem) {
      showSelectedImagesOnMap(withDirection);
    } else if (event.getSource() == showAllOnMapItem) {
      showAllImagesOnMap(withDirection);
    }
  }

  /**
   * Open a web browser and show the image location on a map.
   * 
   * @param showDirection
   *          If the image direction should be shown as well
   */
  private void showOneImageOnMap(final boolean showDirection) {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
    showImagesOnMap(images, showDirection);
  }

  /**
   * Open a web browser and show selected image locations on a map.
   * 
   * @param showDirection
   *          If the image direction should be shown as well
   */
  private void showSelectedImagesOnMap(boolean showDirection) {
    ImagesTableModel tableModel = (ImagesTableModel) imagesTable.getModel();
    int[] selectedRows = imagesTable.getSelectedRows();
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      images.add(tableModel.getImageInfo(selectedRows[index]));
    }
    showImagesOnMap(images, showDirection);
  }

  /**
   * Open a web browser and show all image locations on a map.
   * 
   * @param showDirection
   *          If the image direction should be shown as well
   */
  private void showAllImagesOnMap(boolean showDirection) {
    ImagesTableModel tableModel = (ImagesTableModel) imagesTable.getModel();
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      images.add(tableModel.getImageInfo(index));
    }
    showImagesOnMap(images, showDirection);
  }

  /**
   * Make sure all images have thumbnails and show their locations on a map.
   * 
   * @param images
   * @param showDirection
   */
  private void showImagesOnMap(final List<ImageInfo> images,
      final boolean showDirection) {
    if (images.size() == 1) {
      // if there is only one image we don't need the feedback
      // coming from a BackgroundTask.. the simple Worker will suffice.
      TaskExecutor.execute(new ThumbnailWorker(images.get(0)) {
        @Override
        public void done() {
          showOnMap(images, showDirection);
        }
      });
    } else {
      // more than one thumbnail to be generated. This could take a while
      // and we use a BackgroundTask to give visual feedback via progress bar.
      TaskExecutor.execute(new ThumbnailsTask(i18n
          .tr("Generate thumbnails"), images) { //$NON-NLS-1$
            @Override
            public void done() {
              showOnMap(images, showDirection);
            }
          });
    }
  }

  /**
   * Show locations for a list of images on a map.
   * 
   * @param images
   * @param showDirection
   */
  void showOnMap(final List<ImageInfo> images, final boolean showDirection) {
    // now we gather data for our request - first the map position
    // as default we use a good example to demonstrate the difference
    // between the Airy and WGS84 geoids :-)
    String latitude = Double.toString(Airy.LATITUDE);
    String longitude = Double.toString(Airy.LONGITUDE);
    final int defaultZoomLevel = 5;
    int zoomLevel = defaultZoomLevel;
    // see if we can find a better default in the settings
    // use the last position set via Google maps
    latitude = Settings.get(SETTING.LAST_GOOGLE_MAPS_LATITUDE, latitude);
    longitude = Settings.get(SETTING.LAST_GOOGLE_MAPS_LONGITUDE, longitude);
    zoomLevel = Settings.get(SETTING.LAST_GOOGLE_MAPS_ZOOM_LEVEL, zoomLevel);
    // and zoom a bit out
    if (zoomLevel > defaultZoomLevel + 1) {
      zoomLevel -= 2;
    }
    // a better choice is a location we actually find for one of the images
    for (ImageInfo image : images) {
      if (image.getGpsLatitude() != null && image.getGpsLongitude() != null) {
        latitude = image.getGpsLatitude();
        longitude = image.getGpsLongitude();
        zoomLevel = Settings.get(SETTING.LAST_GOOGLE_MAPS_ZOOM_LEVEL,
            DEFAULT_GOOGLE_ZOOM_LEVEL);
        break;
      }
    }
    String url = "http://localhost:4321/map/map.html?"; //$NON-NLS-1$
    url = url+ "apiVersion=3&"; //$NON-NLS-1$
    url = url + "latitude=" //$NON-NLS-1$
        + latitude + "&longitude=" //$NON-NLS-1$
        + longitude + "&direction=" //$NON-NLS-1$
        + showDirection + "&zoom=" //$NON-NLS-1$
        + zoomLevel + "&images="; //$NON-NLS-1$
    for (int index = 0; index < images.size(); index++) {
      url += (index == 0 ? "" : "_") + images.get(index).getSequenceNumber(); //$NON-NLS-1$ //$NON-NLS-2$
    }
    url += "&language=" //$NON-NLS-1$
        + Locale.getDefault().getLanguage() + "&maptype=" //$NON-NLS-1$
        + Settings.get(SETTING.LAST_GOOGLE_MAPS_MAP_TYPE, "Hybrid"); //$NON-NLS-1$
    url += "&menuopen=" + Settings.get(SETTING.GOOGLE_MAPS_MENU_OPEN, true); //$NON-NLS-1$
    url += "&wheelzoom=" + Settings.get(SETTING.GOOGLE_MAPS_MOUSE_WHEEL_ZOOM, false); //$NON-NLS-1$
    url += "&showtracks=" + Settings.get(SETTING.GOOGLE_MAP_SHOW_TRACKS, false); //$NON-NLS-1$
    url += "&wikipedia=" + Settings.get(SETTING.GOOGLE_MAP_SHOW_WIKIPEDIA, false); //$NON-NLS-1$
    // execute the command
    System.out.println(url);
    BrowserLauncher
        .openURL(Settings.get(SETTING.BROWSER, null), url.toString());
  }

}
