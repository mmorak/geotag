/**
 * Geotag
 * Copyright (C) 2007 Andreas Schneider
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

package org.fibs.geotag.googleearth;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Class representing a URI query (e.g lat=51&lon=0.1&alt=42), splitting it up
 * and putting the key/value pairs in a HashMap for later retrieval.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class Query extends HashMap<String, String> {

  /**
   * Construct a new query and put it's elements into the HashMap
   * 
   * @param query
   */
  public Query(String query) {
    StringTokenizer queryTokenizer = new StringTokenizer(query, "&"); //$NON-NLS-1$
    while (queryTokenizer.hasMoreTokens()) {
      String pair = queryTokenizer.nextToken();
      StringTokenizer pairTokenizer = new StringTokenizer(pair, "="); //$NON-NLS-1$
      if (pairTokenizer.countTokens() == 2) {
        String key = pairTokenizer.nextToken();
        String value = pairTokenizer.nextToken();
        put(key, value);
      }
    }
  }
}
