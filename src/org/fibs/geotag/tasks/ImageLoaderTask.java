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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingWorker;

import org.fibs.geotag.image.ImageFile;
import org.fibs.geotag.image.ImageFileFactory;

/**
 * A class to load image files in the background. Instances should override the
 * done() method to receive the result
 * 
 * @author Andreas Schneider
 * 
 */
public class ImageLoaderTask extends SwingWorker<Void, BufferedImage> {

  /** The image file to be loaded */
  private File file;

  /** the result of loading the image */
  private BufferedImage bufferedImage;

  /**
   * Create a new image loader task
   * 
   * @param file
   */
  public ImageLoaderTask(File file) {
    this.file = file;
  }

  /**
   * @see javax.swing.SwingWorker#doInBackground()
   */
  @Override
  protected Void doInBackground() throws Exception {
    try {
      ImageFile imageFile = ImageFileFactory.createImageFile(file);
      if (imageFile != null) {
        bufferedImage = imageFile.read();
      }
    } catch (IOException e) {
      e.printStackTrace();
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
