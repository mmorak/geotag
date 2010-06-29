/**
 * Geotag
 * Copyright (C) 2007-2009 Andreas Schneider
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.fibs.geotag.exif.Exiftool;
import org.fibs.geotag.gui.MainWindow;
import org.fibs.geotag.gui.WhatNext;
import org.fibs.geotag.i18n.Messages;
import org.fibs.geotag.i18n.Translations;
import org.fibs.geotag.util.Constants;
import org.fibs.geotag.util.LocaleUtil;
import org.fibs.geotag.util.Util;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

/**
 * A class providing the entry point for the application.
 * 
 * @author Andreas Schneider
 */
public final class Geotag {

  /**
   * hide constructor.
   */
  private Geotag() {
    // hide constructor
  }

  /** Minimum memory in megabytes. */
  private static final int MIN_MEMORY = 127;

  /** the application name - should we ever feel the need to change it. */
  public static final String NAME = "Geotag"; //$NON-NLS-1$

  /** the applications web site. */
  public static final String WEBSITE = "http://geotag.sourceforge.net"; //$NON-NLS-1$

  /**
   * Normally the console output is redirected to a File, but for running the
   * program in an IDE we want to be able to see it.
   */
  private static boolean redirectConsole = true;

  /**
 * 
 */
private static void logJavaVersion() {
  System.out.println("java version "+System.getProperty("java.version"));
  System.out.print(System.getProperty("java.runtime.name")+" (");
  System.out.println(System.getProperty("java.runtime.version")+")");
  System.out.print(System.getProperty("java.vm.name")+" (");
  System.out.print(System.getProperty("java.vm.version")+" , ");
  System.out.println(System.getProperty("java.vm.info")+")");
  
}

/**
   * Application entry point.
   * 
   * @param args
   *          Command line arguments
   */
  public static void main(String[] args) {
    // check command line arguments
    // all of this is "developer only"
    // use Settings instead for end users
    for (String arg : args) {
      // each argument must be of form -key=value
      int equalsPos = arg.indexOf('='); // position of first equals sign
      if (arg.length() >= "-k=v".length() && // minimum -k=v //$NON-NLS-1$
          arg.charAt(0) == '-' && // first char is minus
          equalsPos >= 2 && // first value at least one character long
          equalsPos < arg.length() - 1) { // something follows the =
        // looks good so far, let's split the argument
        // start at 1, skipping the minus sign and going to just before
        // the equals sign
        String key = arg.substring(1, equalsPos);
        // start just after the equals sign
        String value = arg.substring(equalsPos + 1);
        // now see what to do with them
        if (key.equals("language") && value.length() > 0) { //$NON-NLS-1$
          Locale locale = LocaleUtil.localeFromString(value);
          Locale.setDefault(locale);
        } else if (key.equals("translate") && value.length() > 0) { //$NON-NLS-1$
          Locale locale = LocaleUtil.localeFromString(value);
          Locale.setDefault(locale);
          Translations.setTranslationLocale(locale);
        } else if (key.equals("console") && value.equals("yes")) { //$NON-NLS-1$ //$NON-NLS-2$
          redirectConsole = false;
        }
      }
    }
    // first of all we redirect the console output to a file
    if (redirectConsole) {
      File logFile = new File(System.getProperty("java.io.tmpdir") //$NON-NLS-1$
          + File.separator + NAME + ".log"); //$NON-NLS-1$
      try {
        PrintStream printStream = new PrintStream(logFile);
        System.setOut(printStream);
        System.setErr(printStream);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    // log java version
    logJavaVersion();
    try {
      UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
    } catch (Exception e) {
      e.printStackTrace();
    }
    // check that the program has enough memory available
    int maxMemory = (int) Math.round(Runtime.getRuntime().maxMemory()
        / Util.square(Constants.ONE_K));
    if (maxMemory < MIN_MEMORY) {
      String message = "<html><center>" //$NON-NLS-1$
          + String.format(Messages.getString("Geotag.MemoryWarningFormat") //$NON-NLS-1$
              + "<br><b><code>java -Xmx256M -jar geotag.jar</code></b>", //$NON-NLS-1$
              NAME, Integer.valueOf(maxMemory), WEBSITE) + "</center></html>"; //$NON-NLS-1$
      JOptionPane.showMessageDialog(null, message, Messages
          .getString("Geotag.NotEnoughMemory"), //$NON-NLS-1$
          JOptionPane.WARNING_MESSAGE);
    }
    // check for new version
    String latestVersion = Version.updateAvaiable();
    String message = "<html><center>" //$NON-NLS-1$
        + String.format(Messages.getString("Geotag.NewVersionFormat"), //$NON-NLS-1$
            latestVersion, NAME, WEBSITE) + "</center></html>"; //$NON-NLS-1$
    if (latestVersion != null) {
      JOptionPane.showMessageDialog(null, message, Messages
          .getString("Geotag.NewVersion"), //$NON-NLS-1$
          JOptionPane.INFORMATION_MESSAGE);
    }
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
        if (!Exiftool.isAvailable()) {
          WhatNext.helpWhatNext(mainWindow, mainWindow.getTableModel());

        }
      }
    });

  }
}
