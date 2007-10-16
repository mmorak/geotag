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

package org.fibs.geotag.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.ImageInfo.THUMBNAIL_STATUS;
import org.fibs.geotag.image.ImageToolTip;
import org.fibs.geotag.image.ThumbnailWorker;
import org.fibs.geotag.table.ImagesTableColumns.COLUMN;
import org.fibs.geotag.util.FontUtil;
import org.fibs.geotag.util.Util;

/**
 * A custom JTable. Main difference is that it can display image tooltips.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class ImagesTable extends JTable {

  /** A string of zeros - used to calculate preferred widths */
  private static final String zeros = "0000000000"; //$NON-NLS-1$

  /** The row the mouse is pointing at */
  int mouseOnRow;

  /** The column the mouse is pointing at */
  int mouseOnColumn;

  /**
   * We keep the last mouse motion event. We need it so we can dispatch it again
   * to redraw the tool tip. This idea was found at:
   * http://forum.java.sun.com/thread.jspa?threadID=320183&messageID=1294878
   */
  MouseEvent lastMouseMovedEvent;

  /** The background colour used for every second row */
  Color alternativeBackground;

  /**
   * Create a new ImagesTable
   * 
   * @param tableModel
   *          the {@link ImagesTableModel} containing the data to be displayed
   */
  public ImagesTable(ImagesTableModel tableModel) {
    super(tableModel);
    alternativeBackground = calculateAlternativeBackgroundColour();
    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    determineColumnLayout();
    usePreferredFont();
    // we need to track mouse motions
    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent event) {
        if (event.getID() == MouseEvent.MOUSE_MOVED) {
          mouseOnRow = rowAtPoint(event.getPoint());
          mouseOnColumn = columnAtPoint(event.getPoint());
          lastMouseMovedEvent = event;
        }
      }
    });
    // Edit cell on single click (default is double click)
    ((DefaultCellEditor) getDefaultEditor(String.class))
        .setClickCountToStart(1);
    setupActions();
  }

  /**
   * 
   */
  private void setupActions() {
    getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "downArrow"); //$NON-NLS-1$
    AbstractAction downArrow = new AbstractAction() {
      public void actionPerformed(ActionEvent evt) {
        // System.out.println("ACTION");
      }
    };
    getActionMap().put("downArrow", downArrow); //$NON-NLS-1$
  }

  /**
   * Calculate the alternative background colour (the colour used as a
   * background for every other row by changing the background colour a little
   * bit towards the foreground colour.
   * 
   * @return The background colour used for every other row
   */
  private Color calculateAlternativeBackgroundColour() {
    double ratio = 0.10; // 10 % seems just about right
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
   * width
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
   * position
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
   * previously save to the Settings
   */
  private void determineColumnLayout() {
    FontMetrics fontMetrics = getFontMetrics(getFont());
    for (int index = 0; index < ImagesTableColumns.COLUMN.values().length; index++) {
      COLUMN column = ImagesTableColumns.COLUMN.values()[index];
      int preferredWidth = 42;
      switch (column) {
        case ALTITUDE:
          preferredWidth = defaultAltitudeWidth(fontMetrics);
          break;
        case DIRECTION:
          preferredWidth = defaultDirectionWith(fontMetrics);
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
      }
      // add a few pixels gap
      preferredWidth += 8;
      // check the settings, they override the above defaults
      SETTING setting = getColumnWidthSetting(column);
      if (setting != null) {
        preferredWidth = Settings.get(setting, preferredWidth);
      }
      String columnName = getModel().getColumnName(index);
      int columnIndex = getColumnModel().getColumnIndex(columnName);
      getColumnModel().getColumn(columnIndex).setPreferredWidth(preferredWidth);
      // now move it to the requested position
      setting = getColumnPositionSetting(column);
      int newColumnIndex = Settings.get(setting, columnIndex);
      getColumnModel().moveColumn(columnIndex, newColumnIndex);
    }
  }

  /**
   * Save the current column widths and positions to the settings
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
        + zeros.substring(0, ImagesTableModel.LONGITUDE_DECIMALS));
  }

  /**
   * @param fontMetrics
   * @return the default latitude string width
   */
  private int defaultLatitudeWidth(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("-90." //$NON-NLS-1$
        + zeros.substring(0, ImagesTableModel.LATITUDE_DECIMALS));
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
        + zeros.substring(0, ImagesTableModel.ALTITUDE_DECIMALS));
  }

  /**
   * @param fontMetrics
   * @return the default direction string width
   */
  private int defaultDirectionWith(FontMetrics fontMetrics) {
    return fontMetrics.stringWidth("359." //$NON-NLS-1$
        + zeros.substring(0, ImagesTableModel.DIRECTION_DECIMALS));
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
   * Make the table use the Font specified in the settings
   */
  public void usePreferredFont() {
    Font font = FontUtil.fontFromID(Settings.get(SETTING.FONT, null));
    if (font != null) {
      setFont(font);
      setRowHeight(font.getSize() + 4);
      invalidate();
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
    // only try loading thumnail once
    if (imageIcon == null
        && imageInfo.getThumbNailStatus() == THUMBNAIL_STATUS.UNKNOWN) {
      ThumbnailWorker worker = new ThumbnailWorker(imageInfo) {
        @Override
        protected void process(List<ImageInfo> chunks) {
          if (tooltip.isShowing()) {
            tooltip.setImageIcon(chunks.get(0).getThumbnail());
            // see
            // http://forum.java.sun.com/thread.jspa?threadID=320183&messageID=1294878
            // for details about this nasty hack:
            dispatchEvent(lastMouseMovedEvent);
          }
        }
      };
      worker.execute();
    }
    return tooltip;
  }

  /**
   * We override this, because we want a custom TableHeader displaying tooltips
   * for each column
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
   * @see javax.swing.JComponent#processMouseMotionEvent(java.awt.event.MouseEvent)
   */
  @Override
  protected void processMouseMotionEvent(MouseEvent event) {
    super.processMouseMotionEvent(event);

  }

}
