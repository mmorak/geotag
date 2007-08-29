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

package org.fibs.geotag.webserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.fibs.geotag.Messages;
import org.fibs.geotag.ui.ImagesTableModel;

import fi.iki.elonen.NanoHTTPD;

/**
 * A small simple web server based on NanoHTTPD I like the context/request
 * handler idea from <code>com.sun.net.httpserver.HttpServer</code>, but
 * don't want to use it, as it is not officially part of Java. Therefore we
 * extend NanoHTTPD to add these features
 * 
 * @author Andreas Schneider
 * 
 */
public class WebServer extends NanoHTTPD {

  /** Message to send if file or resource not found */
  public static final String FILE_NOT_FOUND = Messages
      .getString("WebServer.Error404"); //$NON-NLS-1$

  /** A list of contexts known to the server */
  private List<String> contexts = new ArrayList<String>();

  /** The context handlers registered for the context keys */
  private Map<String, ContextHandler> contextHandlers = new HashMap<String, ContextHandler>();

  /**
   * Constructor
   * 
   * @param port
   * @param imagesTableModel
   * @throws IOException
   */
  public WebServer(int port, ImagesTableModel imagesTableModel)
      throws IOException {
    super(port);
  }

  /**
   * Create a context. If the context path doesn't start with a slash or the
   * context already exists, this method does nothing.
   * 
   * @param path
   * @param contextHandler
   */
  // TODO Throw exceptions?
  public void createContext(String path, ContextHandler contextHandler) {
    // The path must start with a slash
    if (path.charAt(0) == '/') {
      // if the path ends in a slash remove it (unless the path is '/'
      String pathToAdd = path;
      if (pathToAdd.length() > 1
          && pathToAdd.charAt(pathToAdd.length() - 1) == '/') {
        pathToAdd = pathToAdd.substring(0, pathToAdd.length() - 1);
      }
      // we don't allow registering more than one handler per context
      if (contextHandlers.get(pathToAdd) == null) {
        contexts.add(pathToAdd);
        contextHandlers.put(pathToAdd, contextHandler);
      }
    }
  }

  /**
   * @see fi.iki.elonen.NanoHTTPD#serve(java.lang.String, java.lang.String,
   *      java.util.Properties, java.util.Properties)
   */
  @Override
  public Response serve(String uri, String method, Properties header,
      Properties parms) {
    // find the longest matching context
    String context = new String(); // empty string
    for (String candidate : contexts) {
      if (uri.startsWith(candidate) && candidate.length() > context.length()) {
        context = candidate;
      }
    }
    if (context.length() > 0) {
      // strip the context from the uri unless the context is '/'
      String contextUri = uri;
      if (context.length() > 1) {
        contextUri = uri.substring(context.length());
      }
      // find the context handler and handle the context
      return contextHandlers.get(context).serve(this, contextUri, method,
          header, parms);
    }
    // no context found
    return new Response(HTTP_NOTFOUND, MIME_PLAINTEXT, FILE_NOT_FOUND);
  }

  /**
   * @param filename
   *          A filename with a prefix
   * @return the most likely MIME type for the file
   */
  public String mimeType(String filename) {
    String type = null;
    int lastDot = filename.lastIndexOf('.');
    if (lastDot >= 0) {
      type = (String) theMimeTypes.get(filename.substring(lastDot + 1)
          .toLowerCase());
    }
    if (type == null) {
      type = MIME_DEFAULT_BINARY;
    }
    return type;
  }

}
