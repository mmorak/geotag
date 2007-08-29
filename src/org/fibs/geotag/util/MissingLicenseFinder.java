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

package org.fibs.geotag.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;

/**
 * A class to look at our source files and check if the license comment is
 * correct
 * 
 * @author Andreas Schneider
 * 
 */
public class MissingLicenseFinder {

  /** The lines we expect at the beginning of each file */
  public static final String[] correctLines = {
      "/**", //$NON-NLS-1$
      " * Geotag", //$NON-NLS-1$
      " * Copyright (C) 2007 Andreas Schneider", //$NON-NLS-1$
      " *", //$NON-NLS-1$
      " * This program is free software; you can redistribute it and/or", //$NON-NLS-1$
      " * modify it under the terms of the GNU General Public License", //$NON-NLS-1$
      " * as published by the Free Software Foundation; either version 2", //$NON-NLS-1$
      " * of the License, or (at your option) any later version.", //$NON-NLS-1$
      " *", //$NON-NLS-1$
      " * This program is distributed in the hope that it will be useful,", //$NON-NLS-1$
      " * but WITHOUT ANY WARRANTY; without even the implied warranty of", //$NON-NLS-1$
      " * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the", //$NON-NLS-1$
      " * GNU General Public License for more details.", //$NON-NLS-1$
      " *", //$NON-NLS-1$
      " * You should have received a copy of the GNU General Public License", //$NON-NLS-1$
      " * along with this program.  If not, see <http://www.gnu.org/licenses/>.", //$NON-NLS-1$
      " */" //$NON-NLS-1$
  };

  /**
   * @param args -
   *          no args
   */
  public static final void main(String[] args) {
    // Choose the source directory to be examined
    JFileChooser chooser = new JFileChooser();
    // guess the default directory. If run from Eclipse "user.dir" points at the
    // project folder
    String rootDirectoryName = System.getProperty("user.dir"); //$NON-NLS-1$
    rootDirectoryName += "/src/org/fibs"; //$NON-NLS-1$
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
   * process all files in this directory and sub-directories
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
   * Process one file - this currently always overwrites the original
   * 
   * @param file
   */
  private static void processFile(File file) {
    // see if the file starts with the right lines GNU license
    BufferedReader bufferedReader = null;
    try {
      bufferedReader = new BufferedReader(new FileReader(file));
      for (int i = 0; i < correctLines.length; i++) {
        String lineFromFile = bufferedReader.readLine();
        if (!lineFromFile.equals(correctLines[i])) {
          System.out.println(file.getName()
              + " line " + (i + 1) + " != '" + correctLines[i] + '\''); //$NON-NLS-1$ //$NON-NLS-2$
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
