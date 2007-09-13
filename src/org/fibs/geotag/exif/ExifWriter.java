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

package org.fibs.geotag.exif;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.image.ImageFileFilter;
import org.fibs.geotag.util.FileUtil;
import org.fibs.geotag.util.InputStreamGobbler;

/**
 * A class for writing EXIF information
 * 
 * @author Andreas Schneider
 * 
 */
public class ExifWriter {
  /**
   * this does the work of writing the EXIF data by calling exiftool
   * 
   * @param imageInfo
   * @return True if the exiftool process was called successfully
   */
  public boolean write(ImageInfo imageInfo) {
    // check for existence of XMP sidecar file
    File imageFile = new File(imageInfo.getPath());
    File xmpFile = null;
    String xmpFileName = FileUtil.replaceExtension(imageInfo.getPath(), "xmp"); //$NON-NLS-1$
    if (xmpFileName != null) {
      xmpFile = new File(xmpFileName);
    }
    boolean xmpFilesOnly = Settings.get(SETTING.XMP_FILES_ONLY, false);
    if (xmpFilesOnly && xmpFile != null) {
      // Must write XMP
      return writeXMP(imageInfo, xmpFile);
    } else if (xmpFile != null && ImageFileFilter.isReadOnlyRawFile(imageFile)) {
      // can't write to image file, must write XMP
      return writeXMP(imageInfo, xmpFile);
    } else if (xmpFile != null && xmpFile.exists()) {
      // Can write XMP
      return writeXMP(imageInfo, xmpFile);
    } else if (!xmpFilesOnly && !ImageFileFilter.isReadOnlyRawFile(imageFile)) {
      // Won't write XMP;
      return writeEXIF(imageInfo);
    }
    System.err.println("Won't write to " + imageInfo.getPath()); //$NON-NLS-1$
    return false;
  }

  /**
   * Write normal EXIF data to the image itself
   * 
   * @param imageInfo
   *          The image to write to
   * @return True if successful
   */
  private boolean writeEXIF(ImageInfo imageInfo) {
    boolean success = false;
    // first we build the command
    List<String> command = new ArrayList<String>();
    // the command name
    String exiftool = Settings.get(SETTING.EXIFTOOL_PATH, "exiftool"); //$NON-NLS-1$
    command.add(exiftool);
    // option -n: -n write values as numbers instead of words
    command.add("-n"); //$NON-NLS-1$
    // option -s: use tag names instead of descriptions
    command.add("-s"); //$NON-NLS-1$
    // there might be additional exiftool arguments required
    String additionalArguments = Settings.get(SETTING.EXIFTOOL_ARGUMENTS, ""); //$NON-NLS-1$
    if (additionalArguments.length() > 0) {
      StringTokenizer tokenizer = new StringTokenizer(additionalArguments);
      while (tokenizer.hasMoreTokens()) {
        String argument = tokenizer.nextToken();
        command.add(argument);
      }
    }
    for (String string : command) {
      System.out.print(string + ' ');
    }
    System.out.println();
    // the GPSVersion needs to be set to 2.2.0.0
    command.add("-GPSVersionID=2 2 0 0"); //$NON-NLS-1$
    // the latitude
    double latitude = Double.parseDouble(imageInfo.getGPSLatitude());
    command.add("-GPSLatitudeRef=" + (latitude >= 0.0 ? 'N' : 'S')); //$NON-NLS-1$
    command.add("-GPSLatitude=" + Math.abs(latitude)); //$NON-NLS-1$
    // the longitude
    double longitude = Double.parseDouble(imageInfo.getGPSLongitude());
    command.add("-GPSLongitudeRef=" + (longitude >= 0.0 ? 'E' : 'W')); //$NON-NLS-1$
    command.add("-GPSLongitude=" + Math.abs(longitude)); //$NON-NLS-1$
    // the altitude
    double altitude = Double.parseDouble(imageInfo.getGPSAltitude());
    command.add("-GPSAltitudeRef=" + (altitude >= 0.0 ? '0' : '1')); //$NON-NLS-1$
    command.add("-GPSAltitude=" + Math.abs(altitude)); //$NON-NLS-1$
    // the direction if we have one
    if (imageInfo.getGPSImgDirection()!=null) {
      double direction = Double.parseDouble(imageInfo.getGPSImgDirection());
      command.add("-GPSImgDirection="+direction); //$NON-NLS-1$
      command.add("-GPSImgDirectionRef=T"); //$NON-NLS-1$
    }
    // the map datum is always set to WGS-84
    command.add("-GPSMapDatum=WGS-84"); //$NON-NLS-1$
    // and finally we set the GPS date and time
    String gpsDatetime = imageInfo.getGPSDateTime();
    if (gpsDatetime != null) {
      // split the time into the date and the time
      StringTokenizer tokenizer = new StringTokenizer(gpsDatetime); // default
      // delimiters
      // are OK
      // here
      if (tokenizer.countTokens() == 2) {
        // that's what we expect
        String date = tokenizer.nextToken();
        String time = tokenizer.nextToken();
        command.add("-GPSDateStamp=" + date); //$NON-NLS-1$
        command.add("-GPSTimeStamp=" + time); //$NON-NLS-1$
      }
    }
    command.add(imageInfo.getPath());
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      Process process = processBuilder.redirectErrorStream(true).start();
      // now start a thread that reads the input stream of the process and
      // writes it to stdout
      final InputStream inputStream = process.getInputStream();
      new InputStreamGobbler(inputStream, System.out).start();
      // we wait for the process to finish
      process.waitFor();
      success = true;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return success;
  }

