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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingWorker.StateValue;

import org.fibs.geotag.util.BrowserLauncher;

/**
 * @author andreas
 *
 */
@SuppressWarnings("serial")
public class FlattrButton extends JButton {
  
  /** The action command used by this button */
  private static final String FLATTR = "flattr"; //$NON-NLS-1$
  
  
  /**
   * Create a Flattr button for Geotag
   */
  public FlattrButton() {
    this("http://flattr.com/thing/141685/Geotag"); //$NON-NLS-1$
  }
  
  /**
   * Create a Flattr button for the URL of the thing to be flattred
   * @param flattrUrl
   */
  public FlattrButton(final String flattrUrl) {
    super(FlattrImageLoader.ALTERNATIVE_TEXT);
    PropertyChangeListener imageListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent event) {
        if ("state".equals(event.getPropertyName()) //$NON-NLS-1$
            && StateValue.DONE.equals(event.getNewValue())) {
          setIcon(FlattrImageLoader.getImageIcon());
          setText(null);
        }
      }
    };
    ImageIcon imageIcon= FlattrImageLoader.getImageIcon(imageListener);
    if (imageIcon != null) {
      setIcon(imageIcon);
      setText(null);
    }
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
