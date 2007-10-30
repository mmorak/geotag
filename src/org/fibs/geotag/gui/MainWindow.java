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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import javax.swing.undo.UndoManager;

import net.iharder.dnd.FileDrop;

import org.fibs.geotag.Geotag;
import org.fibs.geotag.GlobalUndoManager;
import org.fibs.geotag.Messages;
import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.ImageInfo.THUMBNAIL_STATUS;
import org.fibs.geotag.dcraw.Dcraw;
import org.fibs.geotag.exif.Exiftool;
import org.fibs.geotag.external.ExternalUpdate;
import org.fibs.geotag.external.ExternalUpdateConsumer;
import org.fibs.geotag.googleearth.KmlRequestHandler;
import org.fibs.geotag.gpsbabel.GPSBabel;
import org.fibs.geotag.gui.settings.SettingsDialog;
import org.fibs.geotag.image.ImageFileFilter;
import org.fibs.geotag.table.ImagesTable;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.tasks.BackgroundTask;
import org.fibs.geotag.tasks.BackgroundTaskListener;
import org.fibs.geotag.tasks.ExifReaderTask;
import org.fibs.geotag.tasks.ExternalUpdateTask;
import org.fibs.geotag.tasks.GPSBabelTask;
import org.fibs.geotag.tasks.UndoableBackgroundTask;
import org.fibs.geotag.track.GpxFileFilter;
import org.fibs.geotag.track.GpxReader;
import org.fibs.geotag.track.GpxWriter;
import org.fibs.geotag.track.TrackStore;
import org.fibs.geotag.webserver.GeonamesHandler;
import org.fibs.geotag.webserver.ImageInfoHandler;
import org.fibs.geotag.webserver.MapHandler;
import org.fibs.geotag.webserver.ResourceHandler;
import org.fibs.geotag.webserver.SettingsHandler;
import org.fibs.geotag.webserver.ThumbnailHandler;
import org.fibs.geotag.webserver.TracksHandler;
import org.fibs.geotag.webserver.UpdateHandler;
import org.fibs.geotag.webserver.WebServer;

import com.centerkey.utils.BareBonesBrowserLaunch;
import com.topografix.gpx._1._0.Gpx;
import com.topografix.gpx._1._0.Gpx.Trk;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg;

