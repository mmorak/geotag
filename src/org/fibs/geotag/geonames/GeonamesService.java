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

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;

/**
 * @author Andreas Schneider
 * 
 */
public class GeonamesService {

  @SuppressWarnings("javadoc")
  /**
   * Represents a geonames parameter
   */
  private static class Parameter {
    private String name;

    private String value;

    /**
     * @param name
     * @param value
     */
    public Parameter(String name, String value) {
      this.name = name;
      this.value = value;
    }

    /**
     * @return the parameter name
     */
    public String getName() {
      return name;
    }

    /**
     * @return the parameter value
     */
    public String getValue() {
      return value;
    }
  }

  /** service name for finding nearby locations */
  static final String FIND_NEARBY = "findNearby"; //$NON-NLS-1$

  /** service name for finding nearby Wikipedia entries */
  static final String FIND_NEARBY_WIKIPEDIA = "findNearbyWikipedia"; //$NON-NLS-1$

  /** service name for finding altitudes */
  static final String ALTITUDE = "srtm3"; //$NON-NLS-1$

  /** The service name */
  private String serviceName;

  /** The parameters for the service */
  private List<Parameter> parameters = new ArrayList<Parameter>();

  /**
   * @param name
   */
  public GeonamesService(String name) {
    this.serviceName = name;
  }

  /**
   * @param name
   * @param value
   */
  public void addParameter(String name, String value) {
    parameters.add(new Parameter(name, value));
  }

  /**
   * @param name
   * @param value
   */
  public void addParameter(String name, int value) {
    parameters.add(new Parameter(name, "" + value)); //$NON-NLS-1$
  }

  /**
   * @param name
   * @param value
   */
  public void addParameter(String name, double value) {
    parameters.add(new Parameter(name, "" + value)); //$NON-NLS-1$
  }

  /**
   * @return the service name
   */
  String getServiceName() {
    return serviceName;
  }

  /**
   * @return the service parameters
   */
  List<Parameter> getParameters() {
    return parameters;
  }

  /**
   * @return The geonames URL with leading http:// and appended username
   */
  public String buildURL() {
    StringBuilder url = new StringBuilder("http://"); //$NON-NLS-1$
    url.append(Settings
        .get(SETTING.GEONAMES_URL, Settings.GEONAMES_DEFAULT_URL));
    url.append('/').append(getServiceName()).append('?');
    for (Parameter parameter : getParameters()) {
      url.append(parameter.getName()).append('=').append(parameter.getValue());
      url.append('&'); // this works even for the last parameter as we add the
                       // username
    }
    url.append("username="); //$NON-NLS-1$
    url.append("geotag"); //$NON-NLS-1$

    return url.toString();
  }
}
