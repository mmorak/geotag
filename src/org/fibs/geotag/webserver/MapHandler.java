/**
 * Geotag
 * Copyright (C) 2007-2010 Andreas Schneider
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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(MapHandler.class);

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

  private enum JavascriptVaraibles {
    title("MapHandler.Geotag"), 
    showMenuText("MapHandler.ShowMenu"),
    hideMenuText("MapHandler.HideMenu"),
    mouseZoomText("MapHandler.MouseZoom"),
    showTracksText("MapHandler.DisplayTracks"),
    showWikipediaText("MapHandler.ShowWikipedia"),
    currentImageText("MapHandler.CurrentImage"),
    nextImageText("MapHandler.NextImage"),
    previousImageText("MapHandler.PreviousImage"),
    showAllText("MapHandler.ShowAllImages"),
    instructions("MapHandler.Instructions"),
    instructionsWithDirection("MapHandler.InstructionsWithDirection");
    // The key used to find the value of the variable 
    private final String messagesKey;
    private JavascriptVaraibles(String messagesKey) {
      this.messagesKey = messagesKey;
    }
    public String getMessagesKey() {
      return messagesKey;
    }
  }
  /**
   * Add the language dependent strings of the javascript file.
   * 
   * @param page
   */
  private void createJavascriptLanguageStrings(StringBuilder page) {
    for (JavascriptVaraibles variable : JavascriptVaraibles.values()) {
      addVariable(page, variable);
    }
  }

  /**
   * Generate a line for the Javascript file: <code>var name = 'value'</code>.
   * 
   * @param page
   * @param name
   * @param value
   */
  private void addVariable(StringBuilder page, JavascriptVaraibles variable) {
    String value = i18n.tr(variable.getMessagesKey());
    for (int index = 0; index < value.length(); index++) {
      char character = value.charAt(index);
      if (character < ' ' || (character >= '\u0080' && character < '\u00a0') ||
          (character >= '\u2000' && character < '\u2100')) {
        System.out.println("Invalid character at posistion "+index+" in "+value);
      }
    }
    page
        .append("  var ").append(variable.toString()).append(" = '").append(value).append("'\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }
  
  public static String quote(String string) {
    if (string == null || string.length() == 0) {
        return "''";
    }

    char         b;
    char         c = 0;
    int          i;
    int          len = string.length();
    StringBuffer sb = new StringBuffer(len + 4);
    String       t;

    sb.append("'");
    for (i = 0; i < len; i += 1) {
        b = c;
        c = string.charAt(i);
        switch (c) {
        case '\\':
        case '"':
            sb.append('\\');
            sb.append(c);
            break;
        case '/':
            if (b == '<') {
                sb.append('\\');
            }
            sb.append(c);
            break;
        case '\b':
            sb.append("\\b");
            break;
        case '\t':
            sb.append("\\t");
            break;
        case '\n':
            sb.append("\\n");
            break;
        case '\f':
            sb.append("\\f");
            break;
        case '\r':
            sb.append("\\r");
            break;
        default:
            if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                           (c >= '\u2000' && c < '\u2100')) {
                t = "000" + Integer.toHexString(c);
                sb.append("\\u" + t.substring(t.length() - 4));
            } else {
                sb.append(c);
            }
        }
    }
    sb.append('"');
    return sb.toString();
}
  
}
