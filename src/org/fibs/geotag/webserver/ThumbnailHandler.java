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

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.fibs.geotag.data.ImageInfo;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

/**
 * @author Andreas Schneider
 * 
 */
public class ThumbnailHandler implements ContextHandler {

  /**
   * @see org.fibs.geotag.webserver.ContextHandler#serve(org.fibs.geotag.webserver.WebServer,
   *      java.lang.String, java.lang.String, java.util.Properties,
   *      java.util.Properties)
   */
  @Override
  public Response serve(WebServer server, String uri, String method,
      Properties header, Properties parms) {
    try {
      int dotPosition = uri.indexOf('.');
      if (dotPosition > 1) { // first character is a slash, followed by at
        // least one digit
        int sequenceNumber = Integer.parseInt(uri.substring(1, dotPosition));
        ImageInfo imageInfo = ImageInfo.getImageInfo(sequenceNumber);
        ImageIcon thumbnail = imageInfo.getThumbnail();
        if (thumbnail != null) {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          try {
            ImageIO.write((RenderedImage) thumbnail.getImage(),
                "jpg", byteArrayOutputStream); //$NON-NLS-1$
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
            return server.new Response(NanoHTTPD.HTTP_OK, server.mimeType(uri),
                byteArrayInputStream);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
    return server.new Response(NanoHTTPD.HTTP_NOTFOUND,
        NanoHTTPD.MIME_PLAINTEXT, WebServer.FILE_NOT_FOUND);
  }

}
