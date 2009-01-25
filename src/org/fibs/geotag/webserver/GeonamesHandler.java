/**
 * Geotag
 * Copyright (C) 2007,2008 Andreas Schneider
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.fibs.geotag.geonames.Geonames;
import org.fibs.geotag.util.Constants;
import org.fibs.geotag.util.Proxies;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

/**
 * A ContextHandler that handles geonames.org requests.
 * 
 * @author Andreas Schneider
 * 
 */
public class GeonamesHandler implements ContextHandler {

  /**
   * @see org.fibs.geotag.webserver.ContextHandler#serve(org.fibs.geotag.webserver.WebServer,
   *      java.lang.String, java.lang.String, java.util.Properties,
   *      java.util.Properties)
   */
  public Response serve(WebServer server, String uri, String method,
      Properties header, Properties parms) {
    System.out.println(uri);
    StringBuilder geonamesUrl = new StringBuilder(Geonames.getURL());
    geonamesUrl.append(uri);
    Object[] keys = parms.keySet().toArray();
    for (int i = 0; i < keys.length; i++) {
      geonamesUrl.append(i == 0 ? '?' : '&');
      geonamesUrl.append(keys[i]);
      geonamesUrl.append('=');
      geonamesUrl.append(parms.get(keys[i]));
    }
    System.out.println(geonamesUrl.toString());
    try {
      URL url = new URL(geonamesUrl.toString());
      URLConnection connection = url.openConnection(Proxies.getProxy());
      // 30 second emergency time out
      connection.setReadTimeout((int) Constants.ONE_MINUTE_IN_MILLIS / 2);
      InputStream inputStream = connection.getInputStream();

      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

      byte[] buffer = new byte[Constants.ONE_K];
      int read = 0;
      while ((read = inputStream.read(buffer)) != -1) {
        byteArrayOutputStream.write(buffer, 0, read);
      }
      return server.xmlResponse(byteArrayOutputStream);

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return server.new Response(NanoHTTPD.HTTP_NOTFOUND,
        NanoHTTPD.MIME_PLAINTEXT, WebServer.FILE_NOT_FOUND);
  }
}
