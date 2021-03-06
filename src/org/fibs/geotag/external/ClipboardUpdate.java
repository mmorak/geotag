/**
 * Geotag
 * Copyright (C) 2007-2017 Andreas Schneider
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

package org.fibs.geotag.external;

/**
 * A class encapsulating updates from the clipbboard.
 * 
 * @author Andreas Schneider
 * 
 */
public class ClipboardUpdate {

  /** the new latitude. */
  private double latitude;

  /** the new longitude. */
  private double longitude;

  /**
   * Create a {@link ExternalUpdate} object.
   * 
   * @param latitude
   * @param longitude
   */
  public ClipboardUpdate(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * @return the latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * @return the longitude
   */
  public double getLongitude() {
    return longitude;
  }

}
