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

import java.util.regex.Matcher;

import junit.framework.TestCase;

/**
 * Unit tests for the coordinate parser.
 * 
 * @author Andreas Schneider
 * 
 */
// CHECKSTYLE:OFF
@SuppressWarnings("all")
public class CoordinatesParserTest extends TestCase {

  String coordMachuPicchu = "Machu Picchu 13" + Unicode.DEGREE_SYMBOL + " 9"
      + Unicode.SINGLE_PRIME_MARK + " 47" + Unicode.DOUBLE_PRIME_MARK
      + " S, 72" + Unicode.DEGREE_SYMBOL + " 32" + Unicode.SINGLE_PRIME_MARK
      + " 44" + Unicode.DOUBLE_PRIME_MARK + " W  ";

  String decimalMachuPicchu = "Machu Picchu -13.163056" + Unicode.DEGREE_SYMBOL
      + ", -72.545556" + Unicode.DEGREE_SYMBOL;

  private void printMatches(Matcher matcher) {
    boolean first = true;
    System.out.print("[");
    while (matcher.find()) {
      System.out.print("<" + matcher.group() + ">");
    }
    System.out.println("]");
  }

  public void testFloat() {
    Matcher matcher = CoordinatesParser.FLOAT_PATTERN.matcher(coordMachuPicchu);
    assertNotNull(matcher);
    // matcher should find 6 numbers
    assertTrue(matcher.find());
    assertEquals("13", matcher.group());
    assertTrue(matcher.find());
    assertEquals("9", matcher.group());
    assertTrue(matcher.find());
    assertEquals("47", matcher.group());
    assertTrue(matcher.find());
    assertEquals("72", matcher.group());
    assertTrue(matcher.find());
    assertEquals("32", matcher.group());
    assertTrue(matcher.find());
    assertEquals("44", matcher.group());
    assertFalse(matcher.find());
    // now test the other string
    matcher = CoordinatesParser.FLOAT_PATTERN.matcher(decimalMachuPicchu);
    assertNotNull(matcher);
    assertTrue(matcher.find());
    assertEquals("-13.163056", matcher.group());
    assertTrue(matcher.find());
    assertEquals("-72.545556", matcher.group());
    assertFalse(matcher.find());
  }

  public void testParser() {
    CoordinatesParser parser = new CoordinatesParser(coordMachuPicchu);
    // printMatches(parser.matcher);
    double coordinate = parser.nextCoordinate();
    assertEquals(-13.163056, coordinate, 1e-6);
    coordinate = parser.nextCoordinate();
    assertEquals(-72.545556, coordinate, 1e-6);
    coordinate = parser.nextCoordinate();
    assertTrue(Double.isNaN(coordinate));
  }
  // CHECKSTYLE:ON
}
