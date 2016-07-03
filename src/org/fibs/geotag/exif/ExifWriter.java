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

package org.fibs.geotag.exif;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.fibs.geotag.util.CommandLineTokenizer;
import org.fibs.geotag.util.FileUtil;
import org.fibs.geotag.util.InputStreamGobbler;

/**
 * A class for writing EXIF information.
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
   * this does the work of writing the EXIF or XMP data by calling exiftool.
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
    } else if (xmpFile != null && fileType == FileTypes.CUSTOM_FILE_WITH_XMP) {
      // can't write to image file - must write to XMP file
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
   * Write the EXIF or XMP data.
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
    if (xmp) {
      arguments = xmpArguments(imageInfo);
    } else {
      arguments = exifArguments(imageInfo);
    }

    try {
      for (String argument : arguments) {
        argumentsWriter.write(argument + "\n"); //$NON-NLS-1$
      }

    } catch (UnsupportedEncodingException e1) {
      e1.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      argumentsWriter.flush();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    try {
      argumentsWriter.close();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    try {
      argumentsOutputStream.close();
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      Process process = processBuilder.redirectErrorStream(true).start();
      // now start a thread that reads the input stream of the process and
      // writes it to stdout
      ByteArrayOutputStream exiftoolOutput = new ByteArrayOutputStream();
      new InputStreamGobbler(process, exiftoolOutput).start();
      // we wait for the process to finish
      process.waitFor();
      System.out.println(exiftoolOutput.toString());
      success = !exiftoolOutput.toString().contains(
          "files weren't updated due to errors"); //$NON-NLS-1$
      exiftoolOutput.close();
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
      for (String argument : new CommandLineTokenizer(additionalArguments)
          .tokenize()) {
        arguments.add(argument);
      }
    }
    // the GPSVersion needs to be set to 2.2.0.0
    arguments.add("-GPSVersionID=2 2 0 0"); //$NON-NLS-1$
    // the latitude
    if (imageInfo.getGpsLatitude() != null) {
      double latitude = Double.parseDouble(imageInfo.getGpsLatitude());
      arguments.add("-GPSLatitudeRef=" + (latitude >= 0.0 ? 'N' : 'S')); //$NON-NLS-1$
      arguments.add("-GPSLatitude=" + Math.abs(latitude)); //$NON-NLS-1$
    }
    // the longitude
    if (imageInfo.getGpsLongitude() != null) {
      double longitude = Double.parseDouble(imageInfo.getGpsLongitude());
      arguments.add("-GPSLongitudeRef=" + (longitude >= 0.0 ? 'E' : 'W')); //$NON-NLS-1$
      arguments.add("-GPSLongitude=" + Math.abs(longitude)); //$NON-NLS-1$
    }
    // the altitude
    if (imageInfo.getGpsAltitude() != null) {
      double altitude = Double.parseDouble(imageInfo.getGpsAltitude());
      arguments.add("-GPSAltitudeRef=" + (altitude >= 0.0 ? '0' : '1')); //$NON-NLS-1$
      arguments.add("-GPSAltitude=" + Math.abs(altitude)); //$NON-NLS-1$
    }
    // the direction if we have one
    if (imageInfo.getGpsImgDirection() != null) {
      double direction = Double.parseDouble(imageInfo.getGpsImgDirection());
      arguments.add("-GPSImgDirection=" + direction); //$NON-NLS-1$
      arguments.add("-GPSImgDirectionRef=T"); //$NON-NLS-1$
    }
    // the map datum is always set to WGS-84
    arguments.add("-GPSMapDatum=WGS-84"); //$NON-NLS-1$
    // and finally we set the GPS date and time
    String gpsDatetime = imageInfo.getGpsDateTime();
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
    
    String userComment = imageInfo.getUserComment();
    if (userComment != null) {
      arguments.add("-UserComment="+userComment); //$NON-NLS-1$
    }
    // Now for IPTC location data

    String locationName = imageInfo.getLocationName();
    arguments
        .add("-IPTC:ContentLocationName=" + (locationName == null ? "" : locationName)); //$NON-NLS-1$ //$NON-NLS-2$
    // also write this to the Sub-Location
    arguments
        .add("-IPTC:Sub-Location=" + (locationName == null ? "" : locationName)); //$NON-NLS-1$ //$NON-NLS-2$

    String city = imageInfo.getCityName();
    arguments.add("-IPTC:City=" + (city == null ? "" : city)); //$NON-NLS-1$ //$NON-NLS-2$

    String province = imageInfo.getProvinceName();
    arguments.add("-IPTC:Province-State=" + (province == null ? "" : province)); //$NON-NLS-1$ //$NON-NLS-2$

    String country = imageInfo.getCountryName();
    arguments
        .add("-IPTC:Country-PrimaryLocationName=" + (country == null ? "" : country)); //$NON-NLS-1$ //$NON-NLS-2$

    // overwrite files directly instead of following default behavior to create
    // backups
    boolean createBackups = Settings.get(SETTING.CREATE_BACKUPS, true);
    if (!createBackups) {
      arguments.add("-overwrite_original"); //$NON-NLS-1$
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
      for (String argument : new CommandLineTokenizer(additionalArguments)
          .tokenize()) {
        arguments.add(argument);
      }
    }
    System.out.println();
    // the GPSVersion needs to be set to 2.2.0.0
    arguments.add("-XMP:GPSVersionID=2.2.0.0"); //$NON-NLS-1$
    // the latitude
    if (imageInfo.getGpsLatitude() != null) {
      double latitude = Double.parseDouble(imageInfo.getGpsLatitude());
      // No LatitudeRef or LongitudeRef in XMP - used signed values
      arguments.add("-XMP:GPSLatitude=" + latitude); //$NON-NLS-1$
    }
    // the longitude
    if (imageInfo.getGpsLongitude() != null) {
      double longitude = Double.parseDouble(imageInfo.getGpsLongitude());
      arguments.add("-XMP:GPSLongitude=" + longitude); //$NON-NLS-1$
    }
    // the altitude
    if (imageInfo.getGpsAltitude() != null) {
      double altitude = Double.parseDouble(imageInfo.getGpsAltitude());
      // Strangely the AltitudeRef is still used in XMP
      arguments.add("-XMP:GPSAltitudeRef=" + (altitude >= 0.0 ? '0' : '1')); //$NON-NLS-1$
      arguments.add("-XMP:GPSAltitude=" + Math.abs(altitude)); //$NON-NLS-1$
    }
    // the direction if we have one
    if (imageInfo.getGpsImgDirection() != null) {
      double direction = Double.parseDouble(imageInfo.getGpsImgDirection());
      arguments.add("-XMP:GPSImgDirection=" + direction); //$NON-NLS-1$
      arguments.add("-XMP:GPSImgDirectionRef=T"); //$NON-NLS-1$
    }
    // the map datum is always set to WGS-84
    arguments.add("-XMP:GPSMapDatum=WGS-84"); //$NON-NLS-1$
    // and finally we set the GPS date and time
    String gpsDatetime = imageInfo.getGpsDateTime();
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
        // The tag to write changed with exiftool 7.04
        String tag = "-XMP:GPSTimeStamp="; // up to 7.03 //$NON-NLS-1$
        if ("7.04".compareTo(Exiftool.getVersion()) <= 0) { //$NON-NLS-1$
          tag = "-XMP:GPSDateTime="; // since 7.04 //$NON-NLS-1$
        }
        // Add 'Z' as a timezone if none is specified
        String zone = ""; //$NON-NLS-1$
        if (!time.endsWith("Z") && !time.contains("-") && !time.contains("+")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          zone = "Z"; //$NON-NLS-1$
        }
        arguments.add(tag + '"' + date + ' ' + time + zone + '"');
      }
    }
    String location = imageInfo.getLocationName();
    arguments.add("-XMP:Location=" + (location == null ? "" : location)); //$NON-NLS-1$ //$NON-NLS-2$
    String city = imageInfo.getCityName();
    arguments.add("-XMP:City=" + (city == null ? "" : city)); //$NON-NLS-1$ //$NON-NLS-2$
    String state = imageInfo.getProvinceName();
    arguments.add("-XMP:State=" + (state == null ? "" : state)); //$NON-NLS-1$ //$NON-NLS-2$
    String country = imageInfo.getCountryName();
    arguments.add("-XMP:Country=" + (country == null ? "" : country)); //$NON-NLS-1$ //$NON-NLS-2$
    arguments.add(xmpFileName(imageInfo));
    for (String string : arguments) {
      System.out.print(string + ' ');
    }
    System.out.println();
    return arguments;
  }

}
