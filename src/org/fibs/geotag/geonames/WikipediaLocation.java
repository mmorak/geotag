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

package org.fibs.geotag.geonames;

import javax.swing.ImageIcon;

/**
 * A subclass of Location, adding an icon for Wikipedia locations.
 * 
 * @author Andreas Schneider
 */
public class WikipediaLocation extends Location {

  /** An icon denting a location name derived from Wikipedia. */
  private static ImageIcon wikipediaIcon = new ImageIcon(
      WikipediaLocation.class.getResource("/images/wikipedia.png")); //$NON-NLS-1$

  /**
   * @see org.fibs.geotag.geonames.Location#getIcon()
   */
  @Override
  public ImageIcon getIcon() {
    return wikipediaIcon;
  }

}
