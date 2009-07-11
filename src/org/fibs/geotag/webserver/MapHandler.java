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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.StringTokenizer;

import org.fibs.geotag.i18n.Messages;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

/**
 * A ContextHandler that serves files our Map HTMl/Javascript. It 'includes' the
 * Javascript file in the HTML page. This is ugly, and only done to satisfy
 * Internet Explorer.
 * 
 * @author Andreas Schneider
 * 
 */
public class MapHandler implements ContextHandler {

  /**
   * @see org.fibs.geotag.webserver.ContextHandler#serve(org.fibs.geotag.webserver.WebServer,
   *      java.lang.String, java.lang.String, java.util.Properties,
   *      java.util.Properties)
   */
  public Response serve(WebServer server, String uri, String method,
      Properties header, Properties parms) {

    InputStream stream = createPage(uri);
    if (stream != null) {
      return server.new Response(NanoHTTPD.HTTP_OK, server.mimeType(uri),
          stream);
    }
    return server.new Response(NanoHTTPD.HTTP_NOTFOUND,
        NanoHTTPD.MIME_PLAINTEXT, WebServer.FILE_NOT_FOUND);
  }

  /**
   * Does the dirty work of assembling the page from the HTML and Javascript
   * files.
   * 
   * @param uri
   * @return The page as InputStream
   */
  public InputStream createPage(String uri) {
    StringBuilder page = new StringBuilder();
    try {
      InputStream stream = this.getClass().getClassLoader()
          .getResourceAsStream("htdocs" + uri); //$NON-NLS-1$
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
          stream));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        // Here goes the dirty work for Microsoft:
        if (line.startsWith("#includescript")) { //$NON-NLS-1$
          // the word after #includescript is the name of the file to include
          StringTokenizer tokenizer = new StringTokenizer(line);
          tokenizer.nextToken();
          String filename = tokenizer.nextToken();
          // enclose the file contents in <script> tags
          page.append("<script type=\"text/javascript\">\n"); //$NON-NLS-1$
          // and copy that file as well:
          InputStream includeStream = this.getClass().getClassLoader()
              .getResourceAsStream("htdocs/" + filename); //$NON-NLS-1$
          BufferedReader includeReader = new BufferedReader(
              new InputStreamReader(includeStream));
          while ((line = includeReader.readLine()) != null) {
            if (line.startsWith("//#includeI18N")) { //$NON-NLS-1$
              // The language dependent strings of the javascript file are
              // injected here. This allows us the maintain all translatable
              // text in one place.
              createJavascriptLanguageStrings(page);
            } else {
              page.append(line).append('\n');
            }
          }
          includeReader.close();
          // closing </script> tag
          page.append("</script>\n"); //$NON-NLS-1$
        } else {
          // otherwise, just copy the line
          page.append(line).append('\n');
        }
      }
      bufferedReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // convert the page into an InputStream
    return new ByteArrayInputStream(page.toString().getBytes());
  }

  /**
   * Add the language dependent strings of the javascript file.
   * 
   * @param page
   */
  private void createJavascriptLanguageStrings(StringBuilder page) {
    addText(page, "title", Messages.getString("MapHandler.Geotag")); //$NON-NLS-1$ //$NON-NLS-2$
    addText(page, "showMenuText", Messages.getString("MapHandler.ShowMenu")); //$NON-NLS-1$ //$NON-NLS-2$
    addText(page, "hideMenuText", Messages.getString("MapHandler.HideMenu")); //$NON-NLS-1$ //$NON-NLS-2$
    addText(page, "mouseZoomText", Messages.getString("MapHandler.MouseZoom")); //$NON-NLS-1$ //$NON-NLS-2$
    addText(page,
        "showTracksText", Messages.getString("MapHandler.DisplayTracks")); //$NON-NLS-1$ //$NON-NLS-2$
    addText(page,
        "showWikipediaText", Messages.getString("MapHandler.ShowWikipedia")); //$NON-NLS-1$ //$NON-NLS-2$
    addText(page,
        "currentImageText", Messages.getString("MapHandler.CurrentImage")); //$NON-NLS-1$ //$NON-NLS-2$
    addText(page, "nextImageText", Messages.getString("MapHandler.NextImage")); //$NON-NLS-1$ //$NON-NLS-2$
    addText(page,
        "previousImageText", Messages.getString("MapHandler.PreviousImage")); //$NON-NLS-1$ //$NON-NLS-2$
    addText(page, "showAllText", Messages.getString("MapHandler.ShowAllImages")); //$NON-NLS-1$ //$NON-NLS-2$
    addText(page, "instructions", Messages.getString("MapHandler.Instructions")); //$NON-NLS-1$ //$NON-NLS-2$
    addText(
        page,
        "instructionsWithDirection", Messages.getString("MapHandler.InstructionsWithDirection")); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Generate a line for the Javascript file: <code>var name = 'value'</code>.
   * 
   * @param page
   * @param name
   * @param value
   */
  private void addText(StringBuilder page, String name, String value) {
    page
        .append("  var ").append(name).append(" = '").append(value).append("'\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }
}
