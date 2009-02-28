/**
 * Geotag
 * Copyright (C) 2007-2009 Andreas Schneider
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

package org.fibs.geotag.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Andreas Schneider
 * 
 */
public final class Messages {
  /***/
  static final String BUNDLE_NAME = "org.fibs.geotag.geotag"; //$NON-NLS-1$

  /**
   * A resource bundle control that tries to load property files from local
   * files first
   */
  private static final LocalFileResourceBundleControl control = new LocalFileResourceBundleControl();

  /***/
  private static ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME,
      control);

  static {
    // make sure all bundles are loaded
    new Translations().getKnownLocales();
  }
  /**
   * 
   */
  private Messages() {
    //
  }

  /**
   * @param key
   * @return string
   */
  public static String getString(String key) {
    try {
      return bundle.getString(key);
    } catch (MissingResourceException e) {
      return '?' + key + '?';
    }
  }

  /**
   * Reloads the message bundle. Might be useful to update translations on the
   * fly.
   */
  public static void reloadBundle() {
    ResourceBundle.clearCache(Messages.class.getClassLoader());
    bundle = ResourceBundle.getBundle(BUNDLE_NAME, control);
  }
  
}
