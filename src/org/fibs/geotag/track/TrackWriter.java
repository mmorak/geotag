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
package org.fibs.geotag.track;

import java.io.File;
import java.io.OutputStream;

import com.topografix.gpx._1._0.Gpx;

/**
 * An interface for classes writing tracks to files and streams.
 * @author Andreas Schneider
 */
public interface TrackWriter {
  /**
   * Write the tracks from a GPX object to a file.
   * @param gpx The GPX object containing the tracks
   * @param file the file to write to
   * @throws Exception
   */
  void write(Gpx gpx, File file) throws Exception;
  /**
   * Write the tracks from a GPX object to an output stream.
   * @param gpx The GPX object containing the tracks
   * @param outputStream The output stream to write to
   * @throws Exception
   */
  void write(Gpx gpx, OutputStream outputStream) throws Exception;
}
