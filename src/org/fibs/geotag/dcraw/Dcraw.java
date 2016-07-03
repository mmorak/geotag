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

package org.fibs.geotag.dcraw;

import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
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
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.util.Constants;
import org.fibs.geotag.util.ImageInputStreamGobbler;
import org.fibs.geotag.util.ImageUtil;
import org.fibs.geotag.util.InputStreamGobbler;
import org.fibs.geotag.util.Util;

import com.acme.JPM.Decoders.PpmDecoder;

/**
 * A class handling the dcraw external program.
 * 
 * @author Andreas Schneider
 * 
 */
public final class Dcraw {

  /**
   * hide constructor.
   */
  private Dcraw() {
    // hide constructor
  }

  /**
   * A flag indicating if dcraw is available - changed by calls to
   * checkExiftoolAvailable().
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
    String dcraw = Settings.get(SETTING.DCRAW_PATH, "dcraw"); //$NON-NLS-1$
    command.add(dcraw);
    // option -v: irrelevant, but generates output
    command.add("-v"); //$NON-NLS-1$
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      Process process = processBuilder.redirectErrorStream(true).start();
      // now start a thread that reads the input stream of the process and
      // writes it to stdout
      new InputStreamGobbler(process, System.out).start();
      // we wait for the process to finish
      process.waitFor();
      //inputStream.close();
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
   * Use dcraw to read the embedded image.
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
    long start = System.currentTimeMillis();
    // build the command to be executed
    List<String> command = new ArrayList<String>();
    // Start with the command name
    String dcraw = Settings.get(SETTING.DCRAW_PATH, "dcraw"); //$NON-NLS-1$
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
      InputStreamGobbler gobbler = new ImageInputStreamGobbler(inputStream,
          outputStream);
      gobbler.start();
      // we wait for the process to finish
      try {
        process.waitFor();
        // wait until the gobbler is done gobbling
        final int sleeptime = 10;
        while (gobbler.isAlive()) {
          Thread.sleep(sleeptime);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      // there should now be some image data ready in the output stream
      byte[] imageData = outputStream.toByteArray();
      // there might be an error message in there though, which we check first
      // String imageDataString = new String(imageData);
      byte[] filenameBytes = rawFile.getPath().getBytes();
      inputStream.close();
      outputStream.close();
      if (Util.startsWith(imageData, filenameBytes)) {
        // The image path is certainly not part of the image
        // dcraw error messages however start with it.
        System.err.println(new String(imageData));
        return null;
      }

      // create an InputStream to read an image from the data
      ByteArrayInputStream imageStream = new ByteArrayInputStream(imageData);
      // read the image from the stream - this only works for jpegs
      long readStart = System.currentTimeMillis();
      BufferedImage bufferedImage = ImageIO.read(imageStream);
      long readEnd = System.currentTimeMillis();
      imageStream.close();
      if (bufferedImage == null) {
        // could not read jpeg - try ppm
        imageStream = new ByteArrayInputStream(imageData);
        PpmDecoder producer = new PpmDecoder(imageStream);
        Image image = Toolkit.getDefaultToolkit().createImage(producer);
        MediaTracker mediaTracker = new MediaTracker(new Frame());
        mediaTracker.addImage(image, 0);
        try {
          mediaTracker.waitForID(0);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        bufferedImage = ImageUtil.bufferImage(image);
        imageStream.close();
      }
      if (bufferedImage == null) {
        System.err
            .println("No thumbnail for " + rawFile.getName() + ' ' + imageData.length + " bytes"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      long finished = System.currentTimeMillis();
      System.out.println("Loading RAW " + rawFile.getName() + " " //$NON-NLS-1$ //$NON-NLS-2$
          + ((finished - start) / (double) Constants.ONE_SECOND_IN_MILLIS)
          + " (" //$NON-NLS-1$
          + ((readEnd - readStart) / (double) Constants.ONE_SECOND_IN_MILLIS)
          + ")"); //$NON-NLS-1$
      return bufferedImage;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Test driver to figure out the speed difference based on the buffer size
   * 
   * @param args
   */
  public static void main(String args[]) {
    if (args.length != 1) {
      System.out
          .println("Usage java " + Dcraw.class.getCanonicalName() + " <filename>"); //$NON-NLS-1$ //$NON-NLS-2$
      return;
    }
    checkDcrawAvailable();
    System.out.println(args[0]);
    int[] bufferSizes = { 1, 1024, 2 * 1024, 10 * 1024, 16 * 1024, 20 * 1024,
        10 * 1024 * 1024 };
    for (int bufferSize : bufferSizes) {
      ImageInputStreamGobbler.setDefaultBufferSize(bufferSize);
      File file = new File(args[0]);
      long start = System.currentTimeMillis();
      BufferedImage image = getEmbeddedImage(file);
      System.out.println(image.getHeight());
      long stop = System.currentTimeMillis();
      System.out
          .println("Buffer: " + bufferSize + " : " + (stop - start) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
  }

}
