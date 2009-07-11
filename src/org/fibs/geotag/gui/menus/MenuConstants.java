/**
 * Geotag
 * Copyright (C) 2007-2009 Andreas Schneider
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

import org.fibs.geotag.i18n.Messages;

/**
 * @author andreas
 * 
 */
public interface MenuConstants {

  /** An ellipsis of three dots. */
  public static final String ELLIPSIS = "..."; //$NON-NLS-1$

  /** Text for menu item to add all files in a directory. */
  public static final String ADD_FILES = Messages
      .getString("MainWindow.AddDirectory"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SHOW_THIS_IMAGE = Messages
      .getString("ImagesTablePopupMenu.ThisImage"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SHOW_SELECTED_IMAGES = Messages
      .getString("ImagesTablePopupMenu.SelectedImages"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SHOW_ALL_IMAGES = Messages
      .getString("ImagesTablePopupMenu.AllImages"); //$NON-NLS-1$

  /** Text for sub menu. */
  public static final String COPY_TIME_OFFSET = Messages
      .getString("ImagesTablePopupMenu.CopyTimeOffset"); //$NON-NLS-1$

  /** Text used as the name of the file menu */
  public static final String FILE_MENU_NAME = Messages
      .getString("MainWindow.File"); //$NON-NLS-1$

  /** Text for menu item to add individual files. */
  public static final String ADD_FILE = Messages
      .getString("MainWindow.AddFile"); //$NON-NLS-1$

  /** Text for menu item to load tracks from GPS unit */
  public static final String LOAD_TRACK_FROM_GPS = Messages
      .getString("MainWindow.LoadTrackFromGPS"); //$NON-NLS-1$

  /** Text for menu item to load tracks from a file */
  public static final String LOAD_TRACKS_FROM_FILE = Messages
      .getString("MainWindow.LoadTracksFromFile"); //$NON-NLS-1$

  /** Text for menu item to save tracks */
  public static final String SAVE_TRACK = Messages
      .getString("MainWindow.SaveTrack"); //$NON-NLS-1$

  /** Text for menu item to open settings dialog */
  public static final String SETTINGS = Messages
      .getString("MainWindow.Settings"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String COPY_TIME_OFFSET_SELECTED = Messages
      .getString("ImagesTablePopupMenu.CopyTimeOffsetToSelectedImages"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String COPY_TIME_OFFSET_ALL = Messages
      .getString("ImagesTablePopupMenu.CopyTimeOffsetToAllImages"); //$NON-NLS-1$

  /** Text for sub menu. */
  public static final String GOOGLEEARTH = Messages
      .getString("ImagesTablePopupMenu.GoogleEarth"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SHOW_IN_GOOGLEEARTH = Messages
      .getString("ImagesTablePopupMenu.ShowInGoogleEarth"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String EXPORT_THIS = Messages
      .getString("ImagesTablePopupMenu.ExportThis"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String EXPORT_SELECTED = Messages
      .getString("ImagesTablePopupMenu.ExportSelected"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String EXPORT_ALL = Messages
      .getString("ImagesTablePopupMenu.ExportAll"); //$NON-NLS-1$
  
  /** Text for menu. */
  public static final String SAVE_LOCATIONS = Messages
      .getString("ImagesTablePopupMenu.SaveNewLocations"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SAVE_THIS_LOCATION = Messages
      .getString("ImagesTablePopupMenu.SaveThisImage"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SAVE_SELECTED_LOCATIONS = Messages
      .getString("ImagesTablePopupMenu.SaveSelectedImages"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String SAVE_ALL_LOCATIONS = Messages
      .getString("ImagesTablePopupMenu.SaveAllImages"); //$NON-NLS-1$
  
  /** Text for sub menu. */
  public static final String MATCH_TRACKS = Messages
      .getString("ImagesTablePopupMenu.MatchTracks"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String MATCH_TRACK_THIS = Messages
      .getString("ImagesTablePopupMenu.MatchTracksToThisImage"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String MATCH_TRACK_SELECTED = Messages
      .getString("ImagesTablePopupMenu.MatchTracksToSelectedImages"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String MATCH_TRACK_ALL = Messages
      .getString("ImagesTablePopupMenu.MatchTracksToAllImages"); //$NON-NLS-1$

  /** Text for sub menu. */
  public static final String COPY_LOCATION = Messages
      .getString("ImagesTablePopupMenu.CopyLocation"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String COPY_LOCATION_PREVIOUS = Messages
      .getString("ImagesTablePopupMenu.CopyLocationToPrevious"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String COPY_LOCATION_NEXT = Messages
      .getString("ImagesTablePopupMenu.CopyLocationToNext"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String COPY_LOCATION_SELECTED = Messages
      .getString("ImagesTablePopupMenu.CopyLocationToSelected"); //$NON-NLS-1$

  /** Text for menu item. */
  public static final String COPY_LOCATION_ALL = Messages
      .getString("ImagesTablePopupMenu.CopyLocationToAll"); //$NON-NLS-1$
  
}
