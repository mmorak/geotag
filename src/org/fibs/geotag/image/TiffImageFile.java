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

package org.fibs.geotag.image;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.fibs.geotag.util.ImageInputStreamGobbler;
import org.fibs.geotag.util.ImageUtil;
import org.fibs.geotag.util.InputStreamGobbler;

import com.lizardworks.tiff.Tiff;

/**
 * @author Andreas Schneider
 * 
 */
public class TiffImageFile extends ImageFile {

  /**
   * Constructor with package visibility. Use Factory to construct.
   * 
   * @param file
   */
  TiffImageFile(File file) {
    super(file);
  }

  /**
   * @see org.fibs.geotag.image.ImageFile#read()
   */
  @Override
  public BufferedImage read() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    FileInputStream fileInputStream = new FileInputStream(getFile());
    InputStreamGobbler gobbler = new ImageInputStreamGobbler(
        fileInputStream, outputStream);
    gobbler.start();
    // we wait for the process to finish
    try {
      // wait until the gobbler is done gobbling
      final int sleepTime = 10;
      while (gobbler.isAlive()) {
        Thread.sleep(sleepTime);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Tiff tiff = new Tiff();
    tiff.read(outputStream.toByteArray());
    Image image = tiff.getImage(0);
    BufferedImage bufferedImage = ImageUtil.bufferImage(image);
    fileInputStream.close();
    outputStream.close();
    if (bufferedImage == null) {
      throw new IOException();
    }
    return bufferedImage;
  }

}
