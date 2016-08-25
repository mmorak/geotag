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

package org.fibs.geotag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.util.Proxies;

/**
 * A holder class for the version number and build date/time of the program. The
 * build date/time is stored in a resource file called build.info, which is
 * updated by an ant script.
 * 
 * @author Andreas Schneider
 * 
 */
public final class Version {

  /**
   * hide constructor.
   */
  private Version() {
    // hide constructor
  }

  /** The major version number. */
  public static final int MAJOR = 0;

  /** The minor version number. */
  public static final int MINOR = 99;

  /** The build date of the program. */
  public static final String BUILD_DATE;

  /** The build time of the program. */
  public static final String BUILD_TIME;

  /** the build number. */
  public static final String BUILD_NUMBER;

  // read the build info from the build.info resource
  static {
    Properties properties = new Properties();
    InputStream propertiesStream = Version.class.getClassLoader()
        .getResourceAsStream("build.info"); //$NON-NLS-1$
    if (propertiesStream != null) {
      try {
        properties.load(propertiesStream);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    BUILD_DATE = properties.getProperty("build.date", ""); //$NON-NLS-1$ //$NON-NLS-2$
    BUILD_TIME = properties.getProperty("build.time", ""); //$NON-NLS-1$ //$NON-NLS-2$
    String buildNumber = properties.getProperty("build.number", ""); //$NON-NLS-1$ //$NON-NLS-2$
    while (buildNumber.length() > 1 && buildNumber.charAt(0) == '0') {
      buildNumber = buildNumber.substring(1);
    }
    BUILD_NUMBER = buildNumber;
    try {
      if (propertiesStream != null) {
        propertiesStream.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** The version formatted as a string. */
  @SuppressWarnings( { "boxing", "nls" })
  public static final String VERSION = String.format("%d.%03d", MAJOR, MINOR);

  /**
   * Check a website to see if an update is available.
   * 
   * @return The new version number if available, null otherwise.
   */
  public static String updateAvaiable() {
    if (Settings.get(SETTING.CHECK_FOR_NEW_VERSION, true)) {
      try {
        URL versionFile = new URL(Geotag.WEBSITE + "/version.txt"); //$NON-NLS-1$
        URLConnection connection = versionFile.openConnection(Proxies
            .getProxy());
        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String versionLine = bufferedReader.readLine();
        bufferedReader.close();
        // no exception so far..
        if (versionLine != null && VERSION.compareTo(versionLine) < 0) {
          return versionLine;
        }
      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
}
