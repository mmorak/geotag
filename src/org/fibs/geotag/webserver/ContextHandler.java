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

import java.util.Properties;

import fi.iki.elonen.NanoHTTPD.Response;

/**
 * @author Andreas Schneider
 * 
 */
public interface ContextHandler {
  /**
   * @param server
   * @param uri
   * @param method
   * @param header
   * @param parms
   * @return The Response to be passed on to NanoHTTPD
   */
  Response serve(WebServer server, String uri, String method,
      Properties header, Properties parms);
}
