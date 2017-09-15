/**
 * Geotag
 * Copyright (C) 2007-2017 Andreas Schneider
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
import java.util.StringTokenizer;

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

  /** The major version string extracted from exiftool output. */
  private static int installedMajorVersion = 0;
  /** The minor version string extracted from exiftool output. */
  private static int installedMinorVersion = 0;
  
  /**
   * @return True if exiftool has been detected
   */
  public static boolean isAvailable() {
    return available;
  }
  
  /**
   * Check if Exiftool is recent enough 
   * @param requiredMajorVersion
   * @param requiredMinorVersion
   * @return true if Exiftool is at least of the the given version
   */
  public static boolean versionAtLeast(int requiredMajorVersion, int requiredMinorVersion) {
    if (installedMajorVersion > requiredMajorVersion) {
      return true;
    }
    if (installedMajorVersion == requiredMajorVersion 
        && installedMinorVersion >= requiredMinorVersion) {
      return true;
    }
    return false;
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
      String versionString = outputText.trim();
      StringTokenizer tokenizer = new StringTokenizer(versionString, ".,"); //$NON-NLS-1$
      installedMajorVersion = Integer.parseInt(tokenizer.nextToken());
      installedMinorVersion = Integer.parseInt(tokenizer.nextToken());
      System.out.println("Exiftool " + installedMajorVersion+'-'+installedMinorVersion); //$NON-NLS-1$
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
