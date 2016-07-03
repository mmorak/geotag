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

/**
 * A utility class for detecting the operating system
 * @author andreas
 *
 */
public class OperatingSystem {
  /**
   * @return True if operating system is Linux
   */
  public static boolean isLinux() {
    String osName = System.getProperty("os.name"); //$NON-NLS-1$
    return "Linux".equals(osName); //$NON-NLS-1$
  }
  /**
   * @return True if operating system is Windows
   */
  public static boolean isWindows() {
    String osName = System.getProperty("os.name"); //$NON-NLS-1$
    return osName != null && osName.startsWith("Windows"); //$NON-NLS-1$
  }
  /**
   * @return True if operating system is Mac OS
   */
  public static boolean isMacOS() {
    String osName = System.getProperty("os.name"); //$NON-NLS-1$
    return osName != null && osName.startsWith("Mac OS");     //$NON-NLS-1$
  }
  
}
