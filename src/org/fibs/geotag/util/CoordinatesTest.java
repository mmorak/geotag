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

package org.fibs.geotag.util;

import junit.framework.TestCase;

import org.fibs.geotag.util.Coordinates.FORMAT;

/**
 * @author Andreas Schneider
 * 
 */
public class CoordinatesTest extends TestCase {

  /** A small number. */
  private static final double DELTA = 1e-7;

  /***/
  private static final String D = Unicode.DEGREE_SYMBOL;

  /***/
  private static final String M = Unicode.SINGLE_PRIME_MARK;

  /***/
  private static final String S = Unicode.DOUBLE_PRIME_MARK;

  /**
   * a simple test case.
   */
  @SuppressWarnings("nls")
  public void testLatitude() {
    String formatted = Coordinates.format(FORMAT.SIGNED_DEGREES, Airy.LATITUDE,
        false);
    assertEquals("51.4772222" + D, formatted);
    double parsed = Coordinates.parse(formatted, false);
    assertEquals(Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.SIGNED_DEGREES_MINUTES,
        Airy.LATITUDE, false);
    assertEquals("51" + D + "28.63333" + M, formatted);
    parsed = Coordinates.parse(formatted, false);
    assertEquals(Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.SIGNED_DEGREES_MINUTES_SECONDS,
        Airy.LATITUDE, false);
    assertEquals("51" + D + "28" + M + "38.00" + S, formatted);
    parsed = Coordinates.parse(formatted, false);
    assertEquals(Airy.LATITUDE, parsed, DELTA);

    // now the same without the sign, but with characters
    formatted = Coordinates.format(FORMAT.DEGREES, Airy.LATITUDE, false);
    assertEquals("N 51.4772222" + D, formatted);
    parsed = Coordinates.parse(formatted, false);
    assertEquals(Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates
        .format(FORMAT.DEGREES_MINUTES, Airy.LATITUDE, false);
    assertEquals("N 51" + D + "28.63333" + M, formatted);
    parsed = Coordinates.parse(formatted, false);
    assertEquals(Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.DEGREES_MINUTES_SECONDS,
        Airy.LATITUDE, false);
    assertEquals("N 51" + D + "28" + M + "38.00" + S, formatted);
    parsed = Coordinates.parse(formatted, false);
    assertEquals(Airy.LATITUDE, parsed, DELTA);

    // now the whole lot with negative value
    formatted = Coordinates
        .format(FORMAT.SIGNED_DEGREES, -Airy.LATITUDE, false);
    assertEquals("-51.4772222" + D, formatted);
    parsed = Coordinates.parse(formatted, false);
    assertEquals(-Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.SIGNED_DEGREES_MINUTES,
        -Airy.LATITUDE, false);
    assertEquals("-51" + D + "28.63333" + M, formatted);
    parsed = Coordinates.parse(formatted, false);
    assertEquals(-Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.SIGNED_DEGREES_MINUTES_SECONDS,
        -Airy.LATITUDE, false);
    assertEquals("-51" + D + "28" + M + "38.00" + S, formatted);
    parsed = Coordinates.parse(formatted, false);
    assertEquals(-Airy.LATITUDE, parsed, DELTA);

    // now the same without the sign, but with characters
    formatted = Coordinates.format(FORMAT.DEGREES, -Airy.LATITUDE, false);
    assertEquals("S 51.4772222" + D, formatted);
    parsed = Coordinates.parse(formatted, false);
    assertEquals(-Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.DEGREES_MINUTES, -Airy.LATITUDE,
        false);
    assertEquals("S 51" + D + "28.63333" + M, formatted);
    parsed = Coordinates.parse(formatted, false);
    assertEquals(-Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.DEGREES_MINUTES_SECONDS,
        -Airy.LATITUDE, false);
    assertEquals("S 51" + D + "28" + M + "38.00" + S, formatted);
    parsed = Coordinates.parse(formatted, false);
    assertEquals(-Airy.LATITUDE, parsed, DELTA);
  }

  /**
   * a simple test case.
   */
  @SuppressWarnings("nls")
  public void testLongitude() {
    String formatted = Coordinates.format(FORMAT.SIGNED_DEGREES, Airy.LATITUDE,
        true);
    assertEquals("51.4772222" + D, formatted);
    double parsed = Coordinates.parse(formatted, true);
    assertEquals(Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.SIGNED_DEGREES_MINUTES,
        Airy.LATITUDE, true);
    assertEquals("51" + D + "28.63333" + M, formatted);
    parsed = Coordinates.parse(formatted, true);
    assertEquals(Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.SIGNED_DEGREES_MINUTES_SECONDS,
        Airy.LATITUDE, true);
    assertEquals("51" + D + "28" + M + "38.00" + S, formatted);
    parsed = Coordinates.parse(formatted, true);
    assertEquals(Airy.LATITUDE, parsed, DELTA);

    // now the same without the sign, but with characters
    formatted = Coordinates.format(FORMAT.DEGREES, Airy.LATITUDE, true);
    assertEquals("E 51.4772222" + D, formatted);
    parsed = Coordinates.parse(formatted, true);
    assertEquals(Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.DEGREES_MINUTES, Airy.LATITUDE, true);
    assertEquals("E 51" + D + "28.63333" + M, formatted);
    parsed = Coordinates.parse(formatted, true);
    assertEquals(Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.DEGREES_MINUTES_SECONDS,
        Airy.LATITUDE, true);
    assertEquals("E 51" + D + "28" + M + "38.00" + S, formatted);
    parsed = Coordinates.parse(formatted, true);
    assertEquals(Airy.LATITUDE, parsed, DELTA);

    // now the whole lot with negative value
    formatted = Coordinates.format(FORMAT.SIGNED_DEGREES, -Airy.LATITUDE, true);
    assertEquals("-51.4772222" + D, formatted);
    parsed = Coordinates.parse(formatted, true);
    assertEquals(-Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.SIGNED_DEGREES_MINUTES,
        -Airy.LATITUDE, true);
    assertEquals("-51" + D + "28.63333" + M, formatted);
    parsed = Coordinates.parse(formatted, true);
    assertEquals(-Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.SIGNED_DEGREES_MINUTES_SECONDS,
        -Airy.LATITUDE, true);
    assertEquals("-51" + D + "28" + M + "38.00" + S, formatted);
    parsed = Coordinates.parse(formatted, true);
    assertEquals(-Airy.LATITUDE, parsed, DELTA);

    // now the same without the sign, but with characters
    formatted = Coordinates.format(FORMAT.DEGREES, -Airy.LATITUDE, true);
    assertEquals("W 51.4772222" + D, formatted);
    parsed = Coordinates.parse(formatted, true);
    assertEquals(-Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates
        .format(FORMAT.DEGREES_MINUTES, -Airy.LATITUDE, true);
    assertEquals("W 51" + D + "28.63333" + M, formatted);
    parsed = Coordinates.parse(formatted, true);
    assertEquals(-Airy.LATITUDE, parsed, DELTA);
    formatted = Coordinates.format(FORMAT.DEGREES_MINUTES_SECONDS,
        -Airy.LATITUDE, true);
    assertEquals("W 51" + D + "28" + M + "38.00" + S, formatted);
    parsed = Coordinates.parse(formatted, true);
    assertEquals(-Airy.LATITUDE, parsed, DELTA);
  }

  /**
   * testing conversion failures.
   */
  @SuppressWarnings("nls")
  public void testNaN() {
    String formatted = Coordinates.format(
        FORMAT.SIGNED_DEGREES_MINUTES_SECONDS, -Airy.LATITUDE, true);
    assertEquals("-51" + D + "28" + M + "38.00" + S, formatted);
    double parsed = Coordinates.parse("~" + formatted, true);
    assertEquals(Double.NaN, parsed, DELTA);
    parsed = Coordinates.parse(formatted + "~", true);
    assertEquals(Double.NaN, parsed, DELTA);
  }
}
