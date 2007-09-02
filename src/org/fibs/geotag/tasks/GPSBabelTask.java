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

package org.fibs.geotag.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.fibs.geotag.Messages;
import org.fibs.geotag.gpsbabel.GPSBabel;
import org.fibs.geotag.track.GpxReader;
import org.fibs.geotag.util.InputStreamGobbler;

import com.topografix.gpx._1._0.Gpx;

/**
 * A task that reads data from a GPS
 * 
 * @author Andreas Schneider
 * 
 */
public class GPSBabelTask extends BackgroundTask<Gpx> {

  /** The current progress value */
  int currentProgress;

  /** The minimum progress value */
  int minProgress;

  /** The maximum progress value */
  int maxProgress;

  /** Any error messages detected in the GPSBabel output */
  List<String> errorMessages = new ArrayList<String>();

  /**
   * @param name
   */
  public GPSBabelTask(String name) {
    super(name);
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getCurrentProgress()
   */
  @Override
  public int getCurrentProgress() {
    return currentProgress;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getMaxProgress()
   */
  @Override
  public int getMaxProgress() {
    return maxProgress;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getMinProgress()
   */
  @Override
  public int getMinProgress() {
    return minProgress;
  }

  /**
   * @return the errorMessages
   */
  public List<String> getErrorMessages() {
    return errorMessages;
  }

  /**
   * Check if the terminate variable is set and terminate GPSBabel if it is
   * 
   * @return true if GBSBabel has been terminated
   */
  synchronized boolean checkTerminate() {
    if (terminate) {
      GPSBabel.terminate();
      return true;
    }
    return false;
  }

  /**
   * @see javax.swing.SwingWorker#doInBackground()
   */
  @Override
  protected String doInBackground() throws Exception {
    // as GPSBabel.readTracks needs to block, we start a new
    // thread that checks for termination requests
    new Thread() {
      @Override
      public void run() {
        for (;;) {
          if (checkTerminate() == true) {
            break;
          }
          // sleep half a second
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }.start();
    // read the file using GPSBabel - use our own Gobbler to handle the output
    File file = GPSBabel.readTracks(new Gobbler());
    // now we have to read that file
    if (file != null) {
      Gpx gpx = GpxReader.readFile(file);
      if (gpx != null) {
        publish(gpx);
      }
    }
    // make the termination-check thread terminate
    terminate = true;
    return Messages.getString("GPSBabelTask.FinishedGpsTransfer"); //$NON-NLS-1$
  }

  /**
   * A utility class reading the output of GPSBabel and generating progress or
   * error messages
   * 
   * @author Andreas Schneider
   * 
   */
  class Gobbler extends InputStreamGobbler {

    /** the progress message sent back */
    private String progressMessage = ""; //$NON-NLS-1$

    /**
     * constructor
     */
    public Gobbler() {
      super(null, null);
    }

    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
      // 
      byte[] buffer = new byte[128]; // doesn't need to be big
      int bytesInBuffer = 0;
      try {
        for (;;) {
          int b = inputStream.read();
          if (b == -1)
            break; // lost stream - process probably gone
          buffer[bytesInBuffer] = (byte) b;
          bytesInBuffer++;
          // stop filling buffer if buffer full or end of line encountered
          if (b == '\r' || b == '\n' || bytesInBuffer == buffer.length) {
            // remove trailing \r and \n if found
            while (bytesInBuffer > 0
                && (buffer[bytesInBuffer - 1] == '\r' || buffer[bytesInBuffer - 1] == '\n')) {
              bytesInBuffer--;
            }
            // now we have a line in the buffer, but length can be zero
            // especially if the stream contains \r\n sequences
            if (bytesInBuffer > 0) {
              String line = new String(buffer, 0, bytesInBuffer);
              handleGPSBabelLine(line);
            }
            // mark buffer as empty again
            bytesInBuffer = 0;
          }
        }
        // the for loop terminated (a -1 byte was read)
        inputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    /**
     * Handle a new line of text (progress or error) from GPSBabel
     * 
     * @param line
     */
    public void handleGPSBabelLine(String line) {
      // we are hoping for lines that contain three numbers
      // separated by slashes
      try {
        StringTokenizer tokenizer = new StringTokenizer(line, "/"); //$NON-NLS-1$
        if (tokenizer.countTokens() == 3) {
          // very good! Try to convert the second and third ones into numbers
          tokenizer.nextToken();
          int read = Integer.parseInt(tokenizer.nextToken());
          int expected = Integer.parseInt(tokenizer.nextToken());
          minProgress = 1;
          maxProgress = expected;
          currentProgress = read;
          String message = Messages.getString("GPSBabelTask.Waypoint") + ' ' + currentProgress + ' ' + Messages.getString("GPSBabelTask.Of") + ' ' + maxProgress; //$NON-NLS-1$ //$NON-NLS-2$
          firePropertyChange("progress", progressMessage, message); //$NON-NLS-1$
          progressMessage = message;
        }
      } catch (RuntimeException e) {
        // that line is no progress message - it's an error message
        errorMessages.add(line);
      }
    }
  }

}
