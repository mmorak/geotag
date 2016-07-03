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

import java.util.List;

import junit.framework.TestCase;

/**
 * @author Andreas Schneider
 * 
 */
public class CommandLineTokenizerTest extends TestCase {
  
  /**
   * Check the result
   * @param expected
   * @param actual
   */
  private void check(String[] expected, List<String> actual) {
    assertNotNull(actual);
    assertEquals(expected.length, actual.size());
    for (int index = 0; index < expected.length; index++) {
      assertEquals(expected[index], actual.get(index));
    }
  }

  /**
   * 
   */
  @SuppressWarnings("nls")
  public void testTokenizer() {
    String input = "my line";
    String[] expected = new String[] { "my", "line" };
    check(expected, new CommandLineTokenizer(input).tokenize());
    
    input = "   my    line   ";
    expected = new String[] { "my", "line" };
    check(expected, new CommandLineTokenizer(input).tokenize());

    input = "my \"'sin gle'\"";
    expected = new String[] { "my", "'sin gle'" };
    check(expected, new CommandLineTokenizer(input).tokenize());
    
    input =  "my '\"dou ble\"'";
    expected = new String[] { "my", "\"dou ble\"" };
    check(expected, new CommandLineTokenizer(input).tokenize());
    
    input = "my' funny line'";
    expected = new String[] {"my funny line"};
    check(expected, new CommandLineTokenizer(input).tokenize());

    input = "my\" funny line\"";
    expected = new String[] {"my funny line"};
    check(expected, new CommandLineTokenizer(input).tokenize());

  }

}
