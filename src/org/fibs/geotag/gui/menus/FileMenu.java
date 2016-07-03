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
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo.THUMBNAIL_STATUS;
import org.fibs.geotag.dcraw.Dcraw;
import org.fibs.geotag.exif.Exiftool;
import org.fibs.geotag.gpsbabel.GPSBabel;
import org.fibs.geotag.gui.MainWindow;
import org.fibs.geotag.gui.settings.SettingsDialog;
import org.fibs.geotag.image.FileTypes;
import org.fibs.geotag.image.ImageFileFilter;
import org.fibs.geotag.image.ImageFileFilter.Type;
import org.fibs.geotag.table.ImagesTable;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.tasks.ExifReaderTask;
import org.fibs.geotag.tasks.GPSBabelTask;
import org.fibs.geotag.tasks.GpxReadFileTask;
import org.fibs.geotag.tasks.TaskExecutor;
import org.fibs.geotag.track.GpxFileFilter;
import org.fibs.geotag.track.GpxWriter;
import org.fibs.geotag.track.TrackStore;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.topografix.gpx._1._0.Gpx;
import com.topografix.gpx._1._0.Gpx.Trk;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg;

/**
 * @author andreas
 * 
 */
@SuppressWarnings("serial")
public class FileMenu extends JMenu implements MenuConstants {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(FileMenu.class);

  /** Menu item to add one file. */
  private JMenuItem addFileItem;

  /** Menu item to add all files in a directory. */
  private JMenuItem addDirectoryItem;

  /** Sub-menu to save files with new locations */
  SaveLocationsMenu saveLocationsMenu;

  /** Menu item to add a GPX file. */
  private JMenuItem addTrackItem;

  /** Menu item to save tracks as a GPX file. */
  JMenuItem saveTrackItem;

  /** Menu item to load track from GPS. */
  JMenuItem loadTrackFromGpsItem;

  /** Menu item to open the settings dialog. */
  private JMenuItem settingsItem;

  /** The images table */
  private ImagesTable imagesTable;

  /** The progress bar */
  private JProgressBar progressBar;

