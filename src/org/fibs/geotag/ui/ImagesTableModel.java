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

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.table.DefaultTableModel;

import org.fibs.geotag.Messages;
import org.fibs.geotag.data.ImageInfo;

/**
 * A representation of the data displayed by the main Window
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class ImagesTableModel extends DefaultTableModel {

  /**
   * An enumeration of columns
   * 
   * @author Andreas Schneider
   * 
   */

  /** The number of columns */
  public static final int NUM_COLUMNS = ImagesTableColumns.values().length;

  /** The data to be displayed */
  private ArrayList<ImageInfo> values = new ArrayList<ImageInfo>();

  /** The columns we can display */
  private static final String[] columnNames = {
      Messages.getString("ImagesTableModel.Name"), //$NON-NLS-1$
      Messages.getString("ImagesTableModel.GPSTime"), //$NON-NLS-1$
      Messages.getString("ImagesTableModel.Offset"), //$NON-NLS-1$
      Messages.getString("ImagesTableModel.CameraTime"), //$NON-NLS-1$
      Messages.getString("ImagesTableModel.Latitude"), //$NON-NLS-1$
      Messages.getString("ImagesTableModel.Longitude"), //$NON-NLS-1$
      Messages.getString("ImagesTableModel.Altitude") }; //$NON-NLS-1$

  /**
   * Can be called to re-sort the table (mainly when time-offsets change)
   */
  public void sortRows() {
    // keep the data sorted
    Collections.sort(values);
    // notify the table that the data has changed
    fireTableDataChanged();
  }

  /**
   * Add image info to the data
   * 
   * @param imageInfo
   *          The {@link ImageInfo} to be added
   */
  public void addImageInfo(ImageInfo imageInfo) {
    // add the info
    values.add(imageInfo);
    // keep the data sorted
    sortRows();
  }

  /**
   * Get the {@link ImageInfo} for a table row
   * 
   * @param row
   *          The row we are interested in
   * @return The {@link ImageInfo}
   */
  public ImageInfo getImageInfo(int row) {
    return values.get(row);
  }

  /**
   * @see javax.swing.table.DefaultTableModel#removeRow(int)
   */
  @Override
  public void removeRow(int row) {
    values.remove(row);
  }

  /**
   * Find the row for a given {@link ImageInfo}
   * 
   * @param imageInfo
   *          The {@link ImageInfo} to look for
   * @return The row of the {@link ImageInfo} or -1 if its not found
   */
  public int getRow(ImageInfo imageInfo) {
    for (int row = 0; row < values.size(); row++) {
      if (values.get(row).getSequenceNumber() == imageInfo.getSequenceNumber()) {
        return row;
      }
    }
    return -1;
  }

  /**
   * Should we need any columns not displaying plain text, we need to declare
   * them here
   * 
   * @see javax.swing.table.TableModel#getColumnClass(int)
   */
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
      default:
        return String.class;
    }
  }

  /**
   * @see javax.swing.table.DefaultTableModel#getColumnName(int)
   */
  @Override
  public String getColumnName(int columnIndex) {
    return columnNames[columnIndex];
  }

  /**
   * Declare which columns are editable
   * 
   * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
   */
  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }

  /**
   * @see javax.swing.table.DefaultTableModel#getColumnCount()
   */
  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  /**
   * @see javax.swing.table.DefaultTableModel#getRowCount()
   */
  @Override
  public int getRowCount() {
    // getRowcount() gets called before the values get created
    if (values == null) {
      return 0;
    }
    return values.size();
  }

  /**
   * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
   */
  @Override
  public Object getValueAt(int row, int column) {
    ImageInfo imageInfo = values.get(row);
    switch (ImagesTableColumns.values()[column]) {
      case IMAGE_NAME_COLUMN:
        return imageInfo.getName();
      case CREATE_DATE_COLUMN:
        return values.get(row).getCreateDate();
      case GPS_DATE_COLUMN:
        return values.get(row).getGPSDateTime();
      case TIME_OFFSET_COLUMN:
        return values.get(row).getOffsetString();
      case LATITUDE_COLUMN:
        return values.get(row).getGPSLatitude();
      case LONGITUDE_COLUMN:
        return values.get(row).getGPSLongitude();
      case ALTITUDE_COLUMN:
        return values.get(row).getGPSAltitude();
    }
    return null;
  }

}
