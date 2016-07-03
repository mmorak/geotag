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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public abstract class NavigableTable extends JTable {

  /** keep track of the cell being edited. */
  private int editingCellAtRow = -1;

  /** keep track of the cell being edited. */
  private int editingCellAtColumn = -1;

  /**
   * Same constructors as JTable.
   */
  public NavigableTable() {
    super();
  }

  /**
   * @param numRows
   * @param numColumns
   */
  public NavigableTable(int numRows, int numColumns) {
    super(numRows, numColumns);
  }

  /**
   * @param rowData
   * @param columnNames
   */
  public NavigableTable(Object[][] rowData, Object[] columnNames) {
    super(rowData, columnNames);
  }

  /**
   * @param dm
   * @param cm
   * @param sm
   */
  public NavigableTable(TableModel dm, TableColumnModel cm,
      ListSelectionModel sm) {
    super(dm, cm, sm);
  }

  /**
   * @param dm
   * @param cm
   */
  public NavigableTable(TableModel dm, TableColumnModel cm) {
    super(dm, cm);
  }

  /**
   * @param dm
   */
  public NavigableTable(TableModel dm) {
    super(dm);
  }

  /**
   * @param rowData
   * @param columnNames
   */
  public NavigableTable(Vector<?> rowData, Vector<?> columnNames) {
    super(rowData, columnNames);
  }

  /**
   * Scroll the table so a given cell is visible.
   * 
   * @param row
   * @param column
   */
  void scrollCellToVisible(int row, int column) {
    if (!(getParent() instanceof JViewport)) {
      return;
    }
    JViewport viewport = (JViewport) getParent();
    // The cell rectangle relative to the table
    Rectangle cellRectangle = getCellRect(row, column, true);
    // The location of the viewport top/left relative to the table
    Point viewPosition = viewport.getViewPosition();
    // adjust the cell rectangle, so its relative to the viewport, not the table
    cellRectangle.translate(-viewPosition.x, -viewPosition.y);
    // scroll the viewport if necessary
    viewport.scrollRectToVisible(cellRectangle);
  }

  /**
   * Move the cell editor to a given cell.
   * 
   * @param row
   * @param column
   */
  private void moveEditorTo(int row, int column) {
    editCellAt(row, column);

    setRowSelectionInterval(row, row);
    setColumnSelectionInterval(column, column);
    ((DefaultCellEditor) getCellEditor()).getComponent().requestFocus();
    scrollCellToVisible(row, column);
  }

  /**
   * Move the editor to the next editable cell to the left.
   * 
   * @param keyEvent
   */
  public void navigateLeft(KeyEvent keyEvent) {
    int row = editingCellAtRow;
    int column = editingCellAtColumn;
    for (int offset = 1; offset <= getColumnCount(); offset++) {
      int nextColumn = column - offset;
      if (nextColumn < 0) {
        nextColumn += getColumnCount();
      }
      if (isCellEditable(row, nextColumn)) {
        moveEditorTo(row, nextColumn);
        break;
      }
    }
  }

  /**
   * Move the editor to the next editable cell to the right.
   * 
   * @param keyEvent
   */
  public void navigateRight(KeyEvent keyEvent) {
    int row = editingCellAtRow;
    int column = editingCellAtColumn;
    for (int offset = 1; offset <= getColumnCount(); offset++) {
      int nextColumn = (column + offset) % getColumnCount();

      if (isCellEditable(row, nextColumn)) {
        moveEditorTo(row, nextColumn);
        break;
      }
    }
  }

  /**
   * Move the cell editor to the next editable cell above.
   * 
   * @param keyEvent
   */
  public void navigateUp(KeyEvent keyEvent) {
    TableCellEditor currentEditor = getCellEditor();
    if (currentEditor == null) {
      // not editing - change selection if required
      if (getSelectedRowCount() == 1) {
        int selectedRow = getSelectedRow() - 1;
        if (selectedRow < 0) {
          selectedRow = getRowCount() - 1;
        }
        changeSelection(selectedRow, selectedRow, false, false);
      }
    } else {
      // already editing
      int row = editingCellAtRow;
      int column = editingCellAtColumn;
      TextCellEditor editor = (TextCellEditor) ((DefaultCellEditor) getCellEditor())
          .getComponent();
      String oldValue = editor.getText();
      for (int offset = 1; offset <= getRowCount(); offset++) {
        int nextRow = row - offset;
        if (nextRow < 0) {
          nextRow += getRowCount();
        }
        if (isCellEditable(nextRow, column)) {
          moveEditorTo(nextRow, column);
          if ((keyEvent.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
            editor.setText(oldValue);
          }
          break;
        }
      }
    }
  }

  /**
   * Move the cell editor the the next editable cell below.
   * 
   * @param keyEvent
   */
  public void navigateDown(KeyEvent keyEvent) {
    TableCellEditor currentEditor = getCellEditor();
    if (currentEditor == null) {
      if (getSelectedRowCount() == 1) {
        int selectedRow = (getSelectedRow() + 1) % getRowCount();
        changeSelection(selectedRow, selectedRow, false, false);
      }
    } else {
      int row = editingCellAtRow;
      int column = editingCellAtColumn;
      TextCellEditor editor = (TextCellEditor) ((DefaultCellEditor) getCellEditor())
          .getComponent();
      String oldValue = editor.getText();
      for (int offset = 1; offset <= getRowCount(); offset++) {
        int nextRow = (row + offset) % getRowCount();
        if (isCellEditable(nextRow, column)) {
          moveEditorTo(nextRow, column);
          if ((keyEvent.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
            editor.setText(oldValue);
          }
          break;
        }
      }
    }
  }

  /**
   * @see javax.swing.JTable#editCellAt(int, int, java.util.EventObject)
   */
  @Override
  public boolean editCellAt(int row, int column, EventObject e) {
    if (super.editCellAt(row, column, e)) {
      // keep track of what is being edited
      editingCellAtRow = row;
      editingCellAtColumn = column;
      return true;
    }
    return false;
  }

}
