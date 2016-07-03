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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JMenuItem;
import javax.swing.SwingWorker.StateValue;

import org.fibs.geotag.util.BrowserLauncher;

/**
 * A class that allows the user to flattr us
 * @author andreas
 */
@SuppressWarnings("serial")
public class FlattrMenuItem extends JMenuItem {
  
  /** The custom MenuItemUI */
  private ImageOrTextMenuItemUI menuItemUI = null;
  
  /** The action command used by this button */
  private static final String FLATTR = "flattr"; //$NON-NLS-1$
  
  /**
   * Create a Flattr button for Geotag
   */
  public FlattrMenuItem() {
    this("http://flattr.com/thing/141685/Geotag"); //$NON-NLS-1$
  }
  

  /**
   * @param flattrUrl The URL (as a string) to be opened when the button is clicked
   */
  public FlattrMenuItem(final String flattrUrl) {
    
    PropertyChangeListener imageListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent event) {
        if ("state".equals(event.getPropertyName()) //$NON-NLS-1$
            && StateValue.DONE.equals(event.getNewValue())) {
          menuItemUI.setBackgroundImage(FlattrImageLoader.getImageIcon());
          repaint();
        }
      }
    };
    menuItemUI = new ImageOrTextMenuItemUI(FlattrImageLoader.getImageIcon(imageListener));
    setUI(menuItemUI);
    setText(FlattrImageLoader.ALTERNATIVE_TEXT);

    setActionCommand(FLATTR);
    addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        if (FLATTR.equals(actionEvent.getActionCommand())) {
          BrowserLauncher.openURL(null, flattrUrl);
        }
      }
    });
  }
  
}
