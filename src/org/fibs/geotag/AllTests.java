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

package org.fibs.geotag;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.fibs.geotag.track.KmlTest;
import org.fibs.geotag.track.TrackStoreTest;
import org.fibs.geotag.util.Ascii2NativeTest;
import org.fibs.geotag.util.BoundsTypeUtilTest;
import org.fibs.geotag.util.CommandLineTokenizerTest;
import org.fibs.geotag.util.CoordinatesTest;
import org.fibs.geotag.util.FileUtilTest;
import org.fibs.geotag.util.FontUtilTest;

/**
 * @author Andreas Schneider
 * 
 */
public class AllTests extends TestSuite {
  /**
   * @return test suite
   */
  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(FileUtilTest.class);
    suite.addTestSuite(FontUtilTest.class);
    suite.addTestSuite(TrackStoreTest.class);
    suite.addTestSuite(BoundsTypeUtilTest.class);
    suite.addTestSuite(KmlTest.class);
    suite.addTestSuite(CoordinatesTest.class);
    suite.addTestSuite(Ascii2NativeTest.class);
    suite.addTestSuite(CommandLineTokenizerTest.class);
    return suite;
  }
}