  /**
   * Write XMP info to XMP sidecar file
   * 
   * @param imageInfo
   *          The image supplying the data
   * @param file
   *          The sidecar file to write to
   * @return True if successful
   */
  private boolean writeXMP(ImageInfo imageInfo, File file) {
    boolean success = false;
    // first we build the command
    List<String> command = new ArrayList<String>();
    // the command name
    String exiftool = Settings.get(SETTING.EXIFTOOL_PATH, "exiftool"); //$NON-NLS-1$
    command.add(exiftool);
    // option -n: -n write values as numbers instead of words
    command.add("-n"); //$NON-NLS-1$
    // option -s: use tag names instead of descriptions
    command.add("-s"); //$NON-NLS-1$
    // there might be additional exiftool arguments required
    String additionalArguments = Settings.get(SETTING.EXIFTOOL_ARGUMENTS, ""); //$NON-NLS-1$
    if (additionalArguments.length() > 0) {
      StringTokenizer tokenizer = new StringTokenizer(additionalArguments);
      while (tokenizer.hasMoreTokens()) {
        String argument = tokenizer.nextToken();
        command.add(argument);
      }
    }
    for (String string : command) {
      System.out.print(string + ' ');
    }
    System.out.println();
    // the GPSVersion needs to be set to 2.2.0.0
    command.add("-XMP:GPSVersionID=2.2.0.0"); //$NON-NLS-1$
    // the latitude
    double latitude = Double.parseDouble(imageInfo.getGPSLatitude());
    // No LatitudeRef or LongitudeRef in XMP - used signed values
    command.add("-XMP:GPSLatitude=" + latitude); //$NON-NLS-1$
    // the longitude
    double longitude = Double.parseDouble(imageInfo.getGPSLongitude());
    command.add("-XMP:GPSLongitude=" + longitude); //$NON-NLS-1$
    // the altitude
    double altitude = Double.parseDouble(imageInfo.getGPSAltitude());
    // Strangely the AltitudeRef is still used in XMP
    command.add("-XMP:GPSAltitudeRef=" + (altitude >= 0.0 ? '0' : '1')); //$NON-NLS-1$
    command.add("-XMP:GPSAltitude=" + Math.abs(altitude)); //$NON-NLS-1$
    // the map datum is always set to WGS-84
    command.add("-XMP:GPSMapDatum=WGS-84"); //$NON-NLS-1$
    // and finally we set the GPS date and time
    String gpsDatetime = imageInfo.getGPSDateTime();
    if (gpsDatetime != null) {
      // split the time into the date and the time
      StringTokenizer tokenizer = new StringTokenizer(gpsDatetime); // default
      // delimiters
      // are OK
      // here
      if (tokenizer.countTokens() == 2) {
        // that's what we expect
        String date = tokenizer.nextToken();
        String time = tokenizer.nextToken();
        command.add("-XMP:GPSTimeStamp=" + '"' + date + ' ' + time + 'Z' + '"'); //$NON-NLS-1$
      }
    }
    command.add(file.getPath());
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      Process process = processBuilder.redirectErrorStream(true).start();
      // now start a thread that reads the input stream of the process and
      // writes it to stdout
      final InputStream inputStream = process.getInputStream();
      new InputStreamGobbler(inputStream, System.out).start();
      // we wait for the process to finish
      process.waitFor();
      success = true;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return success;
  }

}
