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

package org.fibs.geotag.gpsbabel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.fibs.geotag.Geotag;
import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.util.InputStreamGobbler;
import org.fibs.geotag.util.OperatingSystem;

/**
 * A class dealing with the GPSBabel program.
 * 
 * @author Andreas Schneider
 * 
 */
public final class GPSBabel {
  
  /**
   * hide constructor.
   */
  private GPSBabel() {
    // hide constructor
  }
  
  /**
   * Indicating if GPSBabel has been found - set every time
   * checkGPSBabelAvailable() is called.
   */
  private static boolean available;

  /** The Process used to run GPSBabel. */
  private static Process process;

  /**
   * check if GPSBabel can be executed and set the available field accordingly.
   */
  public static void checkGPSBabelAvailable() {
    boolean found = true; // set to false if we fail to run it
    // first we build the command
    List<String> command = new ArrayList<String>();
    String exiftool = Settings.get(SETTING.GPSBABEL_PATH, "gpsbabel"); //$NON-NLS-1$
    command.add(exiftool);
    // option -V: write version information
    command.add("-V"); //$NON-NLS-1$
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      process = processBuilder.redirectErrorStream(true).start();
      // now start a thread that reads the input stream of the process and
      // writes it to stdout
      new InputStreamGobbler(process, System.out).start();
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
   * @return true if GPSBabel was detected
   */
  public static boolean isAvailable() {
    return available;
  }

  /**
   * @return a sensible guess of where the serial port is.
   */
  public static String getDefaultDevice() {
    if (OperatingSystem.isLinux()) {
      String file = "/dev/ttyUSB0"; // my personal preference :-) //$NON-NLS-1$
      if ((new File(file)).exists()) {
        return file;
      }
      return "/dev/ttyS0"; // sensible default //$NON-NLS-1$
    } else if (OperatingSystem.isWindows()) {
      return "COM1:"; //$NON-NLS-1$
    }
    return "serial"; // user should change this //$NON-NLS-1$
  }

  /**
   * Create a File that will contain the tracks from the GPS.
   * 
   * @param gobbler
   *          The thread gobbling the output
   * @return The file
   */
  public static File readTracks(InputStreamGobbler gobbler) {
    File file = null;
    // first we build the command
    List<String> command = new ArrayList<String>();
    // -vs -t -i
    // first the executable
    command.add(Settings.get(SETTING.GPSBABEL_PATH, "gpsbabel")); //$NON-NLS-1$
    // next the undocumented -vs argument for getting progress updates
    command.add("-vs"); //$NON-NLS-1$
    // next -t to indicate that we're interested in tracks
    command.add("-t"); //$NON-NLS-1$
    // next -i to say that the next argument is the input format
    command.add("-i"); //$NON-NLS-1$
    // next the input format/protocol - garmin seems a sensible default
    command.add(Settings.get(SETTING.GPSBABEL_PROTOCOL, "garmin")); //$NON-NLS-1$
    // next -f to indicate that the next argument will be the input file/device
    command.add("-f"); //$NON-NLS-1$
    // now the input device or file
    command.add(Settings.get(SETTING.GPSBABEL_DEVICE, getDefaultDevice()));
    // next -o to say that next argument is the output format
    command.add("-o"); //$NON-NLS-1$
    // next the output format. We insist on GPX.
    command.add("gpx"); //$NON-NLS-1$
    // next -F to say that the next argument is the output file
    command.add("-F"); //$NON-NLS-1$
    // and finally the output file.
    file = new File(Geotag.NAME + ".gpx"); //$NON-NLS-1$
    // try to find a better name
    try {
      file = File.createTempFile(Geotag.NAME, ".gpx"); //$NON-NLS-1$
      file.deleteOnExit();
    } catch (IOException e) {
      e.printStackTrace();
    }
    command.add(file.getPath());
    // now we can run GPSBabel
    ProcessBuilder processBuilder = (new ProcessBuilder(command))
        .redirectErrorStream(true);
    try {
      process = processBuilder.redirectErrorStream(true).start();
      // now start a thread that reads the input stream of the process and
      // writes it to stdout
      final InputStream inputStream = process.getInputStream();
      gobbler.setInputStream(inputStream);
      gobbler.start();
      // we wait for the process to finish
      process.waitFor();
      inputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (process == null) {
      // process was interrupted - file is invalid
      // Ignoring return value - not much we can do if file can't be deleted.
      if (!file.delete()) {
        System.out.println("Failed to delete temp gpx file"); //$NON-NLS-1$
      }
      file = null;
    }
    process = null;
    return file;
  }

  /**
   * Can be called to forcefully terminate the GPSBabel process.
   */
  public static synchronized void terminate() {

    if (process != null) {
      process.destroy();
      process = null;
    }
  }
}
