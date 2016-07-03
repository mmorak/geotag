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

package org.fibs.geotag;

import javax.swing.undo.UndoManager;

/**
 * A singleton undo manager.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public final class GlobalUndoManager extends UndoManager {

  /** The singleton instance. */
  private static final GlobalUndoManager MANAGER = new GlobalUndoManager();

  /**
   * private constructor - use getManager.
   */
  private GlobalUndoManager() {
    // nothing to be done
  }

  /**
   * @return The {@link UndoManager} instance
   */
  public static UndoManager getManager() {
    return MANAGER;
  }

}
