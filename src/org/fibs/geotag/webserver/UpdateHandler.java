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

package org.fibs.geotag.webserver;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.SwingUtilities;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.external.ExternalUpdate;
import org.fibs.geotag.external.ExternalUpdateConsumer;

import fi.iki.elonen.NanoHTTPD.Response;

/**
 * A class handling location update messages from the browser.
 * 
 * @author Andreas Schneider
 * 
 */
public class UpdateHandler implements ContextHandler {

  /** The parent that needs to receive the updates. */
  private ExternalUpdateConsumer parent;

  /**
   * @param parent
   *          the parent that needs to receive the updates
   */
  public UpdateHandler(ExternalUpdateConsumer parent) {
    this.parent = parent;
  }

  /**
   * @see org.fibs.geotag.webserver.ContextHandler#serve(org.fibs.geotag.webserver.WebServer,
   *      java.lang.String, java.lang.String, java.util.Properties,
   *      java.util.Properties)
   */
  @Override
  public Response serve(WebServer server, String uri, String method,
      Properties header, Properties parms) {
    Double latitude = null;
    Double longitude = null;
    Double direction = null;
    Integer image = null;
    Enumeration<Object> parameters = parms.keys();
    while (parameters.hasMoreElements()) {
      String parameter = (String) parameters.nextElement();
      String value = parms.getProperty(parameter);
      if (parameter.equals("latitude")) { //$NON-NLS-1$
        latitude = new Double(Double.parseDouble(value));
      } else if (parameter.equals("longitude")) { //$NON-NLS-1$
        longitude = new Double(Double.parseDouble(value));
      } else if (parameter.equals("image")) { //$NON-NLS-1$
        image = Integer.valueOf(Integer.parseInt(value));
      } else if (parameter.equals("direction")) { //$NON-NLS-1$
        direction = new Double(Double.parseDouble(value));
      }
    }
    if (latitude != null && longitude != null && image != null) {
      ExternalUpdate externalUpdate = new ExternalUpdate(image.intValue(),
          latitude.doubleValue(), longitude.doubleValue(),
          direction == null ? Double.NaN : direction.doubleValue());
      final List<ExternalUpdate> updateList = new ArrayList<ExternalUpdate>();
      updateList.add(externalUpdate);
      // Notify the parent, but make sure its done in the event thread
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          getParent().processExternalUpdates(updateList);
        }
      });
      // finally we kept a copy of the latitude and longitude in the
      // settings, to use it for the best guess for images without
      // proper coordinates
      Settings.put(SETTING.LAST_GOOGLE_MAPS_LATITUDE, latitude.toString());
      Settings.put(SETTING.LAST_GOOGLE_MAPS_LONGITUDE, longitude.toString());
    }
    // send back a non-null response
    return server.xmlResponse("<ok/>"); //$NON-NLS-1$
  }

  /**
   * @return the parent
   */
  ExternalUpdateConsumer getParent() {
    return parent;
  }

}
