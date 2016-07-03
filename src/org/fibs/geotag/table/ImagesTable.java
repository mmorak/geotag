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

package org.fibs.geotag.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.Settings.SettingsListener;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.ImageInfo.THUMBNAIL_STATUS;
import org.fibs.geotag.image.ImageToolTip;
import org.fibs.geotag.image.ThumbnailWorker;
import org.fibs.geotag.table.ImagesTableColumns.COLUMN;
import org.fibs.geotag.tasks.TaskExecutor;
import org.fibs.geotag.util.FontUtil;
import org.fibs.geotag.util.Util;

/**
 * A custom JTable. Main difference is that it can display image tooltips.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class ImagesTable extends NavigableTable {

  /** Gap between columns in pixels. */
  private static final int GAP_IN_PIXELS = 8;

  /** How much to change normal colour to get alternative colour. */
  private static final double ALTERNATIVE_BACKGROUND_RATIO = 0.10;

  /** A string of zeros - used to calculate preferred widths. */
  private static final String ZEROS = "0000000000"; //$NON-NLS-1$

  /** The row the mouse is pointing at. */
  private int mouseOnRow;

  /**
   * We keep the last mouse motion event. We need it so we can dispatch it again
   * to redraw the tool tip. This idea was found at:
   * http://forum.java.sun.com/thread.jspa?threadID=320183&messageID=1294878
   */
  private MouseEvent lastMouseMovedEvent;

  /** The background colour used for every second row. */
  private Color alternativeBackground;

  /** The editor text field. */
  private TextCellEditor textCellEditor;

  /**
   * Create a new ImagesTable.
   * 
   * @param tableModel
   *          the {@link ImagesTableModel} containing the data to be displayed
   */
  public ImagesTable(ImagesTableModel tableModel) {
    super(tableModel);
    alternativeBackground = calculateAlternativeBackgroundColour();
    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    determineColumnLayout();
    // we need to track mouse motions
    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent event) {
        if (event.getID() == MouseEvent.MOUSE_MOVED) {
          setMouseOnRow(rowAtPoint(event.getPoint()));
          setLastMouseMovedEvent(event);
        }
      }
    });

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
          case KeyEvent.VK_DOWN:
          case KeyEvent.VK_KP_DOWN:
            navigateDown(event);
            break;
          case KeyEvent.VK_UP:
          case KeyEvent.VK_KP_UP:
            navigateUp(event);
            break;
          default:
            break;
        }
        super.keyPressed(event);
      }

    });

    textCellEditor = new TextCellEditor(this);
    textCellEditor.setFont(getFont());
    textCellEditor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    setDefaultEditor(String.class, new DefaultCellEditor(textCellEditor));
    // How many clicks needed to edit a cell
    // we also want to know if that setting chages
    // So we create a settings listener
    SettingsListener settingsListener = new SettingsListener() {
      @Override
      public void settingChanged(SETTING setting) {
        // no need really to check
        int clicksToEdit = Settings.get(SETTING.CLICKS_TO_EDIT,
            Settings.DEFAULT_CLICKS_TO_EDIT);
        ((DefaultCellEditor) getDefaultEditor(String.class))
            .setClickCountToStart(clicksToEdit);
      }
    };
    // kick it once
    settingsListener.settingChanged(SETTING.CLICKS_TO_EDIT);
    // and install it
    Settings.addListener(SETTING.CLICKS_TO_EDIT, settingsListener);
    // Now a workaround for
    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6503981
    fixBug6503981();
    // use preferred font after editor component is set up
    usePreferredFont();
    setupActions();
  }

  /**
   * A fix for Java bug 6503981. The bug and the workaround are described at
   * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6503981 This bug should
   * be fixed soon and this method might need to check the Java version to apply
   * the fix selectively.
   */
  private void fixBug6503981() {
    final JTableHeader header = getTableHeader();
    header.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent event) {
        int column = header.getColumnModel().getColumnIndexAtX(
            event.getPoint().x);
        if (column >= 0) {
          ImagesTable.this.setColumnSelectionInterval(column, column);
          final Action focusAction = ImagesTable.this.getActionMap().get(
              "focusHeader"); //$NON-NLS-1$
          focusAction.actionPerformed(new ActionEvent(ImagesTable.this, 0,
              "focusHeader")); //$NON-NLS-1$
        }
      }
    });
  }

  /**
   * 
   */
  private void setupActions() {
    getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "downArrow"); //$NON-NLS-1$
    AbstractAction downArrow = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        // System.out.println("DOWN");
      }
    };
    getActionMap().put("downArrow", downArrow); //$NON-NLS-1$
    getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "upArrow"); //$NON-NLS-1$
    AbstractAction upArrow = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        // System.out.println("UP");
      }
    };
    getActionMap().put("upArrow", upArrow); //$NON-NLS-1$
    getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "leftArrow"); //$NON-NLS-1$
    AbstractAction leftArrow = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        // System.out.println("LEFT");
      }
    };
    getActionMap().put("leftArrow", leftArrow); //$NON-NLS-1$
    getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "rightArrow"); //$NON-NLS-1$
    AbstractAction rightArrow = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        // System.out.println("RIGHT");
      }
    };
    getActionMap().put("rightArrow", rightArrow); //$NON-NLS-1$
    // ActionMap actionMap = getActionMap();
    // Object[] allKeys = actionMap.allKeys();
    // for (Object object : allKeys) {
    // //System.out.println("Action "+object);
    // }
  }

  /**
   * Calculate the alternative background colour (the colour used as a
   * background for every other row by changing the background colour a little
   * bit towards the foreground colour.
   * 
   * @return The background colour used for every other row
   */
  private Color calculateAlternativeBackgroundColour() {
    double ratio = ALTERNATIVE_BACKGROUND_RATIO; // 10 % seems just about right
    int red = (int) Util.applyRatio(getBackground().getRed(), getForeground()
        .getRed(), ratio);
    int green = (int) Util.applyRatio(getBackground().getGreen(),
        getForeground().getGreen(), ratio);
    int blue = (int) Util.applyRatio(getBackground().getBlue(), getForeground()
        .getBlue(), ratio);
    return new Color(red, green, blue);
  }

  /**
   * For a given column return the {@link SETTING} that holds its preferred
   * width.
   * 
   * @param column
   *          The column
   * @return The {@link SETTING}
   */
  private SETTING getColumnWidthSetting(ImagesTableColumns.COLUMN column) {
    // I can't make up my mind if this is a nasty hack or elegant
    // It relies on the column width setting's name being
    // the column's name with _WIDTH appended.
    SETTING setting = null;
    try {
      setting = SETTING.valueOf(column.name() + "_WIDTH"); //$NON-NLS-1$
    } catch (RuntimeException e) {
      // make sure we know about a column not having the correct setting
      e.printStackTrace();
    }
    return setting;
  }

  /**
   * For a given column return the {@link SETTING} that holds its preferred
   * position.
   * 
   * @param column
   *          The column
   * @return The {@link SETTING}
   */
  private SETTING getColumnPositionSetting(ImagesTableColumns.COLUMN column) {
    // use the same trick for the column position setting
    SETTING setting = null;
    try {
      setting = SETTING.valueOf(column.name() + "_POSITION"); //$NON-NLS-1$
    } catch (RuntimeException e) {
      // make sure we know about a column not having the correct setting
      e.printStackTrace();
    }
    return setting;
  }

  /**
   * This method lays out the columns to either a sensible default layout or one
   * previously save to the Settings.
   */
  private void determineColumnLayout() {
    FontMetrics fontMetrics = getFontMetrics(getFont());
    // we put the column names in the correct order first, while setting the
    // correct column width for them
    String[] correctOrder = new String[ImagesTableColumns.COLUMN.values().length];
    for (int index = 0; index < ImagesTableColumns.COLUMN.values().length; index++) {
      COLUMN column = ImagesTableColumns.COLUMN.values()[index];
      int preferredWidth = 0;
      switch (column) {
        case ALTITUDE:
          preferredWidth = defaultAltitudeWidth(fontMetrics);
          break;
        case DIRECTION:
          preferredWidth = defaultDirectionWidth(fontMetrics);
          break;
        case GPS_DATE:
          preferredWidth = defaultDateWidth(fontMetrics);
          break;
        case CAMERA_DATE:
          preferredWidth = defaultDateWidth(fontMetrics);
          break;
        case LATITUDE:
          preferredWidth = defaultLatitudeWidth(fontMetrics);
          break;
        case LONGITUDE:
          preferredWidth = defaultLongitudeWidth(fontMetrics);
          break;
        case TIME_OFFSET:
          preferredWidth = defaultOffsetWidth(fontMetrics);
          break;
        case IMAGE_NAME:
          preferredWidth = defaultImageNameWidth(fontMetrics);
          break;
        case LOCATION_NAME:
          preferredWidth = defaultLocationNameWidth(fontMetrics);
          break;
        case CITY_NAME:
          preferredWidth = defaultCityNameWidth(fontMetrics);
          break;
        case PROVINCE_NAME:
          preferredWidth = defaultProvinceNameWidth(fontMetrics);
          break;
        case COUNTRY_NAME:
          preferredWidth = defaultCountryNameWidth(fontMetrics);
          break;
        case USER_COMMENT:
          preferredWidth = defaultUserCommentWidth(fontMetrics);
          break;
        default:
          break;
      }
      // add a few pixels gap
      preferredWidth += GAP_IN_PIXELS;
      // check the settings, they override the above defaults
      SETTING setting = getColumnWidthSetting(column);
      if (setting != null) {
        preferredWidth = Settings.get(setting, preferredWidth);
      }
      String columnName = getModel().getColumnName(index);
      int columnIndex = getColumnModel().getColumnIndex(columnName);
      getColumnModel().getColumn(columnIndex).setPreferredWidth(preferredWidth);
      // now find out where the column should go
      setting = getColumnPositionSetting(column);
      int newColumnIndex = Settings.get(setting, columnIndex);
      // now store the column name in the at the correct index
      if (correctOrder[newColumnIndex] == null) {
        // this is the normal case
        correctOrder[newColumnIndex] = columnName;
      } else {
        // something went wrong - two columns have the same index
        // find a free index.. any free index
        for (int check = 0; check < ImagesTableColumns.COLUMN.values().length; check++) {
          if (correctOrder[check] == null) {
            correctOrder[check] = columnName;
          }
        }
      }
    }
    // now we move columns around. By moving the column that goes at
    // position 0 first and the one that goes last last, we avoid
    // problems with columns being moved away from their correct position
    for (int index = 0; index < correctOrder.length; index++) {
      String columnName = correctOrder[index];
      int currentIndex = getColumnModel().getColumnIndex(columnName);
      // now move it to the requested position
      getColumnModel().moveColumn(currentIndex, index);
    }
  }

  /**
   * Save the current column widths and positions to the settings.
   */
  public void saveColumnSettings() {
    for (int index = 0; index < COLUMN.values().length; index++) {

      String columnName = getModel().getColumnName(index);
      int columnIndex = getColumnModel().getColumnIndex(columnName);
      COLUMN column = COLUMN.values()[index];
      SETTING setting = getColumnWidthSetting(column);
      if (setting != null) {
        int width = getColumnModel().getColumn(columnIndex).getPreferredWidth();
        Settings.put(setting, width);
      }
      setting = getColumnPositionSetting(column);
      if (setting != null) {
        Settings.put(setting, columnIndex);
      }
    }
  }

  /**
   * @param fontMetrics
   * @return the default image name width
   */
  private int defaultImageNameWidth(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("20000000_000000.jpg"); //$NON-NLS-1$
  }

  /**
   * @param fontMetrics
   * @return the default offset string width
   */
  private int defaultOffsetWidth(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("-99:99:99"); //$NON-NLS-1$
  }

  /**
   * @param fontMetrics
   * @return the default longitude string width
   */
  private int defaultLongitudeWidth(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("-180." //$NON-NLS-1$
        + ZEROS.substring(0, ImagesTableModel.LONGITUDE_DECIMALS));
  }

  /**
   * @param fontMetrics
   * @return the default latitude string width
   */
  private int defaultLatitudeWidth(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("-90." //$NON-NLS-1$
        + ZEROS.substring(0, ImagesTableModel.LATITUDE_DECIMALS));
  }

  /**
   * @param fontMetrics
   * @return the default date string width
   */
  private int defaultDateWidth(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("2000:00:00 00:00:00"); //$NON-NLS-1$
  }

  /**
   * @param fontMetrics
   * @return the default altitude string width
   */
  private int defaultAltitudeWidth(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("19999." //$NON-NLS-1$
        + ZEROS.substring(0, ImagesTableModel.ALTITUDE_DECIMALS));
  }

  /**
   * @param fontMetrics
   * @return the default direction string width
   */
  private int defaultDirectionWidth(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("359." //$NON-NLS-1$
        + ZEROS.substring(0, ImagesTableModel.DIRECTION_DECIMALS));
  }

  /**
   * @param fontMetrics
   * @return the default location name string width
   */
  private int defaultLocationNameWidth(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("Greenwich"); //$NON-NLS-1$
  }

  /**
   * @param fontMetrics
   * @return the default city name string width
   */
  private int defaultCityNameWidth(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("Greater London"); //$NON-NLS-1$
  }

  /**
   * @param fontMetrics
   * @return the default province/state string width
   */
  private int defaultProvinceNameWidth(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("England"); //$NON-NLS-1$
  }

  /**
   * @param fontMetrics
   * @return the default country name string width
   */
  private int defaultCountryNameWidth(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("United Kingdom"); //$NON-NLS-1$
  }
  
  /**
   * @param fontMetrics
   * @return the default user comment string width
   */
  private int defaultUserCommentWidth(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("Some sort of description"); //$NON-NLS-1$
  }  

  /**
   * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer,
   *      int, int)
   */
  @Override
  public Component prepareRenderer(TableCellRenderer renderer, int row,
      int column) {
    Component component = super.prepareRenderer(renderer, row, column);
    // the default font is the font of the table
    Font font = ImagesTable.this.getFont();
    ImageInfo imageInfo = ((ImagesTableModel) getModel()).getImageInfo(row);
    if (component instanceof DefaultTableCellRenderer) {
      String text = ((ImagesTableModel) getModel()).getImageInfo(row).getPath();
      // we do the next bit, so the tooltip text changes when a thumbnail
      // image becomes available. The tooltip won't update to a tooltip
      // with image otherwise
      if (imageInfo.getThumbnail() != null) {
        text += ' ';
      }
      ((DefaultTableCellRenderer) component).setToolTipText(text);
    }
    // If the location is new use a bold font to display the row
    if (imageInfo.hasNewLocation()) {
      font = font.deriveFont(Font.BOLD);
    }
    // set the renderer font to either the table font or the table font in bold
    component.setFont(font);
    // alternate row background colour
    if (!isCellSelected(row, column)) {
      Color background = ImagesTable.this.getBackground();
      if (row % 2 == 0) {
        background = alternativeBackground;
      }
      component.setBackground(background);
    }
    return component;
  }

  /**
   * Make the table use the Font specified in the settings.
   */
  public void usePreferredFont() {
    String fontId = Settings.get(SETTING.FONT, null);
    if (fontId != null) {
      Font font = FontUtil.fontFromID(Settings.get(SETTING.FONT, null));
      if (font != null) {
        setFont(font);
        textCellEditor.setFont(font);
        int borderInsets = 0;
        Border border = textCellEditor.getBorder();
        if (border != null) {
          Insets insets = border.getBorderInsets(textCellEditor);
          borderInsets += insets.bottom + insets.top;
        }
        textCellEditor.getBorder();
        FontMetrics fontMetrics = getFontMetrics(font);
        setRowHeight(fontMetrics.getHeight() + borderInsets);
        invalidate();
      }
    }
  }

  /**
   * @see javax.swing.JComponent#createToolTip()
   */
  @Override
  public JToolTip createToolTip() {
    ImageInfo imageInfo = ((ImagesTableModel) getModel())
        .getImageInfo(mouseOnRow);
    // It's OK if imageIcon is null at this stage, the tooltip constructor
    // doesn't mind
    ImageIcon imageIcon = imageInfo.getThumbnail();
    final ImageToolTip tooltip = new ImageToolTip(imageIcon, imageInfo
        .getPath());
    // only try loading thumbnail once
    boolean showThumbnails = Settings.get(SETTING.TUMBNAILS_IN_TOOLTIPS, true);
    if (imageIcon == null && showThumbnails
        && imageInfo.getThumbNailStatus() == THUMBNAIL_STATUS.UNKNOWN) {
      ThumbnailWorker worker = new ThumbnailWorker(imageInfo) {
        @Override
        protected void process(List<ImageInfo> chunks) {
          if (tooltip.isShowing()) {
            tooltip.setImageIcon(chunks.get(0).getThumbnail());
            // see
            // http://forum.java.sun.com/thread.jspa?threadID=320183&messageID=
            // 1294878
            // for details about this nasty hack:
            dispatchEvent(getLastMouseMovedEvent());
          }
        }
      };
      TaskExecutor.execute(worker);
    }
    return tooltip;
  }

  /**
   * Select all images with a location.
   */
  public void selectAllWithLocation() {
    clearSelection();
    for (int row = 0; row < getRowCount(); row++) {
      ImageInfo imageInfo = ((ImagesTableModel) getModel()).getImageInfo(row);
      if (imageInfo.hasLocation()) {
        addRowSelectionInterval(row, row);
      }
    }
  }

  /**
   * Select all images with a new (updated) location.
   */
  public void selectAllWithNewLocation() {
    clearSelection();
    for (int row = 0; row < getRowCount(); row++) {
      ImageInfo imageInfo = ((ImagesTableModel) getModel()).getImageInfo(row);
      if (imageInfo.hasNewLocation()) {
        addRowSelectionInterval(row, row);
      }
    }
  }

  /**
   * Unselect all images.
   */
  public void selectNone() {
    clearSelection();
  }

  /**
   * We override this, because we want a custom TableHeader displaying tooltips
   * for each column.
   * 
   * @see javax.swing.JTable#createDefaultTableHeader()
   */
  @Override
  protected JTableHeader createDefaultTableHeader() {
    JTableHeader defaultTableHeader = new JTableHeader(columnModel) {
      @Override
      public String getToolTipText(MouseEvent e) {
        int columnIndex = columnModel.getColumnIndexAtX(e.getPoint().x);
        int modelIndex = columnModel.getColumn(columnIndex).getModelIndex();
        COLUMN column = COLUMN.values()[modelIndex];
        return ImagesTableColumns.getDescription(column);
      }
    };
    return defaultTableHeader;
  }

  /**
   * @see javax.swing.JComponent#processKeyEvent(java.awt.event.KeyEvent)
   */
  @Override
  protected void processKeyEvent(KeyEvent e) {
    // System.out.println("Table Key " + e.getKeyCode());
    super.processKeyEvent(e);
  }

  /**
   * @return the lastMouseMovedEvent
   */
  MouseEvent getLastMouseMovedEvent() {
    return lastMouseMovedEvent;
  }

  /**
   * @param lastMouseMovedEvent
   *          the lastMouseMovedEvent to set
   */
  void setLastMouseMovedEvent(MouseEvent lastMouseMovedEvent) {
    this.lastMouseMovedEvent = lastMouseMovedEvent;
  }

  /**
   * @return the mouseOnRow
   */
  int getMouseOnRow() {
    return mouseOnRow;
  }

  /**
   * @param mouseOnRow
   *          the mouseOnRow to set
   */
  void setMouseOnRow(int mouseOnRow) {
    this.mouseOnRow = mouseOnRow;
  }

}
