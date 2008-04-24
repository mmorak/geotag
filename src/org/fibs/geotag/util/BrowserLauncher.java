/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fibs.geotag.util;

import java.awt.Desktop;
import java.net.URI;
import javax.swing.JOptionPane;
import org.fibs.geotag.Messages;

/**
 *
 * @author Felipe
 */
public class BrowserLauncher {
   /***/

  private static final String errorMessage = Messages
      .getString("launchBrowser.ErrorLauchingBrowser"); //$NON-NLS-1$
  
  public static void openURL(String specificBrowser, String url) {
    try {
      if (specificBrowser != null && specificBrowser.length() > 0) {
        Runtime.getRuntime().exec(specificBrowser + ' ' + url);
      } else {
        Desktop.getDesktop().browse(new URI(url));
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, errorMessage + ":\n" //$NON-NLS-1$
        + e.getLocalizedMessage());
    }
  }
}
