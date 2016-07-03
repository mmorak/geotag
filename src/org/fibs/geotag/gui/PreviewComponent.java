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

package org.fibs.geotag.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.image.ImageRotator;
import org.fibs.geotag.tasks.PreviewLoaderTask;
import org.fibs.geotag.tasks.TaskExecutor;
import org.fibs.geotag.util.Constants;

/**
 * Swing doesn't have an ImageComponent - here is one.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class PreviewComponent extends Component {
  /** The image to be displayed by this component. */
  private BufferedImage image;

  /** The ImageInfo of the image displayed. */
  private ImageInfo imageInfo;

  /**
   * Create an ImageComponent with no associated picture.
   */
  public PreviewComponent() {
    // nothing to be done
  }

  /**
   * create an ImageComponent and set its name.
   * 
   * @param name
   */
  public PreviewComponent(String name) {
    setName(name);
  }

  /**
   * @see java.awt.Component#getPreferredSize()
   */
  @Override
  public Dimension getPreferredSize() {
    if (image == null) {
      return super.getPreferredSize();
    }
    return new Dimension(image.getWidth(), image.getHeight());
  }

  /**
   * Determine by what factor the image has to be scaled to fit the component.
   * 
   * @return The scale factor between zero (exclusive) and one (inclusive)
   */
  private double determineScaleFactor() {
    double factor = 1.0;
    // if the image is wider than the component
    if (image.getWidth() > getWidth()) {
      // scale by the factor to make the width fit
      factor = (double) getWidth() / (double) image.getWidth();
    }
    // if the image is taller than the component
    if (image.getHeight() > getHeight()) {
      // scale by the factor to make the height fit, but only if its smaller
      // than
      // the factor to make the width fit
      factor = Math.min(factor, (double) getHeight()
          / (double) image.getHeight());
    }
    return factor;
  }

  /**
   * @see java.awt.Component#paint(java.awt.Graphics)
   */
  @Override
  public void paint(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    if (image != null) {
      // we need to scale the image if it is too big
      double scaleFactor = determineScaleFactor();
      AffineTransform scaleTransform = AffineTransform.getScaleInstance(
          scaleFactor, scaleFactor);
      // we also want to centre the image
      double translateX = (getWidth() - image.getWidth() * scaleFactor) / 2.0;
      double translateY = (getHeight() - image.getHeight() * scaleFactor) / 2.0;
      AffineTransform translateTransform = AffineTransform
          .getTranslateInstance(translateX, translateY);
      // The overall transformation is the scaling of the image...
      AffineTransform transform = scaleTransform;
      // ... followed by the translation of the image
      // preConcatenate adds the translation in pixel space (not image space)
      transform.preConcatenate(translateTransform);
      g2d.drawImage(image, transform, null);
    } else {
      if (getName() != null) {
        g2d.drawString(getName(), getWidth() / 2, getHeight() / 2);
      } else {
        super.paint(g);
      }
    }
  }

  /**
   * @return the image info
   */
  public ImageInfo getImageInfo() {
    return imageInfo;
  }

  /**
   * Set the image to be painted from a file.
   * 
   * @param imageInfo
   *          Information about the image
   */
  public void setImageInfo(final ImageInfo imageInfo) {
    this.imageInfo = imageInfo;
    if (imageInfo != null) {
      File file = new File(imageInfo.getPath());
      TaskExecutor.execute(new PreviewLoaderTask(file,
          Constants.ONE_SECOND_IN_MILLIS / 2) {
        @Override
        protected void done() {
          BufferedImage bufferedImage = getImage();
          if (bufferedImage != null) {
            // see if the image should be rotated
            BufferedImage rotatedImage = (new ImageRotator(bufferedImage,
                imageInfo)).rotate();
            setImage(rotatedImage);
          } else {
            setImage(null);
          }
        }
      });
    } else {
      setImage(null);
    }
  }

  /**
   * @param image
   *          the image to set
   */
  void setImage(BufferedImage image) {
    // we want the image to have TYPE_INT_RGB
    if (image != null && image.getType() != BufferedImage.TYPE_INT_RGB) {
      // the image doesn't have the right type - convert it
      // Create a new image of the same size, but with the right type
      BufferedImage rgbImage = new BufferedImage(image.getWidth(), image
          .getHeight(), BufferedImage.TYPE_INT_RGB);
      // draw the original image contents to the new image
      Graphics graphics = rgbImage.getGraphics();
      graphics.drawImage(image, 0, 0, null);
      this.image = rgbImage;
    } else {
      this.image = image;
    }
    repaint();
  }
}
