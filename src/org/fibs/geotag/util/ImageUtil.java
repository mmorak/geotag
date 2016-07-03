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

package org.fibs.geotag.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A class with image utility methods.
 * 
 * @author Andreas Schneider
 * 
 */
public final class ImageUtil {
  /**
   * hide constructor.
   */
  private ImageUtil() {
    // hide constructor
  }

  /**
   * Create a BufferedImage from an Image.
   * 
   * @param image
   *          The original image
   * @return the buffered image
   */
  public static BufferedImage bufferImage(Image image) {
    BufferedImage bufferedImage = null;
    int imageWidth = image.getWidth(null);
    int imageHeight = image.getHeight(null);
    if (imageWidth > 0 && imageHeight > 0) {
      bufferedImage = new BufferedImage(imageWidth, imageHeight,
          BufferedImage.TYPE_INT_RGB);
      Graphics g = bufferedImage.getGraphics();
      g.drawImage(image, 0, 0, null);
    }
    return bufferedImage;
  }

  /**
   * @param name
   * @return An image for the given path
   */
  public static Image loadImage(String name) {
    InputStream stream = ImageUtil.class.getClassLoader().getResourceAsStream(
        name);
    if (stream != null) {
      byte[] buffer = new byte[32000];
      BufferedInputStream bufferedStream = new BufferedInputStream(stream);
      try {
        int read = bufferedStream.read(buffer);
        if (read > 0) {
          Image image = Toolkit.getDefaultToolkit().createImage(buffer);
          return image;
        }
        System.out
            .println("Error reading " + name + ' ' + read + " bytes read"); //$NON-NLS-1$//$NON-NLS-2$
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
    		try {
				bufferedStream.close();
		    } catch (IOException e) {
				  e.printStackTrace();
			  }
      }
    } else {
      System.out.println("Resource not found " + name); //$NON-NLS-1$
    }
    return null;
  }
}
