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

import java.awt.Desktop;
import java.net.URI;

import javax.swing.JOptionPane;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author Felipe
 * @author andreas
 */
public class BrowserLauncher {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(BrowserLauncher.class);

  /** Error message */
  private static final String errorMessage = i18n
      .tr("Error attempting to launch web browser."); //$NON-NLS-1$

  /**
   * Open the url either with the browser specified or with the system default
   * browser
   * 
   * @param specificBrowser
   * @param url
   */
  public static void openURL(String specificBrowser, String url) {
    try {
      if (specificBrowser != null && specificBrowser.length() > 0) {
        Runtime.getRuntime().exec(specificBrowser + ' ' + url);
      } else {
        Desktop.getDesktop().browse(new URI(url));
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, errorMessage + ":\n" //$NON-NLS-1$
          + e.getLocalizedMessage());
    }
  }
}
