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
import java.io.InputStream;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import com.google.earth.kml._2.KmlType;
import com.topografix.gpx._1._0.Gpx;

/**
 * @author Andreas Schneider
 * 
 */
public class KmlTest extends TestCase {

  /** The GPX data. */
  private Gpx gpx;

  /**
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    InputStream stream = TrackStoreTest.class.getClassLoader()
        .getResourceAsStream("all.gpx"); //$NON-NLS-1$
    assertNotNull(stream);
    gpx = GpxReader.read(stream);
    stream.close();
    assertNotNull(gpx);
  }

  /**
   * 
   */
  public void testKml() {
    assertNotNull(gpx);
    KmlType kml;
    kml = new KmlTransformer().transform(gpx);
    assertNotNull(kml);
    try {
      System.out.println();
      File file = new File(System.getProperty("java.io.tmpdir") //$NON-NLS-1$
          + File.separator + "tracksall.kml"); //$NON-NLS-1$
      System.out.println(file.getPath());
      new KmlWriter().write(gpx, file);
    } catch (Exception e) {
      throw new AssertionFailedError();
    }
  }

}
