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

import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.util.Airy;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

/**
 * send information about an image back to the Geotag Javascript.
 * 
 * @author Andreas Schneider
 * 
 */
public class ImageInfoHandler implements ContextHandler {

  /**
   * @see org.fibs.geotag.webserver.ContextHandler#serve(org.fibs.geotag.webserver.WebServer,
   *      java.lang.String, java.lang.String, java.util.Properties,
   *      java.util.Properties)
   */
  @Override
  public Response serve(WebServer server, String uri, String method,
      Properties header, Properties parms) {
    int[] imageIDs = null;
    Enumeration<Object> parameters = parms.keys();
    while (parameters.hasMoreElements()) {
      String parameter = (String) parameters.nextElement();
      String value = parms.getProperty(parameter);
      if (parameter.equals("ids")) { //$NON-NLS-1$
        StringTokenizer tokenizer = new StringTokenizer(value, ","); //$NON-NLS-1$
        imageIDs = new int[tokenizer.countTokens()];
        for (int index = 0; index < imageIDs.length; index++) {
          imageIDs[index] = Integer.parseInt(tokenizer.nextToken());
        }
      }
    }
    if (imageIDs != null) {
      // now that we have segments, create a response
      return server.xmlResponse(imageInfoToXml(imageIDs));
    }
    return server.new Response(NanoHTTPD.HTTP_NOTFOUND,
        NanoHTTPD.MIME_PLAINTEXT, WebServer.FILE_NOT_FOUND);
  }

  /**
   * Assemble XML for a list of images.
   * 
   * @param imageIDs
   *          The IDs of the images
   * @return The XML sent back to Javascript as a String
   */
  private String imageInfoToXml(int[] imageIDs) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<images>\n"); //$NON-NLS-1$
    for (int index = 0; index < imageIDs.length; index++) {
      ImageInfo imageInfo = ImageInfo.getImageInfo(imageIDs[index]);
      if (imageInfo != null) {
        stringBuilder.append(" <image id=\"" + imageInfo.getSequenceNumber() //$NON-NLS-1$
            + "\""); //$NON-NLS-1$
        stringBuilder.append(" name=\"" + imageInfo.getName() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        int width = 0;
        int height = 0;
        ImageIcon thumbnail = imageInfo.getThumbnail();
        if (thumbnail != null) {
          width = thumbnail.getIconWidth();
          height = thumbnail.getIconHeight();
        }
        stringBuilder.append(" width=\"" + width + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        stringBuilder.append(" height=\"" + height + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        String latitude = Double.toString(Airy.LATITUDE);
        String longitude = Double.toString(Airy.LONGITUDE);
        latitude = Settings.get(SETTING.LAST_GOOGLE_MAPS_LATITUDE, latitude);
        longitude = Settings.get(SETTING.LAST_GOOGLE_MAPS_LONGITUDE, longitude);
        if (imageInfo.getGpsLatitude() != null
            && imageInfo.getGpsLongitude() != null) {
          latitude = imageInfo.getGpsLatitude();
          longitude = imageInfo.getGpsLongitude();
        }
        stringBuilder.append(" latitude=\"" + latitude + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        stringBuilder.append(" longitude=\"" + longitude + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        String direction = "-1.0"; // indicates 'no direction' //$NON-NLS-1$
        if (imageInfo.getGpsImgDirection() != null) {
          direction = imageInfo.getGpsImgDirection();
        }
        stringBuilder.append(" direction=\"" + direction + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        stringBuilder.append(" />\n"); //$NON-NLS-1$
      }
    }
    stringBuilder.append("</images>"); //$NON-NLS-1$
    // System.out.println(stringBuilder.toString());
    return stringBuilder.toString();
  }

}
