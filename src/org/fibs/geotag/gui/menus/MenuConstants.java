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

import org.xnap.commons.i18n.I18nFactory;

/**
 * @author andreas
 * 
 */
public interface MenuConstants {
  

  /** An ellipsis of three dots. */
  public static final String ELLIPSIS = "..."; //$NON-NLS-1$

  /** Text for menu item to add all files in a directory. */
  public static final String ADD_FILES = I18nFactory.getI18n(MenuConstants.class)
      .tr("Add images from directory"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SHOW_THIS_IMAGE = I18nFactory.getI18n(MenuConstants.class)
      .tr("This image"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SHOW_SELECTED_IMAGES = I18nFactory.getI18n(MenuConstants.class)
      .tr("Selected images"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SHOW_ALL_IMAGES = I18nFactory.getI18n(MenuConstants.class)
      .tr("All images"); //$NON-NLS-1$

  /** Text for sub menu. */
  public static final String COPY_TIME_OFFSET = I18nFactory.getI18n(MenuConstants.class)
      .tr("Copy time offset"); //$NON-NLS-1$

  /** Text used as the name of the file menu */
  public static final String FILE_MENU_NAME = I18nFactory.getI18n(MenuConstants.class)
      .tr("File"); //$NON-NLS-1$

  /** Text for menu item to add individual files. */
  public static final String ADD_FILE = I18nFactory.getI18n(MenuConstants.class)
      .tr("Add image"); //$NON-NLS-1$

  /** Text for menu item to load tracks from GPS unit */
  public static final String LOAD_TRACK_FROM_GPS = I18nFactory.getI18n(MenuConstants.class)
      .tr("Load tracks from GPS"); //$NON-NLS-1$

  /** Text for menu item to load tracks from a file */
  public static final String LOAD_TRACKS_FROM_FILE = I18nFactory.getI18n(MenuConstants.class)
      .tr("Load tracks from file"); //$NON-NLS-1$

  /** Text for menu item to save tracks */
  public static final String SAVE_TRACK = I18nFactory.getI18n(MenuConstants.class)
      .tr("Save track"); //$NON-NLS-1$

  /** Text for menu item to open settings dialog */
  public static final String SETTINGS = I18nFactory.getI18n(MenuConstants.class)
      .tr("Settings"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String COPY_TIME_OFFSET_SELECTED = I18nFactory.getI18n(MenuConstants.class)
      .tr("to selected images"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String COPY_TIME_OFFSET_ALL = I18nFactory.getI18n(MenuConstants.class)
      .tr("to all images"); //$NON-NLS-1$

  /** Text for sub menu. */
  public static final String GOOGLEEARTH = I18nFactory.getI18n(MenuConstants.class)
      .tr("Google Earth"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SHOW_IN_GOOGLEEARTH = I18nFactory.getI18n(MenuConstants.class)
      .tr("Show in Google Earth"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String EXPORT_THIS = I18nFactory.getI18n(MenuConstants.class)
      .tr("Export this image"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String EXPORT_SELECTED = I18nFactory.getI18n(MenuConstants.class)
      .tr("Export selected images"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String EXPORT_ALL = I18nFactory.getI18n(MenuConstants.class)
      .tr("Export all images"); //$NON-NLS-1$
  
  /** Text for menu. */
  public static final String SAVE_LOCATIONS = I18nFactory.getI18n(MenuConstants.class)
      .tr("Save new locations"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SAVE_THIS_LOCATION = I18nFactory.getI18n(MenuConstants.class)
      .tr("This image"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SAVE_SELECTED_LOCATIONS = I18nFactory.getI18n(MenuConstants.class)
      .tr("Selected images"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SAVE_ALL_LOCATIONS = I18nFactory.getI18n(MenuConstants.class)
      .tr("All images"); //$NON-NLS-1$
  
  /** Text for sub menu. */
  public static final String MATCH_TRACKS = I18nFactory.getI18n(MenuConstants.class)
      .tr("Find locations"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String MATCH_TRACK_THIS = I18nFactory.getI18n(MenuConstants.class)
      .tr("for this image"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String MATCH_TRACK_SELECTED = I18nFactory.getI18n(MenuConstants.class)
      .tr("for selected images"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String MATCH_TRACK_ALL = I18nFactory.getI18n(MenuConstants.class)
      .tr("for all images"); //$NON-NLS-1$

  /** Text for sub menu. */
  public static final String COPY_LOCATION = I18nFactory.getI18n(MenuConstants.class)
      .tr("Copy location"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String COPY_LOCATION_PREVIOUS = I18nFactory.getI18n(MenuConstants.class)
      .tr("to previous image"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String COPY_LOCATION_NEXT = I18nFactory.getI18n(MenuConstants.class)
      .tr("to next image"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String COPY_LOCATION_SELECTED = I18nFactory.getI18n(MenuConstants.class)
      .tr("to selected images"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String COPY_LOCATION_ALL = I18nFactory.getI18n(MenuConstants.class)
      .tr("to all images"); //$NON-NLS-1$
  
}
