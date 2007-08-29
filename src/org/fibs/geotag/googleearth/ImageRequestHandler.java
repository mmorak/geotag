/**
 * Geotag
 * Copyright (C) 2007 Andreas Schneider
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

package org.fibs.geotag.googleearth;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Class to handle image requests for the Webserver
 * 
 * @author Andreas Schneider
 * 
 */
public class ImageRequestHandler implements HttpHandler {

  /** HTTP response OK */
  private static final int RESPONSE_OK = 200;

  /**
   * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
   */
  public void handle(HttpExchange exchange) throws IOException {
    // only handle GET requests
    String requestMethod = exchange.getRequestMethod();
    System.out.println(requestMethod);
    if ("GET".equalsIgnoreCase(requestMethod)) { //$NON-NLS-1$
      // what is the request?
      URI uri = exchange.getRequestURI();
      System.out.println(uri.getPath());
      System.out.println(uri.getQuery());
      // Set headers for response
      Headers responseHeaders = exchange.getResponseHeaders();
      // set the response content type to jpeg
      responseHeaders.set("Content-Type", "image/jpeg"); //$NON-NLS-1$ //$NON-NLS-2$
      // Mark the response as OK
      exchange.sendResponseHeaders(RESPONSE_OK, 0);

      // Get response body - so we can send an answer
      OutputStream responseBody = exchange.getResponseBody();

      responseBody.close();
    }
  }

}
