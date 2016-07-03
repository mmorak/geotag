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
import java.util.TimeZone;

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
import org.fibs.geotag.geonames.Location;
import org.fibs.geotag.gui.menus.CopyLocationMenu;
import org.fibs.geotag.gui.menus.CopyOffsetMenu;
import org.fibs.geotag.gui.menus.GoogleEarthMenu;
import org.fibs.geotag.gui.menus.MatchTracksMenu;
import org.fibs.geotag.gui.menus.MenuConstants;
import org.fibs.geotag.gui.menus.SaveLocationsMenu;
import org.fibs.geotag.gui.menus.ShowOnMapMenu;
import org.fibs.geotag.gui.menus.actions.CopyOffsetToAllAction;
import org.fibs.geotag.table.ImagesTable;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.tasks.CopyLocationNameTask;
import org.fibs.geotag.tasks.FillGapsTask;
import org.fibs.geotag.tasks.FindAltitudeTask;
import org.fibs.geotag.tasks.LocationNamesTask;
import org.fibs.geotag.tasks.RemoveImagesTask;
import org.fibs.geotag.tasks.SelectLocationNameTask;
import org.fibs.geotag.tasks.SetOffsetTask;
import org.fibs.geotag.tasks.TaskExecutor;
import org.fibs.geotag.track.TrackStore;
import org.fibs.geotag.util.Units;
import org.fibs.geotag.util.Units.DISTANCE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A context menu for a image table row. All actions that can be undone should
 * be run as a UndoableBackgroundTask
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class ImagesTablePopupMenu extends JPopupMenu implements ActionListener,
    MenuConstants {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(ImagesTablePopupMenu.class);

  /** Text for menu item. */
  private static final String SELECT_CORRECT_TIME = i18n
      .tr("Set time of image"); //$NON-NLS-1$

  /** Text for sub menu. */
  private static final String FILL_GAPS = i18n
      .tr("Fill gaps"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String FILL_THIS_GAP = i18n
      .tr("for this image"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String FILL_SELECTED_GAPS = i18n
      .tr("between selected images"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String FILL_ALL_GAPS = i18n
      .tr("all gaps between images"); //$NON-NLS-1$

  /** Text fir sub menu. */
  private static final String LOCATION_NAMES = i18n
      .tr("Location names"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String LOCATION_NAME_THIS = i18n
      .tr("find for this image"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String LOCATION_NAMES_SELECTED = i18n
      .tr("find for selected images"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String LOCATION_NAMES_ALL = i18n
      .tr("find for all images"); //$NON-NLS-1$

  /** Text for sub menu. */
  private static final String LOCATION_NAMES_SELECT = i18n
      .tr("Select"); //$NON-NLS-1$

  /** Text for sun menu. */
  private static final String COPY_LOCATION_NAME = i18n
      .tr("Copy"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_LOCATION_NAME_PREVIOUS = i18n
      .tr("to previous image"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_LOCATION_NAME_NEXT = i18n
      .tr("to next image"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_LOCATION_NAME_SELECTED = i18n
      .tr("to selected images"); //$NON-NLS-1$

  /** Text for menu item. */
  private static final String COPY_LOCATION_NAME_ALL = i18n
      .tr("to all images"); //$NON-NLS-1$

  /** Text for sub menu */
  private static final String REMOVE_IMAGES = i18n
      .tr("Remove images"); //$NON-NLS-1$

  /** Text for sub menu */
  private static final String FIND_ALTITUDE = i18n
      .tr("Find altitude"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String FIND_THIS_ALTITUDE = i18n
      .tr("for this image"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String FIND_SELECTED_ALTITUDES = i18n
      .tr("for selected images"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String FIND_ALL_ALTITUDES = i18n
      .tr("for all images"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String REMOVE_THIS_IMAGE = i18n
      .tr("This image"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String REMOVE_SELECTED_IMAGES = i18n
      .tr("Selected images"); //$NON-NLS-1$

  /** Text for menu item */
  private static final String REMOVE_ALL_IMAGES = i18n
      .tr("All images"); //$NON-NLS-1$

  /** The parent JFrame. */
  private JFrame parentFrame;

  /** The table displaying the image data. */
  ImagesTable imagesTable;

  /** the table model is needed here all time, so we keep a reference. */
  private ImagesTableModel tableModel;

  /**
   * An array containing the indices of selected row or an empty array if no
   * rows are selected.
   */
  private int[] selectedRows;

  /** The menu item used to select the correct GMT time for a picture. */
  private JMenuItem chooseTimeItem;

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
  ImageInfo imageInfo;

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

    add(new ShowOnMapMenu(backgroundTask, imagesTable, imageInfo, false));

    add(new ShowOnMapMenu(backgroundTask, imagesTable, imageInfo, true));

    add(new GoogleEarthMenu(backgroundTask, imagesTable, imageInfo));

    chooseTimeItem = new JMenuItem(SELECT_CORRECT_TIME + ELLIPSIS);
    enabled = !backgroundTask; // only if no background task
    chooseTimeItem.setEnabled(enabled);
    chooseTimeItem.addActionListener(this);
    if (enabled) {
      add(chooseTimeItem);
    }

    JMenu copyOffsetMenu = new CopyOffsetMenu(backgroundTask, imagesTable,
        imageInfo);
    if (copyOffsetMenu.isEnabled()) {
      add(copyOffsetMenu);
    }

    JMenu matchTracksMenu = new MatchTracksMenu(backgroundTask, imagesTable,
        imageInfo, trackStore);
    if (matchTracksMenu.isEnabled()) {
      add(matchTracksMenu);
    }

    JMenu copyLocationsMenu = new CopyLocationMenu(backgroundTask, imagesTable,
        imageInfo);
    if (copyLocationsMenu.isEnabled()) {
      add(copyLocationsMenu);
    }

    addFillGapsMenu(backgroundTask, trackStore);

    addLocationNamesMenu(row, backgroundTask);

    addFindAltitudeMenu(row, backgroundTask);

    add(new SaveLocationsMenu(backgroundTask, imagesTable, imageInfo));

    addRemoveImagesMenu(backgroundTask);
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
              TaskExecutor.execute(new SelectLocationNameTask(i18n
                  .tr("Select location name"), //$NON-NLS-1$
                  getTableModel(), getImageInfo(), itemLocation, itemLocation
                      .getName(), DATA_SOURCE.MANUAL));
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
              TaskExecutor
                  .execute(new SelectLocationNameTask(
                      i18n
                          .tr("Select location name"), //$NON-NLS-1$
                      getTableModel(), getImageInfo(), itemLocation, getName(),
                      DATA_SOURCE.MANUAL));
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
                    TaskExecutor
                        .execute(new SelectLocationNameTask(
                            i18n
                                .tr("Select location name"), //$NON-NLS-1$
                            getTableModel(), getImageInfo(), itemLocation,
                            getName(), DATA_SOURCE.MANUAL));
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
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    // determine which menu item was selected
    if (event.getSource() == chooseTimeItem) {
      // Open a DateTimeChooser to select the exact time for the image.
      chooseTime();
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
          i18n.tr("Select Date and Time"), createCalendar, false); //$NON-NLS-1$
      Calendar chosenDate = chooser.openChooser();
      if (chosenDate != null) {
        // user has picked a date for this picture
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
        String gmtTime = dateFormat.format(chosenDate.getTime());
        final int offset = imageInfo.calculateOffset(gmtTime, imageInfo
            .getCameraDate());
        List<ImageInfo> imageList = new ArrayList<ImageInfo>();
        imageList.add(imageInfo);
        SetOffsetTask setOffsetTask = new SetOffsetTask(null,
            SELECT_CORRECT_TIME, tableModel, offset, imageList) {
          @Override
          protected void done() {
            super.done();
            String message = i18n
                .tr("Do you want to use this time difference for all images?"); //$NON-NLS-1$
            if (JOptionPane.showConfirmDialog(getParentFrame(), message,
                ImageInfo.getOffsetString(offset), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
              new CopyOffsetToAllAction(imageInfo, imagesTable).perform();
            }
            getTableModel().sortRows();
          }

        };
        TaskExecutor.execute(setOffsetTask);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Fill the gap this image leaves.
   */
  private void fillThisGap() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
    TaskExecutor.execute(new FillGapsTask(FILL_GAPS, FILL_THIS_GAP, tableModel,
        images));
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
    TaskExecutor.execute(new FillGapsTask(FILL_GAPS, FILL_SELECTED_GAPS,
        tableModel, images));
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
    TaskExecutor.execute(new FillGapsTask(FILL_GAPS, FILL_ALL_GAPS, tableModel,
        images));
  }

  /**
   * Find the location name for a single image.
   */
  private void findOneLocationName() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
    TaskExecutor.execute(new LocationNamesTask(LOCATION_NAMES,
        LOCATION_NAME_THIS, tableModel, images));
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
    TaskExecutor.execute(new LocationNamesTask(LOCATION_NAMES,
        LOCATION_NAMES_SELECTED, tableModel, images));
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
    TaskExecutor.execute(new LocationNamesTask(LOCATION_NAMES,
        LOCATION_NAMES_ALL, tableModel, images));
  }

  /**
   * Find the location name for a single image.
   */
  private void findThisAltitude() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
    TaskExecutor.execute(new FindAltitudeTask(FIND_ALTITUDE,
        FIND_THIS_ALTITUDE, tableModel, images));
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
    TaskExecutor.execute(new FindAltitudeTask(FIND_ALTITUDE,
        FIND_SELECTED_ALTITUDES, tableModel, images));
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
    TaskExecutor.execute(new FindAltitudeTask(FIND_ALTITUDE,
        FIND_ALL_ALTITUDES, tableModel, images));
  }

  /**
   * Copy the location name to the previous image.
   */
  private void copyLocationNameToPrevious() {
    int row = tableModel.getRow(imageInfo);
    if (row > 0) {
      List<ImageInfo> imageList = new ArrayList<ImageInfo>();
      imageList.add(tableModel.getImageInfo(row - 1));
      TaskExecutor.execute(new CopyLocationNameTask(LOCATION_NAMES
          + " - " + COPY_LOCATION_NAME, //$NON-NLS-1$
          COPY_LOCATION_NAME_PREVIOUS, tableModel, imageInfo, imageList));
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
      TaskExecutor.execute(new CopyLocationNameTask(LOCATION_NAMES
          + " - " + COPY_LOCATION_NAME, //$NON-NLS-1$
          COPY_LOCATION_NAME_NEXT, tableModel, imageInfo, imageList));
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
    TaskExecutor.execute(new CopyLocationNameTask(LOCATION_NAMES
        + " - " + COPY_LOCATION_NAME, //$NON-NLS-1$
        COPY_LOCATION_NAME_SELECTED, tableModel, imageInfo, imageList));
  }

  /**
   * Copy the location name to all images.
   */
  private void copyLocationNameToAll() {
    List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      imageList.add(tableModel.getImageInfo(index));
    }
    TaskExecutor.execute(new CopyLocationNameTask(LOCATION_NAMES
        + " - " + COPY_LOCATION_NAME, //$NON-NLS-1$
        COPY_LOCATION_NAME_ALL, tableModel, imageInfo, imageList));
  }

  /**
   * Remove one image from the table
   */
  private void removeOneImage() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    images.add(imageInfo);
    TaskExecutor.execute(new RemoveImagesTask(REMOVE_IMAGES, REMOVE_THIS_IMAGE,
        tableModel, images));
  }

  /**
   * Remove selected images from the table
   */
  private void removeSelectedImages() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < selectedRows.length; index++) {
      images.add(tableModel.getImageInfo(selectedRows[index]));
    }
    TaskExecutor.execute(new RemoveImagesTask(REMOVE_IMAGES,
        REMOVE_SELECTED_IMAGES, tableModel, images));
  }

  /**
   * Remove all images from the table
   */
  private void removeAllImages() {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      images.add(tableModel.getImageInfo(index));
    }
    TaskExecutor.execute(new RemoveImagesTask(REMOVE_IMAGES, REMOVE_ALL_IMAGES,
        tableModel, images));

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
