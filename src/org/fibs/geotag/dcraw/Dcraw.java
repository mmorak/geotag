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

package org.fibs.geotag.dcraw;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.fibs.geotag.Settings;
import org.fibs.geotag.util.InputStreamGobbler;

/**
 * A class handling the dcraw external program
 * 
 * @author Andreas Schneider
 * 
 */
public class Dcraw {
  /**
   * A flag indicating if dcraw is available - changed by calls to
   * checkExiftoolAvailable()
   */
  private static boolean available = false;

  /**
   * @return True if dcraw has been detected
   */
  public static boolean isAvailable() {
    return available;
  }

  /**
   * check if dcraw can be executed and set the available field accordingly.
   */
  public static void checkDcrawAvailable() {
    boolean found = true; // set to false if we fail to run it
    // first we build the command
    // dcraw doesn't have an option to print a version number, so we
    // create a command that prints a short error message
    List<String> command = new ArrayList<String>();
    String dcraw = Settings.get(Settings.DCRAW_PATH, "dcraw"); //$NON-NLS-1$
    command.add(dcraw);
    // option -v: irrelevant, but generates output
    command.add("-v"); //$NON-NLS-1$
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      Process process = processBuilder.redirectErrorStream(true).start();
      // now start a thread that reads the input stream of the process and
      // writes it to stdout
      final InputStream inputStream = process.getInputStream();
      new InputStreamGobbler(inputStream, System.out).start();
      // we wait for the process to finish
      process.waitFor();
    } catch (IOException e) {
      e.printStackTrace();
      found = false;
    } catch (InterruptedException e) {
      e.printStackTrace();
      found = false;
    }
    available = found;
  }

  /**
   * Use dcraw to read the embedded image
   * 
   * @param rawFile
   *          The RAW file to extract the embedded image from
   * @return The embedded image or null if not found
   */
  public static BufferedImage getEmbeddedImage(File rawFile) {
    // don't bother trying if dcraw is not available
    if (!available) {
      return null;
    }
    // build the command to be executed
    List<String> command = new ArrayList<String>();
    // Start with the command name
    String dcraw = Settings.get(Settings.DCRAW_PATH, "dcraw"); //$NON-NLS-1$
    command.add(dcraw);
    // option -c - Write image data to standard output
    command.add("-c"); //$NON-NLS-1$
    // option -e - Extract embedded thumbnail image
    command.add("-e"); //$NON-NLS-1$
    // finally the file path
    command.add(rawFile.getPath());
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      Process process = processBuilder.redirectErrorStream(true).start();
      // now start a thread that reads the input stream of the process and
      // writes it to stdout
      InputStream inputStream = process.getInputStream();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      new InputStreamGobbler(inputStream, outputStream).start();
      // we wait for the process to finish
      process.waitFor();
      // there should now be some image data ready in the output stream
      byte[] imageData = outputStream.toByteArray();
      // create an InputStream to read an image from the data
      ByteArrayInputStream imageStream = new ByteArrayInputStream(imageData);
      BufferedImage bufferedImage = ImageIO.read(imageStream);
      return bufferedImage;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

}
