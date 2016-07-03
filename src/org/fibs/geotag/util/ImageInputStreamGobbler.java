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

package org.fibs.geotag.util;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * An InputStreamGobbler with a large buffer. This speeds up reading large
 * images
 * 
 * @author Andreas Schneider
 * 
 */
public class ImageInputStreamGobbler extends InputStreamGobbler {

  /** the size of the large buffer. Not too big, or Windows won't like it */
  private static int largeBufferSize = 10 * 1024;

  /**
   * @param inputStream
   * @param outputStream
   */
  public ImageInputStreamGobbler(InputStream inputStream,
      OutputStream outputStream) {
    super(inputStream, outputStream);
    setBufferSize(largeBufferSize);
  }

  /**
   * Change the buffer size used
   * 
   * @param bufferSize
   */
  public static void setDefaultBufferSize(int bufferSize) {
    largeBufferSize = bufferSize;
  }

}
