/**
 * Geotag
 * Copyright (C) 2007,2008 Andreas Schneider
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.ResourceBundle.Control;

/**
 * This class is based on source code from
 * http://java.sun.com/developer/JDCTechTips/2005/tt1018.html
 */
public class LocalFileResourceBundleControl extends ResourceBundle.Control {

  /** The properties available for each locale loaded from a local file */
  private Map<Locale, Properties> propertiesByLocale = new HashMap<Locale, Properties>();

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
    ExtendedPropertyResourceBundle bundle = null;
    if (format.equals("java.properties")) { //$NON-NLS-1$
      // we try to load from a file first
      String bundleName = toBundleName(baseName, locale) + '.' + "properties"; //$NON-NLS-1$
      System.out
          .println("Trying to load resource bundle for " + locale + " from " + bundleName); //$NON-NLS-1$ //$NON-NLS-2$
      File bundleFile = new File(bundleName);
      System.out.println(bundleFile.getAbsolutePath());
      if (bundleFile.exists()) {
        try {
          InputStream inputStream = new FileInputStream(bundleFile);
          bundle = new ExtendedPropertyResourceBundle(inputStream);
          storeProperties(locale, bundle);
          inputStream.close();
          return bundle;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    // Couldn't load from local file - try normal properties resource bundle
    // loading from the jar
    return super.newBundle(baseName, locale, format, loader, reload);
  }

  /**
   * Takes a bundle, makes a new Properties object and stores it the global hash
   * map with the locale as key.
   * 
   * @param locale
   * @param bundle
   */
  private void storeProperties(Locale locale,
      ExtendedPropertyResourceBundle bundle) {
    Set<String> keySet = bundle.getKeySet();
    Properties properties = new Properties();
    for (String key : keySet) {
      Object value = bundle.getObject(key);
      properties.put(key, value);
    }
    propertiesByLocale.put(locale, properties);
  }

}
