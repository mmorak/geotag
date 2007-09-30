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

package org.fibs.geotag.tasks;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.fibs.geotag.Messages;

/**
 * A wrapper for SwingWorker with some additional functionality
 * 
 * @author Andreas Schneider
 * @param <V>
 *          Type for passing on intermediate results by the publish() and
 *          process() methods
 * 
 */
public abstract class BackgroundTask<V> extends SwingWorker<String, V>
    implements PropertyChangeListener {

  /** The name of the task */
  private String name;

  /** A List of {@link BackgroundTaskListener}s */
  private static final List<BackgroundTaskListener> listeners = new ArrayList<BackgroundTaskListener>();

  /** A copy of the last progress message */
  private String oldProgressMessage = null;

  /** Subclasses can read this and should terminate if true */
  protected boolean terminate = false;

  /**
   * Construct a new {@link BackgroundTask}
   * 
   * @param name
   */
  public BackgroundTask(String name) {
    this.name = name;
    // listen to property changes generated by subclasses
    addPropertyChangeListener(this);
  }

  /**
   * @return the name of the task
   */
  public String getName() {
    return name;
  }

  /**
   * Must be implemented by subclasses
   * 
   * @return The minimum value of the progress
   */
  public abstract int getMinProgress();

  /**
   * Must be implemented by subclasses
   * 
   * @return The maximum value of the progress
   */
  public abstract int getMaxProgress();

  /**
   * Must be implemented by subclasses
   * 
   * @return The current progress value
   */
  public abstract int getCurrentProgress();

  /**
   * Called as a request to interrupt a running task
   */
  public synchronized void interruptRequest() {
    terminate = true;
  }

  /**
   * Set a new progress message for this task and notify property listeners. The
   * message will be something like 'Image 1 of 42'
   */
  public void setProgressMessage() {
    setProgressMessage(Messages.getString("BackgroundTask.Image")); //$NON-NLS-1$
  }

  /**
   * Set a new progress message for this task and notify property listeners. The
   * message will be something like 'prefix 1 of 42'
   * 
   * @param prefix
   */
  public void setProgressMessage(String prefix) {
    String progressMessage = prefix + ' ' + getCurrentProgress() + ' '
        + Messages.getString("BackgroundTask.Of") + ' ' //$NON-NLS-1$
        + getMaxProgress();
    firePropertyChange(
        "progress", oldProgressMessage, progressMessage.toString()); //$NON-NLS-1$
    oldProgressMessage = progressMessage.toString();
  }

  /**
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent event) {
    if ("state".equals(event.getPropertyName())) { //$NON-NLS-1$
      SwingWorker.StateValue state = (StateValue) event.getNewValue();
      if (state == SwingWorker.StateValue.STARTED) {
        for (BackgroundTaskListener listener : listeners) {
          listener.backgroundTaskStarted(this);
        }
      } else if (state == SwingWorker.StateValue.DONE) {
        for (BackgroundTaskListener listener : listeners) {
          listener.backgroundTaskFinished(this);
        }
      }
    } else if ("progress".equals(event.getPropertyName())) { //$NON-NLS-1$
      String progressMessage = (String) event.getNewValue();
      for (BackgroundTaskListener listener : listeners) {
        listener.backgroundTaskProgress(this, progressMessage);
      }
    }
  }

  /**
   * Register a new listener
   * 
   * @param listener
   *          The listener to be registered
   */
  public static void addBackgroundTaskListener(BackgroundTaskListener listener) {
    listeners.add(listener);
  }

  /**
   * Unregister a new listener
   * 
   * @param listener
   *          The listener to be unregistered
   */
  public static void removeBackgroundTaskListener(
      BackgroundTaskListener listener) {
    listeners.remove(listener);
  }
}
