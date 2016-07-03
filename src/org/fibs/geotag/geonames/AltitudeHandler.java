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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.fibs.geotag.util.Constants;
import org.fibs.geotag.util.Proxies;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A class sending a request to genames.org and parsing the response.
 * 
 * @author Andreas Schneider
 * 
 */
public class AltitudeHandler extends DefaultHandler {

  /** The altitude retrieved from geonames.org */
  private String altitude = null;

  /**
   * @param latitude
   * @param longitude
   */
  public AltitudeHandler(String latitude, String longitude) {
    try {
      // Build the request
      GeonamesService service = new GeonamesService(GeonamesService.ALTITUDE);
      service.addParameter("lat", latitude); //$NON-NLS-1$
      service.addParameter("lng", longitude); //$NON-NLS-1$
      String url = service.buildURL();
      System.out.println(url);
      URL request = new URL(url);
      URLConnection connection = request.openConnection(Proxies.getProxy());
      // time out after 30 seconds - prevent hangs
      connection.setReadTimeout((int) Constants.ONE_MINUTE_IN_MILLIS / 2);
      InputStream inputStream = connection.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(
          inputStream));
      altitude = reader.readLine();
      reader.close();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @return The altitude retrieved from geonames.org
   */
  public String getAltitude() {
    return altitude;
  }

}