/**
 * A class representing the main Window of the application
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame implements BackgroundTaskListener,
    ExternalUpdateConsumer {

  /** Text for menu item to add individual files */
  static final String ADD_FILE = Messages.getString("MainWindow.AddFile"); //$NON-NLS-1$

  /** Text for menu item to add all files in a directory */
  static final String ADD_FILES = Messages.getString("MainWindow.AddDirectory"); //$NON-NLS-1$

  /** An ellipsis of three dots */
  private static final String ELLIPSIS = "..."; //$NON-NLS-1$

  /** The tableModel for the data displayed in this window */
  ImagesTableModel tableModel;

  /** The table used to display the data from the TableModel */
  ImagesTable table;

  /** The menu bar of the main window */
  private JMenuBar menuBar = new JMenuBar();

  /** The {@link JScrollPane} holding the table */
  private JScrollPane tableScrollPane;

  /** The {@link ImageComponent} displaying the previews */
  ImageComponent previewComponent;

  /** A JProgressBar to show progress for lengthy operations */
  JProgressBar progressBar;

  /** Keep track of background tasks - we only allow one at a time */
  BackgroundTask<?> backgroundTask = null;

  /** Where we store external updates while background tasks are running */
  Vector<ExternalUpdate> pendingExternalUpdates = new Vector<ExternalUpdate>();

  /** Menu item to add one file */
  private JMenuItem addFileItem;

  /** Menu item to add all files in a directory */
  private JMenuItem addDirectoryItem;

  /** Menu item to add a GPX file */
  private JMenuItem addTrackItem;

  /** Menu item to save tracks as a GPX file */
  JMenuItem saveTrackItem;

  /** Menu item to load track from GPS */
  JMenuItem loadTrackFromGpsItem;

  /** Menu item to open the settings dialog */
  private JMenuItem settingsItem;

  /** Menu item to undo the last task */
  private JMenuItem undoItem;

  /** Menu item to redo the next task */
  private JMenuItem redoItem;

  /** Menu item to cancel a running task */
  JMenuItem cancelItem;

  /**
   * Constructor for the main window
   */
  public MainWindow() {
    super(Geotag.NAME);
    // Check if external tools are available.
    // This allows later retrieval of the result
    // It needs to be done before the menu bar is set up.
    Exiftool.checkExiftoolAvailable();
    GPSBabel.checkGPSBabelAvailable();
    Dcraw.checkDcrawAvailable();
    // now we can deal with the menu bar
    setupMenuBar();
    setJMenuBar(menuBar);
    setLayout(new BorderLayout());
    tableModel = new ImagesTableModel();
    table = new ImagesTable(tableModel);
    table.addMouseListener(new MouseAdapter() {
      private void popupMenu(MouseEvent event) {
        int mouseOnRow = table.rowAtPoint(event.getPoint());
        ImagesTablePopupMenu popupMenu = new ImagesTablePopupMenu(
            MainWindow.this, table, mouseOnRow, backgroundTask != null);
        popupMenu.show((Component) event.getSource(), event.getX(), event
            .getY());
      }

      @Override
      public void mousePressed(MouseEvent event) {
        super.mousePressed(event);
        if (event.isPopupTrigger()) {
          popupMenu(event);
        } else if (event.getButton() == MouseEvent.BUTTON1) {
          // this needs to be in mousePressed() not mouseClicked()
          int mouseOnRow = table.rowAtPoint(event.getPoint());
          ImageInfo imageInfo = tableModel.getImageInfo(mouseOnRow);
          previewComponent.setImage(imageInfo);
        }
      }

      @Override
      public void mouseReleased(MouseEvent event) {
        super.mouseReleased(event);
        if (event.isPopupTrigger()) {
          popupMenu(event);
        }
      }

    });
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (int) (screenSize.getWidth() / 2);
    width = Settings.get(SETTING.MAIN_WINDOW_WIDHTH, width);
    int height = (int) (screenSize.getHeight() * 0.95);
    height = Settings.get(SETTING.MAIN_WINDOW_HEIGHT, height);
    setSize(width, height);
    int x = Settings.get(SETTING.MAIN_WINDOW_X, 0);
    int y = Settings.get(SETTING.MAIN_WINDOW_Y, 0);
    setLocation(x, y);
    tableScrollPane = new JScrollPane(table);
    previewComponent = new ImageComponent(Messages
        .getString("MainWindow.Preview")); //$NON-NLS-1$
    final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        false, tableScrollPane, previewComponent);
    add(splitPane, BorderLayout.CENTER);
    progressBar = new JProgressBar();
    progressBar.setStringPainted(true);
    progressBar.setBorderPainted(false);
    progressBar.setString(""); //$NON-NLS-1$
    add(progressBar, BorderLayout.SOUTH);

    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    addWindowListener(new WindowAdapter() {
      /**
       * @see java.awt.event.WindowAdapter#windowOpened(java.awt.event.WindowEvent)
       */
      @Override
      public void windowOpened(WindowEvent e) {
        super.windowOpened(e);
        // the preview component should have a 4:3 aspect ratio by default
        int previewHeight = (int) (getWidth() / 4.0 * 3.0);
        previewHeight = Settings.get(SETTING.PREVIEW_HEIGHT, previewHeight);
        splitPane.setDividerLocation((getHeight() - previewHeight));
      }

      @Override
      public void windowClosing(WindowEvent we) {
        if (JOptionPane.showConfirmDialog(MainWindow.this, Messages
            .getString("MainWindow.DoYouReallyWantToExit"), //$NON-NLS-1$
            Messages.getString("MainWindow.ExitProgram"), //$NON-NLS-1$
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
          Settings.put(SETTING.MAIN_WINDOW_X, getLocation().x);
          Settings.put(SETTING.MAIN_WINDOW_Y, getLocation().y);
          Settings.put(SETTING.MAIN_WINDOW_HEIGHT, getHeight());
          Settings.put(SETTING.MAIN_WINDOW_WIDHTH, getWidth());
          Settings.put(SETTING.PREVIEW_HEIGHT, getHeight()
              - splitPane.getDividerLocation());
          table.saveColumnSettings();
          Settings.flush();
          System.exit(0);
        }
      }
    });
    GlobalUndoManager.getManager().setLimit(-1); // no limit
    // enable drag and drop of image files
    new FileDrop(this, new FileDrop.Listener() {
      public void filesDropped(File[] files) {
        ExifReaderTask task = new ExifReaderTask(ADD_FILES, tableModel, files);
        task.execute();
      }
    });

    // if external programs want to use the clipboard, uncomment those lines:
    // ClipboardWorker clipboardMonitor = new ClipboardWorker(this) {
    // @Override
    // protected void process(List<ExternalUpdate> clipboardUpdates) {
    // processExternalUpdates(clipboardUpdates);
    // }
    // };
    // clipboardMonitor.execute();
    // only add myself to background task listeners after the
    // clipboard monitor has started
    BackgroundTask.addBackgroundTaskListener(this);
    // also start the built in web server
    try {
      WebServer webServer = new org.fibs.geotag.webserver.WebServer(4321,
          tableModel);
      webServer.createContext("/", new ResourceHandler()); //$NON-NLS-1$
      webServer.createContext("/images", new ThumbnailHandler()); //$NON-NLS-1$
      webServer.createContext("/update", new UpdateHandler(this)); //$NON-NLS-1$
      webServer.createContext("/kml", new KmlRequestHandler(this)); //$NON-NLS-1$
      webServer.createContext("/tracks", new TracksHandler()); //$NON-NLS-1$
      webServer.createContext("/settings", new SettingsHandler()); //$NON-NLS-1$
      webServer.createContext("/imageinfo", new ImageInfoHandler()); //$NON-NLS-1$
      webServer.createContext("/map", new MapHandler()); //$NON-NLS-1$
      webServer.createContext("/geonames", new GeonamesHandler()); //$NON-NLS-1$
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the menubar and make sure all actions are handled properly.
   */
  private void setupMenuBar() {
    JMenu fileMenu = new JMenu(Messages.getString("MainWindow.File")); //$NON-NLS-1$
    addFileItem = new JMenuItem(ADD_FILE + ELLIPSIS);
    addFileItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addFile();
      }
    });
    fileMenu.add(addFileItem);

    addDirectoryItem = new JMenuItem(ADD_FILES + ELLIPSIS);
    addDirectoryItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addDirectory();
      }
    });
    fileMenu.add(addDirectoryItem);

    addTrackItem = new JMenuItem(
        Messages.getString("MainWindow.OpenTrack") + ELLIPSIS); //$NON-NLS-1$
    addTrackItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addTrackFromFile();
      }
    });
    fileMenu.add(addTrackItem);

    saveTrackItem = new JMenuItem(
        Messages.getString("MainWindow.SaveTrack") + ELLIPSIS); //$NON-NLS-1$
    saveTrackItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveTrack();
      }
    });
    saveTrackItem.setEnabled(false);
    fileMenu.add(saveTrackItem);

    loadTrackFromGpsItem = new JMenuItem(Messages
        .getString("MainWindow.LoadTrackFromGPS")); //$NON-NLS-1$
    loadTrackFromGpsItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadTracksFromGps();
      }
    });
    loadTrackFromGpsItem.setEnabled(GPSBabel.isAvailable());
    fileMenu.add(loadTrackFromGpsItem);

    settingsItem = new JMenuItem(
        Messages.getString("MainWindow.Settings") + ELLIPSIS); //$NON-NLS-1$
    settingsItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // remember availability of dcraw
        boolean dcrawAvailable = Dcraw.isAvailable();
        SettingsDialog settingsDialog = new SettingsDialog(MainWindow.this);
        settingsDialog.openDialog();
        // after the settings dialog has closed we might need to check again
        // if the external programs are still available
        Exiftool.checkExiftoolAvailable();
        GPSBabel.checkGPSBabelAvailable();
        Dcraw.checkDcrawAvailable();
        if (dcrawAvailable == false && Dcraw.isAvailable() == true) {
          // dcraw wasn't available, but now it isn't
          for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (tableModel.getImageInfo(row).getThumbNailStatus() == THUMBNAIL_STATUS.FAI1LED) {
              // now that dcraw is available, loading the thumbnail might work
              tableModel.getImageInfo(row).setThumbNailStatus(
                  THUMBNAIL_STATUS.UNKNOWN);
            }
          }
        }
        // the menu item to load tracks from the GPS should
        // only be enabled if GPSBabel is available
        loadTrackFromGpsItem.setEnabled(GPSBabel.isAvailable());
        // we might need to change the font
        table.usePreferredFont();
      }
    });
    fileMenu.add(settingsItem);

    menuBar.add(fileMenu);

    JMenu editMenu = new JMenu(Messages.getString("MainWindow.Edit")); //$NON-NLS-1$

    undoItem = new JMenuItem();
    undoItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String action = GlobalUndoManager.getManager()
            .getUndoPresentationName();
        GlobalUndoManager.getManager().undo();
        updateUndoMenuItems();
        // this will call tableModel.fireTableDataChanged() for us
        tableModel.sortRows();
        progressBar
            .setString(Messages.getString("MainWindow.Done") + ": " + action); //$NON-NLS-1$ //$NON-NLS-2$
      }
    });
    editMenu.add(undoItem);

    redoItem = new JMenuItem();
    redoItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String action = GlobalUndoManager.getManager()
            .getRedoPresentationName();
        GlobalUndoManager.getManager().redo();
        updateUndoMenuItems();
        tableModel.sortRows();
        progressBar
            .setString(Messages.getString("MainWindow.Done") + ": " + action); //$NON-NLS-1$ //$NON-NLS-2$
      }
    });
    editMenu.add(redoItem);

    cancelItem = new JMenuItem(Messages.getString("MainWindow.Cancel")); //$NON-NLS-1$
    cancelItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (backgroundTask != null) {
          backgroundTask.interruptRequest();
          cancelItem.setVisible(false);
        }
      }
    });
    cancelItem.setVisible(false);
    editMenu.add(cancelItem);

    updateUndoMenuItems();

    JMenu selectMenu = new JMenu(Messages.getString("MainWindow.Select")); //$NON-NLS-1$

    JMenuItem selectAllItem = new JMenuItem(Messages
        .getString("MainWindow.SelectAll")); //$NON-NLS-1$
    selectAllItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        table.selectAll();
      }
    });
    selectMenu.add(selectAllItem);

    JMenuItem selectWithLocationItem = new JMenuItem(Messages
        .getString("MainWindow.SelectWithLocations")); //$NON-NLS-1$
    selectWithLocationItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        table.selectAllWithLocation();
      }

    });
    selectMenu.add(selectWithLocationItem);

    JMenuItem selectWithNewLocationItem = new JMenuItem(Messages
        .getString("MainWindow.SelectWithNewLocations")); //$NON-NLS-1$
    selectWithNewLocationItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        table.selectAllWithNewLocation();
      }

    });
    selectMenu.add(selectWithNewLocationItem);

    editMenu.add(selectMenu);
    menuBar.add(editMenu);

    JMenu helpMenu = new JMenu(Messages.getString("MainWindow.Help")); //$NON-NLS-1$

    JMenuItem whatNextItem = new JMenuItem(Messages
        .getString("MainWindow.WhatNext")); //$NON-NLS-1$
    whatNextItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        WhatNext.helpWhatNext(MainWindow.this, tableModel);
      }
    });

    helpMenu.add(whatNextItem);

    final String about = Messages.getString("MainWindow.About") + ' ' + Geotag.NAME; //$NON-NLS-1$
    JMenuItem aboutItem = new JMenuItem(about + ELLIPSIS);
    aboutItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        (new AboutDialog(MainWindow.this, about)).setVisible(true);
      }
    });

    helpMenu.add(aboutItem);

    JMenuItem websiteItem = new JMenuItem(String.format(Messages
        .getString("MainWindow.WebSiteFormat") + ELLIPSIS, Geotag.NAME)); //$NON-NLS-1$
    websiteItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        BareBonesBrowserLaunch.openURL(Settings.get(SETTING.BROWSER, null),
            Geotag.WEBSITE);
      }
    });

    helpMenu.add(websiteItem);

    menuBar.add(helpMenu);
  }

  /**
   * Put external updates in list of pending update and process them if
   * possible. This method should only called on the event thread
   * 
   * @param externalUpdates
   */
  public void processExternalUpdates(List<ExternalUpdate> externalUpdates) {
    for (ExternalUpdate externalUpdate : externalUpdates) {
      pendingExternalUpdates.add(externalUpdate);
      if (backgroundTask == null) {
        processExternalUpdates();
      }
    }
  }

  /**
   * Check if there are pending updates from external programs and perform the
   * first one, removing it from the list
   */
  void processExternalUpdates() {
    if (pendingExternalUpdates.size() > 0) {
      ExternalUpdate externalUpdate = pendingExternalUpdates.firstElement();
      ExternalUpdateTask task = new ExternalUpdateTask(Messages
          .getString("MainWindow.ExternalCoordinates"), externalUpdate) { //$NON-NLS-1$
        @Override
        protected void process(List<ImageInfo> imageInfo) {
          for (ImageInfo image : imageInfo) {
            int row = tableModel.getRow(image);
            if (row >= 0) {
              tableModel.fireTableRowsUpdated(row, row);
            }
          }
        }
      };
      pendingExternalUpdates.remove(0);
      task.execute();
    }
  }

  /**
   * Query the UndoManager and set the undo/redo menu items to the correct text
   * and state
   */
  void updateUndoMenuItems() {
    UndoManager undoManager = GlobalUndoManager.getManager();
    undoItem.setText(undoManager.getUndoPresentationName());
    undoItem.setEnabled(undoManager.canUndo());
    redoItem.setText(undoManager.getRedoPresentationName());
    redoItem.setEnabled(undoManager.canRedo());
  }

  /**
   * Select an image file and add it to the table
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
    chooser.setFileFilter(new ImageFileFilter());
    chooser.setMultiSelectionEnabled(true);
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File[] files = chooser.getSelectedFiles();
      // just save the first selected file
      Settings.put(SETTING.LAST_FILE_OPENED, files[0].getPath());
      Settings.flush();
      ExifReaderTask task = new ExifReaderTask(ADD_FILE, tableModel, files);
      task.execute();
    }
  }

  /**
   * Select a directory and add all image files in it
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
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File directory = chooser.getSelectedFile();
      Settings.put(SETTING.LAST_DIRECTORY_OPENED, directory.getPath());
      Settings.flush();
      File[] files = directory.listFiles(new ImageFileFilter());
      ExifReaderTask task = new ExifReaderTask(ADD_FILES, tableModel, files);
      task.execute();
    }
  }

  /**
   * Choose a GPX file and read the information from it
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
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      Settings.put(SETTING.LAST_GPX_FILE_OPENED, file.getPath());
      Settings.flush();
      Gpx gpx = GpxReader.read(file);
      int numTrackpoints = 0;
      if (gpx != null) {
        List<Trk> tracks = gpx.getTrk();
        for (Trk trk : tracks) {
          List<Trkseg> segments = trk.getTrkseg();
          for (Trkseg segment : segments) {
            numTrackpoints += segment.getTrkpt().size();
          }
        }
      } else {
        JOptionPane.showMessageDialog(this, Messages
            .getString("MainWindow.CouldNotReadGpxFile") + ":\n" //$NON-NLS-1$ //$NON-NLS-2$
            + file.getPath(),
            Messages.getString("MainWindow.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
      }
      String message = "" + numTrackpoints + ' ' + Messages.getString("MainWindow.LocationsLoaded"); //$NON-NLS-1$ //$NON-NLS-2$
      progressBar.setString(message);
      TrackStore.getTrackStore().addGPX(gpx);
      // now that we have a track, we are allowed to save it
      saveTrackItem.setEnabled(true);
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
    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      try {
        File outputFile = chooser.getSelectedFile();
        if (!gpxFileFilter.accept(outputFile)) {
          // not a gpx file selected - add .gpx suffix
          outputFile = new File(chooser.getSelectedFile().getPath() + ".gpx"); //$NON-NLS-1$
        }
        if (outputFile.exists()) {
          String title = Messages.getString("MainWindow.FileExists"); //$NON-NLS-1$
          String message = String
              .format(
                  Messages.getString("MainWindow.OverwriteFileFormat"), outputFile.getName()); //$NON-NLS-1$
          if (JOptionPane.showConfirmDialog(this, message, title,
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
            return;
          }
        }
        new GpxWriter().write(TrackStore.getTrackStore().getGpx(), outputFile);
        String message = String.format(Messages
            .getString("MainWindow.TracksSavedFormat"), outputFile.getPath()); //$NON-NLS-1$
        progressBar.setString(message);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Try reading track logs from a GPS device
   */
  void loadTracksFromGps() {
    GPSBabelTask task = new GPSBabelTask(Messages
        .getString("MainWindow.LoadTrackFromGPS")) { //$NON-NLS-1$

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
        String message = "" + numTrackpoints + ' ' + Messages.getString("MainWindow.LocationsLoaded"); //$NON-NLS-1$ //$NON-NLS-2$
        progressBar.setString(message);
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
                  MainWindow.this,
                  message,
                  Messages.getString("MainWindow.GPSBabelError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        }
      }
    };
    task.execute();
  }

  /**
   * Some menu items need to disabled while a background task is running
   * 
   * @param enable
   *          Whether to enable (true) or disable (false) those items
   */
  private void updateMenuAvailability(boolean enable) {
    addFileItem.setEnabled(enable);
    addDirectoryItem.setEnabled(enable);
    addTrackItem.setEnabled(enable);
    loadTrackFromGpsItem.setEnabled(enable);
    settingsItem.setEnabled(enable);
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTaskListener#backgroundTaskStarted(org.fibs.geotag.tasks.BackgroundTask)
   */
  public void backgroundTaskStarted(BackgroundTask<?> task) {
    // System.out.println("Started "+task.getName());
    backgroundTask = task;

    progressBar.setMinimum(task.getMinProgress());
    progressBar.setMaximum(task.getMaxProgress());
    progressBar.setValue(task.getMinProgress());
    progressBar.setString(""); //$NON-NLS-1$

    // disable the menu items that can cause trouble
    updateMenuAvailability(false);
    undoItem.setEnabled(false);
    redoItem.setEnabled(false);
    String name = task.getName();
    if (task instanceof UndoableBackgroundTask<?>) {
      name = ((UndoableBackgroundTask<?>) task).getPresentationName();
    }
    cancelItem.setText(Messages.getString("MainWindow.Cancel") + ' ' + name); //$NON-NLS-1$
    cancelItem.setVisible(true);
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTaskListener#backgroundTaskProgress(org.fibs.geotag.tasks.BackgroundTask,
   *      java.lang.String)
   */
  public void backgroundTaskProgress(BackgroundTask<?> task,
      String progressMessage) {
    // System.out.println(progressMessage);
    progressBar.setString(progressMessage);
    progressBar.setMinimum(task.getMinProgress());
    progressBar.setMaximum(task.getMaxProgress());
    progressBar.setValue(task.getCurrentProgress());
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTaskListener#backgroundTaskFinished(org.fibs.geotag.tasks.BackgroundTask)
   */
  public void backgroundTaskFinished(BackgroundTask<?> task) {
    // System.out.println("Finished "+task.getName());
    progressBar.setValue(task.getMinProgress());
    String result = ""; //$NON-NLS-1$
    try {
      result = task.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    progressBar.setString(result);
    backgroundTask = null;
    // re-enable menu items
    updateMenuAvailability(true);
    updateUndoMenuItems();
    cancelItem.setVisible(false);
    // have any external updates arrived while we were busy?
    processExternalUpdates();
  }
}
