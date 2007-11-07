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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.image.FileTypes;
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
   * @param imageInfo
   * @return the XMP file name for a given image
   */
  private String xmpFileName(ImageInfo imageInfo) {
    return FileUtil.replaceExtension(imageInfo.getPath(), "xmp"); //$NON-NLS-1$
  }

  /**
   * this does the work of writing the EXIF or XMP data by calling exiftool
   * 
   * @param imageInfo
   * @return True if the exiftool process was called successfully
   */
  public boolean write(ImageInfo imageInfo) {
    // check for existence of XMP sidecar file
    File imageFile = new File(imageInfo.getPath());
    FileTypes fileType = FileTypes.fileType(imageFile);
    File xmpFile = null;
    String xmpFileName = FileUtil.replaceExtension(imageInfo.getPath(), "xmp"); //$NON-NLS-1$
    if (xmpFileName != null) {
      xmpFile = new File(xmpFileName);
    }
    boolean xmpFilesOnly = Settings.get(SETTING.XMP_FILES_ONLY, false);
    if (xmpFilesOnly && xmpFile != null) {
      // Must write XMP
      return write(imageInfo, true);
    } else if (xmpFile != null && fileType == FileTypes.RAW_READ_ONLY) {
      // can't write to image file, must write XMP
      return write(imageInfo, true);
    } else if (xmpFile != null && xmpFile.exists()) {
      // Can write XMP
      return write(imageInfo, true);
    } else if (!xmpFilesOnly && fileType != FileTypes.RAW_READ_ONLY) {
      // Won't write XMP;
      return write(imageInfo, false);
    }
    System.err.println("Won't write to " + imageInfo.getPath()); //$NON-NLS-1$
    return false;
  }

  /**
   * Write the EXIF or XMP data
   * 
   * @param imageInfo
   * @param xmp
   *          Determines if data is written to image or XMP file
   * @return True if the exiftool process was called successfully
   */
  private boolean write(ImageInfo imageInfo, boolean xmp) {
    boolean success = false;
    // create a temporary file for the arguments
    File argumentsFile = null;
    OutputStream argumentsOutputStream = null;
    Writer argumentsWriter = null;
    try {
      argumentsFile = File.createTempFile("args", null); //$NON-NLS-1$
      argumentsFile.deleteOnExit();
      argumentsOutputStream = new FileOutputStream(argumentsFile);
      argumentsWriter = new OutputStreamWriter(argumentsOutputStream, "UTF-8"); //$NON-NLS-1$
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    // first we build the command
    List<String> command = new ArrayList<String>();
    // the command name
    String exiftool = Settings.get(SETTING.EXIFTOOL_PATH, "exiftool"); //$NON-NLS-1$
    command.add(exiftool);
    command.add("-@"); //$NON-NLS-1$
    command.add(argumentsFile.getPath());

    List<String> arguments = null;
    if (xmp == true) {
      arguments = xmpArguments(imageInfo);
    } else {
      arguments = exifArguments(imageInfo);
    }

    try {
      for (String argument : arguments) {
        argumentsWriter.write(argument + "\n"); //$NON-NLS-1$
      }
      argumentsWriter.flush();
      argumentsWriter.close();
      argumentsOutputStream.close();
    } catch (UnsupportedEncodingException e1) {
      e1.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

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
   * @param imageInfo
   *          The image to write to
   * @return the exiftool arguments to write data to an image
   */
  private List<String> exifArguments(ImageInfo imageInfo) {

    // and the arguments to be read from the arguments file
    List<String> arguments = new ArrayList<String>();

    // option -n: -n write values as numbers instead of words
    arguments.add("-n"); //$NON-NLS-1$
    // option -s: use tag names instead of descriptions
    arguments.add("-s"); //$NON-NLS-1$
    // there might be additional exiftool arguments required
    String additionalArguments = Settings.get(SETTING.EXIFTOOL_ARGUMENTS, ""); //$NON-NLS-1$
    if (additionalArguments.length() > 0) {
      StringTokenizer tokenizer = new StringTokenizer(additionalArguments);
      while (tokenizer.hasMoreTokens()) {
        String argument = tokenizer.nextToken();
        arguments.add(argument);
      }
    }
    for (String string : arguments) {
      System.out.print(string + ' ');
    }
    // the GPSVersion needs to be set to 2.2.0.0
    arguments.add("-GPSVersionID=2 2 0 0"); //$NON-NLS-1$
    // the latitude
    if (imageInfo.getGPSLatitude() != null) {
      double latitude = Double.parseDouble(imageInfo.getGPSLatitude());
      arguments.add("-GPSLatitudeRef=" + (latitude >= 0.0 ? 'N' : 'S')); //$NON-NLS-1$
      arguments.add("-GPSLatitude=" + Math.abs(latitude)); //$NON-NLS-1$
    }
    // the longitude
    if (imageInfo.getGPSLongitude() != null) {
      double longitude = Double.parseDouble(imageInfo.getGPSLongitude());
      arguments.add("-GPSLongitudeRef=" + (longitude >= 0.0 ? 'E' : 'W')); //$NON-NLS-1$
      arguments.add("-GPSLongitude=" + Math.abs(longitude)); //$NON-NLS-1$
    }
    // the altitude
    if (imageInfo.getGPSAltitude() != null) {
      double altitude = Double.parseDouble(imageInfo.getGPSAltitude());
      arguments.add("-GPSAltitudeRef=" + (altitude >= 0.0 ? '0' : '1')); //$NON-NLS-1$
      arguments.add("-GPSAltitude=" + Math.abs(altitude)); //$NON-NLS-1$
    }
    // the direction if we have one
    if (imageInfo.getGPSImgDirection() != null) {
      double direction = Double.parseDouble(imageInfo.getGPSImgDirection());
      arguments.add("-GPSImgDirection=" + direction); //$NON-NLS-1$
      arguments.add("-GPSImgDirectionRef=T"); //$NON-NLS-1$
    }
    // the map datum is always set to WGS-84
    arguments.add("-GPSMapDatum=WGS-84"); //$NON-NLS-1$
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
        arguments.add("-GPSDateStamp=" + date); //$NON-NLS-1$
        arguments.add("-GPSTimeStamp=" + time); //$NON-NLS-1$
      }
    }
    // Now for IPTC location data
    String locationName = imageInfo.getLocationName();
    if (locationName != null && locationName.length() > 0) {
      arguments.add("-City=" + locationName); //$NON-NLS-1$
    }
    String country = imageInfo.getCountryName();
    if (country != null && country.length() > 0) {
      arguments.add("-Country-PrimaryLocationName=" + country); //$NON-NLS-1$
    }
    String province = imageInfo.getProvinceName();
    if (province != null && province.length() > 0) {
      arguments.add("-Province-State=" + province); //$NON-NLS-1$
    }
    for (String string : arguments) {
      System.out.print(string + ' ');
    }
    System.out.println();
    arguments.add(imageInfo.getPath());

    return arguments;
  }

  /**
   * @param imageInfo
   *          The image supplying the data
   * @return the exiftool arguments to write data to an XMP file
   */
  private List<String> xmpArguments(ImageInfo imageInfo) {
    // first we build the command
    List<String> arguments = new ArrayList<String>();
    // option -n: -n write values as numbers instead of words
    arguments.add("-n"); //$NON-NLS-1$
    // option -s: use tag names instead of descriptions
    arguments.add("-s"); //$NON-NLS-1$
    // there might be additional exiftool arguments required
    String additionalArguments = Settings.get(SETTING.EXIFTOOL_ARGUMENTS, ""); //$NON-NLS-1$
    if (additionalArguments.length() > 0) {
      StringTokenizer tokenizer = new StringTokenizer(additionalArguments);
      while (tokenizer.hasMoreTokens()) {
        String argument = tokenizer.nextToken();
        arguments.add(argument);
      }
    }
    System.out.println();
    // the GPSVersion needs to be set to 2.2.0.0
    arguments.add("-XMP:GPSVersionID=2.2.0.0"); //$NON-NLS-1$
    // the latitude
    if (imageInfo.getGPSLatitude() != null) {
      double latitude = Double.parseDouble(imageInfo.getGPSLatitude());
      // No LatitudeRef or LongitudeRef in XMP - used signed values
      arguments.add("-XMP:GPSLatitude=" + latitude); //$NON-NLS-1$
    }
    // the longitude
    if (imageInfo.getGPSLongitude() != null) {
      double longitude = Double.parseDouble(imageInfo.getGPSLongitude());
      arguments.add("-XMP:GPSLongitude=" + longitude); //$NON-NLS-1$
    }
    // the altitude
    if (imageInfo.getGPSAltitude() != null) {
      double altitude = Double.parseDouble(imageInfo.getGPSAltitude());
      // Strangely the AltitudeRef is still used in XMP
      arguments.add("-XMP:GPSAltitudeRef=" + (altitude >= 0.0 ? '0' : '1')); //$NON-NLS-1$
      arguments.add("-XMP:GPSAltitude=" + Math.abs(altitude)); //$NON-NLS-1$
    }
    // the map datum is always set to WGS-84
    arguments.add("-XMP:GPSMapDatum=WGS-84"); //$NON-NLS-1$
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
        arguments
            .add("-XMP:GPSTimeStamp=" + '"' + date + ' ' + time + 'Z' + '"'); //$NON-NLS-1$
      }
    }
    String location = imageInfo.getLocationName();
    if (location != null && location.length() > 0) {
      arguments.add("-XMP:City=" + location); //$NON-NLS-1$
    }
    String state = imageInfo.getProvinceName();
    if (state != null && state.length() > 0) {
      arguments.add("-XMP:State=" + state); //$NON-NLS-1$
    }
    String country = imageInfo.getCountryName();
    if (country != null && country.length() > 0) {
      arguments.add("-XMP:Country=" + country); //$NON-NLS-1$
    }
    arguments.add(xmpFileName(imageInfo));
    return arguments;
  }

}
