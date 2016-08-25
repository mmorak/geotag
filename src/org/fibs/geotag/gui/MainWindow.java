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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.undo.UndoManager;

import net.iharder.dnd.FileDrop;

import org.fibs.geotag.Geotag;
import org.fibs.geotag.GlobalUndoManager;
import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.dcraw.Dcraw;
import org.fibs.geotag.exif.Exiftool;
import org.fibs.geotag.external.ClipboardUpdate;
import org.fibs.geotag.external.ClipboardWorker;
import org.fibs.geotag.external.ExternalUpdate;
import org.fibs.geotag.external.ExternalUpdateConsumer;
import org.fibs.geotag.googleearth.KmlRequestHandler;
import org.fibs.geotag.gpsbabel.GPSBabel;
import org.fibs.geotag.gui.flattr.FlattrMenuItem;
import org.fibs.geotag.gui.menus.FileMenu;
import org.fibs.geotag.gui.menus.MenuConstants;
import org.fibs.geotag.table.ImagesTable;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.tasks.BackgroundTask;
import org.fibs.geotag.tasks.BackgroundTaskListener;
import org.fibs.geotag.tasks.ClipboardUpdateTask;
import org.fibs.geotag.tasks.ExifReaderTask;
import org.fibs.geotag.tasks.ExternalUpdateTask;
import org.fibs.geotag.tasks.TaskExecutor;
import org.fibs.geotag.tasks.UndoableBackgroundTask;
import org.fibs.geotag.util.BrowserLauncher;
import org.fibs.geotag.util.ImageUtil;
import org.fibs.geotag.webserver.GeonamesHandler;
import org.fibs.geotag.webserver.ImageInfoHandler;
import org.fibs.geotag.webserver.MapHandler;
import org.fibs.geotag.webserver.ResourceHandler;
import org.fibs.geotag.webserver.SettingsHandler;
import org.fibs.geotag.webserver.ThumbnailHandler;
import org.fibs.geotag.webserver.TracksHandler;
import org.fibs.geotag.webserver.UpdateHandler;
import org.fibs.geotag.webserver.WebServer;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A class representing the main Window of the application.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame implements BackgroundTaskListener,
    ExternalUpdateConsumer, MenuConstants {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(MainWindow.class);

  /** The default aspect ratio of the preview component. The width */
  private static final int PREVIEW_ASPECT_RATIO_X = 4;

  /** The default aspect ratio of the preview component. The height */
  private static final int PREVIEW_ASPECT_RATIO_Y = 3;

  /** the port for the built-in web server. */
  private static final int WEB_SERVER_PORT = 4321;

  /** The directory containing the icon files */
  private static final String ICON_DIRECTORY = "images"; //$NON-NLS-1$

  /** the names of the icon files */
  private static final String[] ICON_NAMES = {
      "geotag-32.png", "geotag-64.png", "geotag-128.png" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  /** The tableModel for the data displayed in this window. */
  private ImagesTableModel tableModel;

  /** The table used to display the data from the TableModel. */
  private ImagesTable table;

  /** The menu bar of the main window. */
  private JMenuBar menuBar = new JMenuBar();

  /** The {@link JScrollPane} holding the table. */
  private JScrollPane tableScrollPane;

  /** The {@link PreviewComponent} displaying the previews. */
  private PreviewComponent previewComponent;

  /** A JProgressBar to show progress for lengthy operations. */
  private JProgressBar progressBar;

  /** Keep track of background tasks - we only allow one at a time. */
  private BackgroundTask<?> backgroundTask = null;

  /** Where we store external updates while background tasks are running. */
  private Vector<ExternalUpdate> pendingExternalUpdates = new Vector<ExternalUpdate>();

  /** Menu item to undo the last task. */
  private JMenuItem undoItem;

  /** Menu item to redo the next task. */
  private JMenuItem redoItem;

  /** Menu item to cancel a running task. */
  private JMenuItem cancelItem;

  /** The file menu */
  private FileMenu fileMenu;

  /**
   * Constructor for the main window.
   */
  public MainWindow() {
    super(Geotag.NAME);
    // Check if external tools are available.
    // This allows later retrieval of the result
    // It needs to be done before the menu bar is set up.
    Exiftool.checkExiftoolAvailable();
    GPSBabel.checkGPSBabelAvailable();
    Dcraw.checkDcrawAvailable();
    // Handle application icons
    List<Image> iconImageList = new ArrayList<Image>();
    for (String iconName : ICON_NAMES) {
      System.out.println(ICON_DIRECTORY + '/' + iconName);
      Image image = ImageUtil.loadImage(ICON_DIRECTORY + '/' + iconName);
      iconImageList.add(image);
    }
    setIconImages(iconImageList);
    setLayout(new BorderLayout());
    setupTable();
    // now we can deal with the menu bar
    setupMenuBar();
    setJMenuBar(menuBar);
    setupSizeAndPosition();
    tableScrollPane = new JScrollPane(table);
    setupPreviewComponent();
    final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        false, tableScrollPane, previewComponent);
    add(splitPane, BorderLayout.CENTER);
    add(getProgressBar(), BorderLayout.SOUTH);

    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    addWindowListener(new WindowAdapter() {
      /**
       * @see java.awt.event.WindowAdapter#windowOpened(java.awt.event.WindowEvent)
       */
      @Override
      public void windowOpened(WindowEvent e) {
        super.windowOpened(e);
        // the preview component should have a 4:3 aspect ratio by default

        int previewHeight = (int) (getWidth() / ((double) PREVIEW_ASPECT_RATIO_X / PREVIEW_ASPECT_RATIO_Y));
        previewHeight = Settings.get(SETTING.PREVIEW_HEIGHT, previewHeight);
        splitPane.setDividerLocation((getHeight() - previewHeight));
      }

      @Override
      public void windowClosing(WindowEvent we) {
        if (JOptionPane.showConfirmDialog(MainWindow.this, i18n
            .tr("Do you really want to exit Geotag?"), //$NON-NLS-1$
            i18n.tr("Exit Program"), //$NON-NLS-1$
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
          Settings.put(SETTING.MAIN_WINDOW_X, getLocation().x);
          Settings.put(SETTING.MAIN_WINDOW_Y, getLocation().y);
          Settings.put(SETTING.MAIN_WINDOW_HEIGHT, getHeight());
          Settings.put(SETTING.MAIN_WINDOW_WIDHTH, getWidth());
          Settings.put(SETTING.PREVIEW_HEIGHT, getHeight()
              - splitPane.getDividerLocation());
          getTable().saveColumnSettings();
          Settings.flush();
          System.exit(0);
        }
      }
    });
    GlobalUndoManager.getManager().setLimit(-1); // no limit
    // enable drag and drop of image files
    new FileDrop(this, new FileDrop.Listener() {
      @Override
      public void filesDropped(File[] files) {
        ExifReaderTask task = new ExifReaderTask(ADD_FILES, getTableModel(),
            files);
        TaskExecutor.execute(task);
      }
    });

    // here we start monitoring changes to the clipboard
    ClipboardWorker clipboardMonitor = new ClipboardWorker(this) {
      @Override
      protected void process(List<ClipboardUpdate> clipboardUpdates) {
        processClipboardUpdates(clipboardUpdates);
      }
    };
    TaskExecutor.execute(clipboardMonitor);
    // only add myself to background task listeners after the
    // clipboard monitor has started
    BackgroundTask.addBackgroundTaskListener(this);
    // also start the built in web server
    setupWebServer();
  }

  /**
   * 
   */
  private void setupWebServer() {
    try {
      WebServer webServer = new org.fibs.geotag.webserver.WebServer(
          WEB_SERVER_PORT, tableModel);
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
   * 
   */
  private void setupPreviewComponent() {
    previewComponent = new PreviewComponent(i18n
        .tr("Image viewer")); //$NON-NLS-1$
    previewComponent.addMouseListener(new MouseAdapter() {
      private void popupMenu(MouseEvent event) {
        ImageInfo imageInfo = getPreviewComponent().getImageInfo();
        int tableRow = getTableModel().getRow(imageInfo);
        if (imageInfo != null) {
          ImagesTablePopupMenu popupMenu = new ImagesTablePopupMenu(
              MainWindow.this, getTable(), tableRow,
              getBackgroundTask() != null);
          popupMenu.show((Component) event.getSource(), event.getX(), event
              .getY());
        }
      }

      @Override
      public void mousePressed(MouseEvent event) {
        super.mousePressed(event);
        if (event.isPopupTrigger()) {
          popupMenu(event);
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
  }

  /**
   * @throws HeadlessException
   */
  private void setupSizeAndPosition() {
    try {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      int width = (int) (screenSize.getWidth() / 2);
      width = Settings.get(SETTING.MAIN_WINDOW_WIDHTH, width);
      final double factor = 0.95;
      int height = (int) (screenSize.getHeight() * factor);
      height = Settings.get(SETTING.MAIN_WINDOW_HEIGHT, height);
      setSize(width, height);
      int x = Settings.get(SETTING.MAIN_WINDOW_X, 0);
      int y = Settings.get(SETTING.MAIN_WINDOW_Y, 0);
      setLocation(x, y);
    } catch (HeadlessException e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   */
  private void setupTable() {
    tableModel = new ImagesTableModel();
    table = new ImagesTable(tableModel) {
      @Override
      public void valueChanged(ListSelectionEvent event) {
        // the table row selection has changed
        // we might need to change the preview component
        ListSelectionModel newSelectionModel = (ListSelectionModel) event
            .getSource();
        if (newSelectionModel.getValueIsAdjusting()) {
          return;
        }
        int lead = newSelectionModel.getLeadSelectionIndex();
        if (newSelectionModel.isSelectedIndex(lead)) {
          // time to change the preview
          ImageInfo imageInfo = getTableModel().getImageInfo(lead);
          getPreviewComponent().setImageInfo(imageInfo);
        }
        super.valueChanged(event);
      }
    };
    table.addMouseListener(new MouseAdapter() {
      private void popupMenu(MouseEvent event) {
        int mouseOnRow = getTable().rowAtPoint(event.getPoint());
        ImagesTablePopupMenu popupMenu = new ImagesTablePopupMenu(
            MainWindow.this, getTable(), mouseOnRow,
            getBackgroundTask() != null);
        popupMenu.show((Component) event.getSource(), event.getX(), event
            .getY());
      }

      @Override
      public void mousePressed(MouseEvent event) {
        super.mousePressed(event);
        if (event.isPopupTrigger()) {
          popupMenu(event);
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
  }

  /**
   * Create the menubar and make sure all actions are handled properly.
   */
  private void setupMenuBar() {
    fileMenu = new FileMenu(table, getProgressBar());
    menuBar.add(fileMenu);

    JMenu editMenu = setupEditMenu();
    menuBar.add(editMenu);

    JMenu helpMenu = setupHelpMenu();
    menuBar.add(helpMenu);

  }

  /**
   * @return the help menu
   */
  private JMenu setupHelpMenu() {
    JMenu helpMenu = new JMenu(i18n.tr("Help")); //$NON-NLS-1$

    JMenuItem whatNextItem = new JMenuItem(i18n
        .tr("What next?")); //$NON-NLS-1$
    whatNextItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        WhatNext.helpWhatNext(MainWindow.this, getTableModel());
      }
    });

    helpMenu.add(whatNextItem);

    final String about = i18n.tr("About") + ' ' + Geotag.NAME; //$NON-NLS-1$
    JMenuItem aboutItem = new JMenuItem(about + ELLIPSIS);
    aboutItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        (new AboutDialog(MainWindow.this, about)).setVisible(true);
      }
    });

    helpMenu.add(aboutItem);

    JMenuItem websiteItem = new JMenuItem(String.format(i18n
        .tr("%s web site") + ELLIPSIS, Geotag.NAME)); //$NON-NLS-1$
    websiteItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        BrowserLauncher.openURL(Settings.get(SETTING.BROWSER, null),
            Geotag.WEBSITE);
      }
    });

    helpMenu.add(websiteItem);
    
    JMenuItem flattrItem = new FlattrMenuItem();
    helpMenu.add((JComponent)flattrItem);
    return helpMenu;
  }

  /**
   * @return the edit menu
   */
  private JMenu setupEditMenu() {
    JMenu editMenu = new JMenu(i18n.tr("Edit")); //$NON-NLS-1$

    undoItem = new JMenuItem();
    undoItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String action = GlobalUndoManager.getManager()
            .getUndoPresentationName();
        GlobalUndoManager.getManager().undo();
        updateUndoMenuItems();
        // this will call tableModel.fireTableDataChanged() for us
        getTableModel().sortRows();
        getProgressBar().setString(
            i18n.tr("Done") + ": " + action); //$NON-NLS-1$ //$NON-NLS-2$
      }
    });
    editMenu.add(undoItem);

    redoItem = new JMenuItem();
    redoItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String action = GlobalUndoManager.getManager()
            .getRedoPresentationName();
        GlobalUndoManager.getManager().redo();
        updateUndoMenuItems();
        getTableModel().sortRows();
        getProgressBar().setString(
            i18n.tr("Done") + ": " + action); //$NON-NLS-1$ //$NON-NLS-2$
      }
    });
    editMenu.add(redoItem);

    cancelItem = new JMenuItem(i18n.tr("Cancel")); //$NON-NLS-1$
    cancelItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (getBackgroundTask() != null) {
          getBackgroundTask().interruptRequest();
          getCancelItem().setVisible(false);
        }
      }
    });
    cancelItem.setVisible(false);
    editMenu.add(cancelItem);

    updateUndoMenuItems();

    JMenu selectMenu = new JMenu(i18n.tr("Select")); //$NON-NLS-1$

    JMenuItem selectAllItem = new JMenuItem(i18n
        .tr("All images")); //$NON-NLS-1$
    selectAllItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        getTable().selectAll();
      }
    });
    selectMenu.add(selectAllItem);

    JMenuItem selectWithLocationItem = new JMenuItem(i18n
        .tr("Images with locations")); //$NON-NLS-1$
    selectWithLocationItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        getTable().selectAllWithLocation();
      }

    });
    selectMenu.add(selectWithLocationItem);

    JMenuItem selectWithNewLocationItem = new JMenuItem(i18n
        .tr("Images with new locations")); //$NON-NLS-1$
    selectWithNewLocationItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        getTable().selectAllWithNewLocation();
      }

    });
    selectMenu.add(selectWithNewLocationItem);

    JMenuItem selectNoneItem = new JMenuItem(i18n
        .tr("No images")); //$NON-NLS-1$
    selectNoneItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        getTable().selectNone();
      }
    });
    selectMenu.add(selectNoneItem);
    editMenu.add(selectMenu);
    return editMenu;
  }

  /**
   * Put external updates in list of pending update and process them if
   * possible. This method should only called on the event thread
   * 
   * @param externalUpdates
   */
  @Override
  public void processExternalUpdates(List<ExternalUpdate> externalUpdates) {
    for (ExternalUpdate externalUpdate : externalUpdates) {
      pendingExternalUpdates.add(externalUpdate);
      if (backgroundTask == null) {
        processExternalUpdates();
      }
    }
  }

  /**
   * Process coordinate updates received from the clipboard.
   * 
   * @param clipboardUpdates
   */
  public void processClipboardUpdates(List<ClipboardUpdate> clipboardUpdates) {
    // We've got new coordinates from the clipboard. Let's apply them
    // to all selected images if there is no background task running.
    if (backgroundTask == null) {
      int[] selectedRows = getTable().getSelectedRows();
      if (selectedRows.length > 0) {
        List<ImageInfo> selectedImages = new ArrayList<ImageInfo>();
        for (int row = 0; row < selectedRows.length; row++) {
          selectedImages.add(tableModel.getImageInfo(selectedRows[row]));
        }
        // Now we can apply the new coordinates to the selected images
        ClipboardUpdateTask task = new ClipboardUpdateTask(
            i18n.tr("Coordinates from clipboard"), clipboardUpdates, selectedImages) { //$NON-NLS-1$
          @Override
          protected void process(List<ImageInfo> imageInfo) {
            for (ImageInfo image : imageInfo) {
              int row = getTableModel().getRow(image);
              if (row >= 0) {
                getTableModel().fireTableRowsUpdated(row, row);
              }
            }
          }
        };
        TaskExecutor.execute(task);
      }
    }
  }

  /**
   * Check if there are pending updates from external programs and perform the
   * first one, removing it from the list.
   */
  void processExternalUpdates() {
    if (pendingExternalUpdates.size() > 0) {
      ExternalUpdate externalUpdate = pendingExternalUpdates.firstElement();
      ExternalUpdateTask task = new ExternalUpdateTask(i18n
          .tr("External coordinates"), externalUpdate) { //$NON-NLS-1$
        @Override
        protected void process(List<ImageInfo> imageInfo) {
          for (ImageInfo image : imageInfo) {
            int row = getTableModel().getRow(image);
            if (row >= 0) {
              getTableModel().fireTableRowsUpdated(row, row);
            }
          }
        }
      };
      pendingExternalUpdates.remove(0);
      TaskExecutor.execute(task);
    }
  }

  /**
   * Query the UndoManager and set the undo/redo menu items to the correct text
   * and state.
   */
  void updateUndoMenuItems() {
    UndoManager undoManager = GlobalUndoManager.getManager();
    undoItem.setText(undoManager.getUndoPresentationName());
    undoItem.setEnabled(undoManager.canUndo());
    redoItem.setText(undoManager.getRedoPresentationName());
    redoItem.setEnabled(undoManager.canRedo());
  }

  /**
   * Check that the image displayed in the preview component (image viewer) is
   * still available and hasn't been removed. If the image has been removed,
   * clear the image viewer.
   */
  private void validatePreview() {
    ImageInfo previewImage = previewComponent.getImageInfo();
    if (previewImage != null && tableModel.getRow(previewImage) < 0) {
      previewComponent.setImage(null);
    }
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTaskListener#backgroundTaskStarted(org.fibs.geotag.tasks.BackgroundTask)
   */
  @Override
  public void backgroundTaskStarted(BackgroundTask<?> task) {
    // System.out.println("Started "+task.getName());
    backgroundTask = task;
    int minProgress = task.getMinProgress();
    int maxProgress = task.getMaxProgress();
    getProgressBar().setMinimum(minProgress);
    getProgressBar().setMaximum(maxProgress);
    getProgressBar().setValue(minProgress);
    getProgressBar().setIndeterminate(minProgress == maxProgress);
    getProgressBar().setString(""); //$NON-NLS-1$

    // disable the menu items that can cause trouble
    fileMenu.updateMenuAvailability(false);
    undoItem.setEnabled(false);
    redoItem.setEnabled(false);
    String name = task.getName();
    if (task instanceof UndoableBackgroundTask<?>) {
      name = ((UndoableBackgroundTask<?>) task).getPresentationName();
    }
    cancelItem.setText(i18n.tr("Cancel") + ' ' + name); //$NON-NLS-1$
    cancelItem.setVisible(true);

    tableModel.setEditingForbidden(true);
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTaskListener#backgroundTaskProgress(org.fibs.geotag.tasks.BackgroundTask,
   *      java.lang.String)
   */
  @Override
  public void backgroundTaskProgress(BackgroundTask<?> task,
      String progressMessage) {
    // System.out.println(progressMessage);
    int minProgress = task.getMinProgress();
    int maxProgress = task.getMaxProgress();
    getProgressBar().setString(progressMessage);
    getProgressBar().setMinimum(minProgress);
    getProgressBar().setMaximum(maxProgress);
    getProgressBar().setValue(task.getCurrentProgress());
    getProgressBar().setIndeterminate(minProgress == maxProgress);
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTaskListener#backgroundTaskFinished(org.fibs.geotag.tasks.BackgroundTask)
   */
  @Override
  public void backgroundTaskFinished(BackgroundTask<?> task) {
    // System.out.println("Finished "+task.getName());
    getProgressBar().setValue(task.getMinProgress());
    String result = ""; //$NON-NLS-1$
    try {
      result = task.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    getProgressBar().setString(result);
    getProgressBar().setIndeterminate(false);
    backgroundTask = null;
    // re-enable menu items
    fileMenu.updateMenuAvailability(true);
    updateUndoMenuItems();
    validatePreview();
    cancelItem.setVisible(false);

    tableModel.setEditingForbidden(false);
    // have any external updates arrived while we were busy?
    processExternalUpdates();
  }

  /**
   * @return the previewComponent
   */
  PreviewComponent getPreviewComponent() {
    return previewComponent;
  }

  /**
   * @return the tableModel
   */
  public ImagesTableModel getTableModel() {
    return tableModel;
  }

  /**
   * @return the table
   */
  ImagesTable getTable() {
    return table;
  }

  /**
   * @return the progressBar
   */
  JProgressBar getProgressBar() {
    if (progressBar == null) {
      progressBar = new JProgressBar();
      progressBar.setStringPainted(true);
      progressBar.setBorderPainted(false);
      progressBar.setString(""); //$NON-NLS-1$
    }
    return progressBar;
  }

  /**
   * @return the backgroundTask
   */
  BackgroundTask<?> getBackgroundTask() {
    return backgroundTask;
  }

  /**
   * @return the pendingExternalUpdates
   */
  Vector<ExternalUpdate> getPendingExternalUpdates() {
    return pendingExternalUpdates;
  }

  /**
   * @return the cancelItem
   */
  JMenuItem getCancelItem() {
    return cancelItem;
  }

  /**
   * A utility method for finding the main window.
   * 
   * @param component
   *          The component somewhere in the main window
   * @return the JFrame this is ultimately in.
   */
  public static JFrame getMainWindow(Component component) {
    return (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, component);
  }

}
