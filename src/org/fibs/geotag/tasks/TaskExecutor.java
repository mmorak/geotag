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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Jave 1.6.0_18 broker SwingWorkers (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6880336)
 * This is a workaround
 * @author andreas
 *
 */
public class TaskExecutor {
  
  /** The executor instance */
  static final ExecutorService executor = Executors.newCachedThreadPool();

  /**
   * execute a task
   * @param task - Task to be executed
   */
  public static void execute(Runnable task) {
    executor.execute(task);
  }
}
