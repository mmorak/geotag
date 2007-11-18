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

package org.fibs.geotag;

import java.util.ArrayList;
import java.util.List;

import org.fibs.geotag.util.Util;

/**
 * A class retrieving the localised text for the about dialog.
 * 
 * @author Andreas Schneider
 * 
 */
public final class License {

  /**
   * hide constructor.
   */
  private License() {
    // hide constructor
  }

  /** the maximum line width in characters. */
  private static final int MAX_LINE_LENGTH = 60;

  /**
   * @return license info as a List of strings
   */
  public static List<String> licenseInfo() {
    List<String> lines = new ArrayList<String>();
    String paragraph1 = Messages.getString("License.Paragraph1"); //$NON-NLS-1$
    String paragraph2 = Messages.getString("License.Paragraph2"); //$NON-NLS-1$
    String paragraph3 = Messages.getString("License.Paragraph3"); //$NON-NLS-1$
    lines.addAll(Util.splitString(paragraph1, MAX_LINE_LENGTH));
    lines.add(new String()); // blank line
    lines.addAll(Util.splitString(paragraph2, MAX_LINE_LENGTH));
    lines.add(new String());
    lines.addAll(Util.splitString(paragraph3, MAX_LINE_LENGTH));
    return lines;
  }
}
