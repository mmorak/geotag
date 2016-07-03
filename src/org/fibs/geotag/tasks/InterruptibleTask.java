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

package org.fibs.geotag.tasks;

import javax.swing.SwingWorker;

/**
 * A wrapper for a SwingWorker that can be interrupted.
 * 
 * @author Andreas Schneider
 * @param <T>
 * @param <V>
 */
public abstract class InterruptibleTask<T, V> extends SwingWorker<T, V> {

  /** Subclasses can read this and should terminate if true. */
  private boolean terminate = false;

  /**
   * Called as a request to interrupt a running task.
   */
  public synchronized void interruptRequest() {
    terminate = true;
  }
  
  /**
   * @return true is an interrupt was requested.
   */
  protected boolean interruptRequested() {
    return terminate;
  }

}
