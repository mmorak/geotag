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

package org.fibs.geotag.external;

import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.SwingWorker;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.util.Constants;
import org.fibs.geotag.util.CoordinatesParser;

/**
 * A class that monitors the system clipboard for changes and notifies listeners
 * about new positions for images.
 * 
 * @author Andreas Schneider
 * 
 */
public class ClipboardWorker extends SwingWorker<Void, ClipboardUpdate> {

  /** The monitoring thread terminates if this is set to true. */
  private boolean terminating = false;

  /** The system clipboard. */
  private Clipboard clipboard;

  /** The text stored in the system clipboard. */
  private String clipboardText;

  /**
   * Construct a new clipboard listener.
   * 
   * @param parent
   *          The window of the application (needed to find the system
   *          clipboard)
   */
  public ClipboardWorker(Window parent) {
    // find the system clipboard
    clipboard = parent.getToolkit().getSystemClipboard();
    // clear it
    StringSelection empty = new StringSelection(""); //$NON-NLS-1$
    clipboard.setContents(empty, empty);
    // now read the current content (empty text) from the clipboard
    // for future reference
    clipboardText = getClipboardText();
  }

  /**
   * Read the text in the clipboard.
   * 
   * @return The clipboard text or null
   */
  private String getClipboardText() {
    // default is null
    String text = null;
    // retrieve the contents of the clipboard
    Transferable clipData = clipboard.getContents(clipboard);
    if (clipData != null) {
      // try to retrieve the clipboard contents as text
      try {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          text = (String) (clipData.getTransferData(DataFlavor.stringFlavor));
        }
      } catch (UnsupportedFlavorException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return text;
  }

  /**
   * @see java.lang.Thread#run()
   */
  @Override
  public Void doInBackground() {
    // only terminate if requested to do so
    while (!terminating) {
      // 
      String text = getClipboardText();
      // check if there is nothing to be done
      if (text == null || text.equals(clipboardText)) {
        // nothing to be done - sleep for a while
        try {
          Thread.sleep(Constants.ONE_SECOND_IN_MILLIS);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } else {
        // clipboard contents have changed
        clipboardText = text;
        // do we care?
        if (Settings.get(SETTING.CLIPBOARD_ENABLED, false)) {
          try {
            CoordinatesParser parser = new CoordinatesParser(clipboardText);
            double latitude = Double.NaN;
            double longitude = Double.NaN;
            if (Settings.get(SETTING.CLIPBOARD_LATITUDE_FIRST, true)) {
              latitude = parser.nextCoordinate();
              longitude = parser.nextCoordinate();
            } else {
              longitude = parser.nextCoordinate();
              latitude = parser.nextCoordinate();
            }
            if (!Double.isNaN(latitude) && !Double.isNaN(longitude)) {
              ClipboardUpdate update = new ClipboardUpdate(latitude, longitude);
              publish(update);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
    return null;
  }

  /**
   * Terminate the monitor.
   */
  public void terminate() {
    terminating = true;
  }

}
