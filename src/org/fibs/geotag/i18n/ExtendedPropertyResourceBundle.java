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

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.Set;

/**
 * A sub-class of PropertyResourceBundle, exposing handleKeySet()
 */
public class ExtendedPropertyResourceBundle extends PropertyResourceBundle {

  /**
   * Constructor
   * 
   * @param stream
   * @throws IOException
   */
  ExtendedPropertyResourceBundle(InputStream stream) throws IOException {
    super(stream);
  }

  /**
   * @return the set of keys for just this one bundle (without parents)
   */
  public Set<String> getKeySet() {
    return handleKeySet();
  }

}