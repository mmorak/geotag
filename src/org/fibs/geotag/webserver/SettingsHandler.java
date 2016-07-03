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

import java.util.Enumeration;
import java.util.Properties;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;

import fi.iki.elonen.NanoHTTPD.Response;

/**
 * A class receiving user preferences or choices from the Google Map and storing
 * them in the Settings.
 * 
 * @author Andreas Schneider
 * 
 */
public class SettingsHandler implements ContextHandler {

  /**
   * @see org.fibs.geotag.webserver.ContextHandler#serve(org.fibs.geotag.webserver.WebServer,
   *      java.lang.String, java.lang.String, java.util.Properties,
   *      java.util.Properties)
   */
  @Override
  public Response serve(WebServer server, String uri, String method,
      Properties header, Properties parms) {
    Enumeration<Object> parameters = parms.keys();
    while (parameters.hasMoreElements()) {
      String parameter = (String) parameters.nextElement();
      String value = parms.getProperty(parameter);
      if (parameter.equals("zoom")) { //$NON-NLS-1$
        Settings.put(SETTING.LAST_GOOGLE_MAPS_ZOOM_LEVEL, Integer
            .parseInt(value));
      }
      if (parameter.equals("maptype")) { //$NON-NLS-1$
        Settings.put(SETTING.LAST_GOOGLE_MAPS_MAP_TYPE, value);
      }
      if (parameter.equals("menuopen")) { //$NON-NLS-1$
        Settings
            .put(SETTING.GOOGLE_MAPS_MENU_OPEN, Boolean.parseBoolean(value));
      }
      if (parameter.equals("wheelzoom")) { //$NON-NLS-1$
        Settings.put(SETTING.GOOGLE_MAPS_MOUSE_WHEEL_ZOOM, Boolean
            .parseBoolean(value));
      }
      if (parameter.equals("showtracks")) { //$NON-NLS-1$
        Settings.put(SETTING.GOOGLE_MAP_SHOW_TRACKS, Boolean
            .parseBoolean(value));
      }
      if (parameter.equals("wikipedia")) { //$NON-NLS-1$
        Settings.put(SETTING.GOOGLE_MAP_SHOW_WIKIPEDIA, Boolean
            .parseBoolean(value));
      }
    }
    // send back a non-null response
    return server.xmlResponse("<ok/>"); //$NON-NLS-1$
  }

}
