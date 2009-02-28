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

package org.fibs.geotag.util;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Utility class for Locales
 * 
 * @author andreas
 * 
 */
public class LocaleUtil {

  /**
   * @param text
   *          A string containing one or more of the language, country and
   *          variant of a locale, separated by underscores.
   * @return The locale represented by that string
   */
  public static Locale localeFromString(String text) {
    StringTokenizer tokenizer = new StringTokenizer(text, "_"); //$NON-NLS-1$
    String language = tokenizer.nextToken();
    if (tokenizer.hasMoreElements()) {
      String country = tokenizer.nextToken();
      if (tokenizer.hasMoreTokens()) {
        String variant = tokenizer.nextToken(""); //$NON-NLS-1$
        return new Locale(language, country, variant);
      }
      return new Locale(language, country);
    }
    return new Locale(language);
  }

}
