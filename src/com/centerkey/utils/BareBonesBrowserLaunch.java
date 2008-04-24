
package com.centerkey.utils;

import java.lang.reflect.Method;
import javax.swing.JOptionPane;

import org.fibs.geotag.Messages;

/**
 * Bare Bones Browser Launch for Java<br>
 * Utility class to open a web page from a Swing application in the user's
 * default browser.<br>
 * Supports: Mac OS X, GNU/Linux, Unix, Windows XP<br>
 * Example Usage:<code><br> &nbsp; &nbsp;
 *    String url = "http://www.google.com/";<br> &nbsp; &nbsp;
 *    BareBonesBrowserLaunch.openURL(url);<br></code> Latest Version: <a
 * href="http://www.centerkey.com/java/browser/">http://www.centerkey.com/java/browser</a><br>
 * 
 * @author Dem Pilafian<br>
 *         Public Domain Software -- Free to Use as You Like
 * 
 * @version 1.5, December 10, 2005
 * 
 * @author Andreas Schneider - just a cleanup to avoid warnings and match my
 *         coding style a bit better
 */
public class BareBonesBrowserLaunch {

  /***/
  private static final String errorMessage = Messages
      .getString("BareBonesBrowserLaunch.ErrorLauchingBrowser"); //$NON-NLS-1$

  /**
   * Opens the specified web page in a web browser
   * 
   * @param specificBrowser
   *          Used to explicitly set the browser
   * @param url
   *          An absolute URL of a web page (ex: "http://www.google.com/")
   */
  public static void openURL(String specificBrowser, String url) {
    String osName = System.getProperty("os.name"); //$NON-NLS-1$
    try {
      if (osName.startsWith("Mac OS")) { //$NON-NLS-1$
        Class<?> fileMgr = Class.forName("com.apple.eio.FileManager"); //$NON-NLS-1$
        Method openURL = fileMgr.getDeclaredMethod("openURL", //$NON-NLS-1$
            new Class[] { String.class });
        openURL.invoke(null, new Object[] { url });
      } else if (osName.startsWith("Windows")) { //$NON-NLS-1$
        if (specificBrowser != null && specificBrowser.length() > 0) {
          Runtime.getRuntime().exec(specificBrowser + ' ' + url);
        } else {
          Runtime.getRuntime().exec(
              "rundll32 url.dll,FileProtocolHandler " + url); //$NON-NLS-1$
        }
      } else { // assume Unix or Linux
        String[] browsers = { specificBrowser, "firefox", //$NON-NLS-1$
            "opera", //$NON-NLS-1$
            "konqueror", //$NON-NLS-1$
            "epiphany", //$NON-NLS-1$
            "mozilla", //$NON-NLS-1$
            "netscape" //$NON-NLS-1$
        };
        String browser = null;

        for (int count = 0; count < browsers.length && browser == null; count++) {
          if (browsers[count] != null
              && browsers[count].length() > 0
              && Runtime.getRuntime().exec(
                  new String[] { "which", browsers[count] }).waitFor() == 0) { //$NON-NLS-1$
            browser = browsers[count];
          }
        }
        if (browser == null) {
          throw new Exception(Messages
              .getString("BareBonesBrowserLaunch.CouldNotFindBrowser")); //$NON-NLS-1$
        }
        Runtime.getRuntime().exec(new String[] { browser, url });
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, errorMessage + ":\n" //$NON-NLS-1$
          + e.getLocalizedMessage());
    }
  }

}
