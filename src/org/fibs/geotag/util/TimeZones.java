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

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Utility class for time zone names
 * 
 * @author Andreas Schneider
 * 
 */
public class TimeZones {
  /**
   * @return A list of available time zone ids, with the current (local) time
   *         zone leading the list
   */
  public static String[] getAvailable() {
    String currentZone = new GregorianCalendar().getTimeZone().getID();
    List<String> available = new ArrayList<String>();
    available.add(currentZone);
    String[] ids = TimeZone.getAvailableIDs();
    for (String id : ids) {
      available.add(id);
    }
    return available.toArray(new String[available.size()]);
  }
}
