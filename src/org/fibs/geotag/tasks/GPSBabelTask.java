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

package org.fibs.geotag.tasks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.fibs.geotag.gpsbabel.GPSBabel;
import org.fibs.geotag.track.GpxReader;
import org.fibs.geotag.util.Constants;
import org.fibs.geotag.util.InputStreamGobbler;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.topografix.gpx._1._0.Gpx;
import com.topografix.gpx._1._0.Gpx.Trk;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg;

/**
 * A task that reads data from a GPS.
 * 
 * @author Andreas Schneider
 * 
 */
public class GPSBabelTask extends BackgroundTask<Gpx> {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(GPSBabelTask.class);

  /** Number of track points received from GPS. */
  private int trackPointsReceived = 0;
	  
  /** The current progress value. */
  private int currentProgress;

  /** The minimum progress value. */
  private int minProgress;

  /** The maximum progress value. */
  private int maxProgress;

  /** Any error messages detected in the GPSBabel output. */
  private List<String> errorMessages = new ArrayList<String>();

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
   * @param currentProgress the currentProgress to set
   */
  void setCurrentProgress(int currentProgress) {
    this.currentProgress = currentProgress;
  }

  /**
   * @param minProgress the minProgress to set
   */
  void setMinProgress(int minProgress) {
    this.minProgress = minProgress;
  }

  /**
   * @param maxProgress the maxProgress to set
   */
  void setMaxProgress(int maxProgress) {
    this.maxProgress = maxProgress;
  }

  /**
   * @param errorMessages the errorMessages to set
   */
  void setErrorMessages(List<String> errorMessages) {
    this.errorMessages = errorMessages;
  }

  /**
   * Check if the terminate variable is set and terminate GPSBabel if it is.
   * 
   * @return true if GBSBabel has been terminated
   */
  synchronized boolean checkTerminate() {
    if (interruptRequested()) {
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
          if (checkTerminate()) {
            break;
          }
          // sleep half a second
          try {
            Thread.sleep(Constants.ONE_SECOND_IN_MILLIS / 2);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }.start();
    firePropertyChange("progress", null, i18n.tr("Connecting to GPS")); //$NON-NLS-1$ //$NON-NLS-2$
    // read the file using GPSBabel - use our own Gobbler to handle the output
    File file = GPSBabel.readTracks(new Gobbler());
    // now we have to read that file
    if (file != null) {
      Gpx gpx = GpxReader.read(file);
      if (gpx != null) {
        publish(gpx);
        List<Trk> tracks = gpx.getTrk();
        for (Trk trk : tracks) {
          List<Trkseg> segments = trk.getTrkseg();
          for (Trkseg segment : segments) {
            trackPointsReceived += segment.getTrkpt().size();
          }
        }
      }
    }
    // make the termination-check thread terminate
    interruptRequest();
    StringBuilder message = new StringBuilder();
    message.append(i18n.tr("Finished GPS transfer")); //$NON-NLS-1$
    message.append(' ').append(trackPointsReceived).append(' ');
    // Message reused from GpxReadFileTask
    message.append(i18n.tr("locations loaded.")); //$NON-NLS-1$
    return message.toString();
  }

  /**
   * A utility class reading the output of GPSBabel and generating progress or
   * error messages.
   * 
   * @author Andreas Schneider
   * 
   */
  class Gobbler extends InputStreamGobbler {

    /** The size of the buffer used for gobbling.  */
    private static final int BUFFER_SIZE = 128; // doesn't need to be big
    
    /** the progress message sent back. */
    private String progressMessage = ""; //$NON-NLS-1$

    /**
     * Constructor.
     */
    public Gobbler() {
      super((InputStream)null, null);
    }

    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
      InputStream inputStream = getInputStream();
      byte[] buffer = new byte[BUFFER_SIZE]; // doesn't need to be big
      int bytesInBuffer = 0;
      try {
        for (;;) {
          int b = inputStream.read();
          if (b == -1) {
            break; // lost stream - process probably gone
          }
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
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * Handle a new line of text (progress or error) from GPSBabel.
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
          setMinProgress(1);
          setMaxProgress(expected);
          setCurrentProgress(read);
          String message = i18n.tr("Waypoint") + ' ' //$NON-NLS-1$
              + getCurrentProgress() + ' ' + i18n.tr("of") //$NON-NLS-1$
              + ' ' + getMaxProgress();
          firePropertyChange("progress", progressMessage, message); //$NON-NLS-1$
          progressMessage = message;
        } else {
          getErrorMessages().add(line);
        }
      } catch (RuntimeException e) {
        // that line is no progress message - it's an error message
        getErrorMessages().add(line);
      }
    }
  }

}
