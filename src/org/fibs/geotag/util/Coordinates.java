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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.StringTokenizer;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A class allowing formatting of coordinates.
 * 
 * @author Andreas Schneider
 * 
 */
public final class Coordinates {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(Coordinates.class);
  
  /**
   * hide constructor.
   */
  private Coordinates() {
    // hide constructor
  }

  /** An enumeration of supported formats. */
  public enum FORMAT {
    /** degrees with decimal and a leading minus if necessary. */
    SIGNED_DEGREES,
    /** degrees then minutes with decimal and a leading minus if necessary. */
    SIGNED_DEGREES_MINUTES,
    /**
     * degrees, minutes then seconds with decimal and a leading minus if
     * necessary.
     */
    SIGNED_DEGREES_MINUTES_SECONDS,
    /** NSWE then degrees with decimals. */
    DEGREES,
    /** NSWE degrees then minutes with decimals. */
    DEGREES_MINUTES,
    /** NSWE degrees, minutes then seconds with decimals. */
    DEGREES_MINUTES_SECONDS
  }

  /** Display names for supported formats. */
  @SuppressWarnings("nls")
  public static final String[] FORMAT_NAMES = {
      Unicode.PLUS_MINUS_SIGN + "D.dd" + Unicode.DEGREE_SYMBOL,
      Unicode.PLUS_MINUS_SIGN + "D" + Unicode.DEGREE_SYMBOL + "M.mm"
          + Unicode.SINGLE_PRIME_MARK,
      Unicode.PLUS_MINUS_SIGN + "D" + Unicode.DEGREE_SYMBOL + "M"
          + Unicode.SINGLE_PRIME_MARK + "S.ss" + Unicode.DOUBLE_PRIME_MARK,
      "H D.dd" + Unicode.DEGREE_SYMBOL,
      "H D" + Unicode.DEGREE_SYMBOL + "M.mm" + Unicode.SINGLE_PRIME_MARK,
      "H D" + Unicode.DEGREE_SYMBOL + "M" + Unicode.SINGLE_PRIME_MARK + "S.ss"
          + Unicode.DOUBLE_PRIME_MARK };

  /** Prefix for east. */
  public static final String EAST = i18n.tr("E"); //$NON-NLS-1$

  /** Prefix for west. */
  public static final String WEST = i18n.tr("W"); //$NON-NLS-1$

  /** Prefix for north. */
  public static final String NORTH = i18n.tr("N"); //$NON-NLS-1$

  /** Prefix for south. */
  public static final String SOUTH = i18n.tr("S"); //$NON-NLS-1$

  /**
   * @param value
   * @param isLongitude
   * @return the value formatted using the format specified by the settings
   */
  public static String format(double value, boolean isLongitude) {
    FORMAT format = FORMAT.values()[Settings.get(SETTING.COORDINATES_FORMAT, 0)];
    return format(format, value, isLongitude);
  }

  /**
   * @param format
   * @param signum
   * @param isLongitude
   * @return the prefix of the formatted string as specified bu the format
   */
  private static String buildPrefix(FORMAT format, double signum,
      boolean isLongitude) {
    String prefix = ""; //$NON-NLS-1$
    switch (format) {
      case SIGNED_DEGREES:
      case SIGNED_DEGREES_MINUTES:
      case SIGNED_DEGREES_MINUTES_SECONDS:
        if (signum < 0.0) {
          prefix = "-"; //$NON-NLS-1$
        } else {
          prefix = ""; //$NON-NLS-1$
        }
        break;
      case DEGREES:
      case DEGREES_MINUTES:
      case DEGREES_MINUTES_SECONDS:
        if (signum < 0.0) {
          if (isLongitude) {
            prefix = WEST;
          } else {
            prefix = SOUTH;
          }
        } else {
          if (isLongitude) {
            prefix = EAST;
          } else {
            prefix = NORTH;
          }
        }
        prefix += ' ';
        break;
      default:
        break;
    }
    return prefix;
  }

