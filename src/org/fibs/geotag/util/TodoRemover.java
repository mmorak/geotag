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
 * I needed a class to remove 'todo' comments from Java files included from
 * external libraries. This is of course nasty, but unfortunately necessary. It
 * could be put in a separate project, but it's not that good :-) Use at your
 * own risk.. if it messes up your source code don't come crying!
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("nls")
// This is for developers only - no need to translate
public final class TodoRemover {
  /**
   * hide constructor.
   */
  private TodoRemover() {
    // hide constructor
  }
  /**
   * @param args -
   *          no args
   */
  public static void main(String[] args) {
    // Choose the source directory to be messed with
    JFileChooser chooser = new JFileChooser();
    // guess the default directory. If run from Eclipse "user.dir" points at the
    // project folder
    String rootDirectoryName = System.getProperty("user.dir"); //$NON-NLS-1$
    rootDirectoryName += "/src/com"; //$NON-NLS-1$
    File rootDirectory = new File(rootDirectoryName);
    if (rootDirectory.exists() && rootDirectory.isDirectory()) {
      chooser.setSelectedFile(rootDirectory);
    }
    System.out.println(rootDirectoryName);
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
   * process all files in this directory and sub-directories Files containing
   * org/fibs in their path are ignored for safety.
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
        if (file.getPath().indexOf("org/fibs") == -1) {
          processFile(file);
        }
      }
    }
  }

  /**
   * Process one file - this currently always overwrites the original.
   * 
   * @param file
   */
  private static void processFile(File file) {
    System.out.println(file.getPath());
    // make up some temporary file name
    File tmpFile = new File(file.getPath() + ".tmp");
    // one buffered reader to read the source file line by line
    BufferedReader bufferedReader = null;
    // and a print stream to write the (possibly) changed file
    PrintStream printStream = null;
    try {
      bufferedReader = new BufferedReader(new FileReader(file));
      printStream = new PrintStream(tmpFile);
      String line = null;
      while ((line = bufferedReader.readLine()) != null) {
        if (line.indexOf("TODO") != -1) {
          line = line.replaceAll("TODO", "SEP"); // Someone else's problem
          System.out.println(line);
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
