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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 * This class is based on source code from
 * http://java.sun.com/developer/JDCTechTips/2005/tt1018.html
 */
public class LocalFileResourceBundleControl extends ResourceBundle.Control {

  /**
   * @see java.util.ResourceBundle.Control#getFormats(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public List getFormats(String baseName) {
    return Control.FORMAT_DEFAULT;
  }

  /**
   * @see java.util.ResourceBundle.Control#newBundle(java.lang.String,
   *      java.util.Locale, java.lang.String, java.lang.ClassLoader, boolean)
   */
  @Override
  public ResourceBundle newBundle(String baseName, Locale locale,
      String format, ClassLoader loader, boolean reload)
      throws IllegalAccessException, InstantiationException, IOException {
    if ((baseName == null) || (locale == null) || (format == null)
        || (loader == null)) {
      throw new NullPointerException();
    }
    EditableResourceBundle bundle = null;
    if (format.equals("java.properties")) { //$NON-NLS-1$
      // we try to load from a file first
      String bundleName = toBundleName(baseName, locale);
      String bundleFileName = bundleName+ '.' + "properties"; //$NON-NLS-1$
      System.out
          .println("Trying to load resource bundle file for " + locale + " from " + bundleName); //$NON-NLS-1$ //$NON-NLS-2$
      File bundleFile = new File(bundleFileName);
      System.out.println(bundleFile.getAbsolutePath());
      if (bundleFile.exists()) {
        try {
          InputStream inputStream = new FileInputStream(bundleFile);
          bundle = new EditableResourceBundle(inputStream);
          inputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        // load as a resource
        String resourceName = toResourceName(bundleName, "properties"); //$NON-NLS-1$
        InputStream inputStream = loader.getResourceAsStream(resourceName);
        if (inputStream != null) {
          try {
            bundle = new EditableResourceBundle(inputStream);
          } catch (Exception e) {
            // it's a;right to fail
          } finally {
            inputStream.close();
          }
        }
      }
    }
    if (bundle != null) {
      Translations.addLocale(locale, bundle);
    }
    return bundle;
  }
  
}
