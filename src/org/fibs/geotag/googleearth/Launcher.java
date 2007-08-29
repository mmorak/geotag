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

package org.fibs.geotag.googleearth;

import java.io.File;
import java.lang.reflect.Method;
import javax.swing.JOptionPane;

import org.fibs.geotag.Messages;

/**
 * Bare Bones Googleearth Launch for Java<br>
 * Based on com.centerkey.utils.BareBonesBrowserLaunch
 * 
 * @author Dem Pilafian
 * @author Andreas Schneider
 * @version 1.5, December 10, 2005
 */
public class Launcher {

  /** Error message to display */
  private static final String errorMessage = Messages
      .getString("Launcher.ErrorAttemptingToLaunch"); //$NON-NLS-1$

  /**
   * Opens the specified KML file in Google Earth
   * 
   * @param file
   *          The KML file
   */
  public static void openKmlFile(File file) {
    String osName = System.getProperty("os.name"); //$NON-NLS-1$
    try {
      if (osName.startsWith("Mac OS")) { //$NON-NLS-1$
        Class<?> fileMgr = Class.forName("com.apple.eio.FileManager"); //$NON-NLS-1$
        Method openURL = fileMgr.getDeclaredMethod("openURL", //$NON-NLS-1$
            new Class[] { String.class });
        openURL.invoke(null, new Object[] { file.getPath() });
      } else if (osName.startsWith("Windows")) //$NON-NLS-1$
        Runtime.getRuntime().exec(
            "rundll32 url.dll,FileProtocolHandler " + file.getPath()); //$NON-NLS-1$
      else { // assume Unix or Linux
        String executable = "googleearth"; //$NON-NLS-1$
        if (Runtime.getRuntime().exec(new String[] { "which", executable }) //$NON-NLS-1$
            .waitFor() != 0) {
          throw new Exception(Messages.getString("Launcher.CouldNotFind")); //$NON-NLS-1$
        }
        Runtime.getRuntime().exec(new String[] { executable, file.getPath() });
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, errorMessage + ":\n" //$NON-NLS-1$
          + e.getLocalizedMessage());
    }
  }

}
