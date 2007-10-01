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

package org.fibs.geotag.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.fibs.geotag.Messages;
import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.exif.Exiftool;
import org.fibs.geotag.googleearth.GoogleEarthLauncher;
import org.fibs.geotag.image.ThumbnailWorker;
import org.fibs.geotag.table.ImagesTable;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.tasks.CopyLocationTask;
import org.fibs.geotag.tasks.ExifWriterTask;
import org.fibs.geotag.tasks.FillGapsTask;
import org.fibs.geotag.tasks.MatchImagesTask;
import org.fibs.geotag.tasks.SetOffsetTask;
import org.fibs.geotag.tasks.ThumbnailsTask;
import org.fibs.geotag.tasks.UndoableBackgroundTask;
import org.fibs.geotag.track.TrackMatcher;
import org.fibs.geotag.track.TrackStore;
import org.fibs.geotag.util.Airy;

import com.centerkey.utils.BareBonesBrowserLaunch;

/**
 * @author Andreas Schneider
 * 
 */
/**
 * @author Andreas Schneider
 * 
 */
/**
 * A context menu for a image table row. All actions that can be undone should
 * be run as a {@link UndoableBackgroundTask}
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class ImagesTablePopupMenu extends JPopupMenu implements ActionListener {

  /** An ellipsis of three dots */
  private static final String ELLIPSIS = "..."; //$NON-NLS-1$

  /** Text for sub menu */
  private static final String SHOW_ON_MAP = Messages
      .getString("ImagesTablePopupMenu.ShowOnMap"); //$NON-NLS-1$

  /** Text for sub menu item */
  private static final String SHOW_ON_MAP_WITH_DIRECTION = Messages
      .getString("ImagesTablePopupMenu.ShowOnMapWithDirection"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String SHOW_THIS_IMAGE = Messages
      .getString("ImagesTablePopupMenu.ThisImage"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String SHOW_SELECTED_IMAGES = Messages
      .getString("ImagesTablePopupMenu.SelectedImages"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String SHOW_ALL_IMAGES = Messages
      .getString("ImagesTablePopupMenu.AllImages"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String SHOW_IN_GOOGLEEARTH = Messages
      .getString("ImagesTablePopupMenu.ShowInGoogleEarth"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String SELECT_CORRECT_TIME = Messages
      .getString("ImagesTablePopupMenu.SelectCorrectTimeForImage"); //$NON-NLS-1$

  /** Text for sub menu */
  private static final String COPY_TIME_OFFSET = Messages
      .getString("ImagesTablePopupMenu.CopyTimeOffset"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String COPY_TIME_OFFSET_SELECTED = Messages
      .getString("ImagesTablePopupMenu.CopyTimeOffsetToSelectedImages"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String COPY_TIME_OFFSET_ALL = Messages
      .getString("ImagesTablePopupMenu.CopyTimeOffsetToAllImages"); //$NON-NLS-1$

  /** Text for sub menu */
  private static final String MATCH_TRACKS = Messages
      .getString("ImagesTablePopupMenu.MatchTracks"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String MATCH_TRACK_THIS = Messages
      .getString("ImagesTablePopupMenu.MatchTracksToThisImage"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String MATCH_TRACK_SELECTED = Messages
      .getString("ImagesTablePopupMenu.MatchTracksToSelectedImages"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String MATCH_TRACK_ALL = Messages
      .getString("ImagesTablePopupMenu.MatchTracksToAllImages"); //$NON-NLS-1$

  /** Text for sub menu */
  private static final String COPY_LOCATION = Messages
      .getString("ImagesTablePopupMenu.CopyLocation"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String COPY_LOCATION_PREVIOUS = Messages
      .getString("ImagesTablePopupMenu.CopyLocationToPrevious"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String COPY_LOCATION_NEXT = Messages
      .getString("ImagesTablePopupMenu.CopyLocationToNext"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String COPY_LOCATION_SELECTED = Messages
      .getString("ImagesTablePopupMenu.CopyLocationToSelected"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String COPY_LOCATION_ALL = Messages
      .getString("ImagesTablePopupMenu.CopyLocationToAll"); //$NON-NLS-1$

  /** Text for sub menu */
  private static final String FILL_GAPS = Messages
      .getString("ImagesTablePopupMenu.FillGaps"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String FILL_THIS_GAP = Messages
      .getString("ImagesTablePopupMenu.FillThisGap"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String FILL_SELECTED_GAPS = Messages
      .getString("ImagesTablePopupMenu.FillSelectedGaps"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String FILL_ALL_GAPS = Messages
      .getString("ImagesTablePopupMenu.FillAllGaps"); //$NON-NLS-1$

  /** Text for sub menu */
  private static final String SAVE_LOCATIONS = Messages
      .getString("ImagesTablePopupMenu.SaveNewLocations"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String SAVE_THIS_LOCATION = Messages
      .getString("ImagesTablePopupMenu.SaveThisImage"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String SAVE_SELECTED_LOCATIONS = Messages
      .getString("ImagesTablePopupMenu.SaveSelectedImages"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String SAVE_ALL_LOCATIONS = Messages
      .getString("ImagesTablePopupMenu.SaveAllImages"); //$NON-NLS-1$

  /** The parent JFrame */
  JFrame parent;

  /** The table displaying the image data */
  ImagesTable imagesTable;

  /** the table model is needed here all time, so we keep a reference */
  ImagesTableModel tableModel;

  /** matches tracks and time stamps */
  TrackMatcher trackMatcher;

  /**
   * An array containing the indices of selected row or an empty array if no
   * rows are selected.
   */
  int[] selectedRows;

  /** The menu item used to show an image location on a map */
  private JMenuItem showOneOnMapItem;

  /** The menu item used to show selected image locations on a map */
  private JMenuItem showSelectedOnMapItem;

  /** The menu item used to show all image locations on a map */
  private JMenuItem showAllOnMapItem;

  /** The menu item used to show an image location and direction on a map */
  private JMenuItem showOneOnMapWithDirectionItem;

  /** The menu item used to show selected image locations and directions on a map */
  private JMenuItem showSelectedOnMapWithDirectionItem;

  /** The menu item used to show all image locations and directions on a map */
  private JMenuItem showAllOnMapWithDirectionItem;

  /** The menu item used to show an image location in Google Earth */
  private JMenuItem showInGoogleEarthItem;

  /** The menu item used to select the correct GMT time for a picture */
  private JMenuItem chooseTimeItem;

  /** The menu item used to copy the offset to all pictures */
  private JMenuItem copyOffsetToAllItem;

  /** The menu item used to copy the offset to selected pictures */
  private JMenuItem copyOffsetToSelectedItem;

  /** The menu item used to apply the track data to one image */
  private JMenuItem matchTrackToOneImageItem;

  /** The menu item used to apply the track data to selected images */
  private JMenuItem matchTrackToSelectedImagesItem;

  /** The menu item used to apply the track data to all images */
  private JMenuItem matchTrackToAllImagesItem;

  /** The menu item used to copy the location to the previous image */
  private JMenuItem copyLocationToPreviousItem;

  /** The menu item used to copy the location to the next image */
  private JMenuItem copyLocationToNextItem;

  /** The menu item used to copy the location to selected images */
  private JMenuItem copyLocationToSelectedItem;

  /** The menu item used to copy the location to all images */
  private JMenuItem copyLocationToAllItem;

  /** The menu item used to fill a one image gap */
  private JMenuItem fillThisGapItem;

  /** The menu item used to fill gaps in a selection */
  private JMenuItem fillGapsInSelectionItem;

  /** The menu item used to fill all gaps */
  private JMenuItem fillAllGapsItem;

  /** The menu item used to save the location of an image */
  private JMenuItem saveOneLocationItem;

  /** The menu item used to save new locations of selected images */
  private JMenuItem saveSelectedLocationsItem;

  /** The menu item used to save new locations for all images */
  private JMenuItem saveAllLocationsItem;

  /** the {@link ImageInfo} for the image in the row of the table */
  ImageInfo imageInfo;

  /**
   * Create a {@link ImagesTablePopupMenu} for a given {@link ImagesTable} and
   * row
   * 
   * @param parent
   *          The parent JFrame
   * @param imagesTable
   *          The {@link ImagesTable}
   * @param row
   *          The row of the {@link ImagesTable}
   * @param trackMatcher
   *          matches tracks to time stamps
   * @param backgroundTask
   *          True if a background task is running (most menu items will be
   *          disabled)
   */
  public ImagesTablePopupMenu(JFrame parent, ImagesTable imagesTable, int row,
      TrackMatcher trackMatcher, boolean backgroundTask) {
    this.parent = parent;
    this.imagesTable = imagesTable;
    this.tableModel = (ImagesTableModel) imagesTable.getModel();
    this.trackMatcher = trackMatcher;
    // where are the tracks stored?
    TrackStore trackStore = TrackStore.getTrackStore();
    // find out which rows are selected. Some popups only make sense if rows are
    // selected.
    selectedRows = imagesTable.getSelectedRows();
    imageInfo = ((ImagesTableModel) imagesTable.getModel()).getImageInfo(row);
    // Create all the menu items and separators for this pop up menu and decide
    // which ones should be enabled

    JMenuItem headerItem = new JMenuItem(imageInfo.getName());
    int fontSize = headerItem.getFont().getSize();
    int fontStyle = headerItem.getFont().getStyle() + 10;
    Font font = new Font(headerItem.getFont().getName(), fontStyle, fontSize);
    headerItem.setFont(font);
    headerItem.setEnabled(false);
    add(headerItem);
    add(new JSeparator());

    JMenu showOnMapMenu = new JMenu(SHOW_ON_MAP);

    showOneOnMapItem = new JMenuItem(SHOW_THIS_IMAGE);
    boolean enabled = true; // we can always do this safely
    showOneOnMapItem.setEnabled(enabled);
    showOneOnMapItem.addActionListener(this);
    showOnMapMenu.add(showOneOnMapItem);

    showSelectedOnMapItem = new JMenuItem(SHOW_SELECTED_IMAGES);
    enabled = !backgroundTask && (selectedRows.length > 0);
    showSelectedOnMapItem.setEnabled(enabled);
    showSelectedOnMapItem.addActionListener(this);
    showOnMapMenu.add(showSelectedOnMapItem);

    showAllOnMapItem = new JMenuItem(SHOW_ALL_IMAGES);
    enabled = !backgroundTask;
    showAllOnMapItem.setEnabled(enabled);
    showAllOnMapItem.addActionListener(this);
    showOnMapMenu.add(showAllOnMapItem);

    add(showOnMapMenu);

    JMenu showOnMapWithDirectionMenu = new JMenu(SHOW_ON_MAP_WITH_DIRECTION);

    showOneOnMapWithDirectionItem = new JMenuItem(SHOW_THIS_IMAGE);
    enabled = true; // we can always do this safely
    showOneOnMapWithDirectionItem.setEnabled(enabled);
    showOneOnMapWithDirectionItem.addActionListener(this);
    showOnMapWithDirectionMenu.add(showOneOnMapWithDirectionItem);

    showSelectedOnMapWithDirectionItem = new JMenuItem(SHOW_SELECTED_IMAGES);
    enabled = !backgroundTask && (selectedRows.length > 0);
    showSelectedOnMapWithDirectionItem.setEnabled(enabled);
    showSelectedOnMapWithDirectionItem.addActionListener(this);
    showOnMapWithDirectionMenu.add(showSelectedOnMapWithDirectionItem);

    showAllOnMapWithDirectionItem = new JMenuItem(SHOW_ALL_IMAGES);
    enabled = !backgroundTask;
    showAllOnMapWithDirectionItem.setEnabled(enabled);
    showAllOnMapWithDirectionItem.addActionListener(this);
    showOnMapWithDirectionMenu.add(showAllOnMapWithDirectionItem);

    add(showOnMapWithDirectionMenu);

    showInGoogleEarthItem = new JMenuItem(SHOW_IN_GOOGLEEARTH);
    enabled = true; // we can always do this safely
    showInGoogleEarthItem.setEnabled(enabled);
    showInGoogleEarthItem.addActionListener(this);
    add(showInGoogleEarthItem);

    chooseTimeItem = new JMenuItem(SELECT_CORRECT_TIME + ELLIPSIS);
    enabled = !backgroundTask; // only if no background task
    chooseTimeItem.setEnabled(enabled);
    chooseTimeItem.addActionListener(this);
    add(chooseTimeItem);

    JMenu copyOffsetMenu = new JMenu(COPY_TIME_OFFSET);
    boolean addMenu = false;

    copyOffsetToSelectedItem = new JMenuItem(COPY_TIME_OFFSET_SELECTED);
    // enable if there is no background task and there is a selection
    enabled = !backgroundTask && selectedRows.length > 0;
    addMenu |= enabled;
    copyOffsetToSelectedItem.setEnabled(enabled);
    copyOffsetToSelectedItem.addActionListener(this);
    copyOffsetMenu.add(copyOffsetToSelectedItem);

    copyOffsetToAllItem = new JMenuItem(COPY_TIME_OFFSET_ALL);
    enabled = !backgroundTask;
    addMenu |= enabled;
    copyOffsetToAllItem.setEnabled(enabled);
    copyOffsetToAllItem.addActionListener(this);
    copyOffsetMenu.add(copyOffsetToAllItem);

    if (addMenu) {
      add(copyOffsetMenu);
    }

    JMenu matchTracksMenu = new JMenu(MATCH_TRACKS);
    addMenu = false;

    matchTrackToOneImageItem = new JMenuItem(MATCH_TRACK_THIS);
    // enable if there is no background task and tracks have been loaded
    enabled = !backgroundTask && trackStore.hasTracks();
    addMenu |= enabled;
    matchTrackToOneImageItem.setEnabled(enabled);
    matchTrackToOneImageItem.addActionListener(this);
    matchTracksMenu.add(matchTrackToOneImageItem);

    matchTrackToSelectedImagesItem = new JMenuItem(MATCH_TRACK_SELECTED);
    // enable if there is no background task, there are tracks and a selection
    enabled = !backgroundTask && trackStore.hasTracks()
        && (selectedRows.length > 0);
    addMenu |= enabled;
    matchTrackToSelectedImagesItem.setEnabled(enabled);
    matchTrackToSelectedImagesItem.addActionListener(this);
    matchTracksMenu.add(matchTrackToSelectedImagesItem);

    matchTrackToAllImagesItem = new JMenuItem(MATCH_TRACK_ALL);
    // enable if there is no background task and there are tracks available
    enabled = !backgroundTask && trackStore.hasTracks();
    addMenu |= enabled;
    matchTrackToAllImagesItem.setEnabled(enabled);
    matchTrackToAllImagesItem.addActionListener(this);
    matchTracksMenu.add(matchTrackToAllImagesItem);

    if (addMenu) {
      add(matchTracksMenu);
    }

    JMenu copyLocationMenu = new JMenu(COPY_LOCATION);
    addMenu = false;

    copyLocationToPreviousItem = new JMenuItem(COPY_LOCATION_PREVIOUS);
    // Enable if there is no background task and this image has a location and
    // is not the first image
    enabled = !backgroundTask && imageInfo.hasLocation() && row > 0;
    addMenu |= enabled;
    copyLocationToPreviousItem.setEnabled(enabled);
    copyLocationToPreviousItem.addActionListener(this);
    copyLocationMenu.add(copyLocationToPreviousItem);

    copyLocationToNextItem = new JMenuItem(COPY_LOCATION_NEXT);
    // Enable if there is no background task and this image has a location and
    // is not the last image
    enabled = !backgroundTask && imageInfo.hasLocation()
        && row < tableModel.getRowCount() - 1;
    addMenu |= enabled;
    copyLocationToNextItem.setEnabled(enabled);
    copyLocationToNextItem.addActionListener(this);
    copyLocationMenu.add(copyLocationToNextItem);

    copyLocationToSelectedItem = new JMenuItem(COPY_LOCATION_SELECTED);
    // Enable if there is no background task and this image has a location and
    // there is a selection
    enabled = !backgroundTask && imageInfo.hasLocation()
        && selectedRows.length > 0;
    addMenu |= enabled;
    copyLocationToSelectedItem.setEnabled(enabled);
    copyLocationToSelectedItem.addActionListener(this);
    copyLocationMenu.add(copyLocationToSelectedItem);

    copyLocationToAllItem = new JMenuItem(COPY_LOCATION_ALL);
    // Enable if there is no background task and this image has a location
    enabled = !backgroundTask && imageInfo.hasLocation();
    addMenu |= enabled;
    copyLocationToAllItem.setEnabled(enabled);
    copyLocationToAllItem.addActionListener(this);
    copyLocationMenu.add(copyLocationToAllItem);

    if (addMenu) {
      add(copyLocationMenu);
    }

    JMenu fillGapsMenu = new JMenu(FILL_GAPS);
    addMenu = false;

    fillThisGapItem = new JMenuItem(FILL_THIS_GAP);
    // Enable if there is no background task, there is track data available and
    // we don't have coordinates for this image yet.
    enabled = !backgroundTask && trackStore.hasTracks()
        && imageInfo.hasLocation() == false;
    addMenu |= enabled;
    fillThisGapItem.setEnabled(enabled);
    fillThisGapItem.addActionListener(this);
    fillGapsMenu.add(fillThisGapItem);

    fillGapsInSelectionItem = new JMenuItem(FILL_SELECTED_GAPS);
    // enable if there is no background task, there is a selection, we have
    // tracks and there is at least one image in the selection with
    // no coordinates
    enabled = false;
    for (int i = 0; i < selectedRows.length; i++) {
      if (tableModel.getImageInfo(selectedRows[i]).hasLocation() == false) {
        enabled = !backgroundTask && trackStore.hasTracks();
        break;
      }
    }
    addMenu |= enabled;
    fillGapsInSelectionItem.setEnabled(enabled);
    fillGapsInSelectionItem.addActionListener(this);
    fillGapsMenu.add(fillGapsInSelectionItem);

    fillAllGapsItem = new JMenuItem(FILL_ALL_GAPS);
    // enabled if there is no background task, we have track data and there
    // is at least one image without coordinates
    enabled = false;
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      if (tableModel.getImageInfo(i).hasLocation() == false) {
        enabled = !backgroundTask && trackStore.hasTracks();
        break;
      }
    }
    addMenu |= enabled;
    fillAllGapsItem.setEnabled(enabled);
    fillAllGapsItem.addActionListener(this);
    fillGapsMenu.add(fillAllGapsItem);

    if (addMenu) {
      add(fillGapsMenu);
    }

    JMenu saveLocationsMenu = new JMenu(SAVE_LOCATIONS);
    // enable if exiftool is available
    saveLocationsMenu.setEnabled(Exiftool.isAvailable());
    addMenu = false;

    saveOneLocationItem = new JMenuItem(SAVE_THIS_LOCATION);
    // enabled if there is no background task, the image has a location that's
    // not coming from the image itself
    enabled = !backgroundTask && imageInfo.hasNewLocation();
    addMenu |= enabled;
    saveOneLocationItem.setEnabled(enabled);
    saveOneLocationItem.addActionListener(this);
    saveLocationsMenu.add(saveOneLocationItem);

    saveSelectedLocationsItem = new JMenuItem(SAVE_SELECTED_LOCATIONS);
    // enable if there is no background task, there is a selection of images
    // and at least one image in the selection has a new location
    enabled = false;
    for (int i = 0; i < selectedRows.length; i++) {
      if (tableModel.getImageInfo(selectedRows[i]).hasNewLocation() == true) {
        enabled = !backgroundTask;
        break;
      }
    }
    addMenu |= enabled;
    saveSelectedLocationsItem.setEnabled(enabled);
    saveSelectedLocationsItem.addActionListener(this);
    saveLocationsMenu.add(saveSelectedLocationsItem);

    saveAllLocationsItem = new JMenuItem(SAVE_ALL_LOCATIONS);
    // enable if there is no background task and there is at least one image
    // that has a new location
    enabled = false;
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      if (tableModel.getImageInfo(i).hasNewLocation() == true) {
        enabled = !backgroundTask;
        break;
      }
    }
    addMenu |= enabled;
    saveAllLocationsItem.setEnabled(enabled);
    saveAllLocationsItem.addActionListener(this);
    saveLocationsMenu.add(saveAllLocationsItem);

    if (addMenu) {
      add(saveLocationsMenu);
    }
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent event) {
    // determine which menu item was selected
    if (event.getSource() == showOneOnMapItem) {
      // show the image location on a map
      showOneImageOnMap(false);
    } else if (event.getSource() == showSelectedOnMapItem) {
      showSelectedImagesOnMap(false);
    } else if (event.getSource() == showAllOnMapItem) {
      showAllImagesOnMap(false);
    } else if (event.getSource() == showOneOnMapWithDirectionItem) {
      showOneImageOnMap(true);
    } else if (event.getSource() == showSelectedOnMapWithDirectionItem) {
      showSelectedImagesOnMap(true);
    } else if (event.getSource() == showAllOnMapWithDirectionItem) {
      showAllImagesOnMap(true);
    } else if (event.getSource() == showInGoogleEarthItem) {
      showInGoogleEarth();
    } else if (event.getSource() == chooseTimeItem) {
      // Open a DateTimeChooser to select the exact time for the image.
      chooseTime();
    } else if (event.getSource() == copyOffsetToAllItem) {
      copyOffsetToAll();
    } else if (event.getSource() == copyOffsetToSelectedItem) {
      copyOffsetToSelected();
    } else if (event.getSource() == matchTrackToOneImageItem) {
      matchTracksToOneImage();
    } else if (event.getSource() == matchTrackToSelectedImagesItem) {
      matchTracksToSelectedImages();
    } else if (event.getSource() == matchTrackToAllImagesItem) {
      matchTracksToAllImages();
    } else if (event.getSource() == copyLocationToPreviousItem) {
      copyLocationToPrevious();
    } else if (event.getSource() == copyLocationToNextItem) {
      copyLocationToNext();
    } else if (event.getSource() == copyLocationToSelectedItem) {
      copyLocationToSelected();
    } else if (event.getSource() == copyLocationToAllItem) {
      copyLocationToAll();
    } else if (event.getSource() == fillThisGapItem) {
      fillThisGap();
    } else if (event.getSource() == fillGapsInSelectionItem) {
      fillGapsInSelection();
    } else if (event.getSource() == fillAllGapsItem) {
      fillAllGaps();
    } else if (event.getSource() == saveOneLocationItem) {
      saveOneLocation();
    } else if (event.getSource() == saveSelectedLocationsItem) {
      saveSelectedLocations();
    } else if (event.getSource() == saveAllLocationsItem) {
      saveAllLocations();
    }
  }

  /**
   * Open a web browser and show the image location on a map
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
   * Open a web browser and show selected image locations on a map
   * 
   * @param showDirection
   *          If the image direction should be shown as well
   */
  private void showSelectedImagesOnMap(boolean showDirection) {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      images.add(tableModel.getImageInfo(selectedRows[index]));
    }
    showImagesOnMap(images, showDirection);
  }

  /**
   * Open a web browser and show all image locations on a map
   * 
   * @param showDirection
   *          If the image direction should be shown as well
   */
  private void showAllImagesOnMap(boolean showDirection) {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      images.add(tableModel.getImageInfo(i));
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
      new ThumbnailWorker(images.get(0)) {
        @Override
        public void done() {
          showOnMap(images, showDirection);
        }
      }.execute();
    } else {
      // more than one thumbnail to be generated. This could take a while
      // and we use a BackgroundTask to give visual feedback via progress bar.
      new ThumbnailsTask(Messages
          .getString("ImagesTablePopupMenu.GenerateThumbnails"), images) { //$NON-NLS-1$
        @Override
        public void done() {
          showOnMap(images, showDirection);
        }
      }.execute();
    }
  }

  /**
   * Show locations for a list of images on a map
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
    int zoomLevel = 5;
    // see if we can find a better default in the settings
    // use the last position set via Google maps
    latitude = Settings.get(SETTING.LAST_GOOGLE_MAPS_LATITUDE, latitude);
    longitude = Settings.get(SETTING.LAST_GOOGLE_MAPS_LONGITUDE, longitude);
    zoomLevel = Settings.get(SETTING.LAST_GOOGLE_MAPS_ZOOM_LEVEL, zoomLevel);
    // and zoom a bit out
    if (zoomLevel > 6) {
      zoomLevel -= 2;
    }
    // a better choice is a location we actually find for one of the images
    for (ImageInfo image : images) {
      if (image.getGPSLatitude() != null && image.getGPSLongitude() != null) {
        latitude = image.getGPSLatitude();
        longitude = image.getGPSLongitude();
        zoomLevel = Settings.get(SETTING.LAST_GOOGLE_MAPS_ZOOM_LEVEL, 15);
        break;
      }
    }
    String URL = "http://localhost:4321/map/map.html?" + //$NON-NLS-1$
        "latitude=" //$NON-NLS-1$
        + latitude + "&longitude=" //$NON-NLS-1$
        + longitude + "&direction=" //$NON-NLS-1$
        + showDirection + "&zoom=" //$NON-NLS-1$
        + zoomLevel + "&images="; //$NON-NLS-1$
    for (int index = 0; index < images.size(); index++) {
      URL += (index == 0 ? "" : "_") + images.get(index).getSequenceNumber(); //$NON-NLS-1$ //$NON-NLS-2$
    }
    URL += "&language=" //$NON-NLS-1$
        + Locale.getDefault().getLanguage() + "&maptype=" //$NON-NLS-1$
        + Settings.get(SETTING.LAST_GOOGLE_MAPS_MAP_TYPE, "Hybrid"); //$NON-NLS-1$
    // execute the command
    System.out.println(URL);
    BareBonesBrowserLaunch.openURL(Settings.get(SETTING.BROWSER, null), URL
        .toString());
  }

  /**
   * Lauch Google Earth to show the location
   */
  private void showInGoogleEarth() {
    // make sure there is a thumbnail - this won't create the
    // thumbnail again, if it already exists.
    ThumbnailWorker worker = new ThumbnailWorker(imageInfo) {
      @Override
      protected void done() {
        GoogleEarthLauncher.launch(imageInfo);
      }
    };
    worker.execute();
  }

  /**
   * select the exact time the image was taken
   */
  private void chooseTime() {
    // This will allow us to calculate an offset to GMT
    DateFormat dateFormat = new SimpleDateFormat(ImageInfo
        .getDateFormatPattern());
    try {
      Date cameraDate = dateFormat.parse(imageInfo.getCameraDate());
      Calendar createCalendar = Calendar.getInstance();
      createCalendar.setTime(cameraDate);
      DateTimeChooser chooser = new DateTimeChooser(
          parent,
          Messages.getString("ImagesTablePopupMenu.SelectDateAndTime"), createCalendar, false); //$NON-NLS-1$
      Calendar chosenDate = chooser.openChooser();
      if (chosenDate != null) {
        // user has picked a date for this picture
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
        String gmtTime = dateFormat.format(chosenDate.getTime());
        final int offset = ImageInfo.calculateOffset(gmtTime, imageInfo
            .getCameraDate());
        List<ImageInfo> imageList = new ArrayList<ImageInfo>();
        imageList.add(imageInfo);
        SetOffsetTask setOffsetTask = new SetOffsetTask(null,
            SELECT_CORRECT_TIME, tableModel, offset, imageList) {
          @Override
          protected void done() {
            super.done();
            String message = Messages
                .getString("ImagesTablePopupMenu.UseTimeDifferenceforAll"); //$NON-NLS-1$
            if (JOptionPane.showConfirmDialog(parent, message, ImageInfo
                .getOffsetString(offset), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
              copyOffsetToAll();
            }
            tableModel.sortRows();
          }

        };
        setOffsetTask.execute();
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Copy the offset of the current picture to all other pictures
   */
  void copyOffsetToAll() {
    int offset = imageInfo.getOffset();
    List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      ImageInfo image = tableModel.getImageInfo(i);
      imageList.add(image);
    }
    new SetOffsetTask(COPY_TIME_OFFSET, COPY_TIME_OFFSET_ALL, tableModel,
        offset, imageList).execute();
  }

  /**
   * Copy the offset to the selected pictures
   */
  private void copyOffsetToSelected() {
    int offset = imageInfo.getOffset();
    List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      ImageInfo image = tableModel.getImageInfo(selectedRows[index]);
      imageList.add(image);
    }
    new SetOffsetTask(COPY_TIME_OFFSET, COPY_TIME_OFFSET_SELECTED, tableModel,
        offset, imageList).execute();
  }

  /**
   * Use the {@link TrackMatcher} to find coordinates for one image
   */
  private void matchTracksToOneImage() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
    new MatchImagesTask(MATCH_TRACKS, MATCH_TRACK_SELECTED, imagesTable,
        trackMatcher, images).execute();
  }

  /**
   * Use the {@link TrackMatcher} to find coordinates for all selected images
   */
  private void matchTracksToSelectedImages() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      images.add(tableModel.getImageInfo(selectedRows[index]));
    }
    new MatchImagesTask(MATCH_TRACKS, MATCH_TRACK_SELECTED, imagesTable,
        trackMatcher, images).execute();
  }

  /**
   * Use the {@link TrackMatcher} to find coordinates for all images
   */
  private void matchTracksToAllImages() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      images.add(tableModel.getImageInfo(i));
    }
    new MatchImagesTask(MATCH_TRACKS, MATCH_TRACK_ALL, imagesTable,
        trackMatcher, images).execute();
  }

  /**
   * Copy the location to the previous image
   */
  private void copyLocationToPrevious() {
    int row = tableModel.getRow(imageInfo);
    if (row > 0) {
      List<ImageInfo> imageList = new ArrayList<ImageInfo>();
      imageList.add(tableModel.getImageInfo(row - 1));
      new CopyLocationTask(COPY_LOCATION, COPY_LOCATION_PREVIOUS, tableModel,
          imageInfo, imageList).execute();
    }
  }

  /**
   * Copy the location to the next image
   */
  private void copyLocationToNext() {
    int row = tableModel.getRow(imageInfo);
    if (row < tableModel.getRowCount() - 1) {
      List<ImageInfo> imageList = new ArrayList<ImageInfo>();
      imageList.add(tableModel.getImageInfo(row + 1));
      new CopyLocationTask(COPY_LOCATION, COPY_LOCATION_NEXT, tableModel,
          imageInfo, imageList).execute();
    }
  }

  /**
   * Copy the location to all selected images
   */
  private void copyLocationToSelected() {
    List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      imageList.add(tableModel.getImageInfo(selectedRows[index]));
    }
    new CopyLocationTask(COPY_LOCATION, COPY_LOCATION_SELECTED, tableModel,
        imageInfo, imageList).execute();
  }

  /**
   * Copy the location to all images
   */
  private void copyLocationToAll() {
    List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      imageList.add(tableModel.getImageInfo(i));
    }
    new CopyLocationTask(COPY_LOCATION, COPY_LOCATION_ALL, tableModel,
        imageInfo, imageList).execute();
  }

  /**
   * Fill the gap this image leaves
   */
  private void fillThisGap() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
    new FillGapsTask(FILL_GAPS, FILL_THIS_GAP, trackMatcher, tableModel, images)
        .execute();
  }

  /**
   * Use adjacent coordinates to fill in the gaps by interpolation
   */
  private void fillGapsInSelection() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      ImageInfo candidate = tableModel.getImageInfo(selectedRows[index]);
      if (candidate.hasLocation() == false) {
        images.add(candidate);
      }
    }
    new FillGapsTask(FILL_GAPS, FILL_SELECTED_GAPS, trackMatcher, tableModel,
        images).execute();
  }

  /**
   * Have a go at guessing coordinates for all images without them. This is
   * probably not the wisest thing to do, but we offer the possibility anyway as
   * it can be un-done
   */
  private void fillAllGaps() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      ImageInfo candidate = tableModel.getImageInfo(i);
      if (candidate.hasLocation() == false) {
        images.add(candidate);
      }
    }
    new FillGapsTask(FILL_GAPS, FILL_ALL_GAPS, trackMatcher, tableModel, images)
        .execute();
  }

  /**
   * Save the EXIF data to all images in the list
   * 
   * @param images
   */
  private void saveLocations(List<ImageInfo> images) {
    new ExifWriterTask(Messages
        .getString("ImagesTablePopupMenu.SaveNewLocations"), tableModel, images).execute(); //$NON-NLS-1$
  }

  /**
   * Save the EXIF data to this image
   */
  private void saveOneLocation() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
    saveLocations(images);
  }

  /**
   * Save the EXIF data to all selected images
   */
  private void saveSelectedLocations() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      ImageInfo candidate = tableModel.getImageInfo(selectedRows[index]);
      if (candidate.hasNewLocation() == true) {
        images.add(candidate);
      }
    }
    saveLocations(images);
  }

  /**
   * Save the EXIF data to all images
   */
  private void saveAllLocations() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      ImageInfo candidate = tableModel.getImageInfo(i);
      if (candidate.hasNewLocation() == true) {
        images.add(candidate);
      }
    }
    saveLocations(images);
  }

}
