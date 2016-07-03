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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFileChooser;

/**
 * I needed a class to add SuppressWarnings annotations to Java files included
 * from external libraries. This is of course nasty, but unfortunately
 * necessary. It could be put in a separate project, but it's not that good :-)
 * Use at your own risk.. if it messes up your source code don't come crying!
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("nls")
// This is for developers only - no need to translate
public final class WarningSuppressor {
  /**
   * hide constructor.
   */
  private WarningSuppressor() {
    // hide constructor
  }

  /**
   * Lines starting with these strings should be preceded by the annotation. This
   * should of course be more clever with regular expressions or a full blown
   * Java grammar, but that would be slight overkill ;-)
   */
  private static final String[] PATTERNS_TO_SUPPRESS = { "public class",
      "public enum", "public interface", "public abstract class",
      "public final class", "final class", "class" };

  /**
   * @param args -
   *          no args
   */
  public static void main(String[] args) {
    // Choose the source directory to be messed with
    JFileChooser chooser = new JFileChooser();
    // directories only - don't show files
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    // only one directory allowed!
    chooser.setMultiSelectionEnabled(false);
    // Open file chooser and wait for selection
    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      // retrieve directory from chooser
      File directory = chooser.getSelectedFile();
      // and process directory and all sub-directories and java files
      processDirectory(directory);
    }
  }

  /**
   * Process all files in this directory and sub-directories.
   * 
   * @param directory
   */
  private static void processDirectory(File directory) {
    // get all java files and sub-directories
    File[] files = directory.listFiles(new JavaFileFilter());
    // work your way through them
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      if (file.isDirectory()) {
        // recurse down to sub-directory
        processDirectory(file);
      } else {
        // convert the file
        processFile(file);
      }
    }
  }

  /**
   * Process one file - this currently always overwrites the original.
   * 
   * @param file
   */
  private static void processFile(File file) {
    // make up some temporary file name
    File tmpFile = new File(file.getPath() + ".tmp");
    // one buffered reader to read the source file line by line
    BufferedReader bufferedReader = null;
    // and a print stream to write the (possibly) changed file
    PrintStream printStream = null;
    try {
      bufferedReader = new BufferedReader(new FileReader(file));
      printStream = new PrintStream(tmpFile);
      // we try to make sure that running this on the same file more than
      // once doesn't generate multiple annotaions for the same class
      boolean alreadySuppressed = false;
      String line = null;
      while ((line = bufferedReader.readLine()) != null) {
        if (line.startsWith("@SuppressWarnings(\"all\")")) {
          // There is already an annotation.. don't annotate next
          // class/interface/enum declaration
          alreadySuppressed = true;
        }
        // see if the line matches one of the patterns
        for (int i = 0; i < PATTERNS_TO_SUPPRESS.length; i++) {
          String pattern = PATTERNS_TO_SUPPRESS[i];
          if (line.startsWith(pattern)) {
            // It does indeed match
            if (!alreadySuppressed) {
              // and does need annotating
              printStream.println("@SuppressWarnings(\"all\")");
              System.out.println("Suppressed warnings in " + file.getPath());
            }
            // there might be more than one class in the file that
            // needs annotating...
            alreadySuppressed = false;
          }
        }
        // write the original line
        printStream.println(line);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (printStream != null) {
        printStream.close();
      }
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    // rename the temporary file to the original file name
    // This might or might not work for Windows,
    // I don't care.. it works for Linux
    if (tmpFile.renameTo(file)) {
      System.out.println("Done. " + file.getName());
    } else {
      System.out.println("Couldn't rename " + tmpFile.getName());
    }
  }

}
