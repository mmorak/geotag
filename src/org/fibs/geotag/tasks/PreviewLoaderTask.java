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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fibs.geotag.image.ImageFile;
import org.fibs.geotag.image.ImageFileFactory;

/**
 * A class to load image files in the background. Instances should override the
 * done() method to receive the result
 * 
 * @author Andreas Schneider
 * 
 */
public class PreviewLoaderTask extends InterruptibleTask<Void, BufferedImage> {

  /** The image file to be loaded. */
  private File file;

  /** the result of loading the image. */
  private BufferedImage bufferedImage;

  /** delay in milliseconds before. */
  private long delay;

  /** A list of running (actually not-terminated) tasks. */
  private static List<PreviewLoaderTask> runningTasks = new ArrayList<PreviewLoaderTask>();

  /**
   * Create a new image loader task.
   * 
   * @param file
   * @param delay in milliseconds
   */
  public PreviewLoaderTask(File file, long delay) {
    this.file = file;
    this.delay = delay;
    synchronized (runningTasks) {
      // try terminating all running tasks
      for (PreviewLoaderTask task : runningTasks) {
        task.interruptRequest();
      }
      // and add this task to the list
      runningTasks.add(this);
    }
  }

  /**
   * @see javax.swing.SwingWorker#doInBackground()
   */
  @Override
  protected Void doInBackground() throws Exception {
    try {
      Thread.sleep(delay);
      if (!interruptRequested()) {
        ImageFile imageFile = ImageFileFactory.createImageFile(file);
        if (imageFile != null) {
          bufferedImage = imageFile.read();
        }
      } else {
        // System.out.println("Preview cancelled: "+file.getName());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // the task is about to finish - remove it from list of running tasks
    synchronized (runningTasks) {
      runningTasks.remove(this);
    }
    return null;
  }

  /**
   * @return The resulting image or null if an exception occured
   */
  public BufferedImage getImage() {
    return bufferedImage;
  }

}
