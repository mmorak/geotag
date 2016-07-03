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

import java.awt.event.KeyEvent;

import javax.swing.JTextField;

/**
 * A cell editor that tells a {@link NavigableTable} to move on to another cell.
 * 
 * @author Andreas Schneider
 */
@SuppressWarnings("serial")
public class TextCellEditor extends JTextField {
  /** The {@link NavigableTable} being edited by this editor. */
  private NavigableTable table;

  /**
   * @param table
   */
  public TextCellEditor(NavigableTable table) {
    this.table = table;
  }

  /**
   * @see javax.swing.JComponent#processKeyEvent(java.awt.event.KeyEvent)
   */
  @Override
  protected void processKeyEvent(KeyEvent keyEvent) {
    switch (keyEvent.getID()) {
      case KeyEvent.KEY_PRESSED:
        switch (keyEvent.getKeyCode()) {
          // case KeyEvent.VK_ENTER:
          // enter();
          // keyEvent.consume();
          // return;
          // case KeyEvent.VK_CANCEL:
          // case KeyEvent.VK_ESCAPE:
          // cancel();
          // keyEvent.consume();
          // return;
          case KeyEvent.VK_DOWN:
            table.navigateDown(keyEvent);
            keyEvent.consume();
            return;
          case KeyEvent.VK_UP:
            table.navigateUp(keyEvent);
            keyEvent.consume();
            return;
          case KeyEvent.VK_RIGHT:
            // only navigate right if cursor is at end of text
            if (getSelectionEnd() == getText().length()) {
              table.navigateRight(keyEvent);
              keyEvent.consume();
              return;
            }
            break;
          case KeyEvent.VK_LEFT:
            // only navigate left if cursor is at beginning of text
            if (getSelectionStart() == 0) {
              table.navigateLeft(keyEvent);
              keyEvent.consume();
              return;
            }
            break;
          default:
            break;
        }
        break;
      default:
        break;

    }
    super.processKeyEvent(keyEvent);
    return;
  }
}
