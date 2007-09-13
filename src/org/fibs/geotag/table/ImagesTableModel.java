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

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.table.AbstractTableModel;

import org.fibs.geotag.Messages;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.table.ImagesTableColumns.COLUMN;
import org.fibs.geotag.util.Util;

/**
 * A representation of the data displayed by the main Window
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class ImagesTableModel extends AbstractTableModel {

  /**
   * An enumeration of columns
   * 
   * @author Andreas Schneider
   * 
   */

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
      Messages.getString("ImagesTableModel.Altitude"), //$NON-NLS-1$
      Messages.getString("ImagesTableModel.Direction") }; //$NON-NLS-1$

  /** How many decimals of the latitude to display */
  public static final int LATITUDE_DECIMALS = 7;

  /** How many decimals of the longitude to display */
  public static final int LONGITUDE_DECIMALS = 7;

  /** How many decimals of the altitude to display */
  public static final int ALTITUDE_DECIMALS = 1;

  /** How many decimals of the direction to display */
  public static final int DIRECTION_DECIMALS = 2;

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
   * Remove a row from the table model
   * 
   * @param row
   *          the index of the row to be removed
   */
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
    switch (COLUMN.values()[column]) {
      case IMAGE_NAME:
        return imageInfo.getName();
      case CAMERA_DATE:
        return values.get(row).getCameraDate();
      case GPS_DATE:
        return values.get(row).getGPSDateTime();
      case TIME_OFFSET:
        return values.get(row).getOffsetString();
      case LATITUDE:
        return round(values.get(row).getGPSLatitude(), LATITUDE_DECIMALS);
      case LONGITUDE:
        return round(values.get(row).getGPSLongitude(), LONGITUDE_DECIMALS);
      case ALTITUDE:
        return round(values.get(row).getGPSAltitude(), ALTITUDE_DECIMALS);
      case DIRECTION:
        return round(values.get(row).getGPSImgDirection(), DIRECTION_DECIMALS);
    }
    return null;
  }

  /**
   * We don't need to display latitude, longitude and altitude to all available
   * decimals. this method cuts off the String after the specified number of
   * decimals after the decimal point
   * 
   * @param value
   * @param decimals
   * @return The rounded value
   */
  private String round(String value, int decimals) {
    String result = value;
    if (value != null && value.length() > 0) {
      try {
        double theValue = Double.parseDouble(value);
        double factor = Util.powerOf10(decimals);
        double rounded = Math.round(factor * theValue) / factor;
        result = Double.toString(rounded);
      } catch (NumberFormatException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

}
