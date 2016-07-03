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
package org.fibs.geotag.gui.flattr;

import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

/**
 * Loads a Flattr button image over the internet
 * @author andreas
 */
public class FlattrImageLoader {
  
  /** The alternative text for the button */
  public static final String ALTERNATIVE_TEXT = "Flattr this!"; //$NON-NLS-1$
  
  /** The background loader for the image */
  private static SwingWorker<ImageIcon, Void> imageLoader;
  
  /** the Flattr buttom image downloaded from Flattr */
  private static ImageIcon imageIcon = null;

  /**
   * Private constructor
   */
  private FlattrImageLoader() {
    // private
  }
  
  // start loading image as soon as class is loaded
  static {
    imageLoader = new SwingWorker<ImageIcon, Void>() {
      @Override
      protected synchronized void done() {
        try {
          FlattrImageLoader.imageIcon = get();
        } catch (InterruptedException exception) {
          exception.printStackTrace();
        } catch (ExecutionException exception) {
          exception.printStackTrace();
        }
      }

      @Override
      protected ImageIcon doInBackground() throws Exception {
        // The URL of the static flattr image
        URL imageUrl = new URL("http://api.flattr.com/button/flattr-badge-large.png"); //$NON-NLS-1$
        // This can take a while - that's why this is in a SwingWorker
        ImageIcon icon = new ImageIcon(imageUrl);
        return icon;
      }
    };
    imageLoader.execute();
  }
  
  /**
   * Return the Flattr icon if it has arrived or register a listener to
   * be notified when it does. If the image is return the listener will not be notified.
   * @param listener A listener that is called when the image arrives
   * @return The image if has arrived or null if not
   */
  public static synchronized ImageIcon getImageIcon(PropertyChangeListener listener) {
    if (imageIcon != null) {
      return imageIcon;
    }
    imageLoader.addPropertyChangeListener(listener);
    return null;
  }
  
  /**
   * @return The Flattr image if has arrived or null otherwise
   */
  public static ImageIcon getImageIcon() {
    return imageIcon;
  }
  
}
