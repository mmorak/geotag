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

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import org.fibs.geotag.util.LocaleUtil;

/**
 * A class to discover known translations and their resource bundles
 * 
 * @author andreas
 */
public class Translations {

  /** The officially supported translations */
  private static final String[] translations = { "en_GB", //$NON-NLS-1$
      "de", //$NON-NLS-1$
      "da" }; //$NON-NLS-1$

  /** Map a locale to a resource bundle */
  private static Map<Locale, EditableResourceBundle> bundles = new HashMap<Locale, EditableResourceBundle>();

  /** How locales are sorted */
  private static Comparator<Locale> localeComparator = new Comparator<Locale>() {
    @Override
    public int compare(Locale locale1, Locale locale2) {
      return locale1.toString().compareTo(locale2.toString());
    }
  };

  static {
    LocalFileResourceBundleControl control = new LocalFileResourceBundleControl();
    for (int index = 0; index < translations.length; index++) {
      String translation = translations[index];
      Locale locale = LocaleUtil.localeFromString(translation);
      if (locale != null) {
        EditableResourceBundle bundle = null;
        try {
          bundle = (EditableResourceBundle) control.newBundle(
              Messages.BUNDLE_NAME, locale, "java.properties", //$NON-NLS-1$
              Translations.class.getClassLoader(), true);
          addLocale(locale, bundle);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Remember a locale that had a bundle loaded
   * 
   * @param locale
   * @param bundle
   */
  public static void addLocale(Locale locale, EditableResourceBundle bundle) {
    bundles.put(locale, bundle);
  }

  /**
   * @return A list of locales we have come across.
   */
  public static List<Locale> getKnownLocales() {
    List<Locale> knownLocales = new Vector<Locale>();
    knownLocales.addAll(bundles.keySet());
    Collections.sort(knownLocales, localeComparator);
    return knownLocales;
  }

  /**
   * @param locale
   * @return The bundle for that locale only
   */
  public static EditableResourceBundle getBundle(Locale locale) {
    return bundles.get(locale);
  }

  /**
   * @return A list of class names recognised amongst the keys of the root
   *         bundle
   */
  public static Vector<String> getKnownClasses() {
    ResourceBundle bundle = getBundle(Locale.ROOT);
    Vector<String> result = new Vector<String>();
    Enumeration<String> keys = bundle.getKeys();
    int keyCount = 0;
    while (keys.hasMoreElements()) {
      keyCount++;
      String key = keys.nextElement();
      StringTokenizer tokenizer = new StringTokenizer(key, "."); //$NON-NLS-1$
      if (tokenizer.countTokens() == 2) {
        String className = tokenizer.nextToken();
        if (!result.contains(className)) {
          result.add(className);
        }
      }
    }
    Collections.sort(result);
    return result;
  }

  /**
   * @param className
   * @return A list of known identifiers found amongst the keys of the root
   *         bundle and belonging to a given class name
   */
  public static Vector<String> getKnownIdentifiers(String className) {
    ResourceBundle bundle = getBundle(Locale.ROOT);
    Vector<String> result = new Vector<String>();
    Enumeration<String> keys = bundle.getKeys();
    int keyCount = 0;
    while (keys.hasMoreElements()) {
      keyCount++;
      String key = keys.nextElement();
      StringTokenizer tokenizer = new StringTokenizer(key, "."); //$NON-NLS-1$
      if (tokenizer.countTokens() >= 2) {
        if (className.equals(tokenizer.nextToken())) {
          // get the rest of the key
          String identifier = tokenizer.nextToken();
          result.add(identifier);
        }
      }
    }
    Collections.sort(result);
    return result;
  }

}
