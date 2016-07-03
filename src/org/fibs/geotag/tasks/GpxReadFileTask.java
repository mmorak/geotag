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
import java.util.List;

import org.fibs.geotag.track.GpxOneOneReader;
import org.fibs.geotag.track.GpxReader;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.topografix.gpx._1._0.Gpx;
import com.topografix.gpx._1._0.Gpx.Trk;
import com.topografix.gpx._1._0.Gpx.Trk.Trkseg;

/**
 * @author Andreas Schneider
 * 
 */
public class GpxReadFileTask extends BackgroundTask<Gpx> {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(GpxReadFileTask.class);

  /** The files to be read. */
  private File[] files;

  /** Number of track points found in that file. */
  private int trackPointsFound = 0;

  /**
   * @param name
   * @param files
   */
  public GpxReadFileTask(String name, File[] files) {
    super(name);
    this.files = files;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getCurrentProgress()
   */
  @Override
  public int getCurrentProgress() {
    return 0;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getMaxProgress()
   */
  @Override
  public int getMaxProgress() {
    return 0;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getMinProgress()
   */
  @Override
  public int getMinProgress() {
    return 0;
  }

  /**
   * @see javax.swing.SwingWorker#doInBackground()
   */
  @Override
  protected String doInBackground() throws Exception {
    for (File file : files) {
      Gpx gpx = null;
      try {
        gpx = GpxReader.read(file);
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (gpx == null) {
        try {
          gpx = GpxOneOneReader.read(file);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      publish(gpx);
      if (gpx != null) {
        List<Trk> tracks = gpx.getTrk();
        for (Trk trk : tracks) {
          List<Trkseg> segments = trk.getTrkseg();
          for (Trkseg segment : segments) {
            trackPointsFound += segment.getTrkpt().size();
          }
        }
      }
    }
    String message = trackPointsFound
        + " " + i18n.tr("locations loaded."); //$NON-NLS-1$//$NON-NLS-2$
    return message;
  }

}
