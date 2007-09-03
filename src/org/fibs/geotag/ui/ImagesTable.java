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

package org.fibs.geotag.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.ImageInfo.THUMBNAIL_STATUS;
import org.fibs.geotag.image.ThumbnailWorker;

/**
 * A custom JTable. Main difference is that it can display image tooltips.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class ImagesTable extends JTable {

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

  /**
   * Create a new ImagesTable
   * 
   * @param tableModel
   *          the {@link ImagesTableModel} containing the data to be displayed
   */
  public ImagesTable(ImagesTableModel tableModel) {
    super(tableModel);
    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    FontMetrics fontMetrics = getFontMetrics(getFont());
    for (int index = 0; index < ImagesTableColumns.values().length; index++) {
      ImagesTableColumns column = ImagesTableColumns.values()[index];
      int preferredWidth = 42;
      switch (column) {
        case ALTITUDE_COLUMN:
          preferredWidth = fontMetrics.stringWidth("19999"); //$NON-NLS-1$
          break;
        case GPS_DATE_COLUMN:
        case CREATE_DATE_COLUMN:
          preferredWidth = fontMetrics.stringWidth("2000:00:00 00:00:00"); //$NON-NLS-1$
          break;
        case LATITUDE_COLUMN:
          preferredWidth = fontMetrics.stringWidth("-90.0000000"); //$NON-NLS-1$
          break;
        case LONGITUDE_COLUMN:
          preferredWidth = fontMetrics.stringWidth("-180.0000000"); //$NON-NLS-1$
          break;
        case TIME_OFFSET_COLUMN:
          preferredWidth = fontMetrics.stringWidth("-99:99:99"); //$NON-NLS-1$
          break;
        case IMAGE_NAME_COLUMN:
          preferredWidth = fontMetrics.stringWidth("20000000_000000.jpg"); //$NON-NLS-1$
          break;
      }
      getColumnModel().getColumn(index).setPreferredWidth(preferredWidth + 8);
    }
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
    return component;
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
   * @see javax.swing.JComponent#processMouseMotionEvent(java.awt.event.MouseEvent)
   */
  @Override
  protected void processMouseMotionEvent(MouseEvent event) {
    super.processMouseMotionEvent(event);

  }

}
