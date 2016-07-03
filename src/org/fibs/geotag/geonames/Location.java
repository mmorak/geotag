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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import org.fibs.geotag.util.Units;
import org.fibs.geotag.util.Units.DISTANCE;

/**
 * A class holding location information retrieved from geonames.org.
 * 
 * @author Andreas Schneider
 * 
 */
public class Location implements Comparable<Location> {

  /** The location name. */
  private String name;

  /** The latitude of the location. */
  private String latitude;

  /** The longitude of the location. */
  private String longitude;

  /** The country name of the location. */
  private String countryName;

  /** the province/state etc name. */
  private String province;

  /** The distance in km from the query location. */
  private double distance;

  /** Alternative names for a place. */
  private List<String> alternateNames = null;

  /** feature code name. */
  private String featureName;

  /** One letter feature class - e.g. P for 'populated place'. */
  private String featureClass;

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return new StringBuilder().append('[').append(name).append(',').append(
        latitude).append(',').append(longitude).append(',').append(province)
        .append(',').append(countryName).append(',').append(distance).append(
            ']').toString();
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the latitude
   */
  public String getLatitude() {
    return latitude;
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  /**
   * @return the longitude
   */
  public String getLongitude() {
    return longitude;
  }

  /**
   * @param longitude
   *          the longitude to set
   */
  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  /**
   * @return the countryName
   */
  public String getCountryName() {
    return countryName;
  }

  /**
   * @param countryName
   *          the countryName to set
   */
  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

  /**
   * @return the province
   */
  public String getProvince() {
    return province;
  }

  /**
   * @param province
   *          the province to set
   */
  public void setProvince(String province) {
    this.province = province;
  }

  /**
   * @return the distance
   */
  public double getDistance() {
    return distance;
  }

  /**
   * @param unit
   * @return the distance converted to a distance unit
   */
  public double getDistance(DISTANCE unit) {
    return Units.convert(distance, DISTANCE.KILOMETRES, unit);
  }

  /**
   * @param distance
   *          the distance to set
   */
  public void setDistance(double distance) {
    this.distance = distance;
  }

  /**
   * Add a single alternate name.
   * 
   * @param alternateName
   */
  public void addAlternateName(String alternateName) {
    if (alternateNames == null) {
      alternateNames = new ArrayList<String>();
    }
    alternateNames.add(alternateName);
  }

  /**
   * Add a comma separated list of alternate names.
   * 
   * @param names
   *          alternate names separated by commas
   */
  public void setAlternateNames(String names) {
    StringTokenizer tokenizer = new StringTokenizer(names, ","); //$NON-NLS-1$
    while (tokenizer.hasMoreTokens()) {
      String alternateName = tokenizer.nextToken().trim();
      addAlternateName(alternateName);
    }
  }

  /**
   * @return A List of alternate names or null if none exist
   */
  public List<String> getAlternateNames() {
    return alternateNames;
  }

  /**
   * @return the featureName
   */
  public String getFeatureName() {
    return featureName;
  }

  /**
   * @param featureName
   *          the featureName to set
   */
  public void setFeatureName(String featureName) {
    this.featureName = featureName;
  }

  /**
   * @return the featureClass
   */
  public String getFeatureClass() {
    return featureClass;
  }

  /**
   * @param featureClass
   *          the featureClass to set
   */
  public void setFeatureClass(String featureClass) {
    this.featureClass = featureClass;
  }

  /**
   * @return True if the location is a populated place (feature class 'P')
   */
  public boolean isPopulatedPlace() {
    return "P".equals(featureClass); //$NON-NLS-1$
  }

  /**
   * Subclasses can override this.
   * 
   * @return An ImageIcon for this location
   */
  public ImageIcon getIcon() {
    return null;
  }

  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Location other) {
    if (distance < other.distance) {
      return -1;
    }
    if (distance > other.distance) {
      return 1;
    }
    return name.compareTo(other.name);
  }

}
