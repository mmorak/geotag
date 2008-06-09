/**
 * Geotag
 * Copyright (C) 2007,2008 Andreas Schneider
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
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.exif.Exiftool;
import org.fibs.geotag.geonames.Location;
import org.fibs.geotag.googleearth.GoogleEarthLauncher;
import org.fibs.geotag.googleearth.GoogleearthFileFilter;
import org.fibs.geotag.i18n.Messages;
import org.fibs.geotag.image.ThumbnailWorker;
import org.fibs.geotag.table.ImagesTable;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.tasks.CopyLocationNameTask;
import org.fibs.geotag.tasks.CopyLocationTask;
import org.fibs.geotag.tasks.ExifWriterTask;
import org.fibs.geotag.tasks.FillGapsTask;
import org.fibs.geotag.tasks.FindAltitudeTask;
import org.fibs.geotag.tasks.GoogleEarthExportTask;
import org.fibs.geotag.tasks.LocationNamesTask;
import org.fibs.geotag.tasks.MatchImagesTask;
import org.fibs.geotag.tasks.RemoveImagesTask;
import org.fibs.geotag.tasks.SelectLocationNameTask;
import org.fibs.geotag.tasks.SetOffsetTask;
import org.fibs.geotag.tasks.ThumbnailsTask;
import org.fibs.geotag.track.TrackStore;
import org.fibs.geotag.util.Airy;
import org.fibs.geotag.util.BrowserLauncher;
import org.fibs.geotag.util.Units;
import org.fibs.geotag.util.Units.DISTANCE;

/**
 * A context menu for a image table row. All actions that can be undone should
 * be run as a UndoableBackgroundTask
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class ImagesTablePopupMenu extends JPopupMenu implements ActionListener {

  /** the default Google zoom level. */
  private static final int DEFAULT_GOOGLE_ZOOM_LEVEL = 15;

  /** An ellipsis of three dots. */
  private static final String ELLIPSIS = "..."; //$NON-NLS-1$

  /** Text for sub menu. */
  private static final String SHOW_ON_MAP = Messages
      .getString("ImagesTablePopupMenu.ShowOnMap"); //$NON-NLS-1$

  /** Text for sub menu item. */
  private static final String SHOW_ON_MAP_WITH_DIRECTION = Messages
      .getString("ImagesTablePopupMenu.ShowOnMapWithDirection"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String SHOW_THIS_IMAGE = Messages
      .getString("ImagesTablePopupMenu.ThisImage"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String SHOW_SELECTED_IMAGES = Messages
      .getString("ImagesTablePopupMenu.SelectedImages"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String SHOW_ALL_IMAGES = Messages
      .getString("ImagesTablePopupMenu.AllImages"); //$NON-NLS-1$

  /** Text for sub menu. */
  private static final String GOOGLEEARTH = Messages
      .getString("ImagesTablePopupMenu.GoogleEarth"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String SHOW_IN_GOOGLEEARTH = Messages
      .getString("ImagesTablePopupMenu.ShowInGoogleEarth"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String EXPORT_THIS = Messages
      .getString("ImagesTablePopupMenu.ExportThis"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String EXPORT_SELECTED = Messages
      .getString("ImagesTablePopupMenu.ExportSelected"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String EXPORT_ALL = Messages
      .getString("ImagesTablePopupMenu.ExportAll"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String SELECT_CORRECT_TIME = Messages
      .getString("ImagesTablePopupMenu.SelectCorrectTimeForImage"); //$NON-NLS-1$

  /** Text for sub menu. */
  private static final String COPY_TIME_OFFSET = Messages
      .getString("ImagesTablePopupMenu.CopyTimeOffset"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_TIME_OFFSET_SELECTED = Messages
      .getString("ImagesTablePopupMenu.CopyTimeOffsetToSelectedImages"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_TIME_OFFSET_ALL = Messages
      .getString("ImagesTablePopupMenu.CopyTimeOffsetToAllImages"); //$NON-NLS-1$

  /** Text for sub menu. */
  private static final String MATCH_TRACKS = Messages
      .getString("ImagesTablePopupMenu.MatchTracks"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String MATCH_TRACK_THIS = Messages
      .getString("ImagesTablePopupMenu.MatchTracksToThisImage"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String MATCH_TRACK_SELECTED = Messages
      .getString("ImagesTablePopupMenu.MatchTracksToSelectedImages"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String MATCH_TRACK_ALL = Messages
      .getString("ImagesTablePopupMenu.MatchTracksToAllImages"); //$NON-NLS-1$

  /** Text for sub menu. */
  private static final String COPY_LOCATION = Messages
      .getString("ImagesTablePopupMenu.CopyLocation"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_LOCATION_PREVIOUS = Messages
      .getString("ImagesTablePopupMenu.CopyLocationToPrevious"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_LOCATION_NEXT = Messages
      .getString("ImagesTablePopupMenu.CopyLocationToNext"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_LOCATION_SELECTED = Messages
      .getString("ImagesTablePopupMenu.CopyLocationToSelected"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_LOCATION_ALL = Messages
      .getString("ImagesTablePopupMenu.CopyLocationToAll"); //$NON-NLS-1$

  /** Text for sub menu. */
  private static final String FILL_GAPS = Messages
      .getString("ImagesTablePopupMenu.FillGaps"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String FILL_THIS_GAP = Messages
      .getString("ImagesTablePopupMenu.FillThisGap"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String FILL_SELECTED_GAPS = Messages
      .getString("ImagesTablePopupMenu.FillSelectedGaps"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String FILL_ALL_GAPS = Messages
      .getString("ImagesTablePopupMenu.FillAllGaps"); //$NON-NLS-1$

  /** Text fir sub menu. */
  private static final String LOCATION_NAMES = Messages
      .getString("ImagesTablePopupMenu.LocationNames"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String LOCATION_NAME_THIS = Messages
      .getString("ImagesTablePopupMenu.LocationForThisImage"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String LOCATION_NAMES_SELECTED = Messages
      .getString("ImagesTablePopupMenu.LocationForSelectedImages"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String LOCATION_NAMES_ALL = Messages
      .getString("ImagesTablePopupMenu.LocationForAllImages"); //$NON-NLS-1$

  /** Text for sub menu. */
  private static final String LOCATION_NAMES_SELECT = Messages
      .getString("ImagesTablePopupMenu.SelectLocation"); //$NON-NLS-1$

  /** Text for sun menu. */
  private static final String COPY_LOCATION_NAME = Messages
      .getString("ImagesTablePopupMenu.CopyName"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_LOCATION_NAME_PREVIOUS = Messages
      .getString("ImagesTablePopupMenu.CopyNamePrevious"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_LOCATION_NAME_NEXT = Messages
      .getString("ImagesTablePopupMenu.CopyNameNext"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_LOCATION_NAME_SELECTED = Messages
      .getString("ImagesTablePopupMenu.CopyNameSelected"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_LOCATION_NAME_ALL = Messages
      .getString("ImagesTablePopupMenu.CopyNameAll"); //$NON-NLS-1$

  /** Text for sub menu. */
  private static final String SAVE_LOCATIONS = Messages
      .getString("ImagesTablePopupMenu.SaveNewLocations"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String SAVE_THIS_LOCATION = Messages
      .getString("ImagesTablePopupMenu.SaveThisImage"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String SAVE_SELECTED_LOCATIONS = Messages
      .getString("ImagesTablePopupMenu.SaveSelectedImages"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String SAVE_ALL_LOCATIONS = Messages
      .getString("ImagesTablePopupMenu.SaveAllImages"); //$NON-NLS-1$

  /** Text for sub menu */
  private static final String REMOVE_IMAGES = Messages
      .getString("ImagesTablePopupMenu.RemoveImages"); //$NON-NLS-1$

  /** Text for sub menu */
  private static final String FIND_ALTITUDE = Messages
      .getString("ImagesTablePopupMenu.FIndAltitude"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String FIND_THIS_ALTITUDE = Messages
      .getString("ImagesTablePopupMenu.FindThisAltitude"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String FIND_SELECTED_ALTITUDES = Messages
      .getString("ImagesTablePopupMenu.FindSelectedAltitudes"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String FIND_ALL_ALTITUDES = Messages
      .getString("ImagesTablePopupMenu.FindAllAltitudes"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String REMOVE_THIS_IMAGE = Messages
      .getString("ImagesTablePopupMenu.RemoveThisImage"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String REMOVE_SELECTED_IMAGES = Messages
      .getString("ImagesTablePopupMenu.RemoveSelectedImages"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String REMOVE_ALL_IMAGES = Messages
      .getString("ImagesTablePopupMenu.RemoveAllImages"); //$NON-NLS-1$

  /** The parent JFrame. */
  private JFrame parentFrame;

  /** The table displaying the image data. */
  private ImagesTable imagesTable;

  /** the table model is needed here all time, so we keep a reference. */
  private ImagesTableModel tableModel;

  /**
   * An array containing the indices of selected row or an empty array if no
   * rows are selected.
   */
  private int[] selectedRows;

  /** The menu item used to show an image location on a map. */
  private JMenuItem showOneOnMapItem;

  /** The menu item used to show selected image locations on a map. */
  private JMenuItem showSelectedOnMapItem;

  /** The menu item used to show all image locations on a map. */
  private JMenuItem showAllOnMapItem;

  /** The menu item used to show an image location and direction on a map. */
  private JMenuItem showOneOnMapWithDirectionItem;

  /**
   * The menu item used to show selected image locations and directions on a
   * map.
   */
  private JMenuItem showSelectedOnMapWithDirectionItem;

  /** The menu item used to show all image locations and directions on a map. */
  private JMenuItem showAllOnMapWithDirectionItem;

  /** The menu item used to show an image location in Google Earth. */
  private JMenuItem showInGoogleEarthItem;

  /** The menu item used to export a single image to a KML/KMZ file. */
  private JMenuItem exportOneImageToKmlItem;

  /** The menu item used to export selected images to a KML/KMZ file. */
  private JMenuItem exportSelectedToKmlItem;

  /** The menu item used to export all images with locations to a KML/KMZ file. */
  private JMenuItem exportAllToKmlItem;

  /** The menu item used to select the correct GMT time for a picture. */
  private JMenuItem chooseTimeItem;

  /** The menu item used to copy the offset to all pictures. */
  private JMenuItem copyOffsetToAllItem;

  /** The menu item used to copy the offset to selected pictures. */
  private JMenuItem copyOffsetToSelectedItem;

  /** The menu item used to apply the track data to one image. */
  private JMenuItem matchTrackToOneImageItem;

  /** The menu item used to apply the track data to selected images. */
  private JMenuItem matchTrackToSelectedImagesItem;

  /** The menu item used to apply the track data to all images. */
  private JMenuItem matchTrackToAllImagesItem;

  /** The menu item used to copy the location to the previous image. */
  private JMenuItem copyLocationToPreviousItem;

  /** The menu item used to copy the location to the next image. */
  private JMenuItem copyLocationToNextItem;

  /** The menu item used to copy the location to selected images. */
  private JMenuItem copyLocationToSelectedItem;

  /** The menu item used to copy the location to all images. */
  private JMenuItem copyLocationToAllItem;

  /** The menu item used to fill a one image gap. */
  private JMenuItem fillThisGapItem;

  /** The menu item used to fill gaps in a selection. */
  private JMenuItem fillGapsInSelectionItem;

  /** The menu item used to fill all gaps. */
  private JMenuItem fillAllGapsItem;

  /** The menu item used to find the location name for one image. */
  private JMenuItem locationNameThisItem;

  /** The menu item used to find the location names for a selection of images. */
  private JMenuItem locationNamesSelectedItem;

  /** the menu item used to find the location names for all images. */
  private JMenuItem locationNamesAllItem;

  /** the menu item used to copy a location name to the previous image. */
  private JMenuItem copyLocationNamePreviousItem;

  /** the menu item used to copy a location name to the next image. */
  private JMenuItem copyLocationNameNextItem;

  /** the menu item used to copy a location name to a selection of images. */
  private JMenuItem copyLocationNameSelectedItem;

  /** the menu item used to copy a location name to all images. */
  private JMenuItem copyLocationNameAllItem;

  /** The menu item used to save the location of an image. */
  private JMenuItem saveOneLocationItem;

  /** The menu item used to save new locations of selected images. */
  private JMenuItem saveSelectedLocationsItem;

  /** The menu item used to save new locations for all images. */
  private JMenuItem saveAllLocationsItem;

  /** The menu item used to remove a single image */
  private JMenuItem removeThisImageItem;

  /** The menu item used to remove selected images */
  private JMenuItem removeSelectedImagesItem;

  /** The menu item used to remove all images */
  private JMenuItem removeAllImagesItem;

  /** The menu item used to find altitude for a single image */
  private JMenuItem findAltitudeThisImageItem;

  /** The menu item used to find altitude for selected images */
  private JMenuItem findAltitudeSelectedImagesItem;

  /** The menu item used to find altitude for all images */
  private JMenuItem findAltitudeAllImagesItem;

  /** the {@link ImageInfo} for the image in the row of the table. */
  private ImageInfo imageInfo;

  /**
   * Create a {@link ImagesTablePopupMenu} for a given {@link ImagesTable} and
   * row.
   * 
   * @param parentFrame
   *          The parent JFrame
   * @param imagesTable
   *          The {@link ImagesTable}
   * @param row
   *          The row of the {@link ImagesTable}
   * @param backgroundTask
   *          True if a background task is running (most menu items will be
   *          disabled)
   */
  public ImagesTablePopupMenu(JFrame parentFrame, ImagesTable imagesTable,
      int row, boolean backgroundTask) {
    this.parentFrame = parentFrame;
    this.imagesTable = imagesTable;
    this.tableModel = (ImagesTableModel) imagesTable.getModel();
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
    int fontStyle = headerItem.getFont().getStyle();
    Font font = new Font(headerItem.getFont().getName(), fontStyle, fontSize);
    headerItem.setFont(font);
    headerItem.setEnabled(false);
    add(headerItem);
    add(new JSeparator());

    boolean enabled;

    addShowOnMapMenu(backgroundTask);

    addShowOnMapWithDirectionMenu(backgroundTask);

    addGoogleEarthMenu(backgroundTask);

    chooseTimeItem = new JMenuItem(SELECT_CORRECT_TIME + ELLIPSIS);
    enabled = !backgroundTask; // only if no background task
    chooseTimeItem.setEnabled(enabled);
    chooseTimeItem.addActionListener(this);
    if (enabled) {
      add(chooseTimeItem);
    }

    addCopyOffsetMenu(backgroundTask);

    addMatchTracksMenu(backgroundTask, trackStore);

    addCopyLocationsMenu(row, backgroundTask);

    addFillGapsMenu(backgroundTask, trackStore);

    addLocationNamesMenu(row, backgroundTask);

    addFindAltitudeMenu(row, backgroundTask);

    addSaveLocationsMenu(backgroundTask);

    addRemoveImagesMenu(backgroundTask);
  }

  /**
   * @param backgroundTask
   */
  private void addSaveLocationsMenu(boolean backgroundTask) {
    boolean enabled;
    boolean addMenu;
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
    for (int index = 0; index < selectedRows.length; index++) {
      if (tableModel.getImageInfo(selectedRows[index]).hasNewLocation()) {
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
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      if (tableModel.getImageInfo(index).hasNewLocation()) {
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
   * @param row
   * @param backgroundTask
   */
  private void addFindAltitudeMenu(int row, boolean backgroundTask) {
    boolean enabled;
    boolean addMenu;
    JMenu findAltitudeMenu = new JMenu(FIND_ALTITUDE);
    addMenu = false;

    findAltitudeThisImageItem = new JMenuItem(FIND_THIS_ALTITUDE);
    enabled = !backgroundTask && imageInfo.hasLocation();
    addMenu |= enabled;
    findAltitudeThisImageItem.setEnabled(enabled);
    findAltitudeThisImageItem.addActionListener(this);
    findAltitudeMenu.add(findAltitudeThisImageItem);

    findAltitudeSelectedImagesItem = new JMenuItem(FIND_SELECTED_ALTITUDES);
    // enable if there is no background task, there is a selection of images
    // and at least one image in the selection has a location
    enabled = false;
    for (int index = 0; index < selectedRows.length; index++) {
      if (tableModel.getImageInfo(selectedRows[index]).hasLocation()) {
        enabled = !backgroundTask;
        break;
      }
    }
    addMenu |= enabled;
    findAltitudeSelectedImagesItem.setEnabled(enabled);
    findAltitudeSelectedImagesItem.addActionListener(this);
    findAltitudeMenu.add(findAltitudeSelectedImagesItem);

    findAltitudeAllImagesItem = new JMenuItem(FIND_ALL_ALTITUDES);
    // enable if there is no background task and there is at least one image
    // that has a location
    enabled = false;
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      if (tableModel.getImageInfo(index).hasLocation()) {
        enabled = !backgroundTask;
        break;
      }
    }
    addMenu |= enabled;
    findAltitudeAllImagesItem.setEnabled(enabled);
    findAltitudeAllImagesItem.addActionListener(this);
    findAltitudeMenu.add(findAltitudeAllImagesItem);

    if (addMenu) {
      add(findAltitudeMenu);
    }
  }

  /**
   * @param row
   * @param backgroundTask
   */
  private void addLocationNamesMenu(int row, boolean backgroundTask) {
    boolean enabled;
    boolean addMenu;
    JMenu locationNamesMenu = new JMenu(LOCATION_NAMES);
    addMenu = false;

    locationNameThisItem = new JMenuItem(LOCATION_NAME_THIS);
    enabled = !backgroundTask && imageInfo.hasLocation();
    addMenu |= enabled;
    locationNameThisItem.setEnabled(enabled);
    locationNameThisItem.addActionListener(this);
    locationNamesMenu.add(locationNameThisItem);

    locationNamesSelectedItem = new JMenuItem(LOCATION_NAMES_SELECTED);
    // enable if there is no background task, there is a selection of images
    // and at least one image in the selection has a location
    enabled = false;
    for (int index = 0; index < selectedRows.length; index++) {
      if (tableModel.getImageInfo(selectedRows[index]).hasLocation()) {
        enabled = !backgroundTask;
        break;
      }
    }
    addMenu |= enabled;
    locationNamesSelectedItem.setEnabled(enabled);
    locationNamesSelectedItem.addActionListener(this);
    locationNamesMenu.add(locationNamesSelectedItem);

    locationNamesAllItem = new JMenuItem(LOCATION_NAMES_ALL);
    // enable if there is no background task and there is at least one image
    // that has a location
    enabled = false;
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      if (tableModel.getImageInfo(index).hasLocation()) {
        enabled = !backgroundTask;
        break;
      }
    }
    addMenu |= enabled;
    locationNamesAllItem.setEnabled(enabled);
    locationNamesAllItem.addActionListener(this);
    locationNamesMenu.add(locationNamesAllItem);

    addSelectLocationsMenu(locationNamesMenu);

    JMenu copyLocationNameMenu = new JMenu(COPY_LOCATION_NAME);
    boolean addSubMenu = false;

    copyLocationNamePreviousItem = new JMenuItem(COPY_LOCATION_NAME_PREVIOUS);
    // Enable if there is no background task and this image has a location name
    // and
    // is not the first image
    enabled = !backgroundTask && imageInfo.hasLocationName() && row > 0;
    addSubMenu |= enabled;
    copyLocationNamePreviousItem.setEnabled(enabled);
    copyLocationNamePreviousItem.addActionListener(this);
    copyLocationNameMenu.add(copyLocationNamePreviousItem);

    copyLocationNameNextItem = new JMenuItem(COPY_LOCATION_NAME_NEXT);
    // Enable if there is no background task and this image has a location name
    // and
    // is not the last image
    enabled = !backgroundTask && imageInfo.hasLocationName()
        && row < tableModel.getRowCount() - 1;
    addSubMenu |= enabled;
    copyLocationNameNextItem.setEnabled(enabled);
    copyLocationNameNextItem.addActionListener(this);
    copyLocationNameMenu.add(copyLocationNameNextItem);

    copyLocationNameSelectedItem = new JMenuItem(COPY_LOCATION_NAME_SELECTED);
    // Enable if there is no background task and this image has a location name
    // and
    // there is a selection
    enabled = !backgroundTask && imageInfo.hasLocationName()
        && selectedRows.length > 0;
    addSubMenu |= enabled;
    copyLocationNameSelectedItem.setEnabled(enabled);
    copyLocationNameSelectedItem.addActionListener(this);
    copyLocationNameMenu.add(copyLocationNameSelectedItem);

    copyLocationNameAllItem = new JMenuItem(COPY_LOCATION_NAME_ALL);
    // Enable if there is no background task and this image has a location name
    enabled = !backgroundTask && imageInfo.hasLocationName();
    addSubMenu |= enabled;
    copyLocationNameAllItem.setEnabled(enabled);
    copyLocationNameAllItem.addActionListener(this);
    copyLocationNameMenu.add(copyLocationNameAllItem);

    if (addSubMenu) {
      locationNamesMenu.add(copyLocationNameMenu);
    }

    if (addMenu) {
      add(locationNamesMenu);
    }
  }

  /**
   * @param locationNamesMenu
   */
  private void addSelectLocationsMenu(JMenu locationNamesMenu) {
    JMenu selectLocationMenu = new JMenu(LOCATION_NAMES_SELECT);
    List<Location> nearbyLocations = imageInfo.getNearbyLocations();
    if (nearbyLocations != null && nearbyLocations.size() > 0) {

      for (Location location : nearbyLocations) {
        final Location itemLocation = location;
        StringBuilder locationText = new StringBuilder(location.getName());
        if (location.getProvince() != null
            && location.getProvince().length() > 0) {
          locationText.append(", ").append(location.getProvince()); //$NON-NLS-1$
        }
        if (location.getCountryName() != null
            && location.getCountryName().length() > 0) {
          locationText.append(", ").append(location.getCountryName()); //$NON-NLS-1$
        }
        // TODO: This should probably show in meters/yards if small enough
        DISTANCE distanceUnit = DISTANCE.values()[Settings.get(
            SETTING.DISTANCE_UNIT, 0)];
        locationText.append(" ("); //$NON-NLS-1$
        if (location.getFeatureName() != null) {
          locationText.append(location.getFeatureName());
          locationText.append(" - "); //$NON-NLS-1$
        }
        locationText.append(String.format("%.2f %s)", new Double(location //$NON-NLS-1$
            .getDistance(distanceUnit)), Units.getAbbreviation(distanceUnit)));
        if (itemLocation.getAlternateNames() == null) {
          // no alternate names - add name as menu item
          JMenuItem selectLocationItem = new JMenuItem(locationText.toString(),
              location.getIcon());
          selectLocationItem.addActionListener(new LocationNameActionListener(
              itemLocation.getName()) {
            @Override
            public void actionPerformed(ActionEvent e) {
              new SelectLocationNameTask(Messages
                  .getString("ImagesTablePopupMenu.SelectLocationName"), //$NON-NLS-1$
                  getTableModel(), getImageInfo(), itemLocation, itemLocation
                      .getName(), DATA_SOURCE.MANUAL).execute();
            }
          });
          selectLocationMenu.add(selectLocationItem);
        } else {
          // alternate names available - add sub-menu
          JMenu selectLocationNameMenu = new JMenu(locationText.toString());
          // first add menu item for main name
          // Don't specify icon, as Wikipedia entries have no alternate names
          // (yet)
          JMenuItem selectLocationItem = new JMenuItem(itemLocation.getName());
          selectLocationItem.addActionListener(new LocationNameActionListener(
              itemLocation.getName()) {
            @Override
            public void actionPerformed(ActionEvent e) {
              new SelectLocationNameTask(
                  Messages.getString("ImagesTablePopupMenu.SelectLocationName"), //$NON-NLS-1$
                  getTableModel(), getImageInfo(), itemLocation, getName(),
                  DATA_SOURCE.MANUAL).execute();
            }
          });
          selectLocationNameMenu.add(selectLocationItem);
          // then menu items for all alternate names
          for (String alternateName : itemLocation.getAlternateNames()) {
            selectLocationItem = new JMenuItem(alternateName);
            selectLocationItem
                .addActionListener(new LocationNameActionListener(alternateName) {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    new SelectLocationNameTask(
                        Messages
                            .getString("ImagesTablePopupMenu.SelectLocationName"), //$NON-NLS-1$
                        getTableModel(), getImageInfo(), itemLocation,
                        getName(), DATA_SOURCE.MANUAL).execute();
                  }
                });
            selectLocationNameMenu.add(selectLocationItem);
          }
          selectLocationMenu.add(selectLocationNameMenu);
        }

      }
      locationNamesMenu.add(selectLocationMenu);
    }
  }

  /**
   * @param backgroundTask
   * @param trackStore
   */
  private void addFillGapsMenu(boolean backgroundTask, TrackStore trackStore) {
    boolean enabled;
    boolean addMenu;
    JMenu fillGapsMenu = new JMenu(FILL_GAPS);
    addMenu = false;

    fillThisGapItem = new JMenuItem(FILL_THIS_GAP);
    // Enable if there is no background task, there is track data available and
    // we don't have coordinates for this image yet.
    enabled = !backgroundTask && trackStore.hasTracks()
        && !imageInfo.hasLocation();
    addMenu |= enabled;
    fillThisGapItem.setEnabled(enabled);
    fillThisGapItem.addActionListener(this);
    fillGapsMenu.add(fillThisGapItem);

    fillGapsInSelectionItem = new JMenuItem(FILL_SELECTED_GAPS);
    // enable if there is no background task, there is a selection, we have
    // tracks and there is at least one image in the selection with
    // no coordinates
    enabled = false;
    for (int index = 0; index < selectedRows.length; index++) {
      if (!tableModel.getImageInfo(selectedRows[index]).hasLocation()) {
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
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      if (!tableModel.getImageInfo(index).hasLocation()) {
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
  }

  /**
   * @param row
   * @param backgroundTask
   */
  private void addCopyLocationsMenu(int row, boolean backgroundTask) {
    boolean enabled;
    boolean addMenu;
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
  }

  /**
   * @param backgroundTask
   * @param trackStore
   */
  private void addMatchTracksMenu(boolean backgroundTask, TrackStore trackStore) {
    boolean enabled;
    boolean addMenu;
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
  }

  /**
   * @param backgroundTask
   */
  private void addCopyOffsetMenu(boolean backgroundTask) {
    boolean enabled;
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
  }

  /**
   * @param backgroundTask
   */
  private void addRemoveImagesMenu(boolean backgroundTask) {
    boolean enabled;
    JMenu removeImagesMenu = new JMenu(REMOVE_IMAGES);
    boolean addMenu = false;

    removeThisImageItem = new JMenuItem(REMOVE_THIS_IMAGE);
    enabled = !backgroundTask;
    addMenu |= enabled;
    removeThisImageItem.setEnabled(enabled);
    removeThisImageItem.addActionListener(this);
    removeImagesMenu.add(removeThisImageItem);

    removeSelectedImagesItem = new JMenuItem(REMOVE_SELECTED_IMAGES);
    // enable if there is no background task and there is a selection
    enabled = !backgroundTask && selectedRows.length > 0;
    addMenu |= enabled;
    removeSelectedImagesItem.setEnabled(enabled);
    removeSelectedImagesItem.addActionListener(this);
    removeImagesMenu.add(removeSelectedImagesItem);

    removeAllImagesItem = new JMenuItem(REMOVE_ALL_IMAGES);
    enabled = !backgroundTask;
    addMenu |= enabled;
    removeAllImagesItem.setEnabled(enabled);
    removeAllImagesItem.addActionListener(this);
    removeImagesMenu.add(removeAllImagesItem);

    if (addMenu) {
      add(removeImagesMenu);
    }
  }

  /**
   * @param backgroundTask
   */
  private void addGoogleEarthMenu(boolean backgroundTask) {
    boolean enabled;
    JMenu googleEarthMenu = new JMenu(GOOGLEEARTH);

    showInGoogleEarthItem = new JMenuItem(SHOW_IN_GOOGLEEARTH);
    enabled = true; // we can always do this safely
    showInGoogleEarthItem.setEnabled(enabled);
    showInGoogleEarthItem.addActionListener(this);
    googleEarthMenu.add(showInGoogleEarthItem);

    exportOneImageToKmlItem = new JMenuItem(EXPORT_THIS);
    // enable if there is no background task this image has a location
    enabled = !backgroundTask && imageInfo.hasLocation();
    exportOneImageToKmlItem.setEnabled(enabled);
    exportOneImageToKmlItem.addActionListener(this);
    googleEarthMenu.add(exportOneImageToKmlItem);

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
    googleEarthMenu.add(exportSelectedToKmlItem);

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
    googleEarthMenu.add(exportAllToKmlItem);
    add(googleEarthMenu);
  }

  /**
   * @param backgroundTask
   */
  private void addShowOnMapWithDirectionMenu(boolean backgroundTask) {
    boolean enabled;
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
  }

  /**
   * @param backgroundTask
   */
  private void addShowOnMapMenu(boolean backgroundTask) {
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
    } else if (event.getSource() == exportOneImageToKmlItem) {
      exportOneToKml();
    } else if (event.getSource() == exportSelectedToKmlItem) {
      exportSelectedToKml();
    } else if (event.getSource() == exportAllToKmlItem) {
      exportAllToKml();
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
    } else if (event.getSource() == locationNameThisItem) {
      findOneLocationName();
    } else if (event.getSource() == locationNamesSelectedItem) {
      findSelectedLocationNames();
    } else if (event.getSource() == locationNamesAllItem) {
      findAllLocationNames();
    } else if (event.getSource() == copyLocationNamePreviousItem) {
      copyLocationNameToPrevious();
    } else if (event.getSource() == copyLocationNameNextItem) {
      copyLocationNameToNext();
    } else if (event.getSource() == copyLocationNameSelectedItem) {
      copyLocationNameToSelected();
    } else if (event.getSource() == copyLocationNameAllItem) {
      copyLocationNameToAll();
    } else if (event.getSource() == saveOneLocationItem) {
      saveOneLocation();
    } else if (event.getSource() == saveSelectedLocationsItem) {
      saveSelectedLocations();
    } else if (event.getSource() == saveAllLocationsItem) {
      saveAllLocations();
    } else if (event.getSource() == removeThisImageItem) {
      removeOneImage();
    } else if (event.getSource() == removeSelectedImagesItem) {
      removeSelectedImages();
    } else if (event.getSource() == removeAllImagesItem) {
      removeAllImages();
    } else if (event.getSource() == findAltitudeThisImageItem) {
      findThisAltitude();
    } else if (event.getSource() == findAltitudeSelectedImagesItem) {
      findSelectedAltitudes();
    } else if (event.getSource() == findAltitudeAllImagesItem) {
      findAllAltitudes();
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
    String url = "http://localhost:4321/map/map.html?" + //$NON-NLS-1$
        "latitude=" //$NON-NLS-1$
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

  /**
   * Launch Google Earth to show the location.
   */
  private void showInGoogleEarth() {
    // make sure there is a thumbnail - this won't create the
    // thumbnail again, if it already exists.
    ThumbnailWorker worker = new ThumbnailWorker(imageInfo) {
      @Override
      protected void done() {
        GoogleEarthLauncher.launch(getImageInfo());
      }
    };
    worker.execute();
  }

  /**
   * Export a single image to a KML/KMZ file.
   */
  private void exportOneToKml() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
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

    if (chooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
      try {
        File outputFile = chooser.getSelectedFile();
        if (!fileFilter.accept(outputFile)) {
          // not a kml/kmz file selected - add .kml suffix
          outputFile = new File(chooser.getSelectedFile().getPath() + ".kml"); //$NON-NLS-1$
        }
        if (outputFile.exists()) {
          // TODO decide if these messages should get their own class
          String title = Messages.getString("MainWindow.FileExists"); //$NON-NLS-1$
          String message = String
              .format(
                  Messages.getString("MainWindow.OverwriteFileFormat"), outputFile.getName()); //$NON-NLS-1$
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
          new ThumbnailsTask(Messages
              .getString("ImagesTablePopupMenu.GenerateThumbnails"), images) { //$NON-NLS-1$
            @Override
            public void done() {
              exportToKml(images, file);
            }
          }.execute();

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
    new GoogleEarthExportTask(Messages
        .getString("ImagesTablePopupMenu.ExportForGoogleEarth"), images, file).execute(); //$NON-NLS-1$
    Settings.put(SETTING.GOOGLEEARTH_LAST_FILE_SAVED, file.getPath());
  }

  /**
   * select the exact time the image was taken.
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
          parentFrame,
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
            if (JOptionPane.showConfirmDialog(getParentFrame(), message,
                ImageInfo.getOffsetString(offset), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
              copyOffsetToAll();
            }
            getTableModel().sortRows();
          }

        };
        setOffsetTask.execute();
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Copy the offset of the current picture to all other pictures.
   */
  void copyOffsetToAll() {
    int offset = imageInfo.getOffset();
    List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      ImageInfo image = tableModel.getImageInfo(index);
      imageList.add(image);
    }
    new SetOffsetTask(COPY_TIME_OFFSET, COPY_TIME_OFFSET_ALL, tableModel,
        offset, imageList).execute();
  }

  /**
   * Copy the offset to the selected pictures.
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
   * Use the TrackMatcher to find coordinates for one image.
   */
  private void matchTracksToOneImage() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
    new MatchImagesTask(MATCH_TRACKS, MATCH_TRACK_SELECTED, imagesTable, images)
        .execute();
  }

  /**
   * Use the TrackMatcher to find coordinates for all selected images.
   */
  private void matchTracksToSelectedImages() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      images.add(tableModel.getImageInfo(selectedRows[index]));
    }
    new MatchImagesTask(MATCH_TRACKS, MATCH_TRACK_SELECTED, imagesTable, images)
        .execute();
  }

  /**
   * Use the TrackMatcher to find coordinates for all images.
   */
  private void matchTracksToAllImages() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      images.add(tableModel.getImageInfo(index));
    }
    new MatchImagesTask(MATCH_TRACKS, MATCH_TRACK_ALL, imagesTable, images)
        .execute();
  }

  /**
   * Copy the location to the previous image.
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
   * Copy the location to the next image.
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
   * Copy the location to all selected images.
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
   * Copy the location to all images.
   */
  private void copyLocationToAll() {
    List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    for (int iindex = 0; iindex < tableModel.getRowCount(); iindex++) {
      imageList.add(tableModel.getImageInfo(iindex));
    }
    new CopyLocationTask(COPY_LOCATION, COPY_LOCATION_ALL, tableModel,
        imageInfo, imageList).execute();
  }

  /**
   * Fill the gap this image leaves.
   */
  private void fillThisGap() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
    new FillGapsTask(FILL_GAPS, FILL_THIS_GAP, tableModel, images).execute();
  }

  /**
   * Use adjacent coordinates to fill in the gaps by interpolation.
   */
  private void fillGapsInSelection() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      ImageInfo candidate = tableModel.getImageInfo(selectedRows[index]);
      if (!candidate.hasLocation()) {
        images.add(candidate);
      }
    }
    new FillGapsTask(FILL_GAPS, FILL_SELECTED_GAPS, tableModel, images)
        .execute();
  }

  /**
   * Have a go at guessing coordinates for all images without them. This is
   * probably not the wisest thing to do, but we offer the possibility anyway as
   * it can be un-done
   */
  private void fillAllGaps() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      ImageInfo candidate = tableModel.getImageInfo(index);
      if (!candidate.hasLocation()) {
        images.add(candidate);
      }
    }
    new FillGapsTask(FILL_GAPS, FILL_ALL_GAPS, tableModel, images).execute();
  }

  /**
   * Find the location name for a single image.
   */
  private void findOneLocationName() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
    new LocationNamesTask(LOCATION_NAMES, LOCATION_NAME_THIS, tableModel,
        images).execute();
  }

  /**
   * Find the location name for a selection of images (with coordinates).
   */
  private void findSelectedLocationNames() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      ImageInfo candidate = tableModel.getImageInfo(selectedRows[index]);
      if (candidate.hasLocation()) {
        images.add(candidate);
      }
    }
    new LocationNamesTask(LOCATION_NAMES, LOCATION_NAMES_SELECTED, tableModel,
        images).execute();
  }

  /**
   * Find the location name for all images (with coordinates).
   */
  private void findAllLocationNames() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      ImageInfo candidate = tableModel.getImageInfo(index);
      if (candidate.hasLocation()) {
        images.add(candidate);
      }
    }
    new LocationNamesTask(LOCATION_NAMES, LOCATION_NAMES_ALL, tableModel,
        images).execute();
  }

  /**
   * Find the location name for a single image.
   */
  private void findThisAltitude() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
    new FindAltitudeTask(FIND_ALTITUDE, FIND_THIS_ALTITUDE, tableModel, images)
        .execute();
  }

  /**
   * Find the location name for a selection of images (with coordinates).
   */
  private void findSelectedAltitudes() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      ImageInfo candidate = tableModel.getImageInfo(selectedRows[index]);
      if (candidate.hasLocation()) {
        images.add(candidate);
      }
    }
    new FindAltitudeTask(FIND_ALTITUDE, FIND_SELECTED_ALTITUDES, tableModel,
        images).execute();
  }

  /**
   * Find the location name for all images (with coordinates).
   */
  private void findAllAltitudes() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      ImageInfo candidate = tableModel.getImageInfo(index);
      if (candidate.hasLocation()) {
        images.add(candidate);
      }
    }
    new FindAltitudeTask(FIND_ALTITUDE, FIND_ALL_ALTITUDES, tableModel, images)
        .execute();
  }

  /**
   * Copy the location name to the previous image.
   */
  private void copyLocationNameToPrevious() {
    int row = tableModel.getRow(imageInfo);
    if (row > 0) {
      List<ImageInfo> imageList = new ArrayList<ImageInfo>();
      imageList.add(tableModel.getImageInfo(row - 1));
      new CopyLocationNameTask(LOCATION_NAMES + " - " + COPY_LOCATION_NAME, //$NON-NLS-1$
          COPY_LOCATION_NAME_PREVIOUS, tableModel, imageInfo, imageList)
          .execute();
    }
  }

  /**
   * Copy the location name to the next image.
   */
  private void copyLocationNameToNext() {
    int row = tableModel.getRow(imageInfo);
    if (row < tableModel.getRowCount() - 1) {
      List<ImageInfo> imageList = new ArrayList<ImageInfo>();
      imageList.add(tableModel.getImageInfo(row + 1));
      new CopyLocationNameTask(LOCATION_NAMES + " - " + COPY_LOCATION_NAME, //$NON-NLS-1$
          COPY_LOCATION_NAME_NEXT, tableModel, imageInfo, imageList).execute();
    }
  }

  /**
   * Copy the location name to all selected images.
   */
  private void copyLocationNameToSelected() {
    List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      imageList.add(tableModel.getImageInfo(selectedRows[index]));
    }
    new CopyLocationNameTask(LOCATION_NAMES + " - " + COPY_LOCATION_NAME, //$NON-NLS-1$
        COPY_LOCATION_NAME_SELECTED, tableModel, imageInfo, imageList)
        .execute();
  }

  /**
   * Copy the location name to all images.
   */
  private void copyLocationNameToAll() {
    List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      imageList.add(tableModel.getImageInfo(index));
    }
    new CopyLocationNameTask(LOCATION_NAMES + " - " + COPY_LOCATION_NAME, //$NON-NLS-1$
        COPY_LOCATION_NAME_ALL, tableModel, imageInfo, imageList).execute();
  }

  /**
   * Save the EXIF data to all images in the list.
   * 
   * @param images
   */
  private void saveLocations(List<ImageInfo> images) {
    new ExifWriterTask(Messages
        .getString("ImagesTablePopupMenu.SaveNewLocations"), tableModel, images).execute(); //$NON-NLS-1$
  }

  /**
   * Save the EXIF data to this image.
   */
  private void saveOneLocation() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
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
   * Remove one image from the table
   */
  private void removeOneImage() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
    new RemoveImagesTask(REMOVE_IMAGES, REMOVE_THIS_IMAGE, tableModel, images)
        .execute();
  }

  /**
   * Remove selected images from the table
   */
  private void removeSelectedImages() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      images.add(tableModel.getImageInfo(selectedRows[index]));
    }
    new RemoveImagesTask(REMOVE_IMAGES, REMOVE_SELECTED_IMAGES, tableModel,
        images).execute();
  }

  /**
   * Remove all images from the table
   */
  private void removeAllImages() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      images.add(tableModel.getImageInfo(index));
    }
    new RemoveImagesTask(REMOVE_IMAGES, REMOVE_ALL_IMAGES, tableModel, images)
        .execute();

  }

  /**
   * An action listener remembering a name.
   */
  abstract class LocationNameActionListener implements ActionListener {
    /** The name to remember. */
    private String name;

    /**
     * Constructor.
     * 
     * @param name
     */
    public LocationNameActionListener(String name) {
      this.name = name;
    }

    /**
     * @return The name
     */
    public String getName() {
      return name;
    }
  }

  /**
   * @return the parent frame
   */
  JFrame getParentFrame() {
    return parentFrame;
  }

  /**
   * @return the imagesTable
   */
  ImagesTable getImagesTable() {
    return imagesTable;
  }

  /**
   * @return the tableModel
   */
  ImagesTableModel getTableModel() {
    return tableModel;
  }

  /**
   * @return the selectedRows
   */
  int[] getSelectedRows() {
    return selectedRows;
  }

  /**
   * @return the imageInfo
   */
  ImageInfo getImageInfo() {
    return imageInfo;
  }
}
