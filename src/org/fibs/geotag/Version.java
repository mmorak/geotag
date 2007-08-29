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

/**
 * A holder class for the version number of the program
 * 
 * @author Andreas Schneider
 * 
 */
public class Version {
  /** The major version number */
  public static final int MAJOR = 0;

  /** The minor version number */
  public static final int MINOR = 1;

  /** The version formatted as a string */
  @SuppressWarnings( { "boxing", "nls" })
  public static final String VERSION = String.format("%d.%03d", MAJOR, MINOR);
}
