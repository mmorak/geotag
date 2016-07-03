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
  @Override
  public Response serve(WebServer server, String uri, String method,
      Properties header, Properties parms) {

    InputStream stream = createPage(uri);
    if (stream != null) {
      Response response = server.new Response(NanoHTTPD.HTTP_OK, server.mimeType(uri),
          stream);
      try {
        stream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return response;
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

  @SuppressWarnings("all")
  private enum JavascriptVaraibles {
    // This string doesn't need translating
    title("Geotag"),
    // Translators: Move the <u> and </u>  if necessary
    showMenuText(i18n.tr("Show <u>m</u>enu")),
    // Translators: Move the <u> and </u>  if necessary
    hideMenuText(i18n.tr("Hide <u>m</u>enu")),
    // Translators: Move the <u> and </u>  if necessary
    mouseZoomText(i18n.tr("Enable scroll wheel <u>z</u>oom")),
    // Translators: Move the <u> and </u>  if necessary
    showTracksText(i18n.tr("Display <u>t</u>racks")),
    // Translators: Move the <u> and </u>  if necessary
    showWikipediaText(i18n.tr("Show <u>W</u>ikipedia entries")),
    // Translators: Move the <u> and </u>  if necessary
    currentImageText(i18n.tr("Center on <u>c</u>urrent image")),
    // Translators: Move the <u> and </u>  if necessary
    nextImageText(i18n.tr("<u>N</u>ext image")),
    // Translators: Move the <u> and </u>  if necessary
    previousImageText(i18n.tr("<u>P</u>revious image")),
    // Translators: Move the <u> and </u>  if necessary
    showAllText(i18n.tr("Show <u>a</u>ll images")),
    instructions(i18n.tr("Move the marker to<br>select a different location.")),
    instructionsWithDirection(i18n.tr("Move camera marker to<br>select a different location.<br>Move other marker to<br>change image direction."));
    
    // The key used to find the value of the variable 
    private final String messagesKey;
    private JavascriptVaraibles(String messagesKey) {
      this.messagesKey = safeEncode(messagesKey);
    }
    public String getMessagesKey() {
      return messagesKey;
    }
    public static String safeEncode(String text) {
      StringBuffer buffer = new StringBuffer();
      for (int index = 0; index < text.length(); index++) {
        char character = text.charAt(index);
        if (character == '\'') {
          buffer.append("&#").append((int)character).append(";");
        } else {
          buffer.append(character);
        }
      }
      return buffer.toString();
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
   * @param variable
   */
  private void addVariable(StringBuilder page, JavascriptVaraibles variable) {
    String value = i18n.tr(variable.getMessagesKey());
    StringBuffer buffer = new StringBuffer();
    for (int index = 0; index < value.length(); index++) {
      char character = value.charAt(index);
      if (character < ' ' || (character >= '\u0080' && character < '\u00a0') ||
          (character >= '\u2000' && character < '\u2100')) {
        System.out.println("Invalid character at posistion "+index+" in "+value); //$NON-NLS-1$ //$NON-NLS-2$
      }
      if (character < ' ' || character > 127) {
        buffer.append("&#"+(int)character+";"); //$NON-NLS-1$ //$NON-NLS-2$
      } else {
        buffer.append(character);
      }
    }
    page
        .append("  var ").append(variable.toString()).append(" = '").append(buffer.toString()).append("'\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }
  
}