  /**
   * Constructor for File menu. We need to inject two bit here:
   * 
   * @param imagesTable
   *          The images table
   * @param progressBar
   *          The progress bar
   */
  public FileMenu(ImagesTable imagesTable, JProgressBar progressBar) {
    super(FILE_MENU_NAME);
    this.imagesTable = imagesTable;
    this.progressBar = progressBar;
    addFileItem = new JMenuItem(ADD_FILE + ELLIPSIS);
    addFileItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addFile();
      }
    });
    this.add(addFileItem);

    addDirectoryItem = new JMenuItem(ADD_FILES + ELLIPSIS);
    addDirectoryItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addDirectory();
      }
    });
    this.add(addDirectoryItem);

    saveLocationsMenu = new SaveLocationsMenu(false, imagesTable, null);
    this.add(saveLocationsMenu);

    addTrackItem = new JMenuItem(LOAD_TRACKS_FROM_FILE + ELLIPSIS);
    addTrackItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addTrackFromFile();
      }
    });
    this.add(addTrackItem);

    saveTrackItem = new JMenuItem(SAVE_TRACK + ELLIPSIS);
    saveTrackItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveTrack();
      }
    });
    saveTrackItem.setEnabled(false);
    this.add(saveTrackItem);

    loadTrackFromGpsItem = new JMenuItem(LOAD_TRACK_FROM_GPS);
    loadTrackFromGpsItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        loadTracksFromGps();
      }
    });
    loadTrackFromGpsItem.setEnabled(GPSBabel.isAvailable());
    this.add(loadTrackFromGpsItem);

    settingsItem = new JMenuItem(SETTINGS + ELLIPSIS);
    settingsItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // remember availability of dcraw
        boolean dcrawAvailable = Dcraw.isAvailable();
        JFrame mainWindow = MainWindow.getMainWindow(FileMenu.this);
        SettingsDialog settingsDialog = new SettingsDialog(mainWindow);
        settingsDialog.openDialog();
        // after the settings dialog has closed we might need to check again
        // if the external programs are still available
        Exiftool.checkExiftoolAvailable();
        GPSBabel.checkGPSBabelAvailable();
        Dcraw.checkDcrawAvailable();
        if (!dcrawAvailable && Dcraw.isAvailable()) {
          // dcraw wasn't available, but now it isn't
          for (int row = 0; row < getTableModel().getRowCount(); row++) {
            if (getTableModel().getImageInfo(row).getThumbNailStatus() == THUMBNAIL_STATUS.FAI1LED) {
              // now that dcraw is available, loading the thumbnail might work
              getTableModel().getImageInfo(row).setThumbNailStatus(
                  THUMBNAIL_STATUS.UNKNOWN);
            }
          }
        }
        // the menu item to load tracks from the GPS should
        // only be enabled if GPSBabel is available
        loadTrackFromGpsItem.setEnabled(GPSBabel.isAvailable());
        // we might need to change the font
        getTable().usePreferredFont();
        getTableModel().fireTableDataChanged();
      }
    });
    this.add(settingsItem);
    this.getPopupMenu().addPopupMenuListener(new PopupMenuListener() {
      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        saveLocationsMenu.populate(false, getTable(), null);
      }

    });
  }

  /**
   * @return the images table
   */
  ImagesTable getTable() {
    return imagesTable;
  }

  /**
   * @return the image table's model
   */
  ImagesTableModel getTableModel() {
    return (ImagesTableModel) imagesTable.getModel();
  }

  /**
   * @return The progress bar
   */
  JProgressBar getProgressBar() {
    return progressBar;
  }

  /**
   * Select an image file and add it to the table.
   */
  void addFile() {
    JFileChooser chooser = new JFileChooser();
    String fileName = Settings.get(SETTING.LAST_FILE_OPENED, null);
    if (fileName != null) {
      File file = new File(fileName);
      if (file.exists()) {
        chooser.setSelectedFile(file);
      }
    }
    // we don't want the default AcceptAllFilter
    chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
    chooser.addChoosableFileFilter(ImageFileFilter.getFilter(Type.ALL_IMAGES));
    chooser.addChoosableFileFilter(ImageFileFilter.getFilter(Type.JPEG));
    chooser.addChoosableFileFilter(ImageFileFilter.getFilter(Type.RAW));
    chooser.addChoosableFileFilter(ImageFileFilter.getFilter(Type.TIFF));
    if (FileTypes.getFileTypesSupportedByXmp().size() > 0) {
      chooser.addChoosableFileFilter(ImageFileFilter.getFilter(Type.CUSTOM_FILE_WITH_XMP));
    }
    chooser.setFileFilter(ImageFileFilter.getLastFilterUsed());
    chooser.setMultiSelectionEnabled(true);
    if (chooser.showOpenDialog(MainWindow.getMainWindow(FileMenu.this)) == JFileChooser.APPROVE_OPTION) {
      File[] files = chooser.getSelectedFiles();
      // just save the first selected file
      Settings.put(SETTING.LAST_FILE_OPENED, files[0].getPath());
      Settings.flush();
      ImageFileFilter filter = (ImageFileFilter) chooser.getFileFilter();
      ImageFileFilter.storeLastFilterUsed(filter);
      ExifReaderTask task = new ExifReaderTask(ADD_FILE, getTableModel(), files);
      TaskExecutor.execute(task);
    }
  }

  /**
   * Select a directory and add all image files in it.
   */
  void addDirectory() {
    JFileChooser chooser = new JFileChooser();
    String directoryName = Settings.get(SETTING.LAST_DIRECTORY_OPENED, null);
    if (directoryName != null) {
      File directory = new File(directoryName);
      if (directory.exists() && directory.isDirectory()) {
        chooser.setCurrentDirectory(directory);
      }
    }
    // we don't want the default AcceptAllFilter
    chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
    chooser.addChoosableFileFilter(ImageFileFilter.getFilter(Type.ALL_IMAGES));
    chooser.addChoosableFileFilter(ImageFileFilter.getFilter(Type.JPEG));
    chooser.addChoosableFileFilter(ImageFileFilter.getFilter(Type.RAW));
    chooser.addChoosableFileFilter(ImageFileFilter.getFilter(Type.TIFF));
    if (FileTypes.getFileTypesSupportedByXmp().size() > 0) {
      chooser.addChoosableFileFilter(ImageFileFilter.getFilter(Type.CUSTOM_FILE_WITH_XMP));
    }
    chooser.setFileFilter(ImageFileFilter.getLastFilterUsed());
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    if (chooser.showOpenDialog(MainWindow.getMainWindow(FileMenu.this)) == JFileChooser.APPROVE_OPTION) {
      File directory = chooser.getSelectedFile();
      Settings.put(SETTING.LAST_DIRECTORY_OPENED, directory.getPath());
      Settings.flush();
      ImageFileFilter filter = (ImageFileFilter) chooser.getFileFilter();
      ImageFileFilter.storeLastFilterUsed(filter);
      File[] files = directory.listFiles(filter);
      ExifReaderTask task = new ExifReaderTask(ADD_FILES, getTableModel(),
          files);
      TaskExecutor.execute(task);
    }
  }

  /**
   * Choose a GPX file and read the information from it.
   */
  void addTrackFromFile() {
    JFileChooser chooser = new JFileChooser();
    String lastFile = Settings.get(SETTING.LAST_GPX_FILE_OPENED, null);
    if (lastFile != null) {
      File file = new File(lastFile);
      if (file.exists()) {
        chooser.setSelectedFile(file);
      }
    }
    chooser.setFileFilter(new GpxFileFilter());
    chooser.setMultiSelectionEnabled(true);
    if (chooser.showOpenDialog(MainWindow.getMainWindow(FileMenu.this)) == JFileChooser.APPROVE_OPTION) {
      final File[] files = chooser.getSelectedFiles();
      Settings.put(SETTING.LAST_GPX_FILE_OPENED, files[0].getPath());
      Settings.flush();
      TaskExecutor.execute(new GpxReadFileTask(LOAD_TRACKS_FROM_FILE, files) {

        @Override
        protected void process(List<Gpx> chunks) {
          super.process(chunks);
          for (Gpx gpx : chunks) {
            if (gpx != null) {
              TrackStore.getTrackStore().addGPX(gpx);
            } else {
              JOptionPane
                  .showMessageDialog(
                      MainWindow.getMainWindow(FileMenu.this),
                      i18n.tr("Could not read GPX file"), //$NON-NLS-1$
                      i18n.tr("Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
            }
          }
        }

        @Override
        protected void done() {
          super.done();
          // now that we have a track, we are allowed to save it
          saveTrackItem.setEnabled(true);
        }
      });
    }
  }

  /**
   * Save the tracks to a file selected by the user.
   */
  void saveTrack() {
    JFileChooser chooser = new JFileChooser();
    String lastFile = Settings.get(SETTING.LAST_GPX_FILE_OPENED, null);
    if (lastFile != null) {
      File file = new File(lastFile);
      if (file.exists() && file.getParentFile() != null) {
        chooser.setCurrentDirectory(file.getParentFile());
      }
    }
    GpxFileFilter gpxFileFilter = new GpxFileFilter();
    chooser.setFileFilter(gpxFileFilter);
    chooser.setMultiSelectionEnabled(false);
    if (chooser.showSaveDialog(MainWindow.getMainWindow(FileMenu.this)) == JFileChooser.APPROVE_OPTION) {
      try {
        File outputFile = chooser.getSelectedFile();
        if (!gpxFileFilter.accept(outputFile)) {
          // not a gpx file selected - add .gpx suffix
          outputFile = new File(chooser.getSelectedFile().getPath() + ".gpx"); //$NON-NLS-1$
        }
        if (outputFile.exists()) {
          String title = i18n.tr("File exists"); //$NON-NLS-1$
          String message = String
              .format(
                  i18n.tr("Overwrite existing file %s?"), outputFile.getName()); //$NON-NLS-1$
          if (JOptionPane.showConfirmDialog(MainWindow
              .getMainWindow(FileMenu.this), message, title,
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
            return;
          }
        }
        new GpxWriter().write(TrackStore.getTrackStore().getGpx(), outputFile);
        String message = String.format(i18n
            .tr("Tracks saved to %s."), outputFile.getPath()); //$NON-NLS-1$
        progressBar.setString(message);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Try reading track logs from a GPS device.
   */
  void loadTracksFromGps() {
    GPSBabelTask task = new GPSBabelTask(LOAD_TRACK_FROM_GPS) {

      @Override
      protected void process(List<Gpx> chunks) {
        int numTrackpoints = 0;
        for (Gpx gpx : chunks) {
          TrackStore.getTrackStore().addGPX(gpx);
          List<Trk> tracks = gpx.getTrk();
          for (Trk trk : tracks) {
            List<Trkseg> segments = trk.getTrkseg();
            for (Trkseg segment : segments) {
              numTrackpoints += segment.getTrkpt().size();
            }
          }
        }
        String message = "" + numTrackpoints + ' ' + i18n.tr("locations loaded"); //$NON-NLS-1$ //$NON-NLS-2$
        getProgressBar().setString(message);
        // now that we have a track, we are allowed to save it
        saveTrackItem.setEnabled(true);
      }

      @Override
      protected void done() {
        List<String> errorMessages = getErrorMessages();
        if (errorMessages.size() > 0) {
          // there have been error messages.. display them
          StringBuilder message = new StringBuilder();
          for (String string : errorMessages) {
            message.append(string).append('\n');
          }
          JOptionPane
              .showMessageDialog(
                  MainWindow.getMainWindow(FileMenu.this),
                  message,
                  i18n.tr("GPSBabel error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        }
      }
    };
    TaskExecutor.execute(task);
  }

  /**
   * Some menu items need to disabled while a background task is running.
   * 
   * @param enable
   *          Whether to enable (true) or disable (false) those items
   */
  public void updateMenuAvailability(boolean enable) {
    addFileItem.setEnabled(enable);
    addDirectoryItem.setEnabled(enable);
    addTrackItem.setEnabled(enable);
    loadTrackFromGpsItem.setEnabled(enable);
    settingsItem.setEnabled(enable);
  }

}
