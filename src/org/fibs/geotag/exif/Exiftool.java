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

package org.fibs.geotag.exif;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.util.InputStreamGobbler;

/**
 * @author Andreas Schneider
 * 
 */
public final class Exiftool {

  /**
   * hide constructor.
   */
  private Exiftool() {
    // hide constructor
  }

  /**
   * A flag indicating if exiftool is available - changed by calls to
   * checkExiftoolAvailable().
   */
  private static boolean available = false;

  /** The version string extracted from exiftool output. */
  private static String version = null;

  /**
   * @return True if exiftool has been detected
   */
  public static boolean isAvailable() {
    return available;
  }

  /**
   * @return The exiftool version string if available, null if not.
   */
  public static String getVersion() {
    return version;
  }

  /**
   * check if exiftool can be executed and set the available field accordingly.
   */
  public static void checkExiftoolAvailable() {
    boolean found = true; // set to false if we fail to run it
    // first we build the command
    List<String> command = new ArrayList<String>();
    String exiftool = Settings.get(SETTING.EXIFTOOL_PATH, "exiftool"); //$NON-NLS-1$
    command.add(exiftool);
    // option -ver: just write the version number
    command.add("-ver"); //$NON-NLS-1$
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      Process process = processBuilder.redirectErrorStream(true).start();
      // now start a thread that reads the input stream of the process and
      // writes it to stdout
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      new InputStreamGobbler(process, outputStream).start();
      // we wait for the process to finish
      process.waitFor();
      byte[] output = outputStream.toByteArray();
      String outputText = new String(output);
      version = outputText.trim();
      System.out.println("Exiftool " + version); //$NON-NLS-1$
    } catch (IOException e) {
      e.printStackTrace();
      found = false;
    } catch (InterruptedException e) {
      e.printStackTrace();
      found = false;
    } catch (NumberFormatException e) {
      e.printStackTrace();
      found = false;
    }
    available = found;
  }
}
