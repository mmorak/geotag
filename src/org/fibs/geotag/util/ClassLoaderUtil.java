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
 * This utility class works around class loader problems
 * with Java Web Start
 * (see http://java.sun.com/javase/technologies/desktop/javawebstart/faq.html#54 )
 * @author andreas
 *
 */
public class ClassLoaderUtil {
  /**
   * @return The actual class loader used on this thread right now
   */
  public static ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }
  
  /**
   * A replacement for Class.getName() that works with Java Web Start
   * @param className
   * @return the class with that name
   * @throws ClassNotFoundException
   */
  public static Class<?> getClassForName(String className) throws ClassNotFoundException {
    return getClassLoader().loadClass(className);
  }
  
}
