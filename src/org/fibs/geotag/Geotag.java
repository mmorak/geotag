/**
 * Geotag
 * Copyright (C) 2007 Andreas Schneider
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

import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.UIManager;

import org.fibs.geotag.ui.MainWindow;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

/**
 * A class providing the entry point of the application
 * 
 * @author Andreas Schneider
 */
public class Geotag {
  /** the application name - should we ever feel the need to change it */
  public static final String NAME = "Geotag"; //$NON-NLS-1$

  /**
   * Application entry point
   * 
   * @param args
   *          Command line arguments
   */
  public static final void main(String[] args) {
    // is there a two letter lower case argument?
    if (args.length == 1 && args[0].length() == 2
        && Character.isLowerCase(args[0].charAt(0))
        && Character.isLowerCase(args[0].charAt(1))) {
      // assume this is a language and use it to make a Locale
      Locale.setDefault(new Locale(args[0]));
    }
    if (args.length == 1 && args[0].indexOf('=') >= 0) {
      // argument of form a=b, use to update settings
      StringTokenizer tokenizer = new StringTokenizer("="); //$NON-NLS-1$
      if (tokenizer.countTokens() == 2) {
        String key = tokenizer.nextToken();
        // is the key a valid setting?
        if (Settings.get(key, null) != null) {
          // there is already a value for this key, hence the key is valid
          String value = tokenizer.nextToken();
          Settings.put(key, value);
          Settings.flush();
          System.out.println(Messages.getString("Geotag.Done")); //$NON-NLS-1$
        } else {
          System.out
              .println(Messages.getString("Geotag.InvalidKey") + ' ' + key); //$NON-NLS-1$
        }
        System.exit(0);
      }
    }
    try {
      UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
    } catch (Exception e) {
      e.printStackTrace();
    }
    MainWindow mainWindow = new MainWindow();
    mainWindow.setVisible(true);
  }
}
