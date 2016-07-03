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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.fibs.geotag.table.ImagesTableModel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(WebServer.class);

  /** The MIME type for XML files. */
  public static final String XML_MIME_TYPE = "application/xml"; //$NON-NLS-1$

  /** Message to send if file or resource not found. */
  public static final String FILE_NOT_FOUND = i18n
      .tr("Error 404, file not found."); //$NON-NLS-1$

  /** A list of contexts known to the server. */
  private List<String> contexts = new ArrayList<String>();

  /** The context handlers registered for the context keys. */
  private Map<String, ContextHandler> contextHandlers = new HashMap<String, ContextHandler>();

  /**
   * Constructor.
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
      Properties parameters) {
    // uncomment the following lines for debugging:
    // System.out.print(uri + ' ');
    // Enumeration<Object> keys = parameters.keys();
    // while (keys.hasMoreElements()) {
    // String key = (String) keys.nextElement();
    // String value = parameters.getProperty(key);
    // System.out.print(key + "=" + value + ' ');
    // }
    // System.out.println();

    // find the longest matching context
    String context = ""; //$NON-NLS-1$
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
          header, parameters);
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

  /**
   * Convenience method.
   * 
   * @param xml
   *          An xml string
   * @return A response with that content and MIME type application/xml
   */
  public Response xmlResponse(String xml) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      byteArrayOutputStream.write(xml.getBytes());
      return xmlResponse(byteArrayOutputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Convenience method.
   * 
   * @param byteArrayOutputStream
   *          A {@link ByteArrayOutputStream} containing XML data
   * @return A response with that content and MIME type application/xml
   */
  public Response xmlResponse(ByteArrayOutputStream byteArrayOutputStream) {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
        byteArrayOutputStream.toByteArray());
    return new Response(NanoHTTPD.HTTP_OK, WebServer.XML_MIME_TYPE,
        byteArrayInputStream);
  }

}
