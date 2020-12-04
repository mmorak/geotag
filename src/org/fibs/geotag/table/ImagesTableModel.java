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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.fibs.geotag.Messages;
import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;
import org.fibs.geotag.table.ImagesTableColumns.COLUMN;
import org.fibs.geotag.tasks.EditAltitudeTask;
import org.fibs.geotag.tasks.EditDirectionTask;
import org.fibs.geotag.tasks.EditLatitudeTask;
import org.fibs.geotag.tasks.EditLongitudeTask;
import org.fibs.geotag.tasks.ManualEditTask;
import org.fibs.geotag.util.Coordinates;
import org.fibs.geotag.util.Unicode;
import org.fibs.geotag.util.Units;
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
      Messages.getString("ImagesTableModel.Direction"), //$NON-NLS-1$
      Messages.getString("ImagesTableModel.Location"), //$NON-NLS-1$
      Messages.getString("ImagesTableModel.Province"), //$NON-NLS-1$
      Messages.getString("ImagesTableModel.Country") }; //$NON-NLS-1$

  /** How many decimals of the latitude to display */
  public static final int LATITUDE_DECIMALS = 7;

  /** How many decimals of the longitude to display */
  public static final int LONGITUDE_DECIMALS = 7;

  /** How many decimals of the altitude to display */
  public static final int ALTITUDE_DECIMALS = 1;

  /** How many decimals of the direction to display */
  public static final int DIRECTION_DECIMALS = 1;

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
    switch (COLUMN.values()[columnIndex]) {
      case IMAGE_NAME:
      case CAMERA_DATE:
      case GPS_DATE:
      case TIME_OFFSET:
        return false;
      case LATITUDE:
      case LONGITUDE:
        return true;
      case ALTITUDE:
        return true;
      case DIRECTION:
        ImageInfo imageInfo = getImageInfo(rowIndex);
        return imageInfo.hasLocation();
      case LOCATION_NAME:
      case PROVINCE_NAME:
      case COUNTRY_NAME:
        return true;
    }
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
        return imageInfo.getCameraDate();
      case GPS_DATE:
        return imageInfo.getGPSDateTime();
      case TIME_OFFSET:
        return imageInfo.getOffsetString();
      case LATITUDE:
        String latitude = imageInfo.getGPSLatitude();
        if (latitude != null) {
          latitude = Coordinates.format(Double.parseDouble(latitude), false);
        }
        return latitude;
      case LONGITUDE:
        String longitude = imageInfo.getGPSLongitude();
        if (longitude != null) {
          longitude = Coordinates.format(Double.parseDouble(longitude), true);
        }
        return longitude;
      case ALTITUDE:
        Units.ALTITUDE unit = Units.ALTITUDE.values()[Settings.get(
            SETTING.ALTITUDE_UNIT, 0)];
        String altitude = imageInfo.getGPSAltitude();
        if (unit != Units.ALTITUDE.METRES) {
          try {
            if (altitude != null && altitude.length() > 0) {
              double altitudeMeters = Double.parseDouble(altitude);
              double displayAltitude = Units.convert(altitudeMeters,
                  Units.ALTITUDE.METRES, unit);
              altitude = Double.toString(displayAltitude);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        return round(altitude, ALTITUDE_DECIMALS);
      case DIRECTION:
        String direction = imageInfo.getGPSImgDirection();
        if (direction != null) {
          // round value and add degree symbol
          direction = round(imageInfo.getGPSImgDirection(), DIRECTION_DECIMALS)
              + Unicode.DEGREE_SYMBOL;
        }
        return direction;
      case LOCATION_NAME:
        return imageInfo.getLocationName();
      case PROVINCE_NAME:
        return imageInfo.getProvinceName();
      case COUNTRY_NAME:
        return imageInfo.getCountryName();
    }
    return null;
  }

  /**
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int,
   *      int)
   */
  @Override
  public void setValueAt(Object value, int rowIndex, int columnIndex) {
    super.setValueAt(value, rowIndex, columnIndex);
    ImageInfo imageInfo = getImageInfo(rowIndex);
    COLUMN column = COLUMN.values()[columnIndex];
    String columnName = getColumnName(columnIndex);
    String oldString;
    String newString;
    boolean update;
    switch (column) {
      case IMAGE_NAME:
      case CAMERA_DATE:
      case GPS_DATE:
      case TIME_OFFSET:
        // all those are not editable
        break;
      case LATITUDE:
        update = true;
        oldString = imageInfo.getGPSLatitude();
        newString = (String) value;
        if (oldString == null && newString.length() == 0) {
          update = false;
        } else {
          try {
            double oldLatitude = 999.9; // any impossible value
            if (oldString != null) {
              oldLatitude = Double.parseDouble(oldString);
            }
            double newLatitude = Coordinates.parse(newString, false);
            newString = Double.toString(newLatitude);
            if (Double.isNaN(newLatitude)
                || Math.abs(oldLatitude - newLatitude) < 1e-8) {
              update = false;
            }
          } catch (RuntimeException e) {
            e.printStackTrace();
          }
          if (update) {
            new EditLatitudeTask(
                Messages.getString("ImagesTableModel.EditLatitude"), imageInfo, newString, DATA_SOURCE.MANUAL) { //$NON-NLS-1$
              @Override
              protected void process(List<ImageInfo> imageInfos) {
                for (ImageInfo image : imageInfos) {
                  int row = getRow(image);
                  if (row >= 0) {
                    fireTableRowsUpdated(row, row);
                    fireTableDataChanged();
                  }
                }
              }
            }.execute();
          }
        }
        break;
      case LONGITUDE:
        update = true;
        oldString = imageInfo.getGPSLatitude();
        newString = (String) value;
        if (oldString == null && newString.length() == 0) {
          update = false;
        } else {
          try {
            double oldLongitude = 999.9; // any impossible value
            if (oldString != null) {
              oldLongitude = Double.parseDouble(oldString);
            }
            double newLongitude = Coordinates.parse(newString, true);
            newString = Double.toString(newLongitude);
            if (Double.isNaN(newLongitude)
                || Math.abs(oldLongitude - newLongitude) < 1e-8) {
              update = false;
            }
          } catch (RuntimeException e) {
            e.printStackTrace();
          }
          if (update) {
            new EditLongitudeTask(
                Messages.getString("ImagesTableModel.EditLongitude"), imageInfo, newString, DATA_SOURCE.MANUAL) { //$NON-NLS-1$
              @Override
              protected void process(List<ImageInfo> imageInfos) {
                for (ImageInfo image : imageInfos) {
                  int row = getRow(image);
                  if (row >= 0) {
                    fireTableRowsUpdated(row, row);
                    fireTableDataChanged();
                  }
                }
              }
            }.execute();
          }
        }
        break;
      case ALTITUDE:
        update = true;
        oldString = imageInfo.getGPSAltitude();
        newString = (String) value;
        if (oldString == null && newString.length() == 0) {
          update = false;
        } else {
          try {
            DecimalFormat decimalFormat = new DecimalFormat();
            Number newAltitude = decimalFormat.parse(newString);
            newString = Double.toString(newAltitude.doubleValue());
          } catch (RuntimeException e) {
            e.printStackTrace();
            update = false;
          } catch (ParseException e) {
            e.printStackTrace();
            update = false;
          }
        }
        if (update) {
          new EditAltitudeTask(
              "Edit altitude", imageInfo, newString, DATA_SOURCE.MANUAL) { //$NON-NLS-1$
            @Override
            protected void process(List<ImageInfo> imageInfos) {
              for (ImageInfo image : imageInfos) {
                int row = getRow(image);
                if (row >= 0) {
                  fireTableRowsUpdated(row, row);
                  fireTableDataChanged();
                }
              }
            }
          }.execute();
        }
        return;
      case DIRECTION:
        update = true;
        // there are two exceptions:
        oldString = imageInfo.getGPSImgDirection();
        newString = (String) value;
        if (oldString == null && newString.length() == 0) {
          update = false;
        } else {
          try {
            double oldDouble = 0.0;
            if (oldString != null) {
              oldDouble = Double.parseDouble(oldString);
            }
            // the newString might contain a degree symbol
            // but the DecimalFormat doesn't care about trailing nonsense
            DecimalFormat decimalFormat = new DecimalFormat();
            Number newNumber = decimalFormat.parse(newString);
            double newDouble = newNumber.doubleValue();
            newString = Double.toString(newDouble);
            // determine a value below which we consider the difference equal.
            // we use Math.pow(10, -(DIRECTION_DECIMALS + 1)
            // Math.pow() is evil, so I won't use it
            double insignificant = 1.0;
            for (int index = 0; index < DIRECTION_DECIMALS + 1; index++) {
              insignificant /= 10.0;
            }
            // now see if old and new value are too close to make a difference
            if (Math.abs(oldDouble - newDouble) < insignificant) {
              update = false;
            }
          } catch (NumberFormatException e) {
            e.printStackTrace();
          } catch (ParseException e) {
            e.printStackTrace();
          }
        }
        if (update) {
          new EditDirectionTask(
              Messages.getString("ImagesTableModel.EditDirection"), imageInfo, newString, DATA_SOURCE.MANUAL) { //$NON-NLS-1$
            @Override
            protected void process(List<ImageInfo> imageInfos) {
              for (ImageInfo image : imageInfos) {
                int row = getRow(image);
                if (row >= 0) {
                  fireTableRowsUpdated(row, row);
                  fireTableDataChanged();
                }
              }
            }
          }.execute();
        }
        break;
      case LOCATION_NAME:
        newString = (String) value;
        oldString = imageInfo.getLocationName();
        if (!Util.sameContent(oldString, newString)) {
          commitManualEdit(imageInfo, column, columnName, newString);
        }
        break;
      case PROVINCE_NAME:
        newString = (String) value;
        oldString = imageInfo.getProvinceName();
        if (!Util.sameContent(oldString, newString)) {
          commitManualEdit(imageInfo, column, columnName, newString);
        }
        break;
      case COUNTRY_NAME:
        newString = (String) value;
        oldString = imageInfo.getCountryName();
        if (!Util.sameContent(oldString, newString)) {
          commitManualEdit(imageInfo, column, columnName, newString);
        }
        break;
    }
  }

  /**
   * @param imageInfo
   * @param column
   * @param columnName
   * @param newString
   */
  private void commitManualEdit(ImageInfo imageInfo, COLUMN column,
      String columnName, String newString) {
    new ManualEditTask(columnName, imageInfo, column, newString) {
      @Override
      protected void process(List<ImageInfo> imageInfos) {
        for (ImageInfo image : imageInfos) {
          int row = getRow(image);
          if (row >= 0) {
            fireTableRowsUpdated(row, row);
            fireTableDataChanged();
          }
        }
      }
    }.execute();
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
    String format = String.format("%%.%df", new Integer(decimals)); //$NON-NLS-1$
    if (value != null && value.length() > 0) {
      try {
        Double theValue = new Double(value);
        result = String.format(format, theValue);
      } catch (NumberFormatException e) {
        // e.printStackTrace();
      }
    }
    return result;
  }

}