  /**
   * @param format
   * @param value
   * @param isLongitude
   * @return The value formatted as specified by the format
   */
  public static String format(FORMAT format, double value, boolean isLongitude) {
    double signum = Math.signum(value);
    String prefix = buildPrefix(format, signum, isLongitude);
    String coordinates = ""; //$NON-NLS-1$
    double absValue = Math.abs(value);
    double degrees;
    double minutes;
    double seconds;
    switch (format) {
      case DEGREES:
      case SIGNED_DEGREES:
        coordinates = String.format(
            "%.7f%s", new Double(absValue), Unicode.DEGREE_SYMBOL); //$NON-NLS-1$
        break;
      case DEGREES_MINUTES:
      case SIGNED_DEGREES_MINUTES:
        degrees = Math.floor(absValue);
        minutes = (absValue - degrees) * Constants.MINUTES_PER_DEGREE;
        coordinates = String.format("%d%s%08.5f%s", Integer.valueOf((int) degrees), //$NON-NLS-1$
            Unicode.DEGREE_SYMBOL, new Double(minutes),
            Unicode.SINGLE_PRIME_MARK);
        break;
      case DEGREES_MINUTES_SECONDS:
      case SIGNED_DEGREES_MINUTES_SECONDS:
        degrees = Math.floor(absValue);
        minutes = Math.floor((absValue - degrees)
            * Constants.MINUTES_PER_DEGREE);
        seconds = (absValue - degrees - minutes / Constants.MINUTES_PER_DEGREE)
            * Constants.SECONDS_PER_DEGREE;
        coordinates = String.format(
            "%d%s%02d%s%05.2f%s", Integer.valueOf((int) degrees), //$NON-NLS-1$
            Unicode.DEGREE_SYMBOL, Integer.valueOf((int) minutes),
            Unicode.SINGLE_PRIME_MARK, new Double(seconds),
            Unicode.DOUBLE_PRIME_MARK);
        break;
      default:
        break;
    }
    return prefix + coordinates;
  }

  /**
   * Tries its best to parse a string and convert it into degrees.
   * 
   * @param value
   * @param isLongitude
   * @return A double representing the latitude/longitude in degrees or
   *         Double.NaN if the String could not be parsed
   */
  public static double parse(String value, boolean isLongitude) {
    double parsed = Double.NaN;
    String text = value;
    if (text == null || text.length() == 0) {
      return parsed;
    }
    // examine first character and decide if it is a valid prefix
    int signum = 1;
    char first = text.charAt(0);
    if (first == EAST.charAt(0) && isLongitude) {
      text = text.substring(1);
    } else if (first == WEST.charAt(0) && isLongitude) {
      signum = -1;
      text = text.substring(1);
    } else if (first == NORTH.charAt(0) && !isLongitude) {
      text = text.substring(1);
    } else if (first == SOUTH.charAt(0) && !isLongitude) {
      signum = -1;
      text = text.substring(1);
    } else if (first == '-') {
      signum = -1;
      text = text.substring(1);
    }
    // now we split the text into tokens
    // TODO Decide if certain or all letters should be delimiters
    // we allow the following delimiters
    StringBuilder delimiters = new StringBuilder();
    delimiters.append(' '); // white space
    delimiters.append('\''); // single quote for minutes
    delimiters.append('"'); // double quote for seconds
    // and the proper unicode characters for degrees, minutes and seconds
    delimiters.append(Unicode.DEGREE_SYMBOL);
    delimiters.append(Unicode.SINGLE_PRIME_MARK);
    delimiters.append(Unicode.DOUBLE_PRIME_MARK);
    for (char delimiter = 'A'; delimiter <= 'Z'; delimiter++) {
      delimiters.append(delimiter);
    }
    for (char delimiter = 'a'; delimiter <= 'z'; delimiter++) {
      delimiters.append(delimiter);
    }
    StringTokenizer tokenizer = new StringTokenizer(text, delimiters.toString());
    // how to interpret the text depends on the number of tokens
    double degrees = 0.0;
    double minutes = 0.0;
    double seconds = 0.0;
    try {
      DecimalFormat decimalformat = new DecimalFormat();
      if (tokenizer.countTokens() == 1) {
        // degrees with decimals
        degrees = decimalformat.parse(tokenizer.nextToken()).doubleValue();
        // degrees = Double.parseDouble(tokenizer.nextToken());
      } else if (tokenizer.countTokens() == 2) {
        // degrees, then minutes with decimals
        degrees = Integer.parseInt(tokenizer.nextToken());
        minutes = decimalformat.parse(tokenizer.nextToken()).doubleValue();
      } else if (tokenizer.countTokens() == 3) {
        degrees = Integer.parseInt(tokenizer.nextToken());
        minutes = Integer.parseInt(tokenizer.nextToken());
        seconds = decimalformat.parse(tokenizer.nextToken()).doubleValue();
      } else {
        return parsed; // NaN
      }
      parsed = signum
          * (degrees + minutes / Constants.MINUTES_PER_DEGREE + seconds
              / Constants.SECONDS_PER_DEGREE);
    } catch (NumberFormatException e) {
      // e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return parsed;
  }
}
