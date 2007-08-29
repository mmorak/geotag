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
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

/**
 * @author Andreas Schneider
 * @deprecated Use NanoHTTPD instead
 */

@Deprecated
public class WebServer {

  /**
   * create a web server instance listening at a given port
   * 
   * @param port
   *          the port to use
   */
  public WebServer(int port) {
    InetSocketAddress address = new InetSocketAddress(port);
    try {
      HttpServer server = HttpServer.create(address, 0);
      // server.createContext("/geotag", new KmlRequestHandler()); //$NON-NLS-1$
      // server.createContext("/images", new ImageRequestHandler());
      // //$NON-NLS-1$
      // Use an Executor to handle requests for us
      server.setExecutor(Executors.newCachedThreadPool());
      // Start server
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
