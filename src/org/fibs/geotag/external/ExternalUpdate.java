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

package org.fibs.geotag.external;

/**
 * A class encapsulating updates from external programs.
 * 
 * @author Andreas Schneider
 * 
 */
public class ExternalUpdate {
  /** The image's sequence number. */
  private int imageNumber;

  /** the new latitude. */
  private double latitude;

  /** the new longitude. */
  private double longitude;
  
  /** the new direction or NaN if not given. */
  private double direction = Double.NaN;

  /**
   * Create a {@link ExternalUpdate} object.
   * 
   * @param imageNumber
   * @param latitude
   * @param longitude
   * @param direction Set this to Double.NaN if not applicable
   */
  public ExternalUpdate(int imageNumber, double latitude, double longitude, double direction) {
    this.imageNumber = imageNumber;
    this.latitude = latitude;
    this.longitude = longitude;
    this.direction = direction;
  }

  /**
   * @return the imageNumber
   */
  public int getImageNumber() {
    return imageNumber;
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
  
  /**
   * @return the direction or Double.NaN if not known
   */
  public double getDirection() {
    return direction;
  }
}
