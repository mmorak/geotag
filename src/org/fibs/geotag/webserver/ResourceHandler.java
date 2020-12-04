/**
 * Geotag
 * Copyright (C) 2007-2009 Andreas Schneider
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

import java.io.InputStream;
import java.util.Properties;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

/**
 * A ContextHandler that serves files that are jar resources.
 * 
 * @author Andreas Schneider
 * 
 */
public class ResourceHandler implements ContextHandler {

  /**
   * @see org.fibs.geotag.webserver.ContextHandler#serve(org.fibs.geotag.webserver.WebServer,
   *      java.lang.String, java.lang.String, java.util.Properties,
   *      java.util.Properties)
   */
  public Response serve(WebServer server, String uri, String method,
      Properties header, Properties parms) {
    InputStream stream = WebServer.class.getClassLoader().getResourceAsStream(
        "htdocs" + uri); //$NON-NLS-1$
    if (stream != null) {
      return server.new Response(NanoHTTPD.HTTP_OK, server.mimeType(uri),
          stream);
    }
    return server.new Response(NanoHTTPD.HTTP_NOTFOUND,
        NanoHTTPD.MIME_PLAINTEXT, WebServer.FILE_NOT_FOUND);
  }
}
