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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JFileChooser;

/**
 * A class to look at our source files and check if the license comment is
 * correct
 * 
 * @author Andreas Schneider
 * 
 */
public final class MissingLicenseFinder {

  /** Overwrite files if true */
  private static boolean OVERWRITE = true;
  
  /** Number of files checked. */
  private static int filesChecked = 0;

  /** Number of files that failed the test. */
  private static int filesFailed = 0;
  
  /** Number of files corrected */
  private static int filesCorrected = 0;

  /**
   * hide constructor.
   */
  private MissingLicenseFinder() {
    // hide constructor
  }

  /** The current year */
  private static final int YEAR = new GregorianCalendar().get(Calendar.YEAR);
  
  /** The lines we expect at the beginning of each file. */
  private static final String[] CORRECT_LINES = {
      "/**", //$NON-NLS-1$
      " * Geotag", //$NON-NLS-1$
      " * Copyright (C) 2007-"+YEAR+" Andreas Schneider", //$NON-NLS-1$ //$NON-NLS-2$
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
   *          OVERWRITE as first argument sets overwrite mode
   */
  public static void main(String[] args) {
    if (args.length == 1 && "OVERWRITE".equals(args[0])) { //$NON-NLS-1$
      OVERWRITE = true;
    }
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
      System.out.println("Failures: " + filesFailed + "/" + filesChecked); //$NON-NLS-1$//$NON-NLS-2$
      System.out.println("Corrected: "+filesCorrected + "/" +filesFailed); //$NON-NLS-1$ //$NON-NLS-2$
      if (filesFailed > 0 && filesCorrected != filesFailed) {
        System.out.println("To fix all failed files re-run with OVERWRITE set to true"); //$NON-NLS-1$
      } else {
        System.out.println("All is well"); //$NON-NLS-1$
      }
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
        try {
          processFile(file);
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    }
  }

  /**
   * Process one file
   * 
   * @param file
   * @throws IOException 
   */
  private static void processFile(File file) throws IOException {
    boolean failed = false;
    File tempFile = File.createTempFile(file.getName(), null);
    tempFile.deleteOnExit();
    BufferedWriter tempFileWriter = new BufferedWriter(new FileWriter(tempFile));
    filesChecked++;
    // see if the file starts with the right lines GNU license
    BufferedReader bufferedReader = null;
    String lineFromFile = null;
    try {
      bufferedReader = new BufferedReader(new FileReader(file));
      for (int i = 0; i < CORRECT_LINES.length; i++) {
        tempFileWriter.write(CORRECT_LINES[i]);
        tempFileWriter.newLine();
        lineFromFile = bufferedReader.readLine();
        if (!CORRECT_LINES[i].equals(lineFromFile)) {
          filesFailed++;
          if (!failed) {
            System.out.println(file.getName()
                + " line " + (i + 1) + " != '" + CORRECT_LINES[i] + '\''); //$NON-NLS-1$ //$NON-NLS-2$
          }
          failed= true;
        }
      }
      // now read the rest of the file and append to the temp file
      while ((lineFromFile = bufferedReader.readLine())!=null) {
        tempFileWriter.write(lineFromFile);
        tempFileWriter.newLine();
      }
      bufferedReader.close();
      bufferedReader = null;
      tempFileWriter.close();
      if (failed && OVERWRITE) {
        overwrite(tempFile, file);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
        try {
          if (bufferedReader != null) {
          bufferedReader.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
  }
  
  /**
   * Copy contents of one file to another
   * @param from One file
   * @param to Another file
   */
  private static void overwrite(File from, File to) {
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(from));
      BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(to));
      String line = null;
      while ((line = bufferedReader.readLine())!= null) {
        bufferedWriter.write(line);
        bufferedWriter.newLine();
      }
      bufferedReader.close();
      bufferedWriter.close();
      filesCorrected++;
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
