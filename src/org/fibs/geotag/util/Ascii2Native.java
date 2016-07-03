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

/**
 * A utility class to convert UTF strings to ASCII text as required by
 * ResourceBundles. This strict implementation is actually not required by
 * ResourceBundles, but the Eclipse "Externalise Strings" seems to generate
 * strings with only 7 bits of data, so I'll follow that. The whole
 * Properties/ResourceBundle/Encoding stuff is one of the worst aspects of Java.
 * 
 * @author andreas
 * 
 */
public class Ascii2Native {
  /** Any character above that will be encoded */
  private static int HIGHEST_ASCII_CHARACTER = 127;

  /** Valid HEX characters */
  private static String HEX_CHARACTERS = "0123456789ABCDEF"; //$NON-NLS-1$

  /**
   * @param nativeString
   * @return The native String with all characters that are not strict ASCII
   *         encoded
   */
  @SuppressWarnings("boxing")
  public static String native2Ascii(String nativeString) {
    StringBuilder builder = new StringBuilder();
    for (int index = 0; index < nativeString.length(); index++) {
      char character = nativeString.charAt(index);
      if (character <= HIGHEST_ASCII_CHARACTER) {
        builder.append(character);
      } else {
        builder.append(String.format("\\u%1$04X", (int) character)); //$NON-NLS-1$
      }
    }
    return builder.toString();
  }

  /**
   * Try and decode a hex value from the string at the given index.
   * 
   * @param string
   * @param index
   * @return the decoded character or -1 if there is no hex string at the given
   *         index
   */
  private static int decodeHexValue(String string, int index) {
    // are the at least six characters left ?
    if (index + 6 <= string.length()) {
      // good - is the first character a backslash?
      if (string.charAt(index) == '\\') {
        // followed by a u
        if (string.charAt(index + 1) == 'u') {
          // are the next four characters valid hex
          boolean foundFourHex = true;
          for (int position = index + 2; position < index + 6; position++) {
            if (HEX_CHARACTERS.indexOf(string.charAt(position)) == -1) {
              foundFourHex = false;
              break;
            }
          }
          if (foundFourHex) {
            // we're in business - "just" need to convert the 4 characters
            String hexString = string.substring(index + 2, index + 6);
            int hex = Integer.parseInt(hexString, 16);
            return hex;
          }
        }
      }
    }
    return -1;
  }

  /**
   * Takes a string that might contain Hex-encoded non-Ascii characters and
   * converts them back to native strings
   * 
   * @param ascii
   * @return The native representation of the string
   */
  public static String ascii2Native(String ascii) {
    StringBuilder builder = new StringBuilder();
    int index = 0;
    while (index < ascii.length()) {
      int hexValue = decodeHexValue(ascii, index);
      if (hexValue == -1) {
        builder.append(ascii.charAt(index));
        index++;
      } else {
        builder.append((char) hexValue);
        index += 6;
      }
    }
    return builder.toString();
  }

}
