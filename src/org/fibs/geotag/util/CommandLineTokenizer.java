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

import java.util.ArrayList;
import java.util.List;

/**
 * A class for parsing command line arguments for
 * external programs. This is not supposed to be
 * a fully featured command line parser, just one that
 * does a bit better than a StringTokenizer
 * 
 * @author andreas
 *
 */
public class CommandLineTokenizer {

  /**
   * A tiny state machine with these states
   */
  private enum State {
    /** The normal state - read until next space is found */
    NORMAL,
    /** Encountered a single quote ('), now waiting for the closing one */
    SINGLE_QUOTE, 
    /** Encountered a double quote ("), now waiting for the closing one */
    DOUBLE_QUOTE
  }

  /** The command line string to be tokenized */
  private String commandLine;

  /**
   * @param commandLine The command line string to be tokenized
   */
  public CommandLineTokenizer(String commandLine) {
    this.commandLine = commandLine;
  }

  /**
   * @return The list of tokens recognised.
   */
  public List<String> tokenize() {
    State state = State.NORMAL;
    List<String> result = new ArrayList<String>();
    StringBuilder token = new StringBuilder();
    // Look at each character
    for (int index = 0; index < commandLine.length(); index++) {
      char character = commandLine.charAt(index);
      switch (state) {
        case NORMAL:
          switch (character) {
            case '\'':
              state = State.SINGLE_QUOTE;
              break;
            case '"':
              state = State.DOUBLE_QUOTE;
              break;
            case ' ':
              if (token.length() > 0) {
                result.add(token.toString());
                token = new StringBuilder();
              }
              break;
            default:
              token.append(character);
              break;
          }
          break;
        case SINGLE_QUOTE:
          if (character == '\'') {
            state = State.NORMAL;
          } else {
            token.append(character);
          }
          break;
        case DOUBLE_QUOTE:
          if (character == '"') {
            state = State.NORMAL;
          } else {
            token.append(character);
          }
          break;
        default:
          // nothing else
          break;
      }
    }
    if (token.length() > 0) {
      result.add(token.toString());
    }
    return result;
  }
}
