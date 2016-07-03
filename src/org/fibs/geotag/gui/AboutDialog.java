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

package org.fibs.geotag.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.fibs.geotag.Geotag;
import org.fibs.geotag.License;
import org.fibs.geotag.Settings;
import org.fibs.geotag.Version;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.gui.flattr.FlattrButton;
import org.fibs.geotag.util.BrowserLauncher;
import org.fibs.geotag.util.ImageUtil;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The about dialog (with working hyperlinks).
 * 
 * @author Andreas Schneider
 */
@SuppressWarnings("serial")
public class AboutDialog extends JDialog implements HyperlinkListener {

  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(AboutDialog.class);

  /** The path to the icon displayed in the dialog */
  private static final String ICON_NAME = "images/geotag-48.png"; //$NON-NLS-1$
  
  /** Size of blank space around text in pixels. */
  private static final int BORDER_SIZE = 20;

  /**
   * @param owner
   * @param title
   */
  public AboutDialog(Window owner, String title) {
    super(owner, title, ModalityType.APPLICATION_MODAL);
    System.out.println(title);
    Image image = ImageUtil.loadImage(ICON_NAME);
    ImageIcon icon = new ImageIcon(image);
    JLabel logo = new JLabel(icon);
    logo.setBorder(new EmptyBorder(BORDER_SIZE,0,BORDER_SIZE, 0));
    add(logo, BorderLayout.NORTH);
    // a panel containing all the text
    JPanel panel = new JPanel(new BorderLayout());
    // The first lines contain hyperlinks, so we use a JTextPane
    JTextPane header = new JTextPane();
    // displaying HTML
    header.setContentType("text/html"); //$NON-NLS-1$
    // It's for displaying only - don't allow editing
    header.setEditable(false);
    // make sure the background colour is right
    header.setBackground(getContentPane().getBackground());
    // and listen for click on hyperlink
    header.addHyperlinkListener(this);
    StringBuilder message = new StringBuilder();
    // make the first line bigger and bold
    message.append("<center><font size=\"+1\"<b>"); //$NON-NLS-1$
    // Show program name version and build number
    message.append(Geotag.NAME).append(' ').append(Version.VERSION).append(' ')
        .append("(Build ").append(Version.BUILD_NUMBER).append(')'); //$NON-NLS-1$
    message.append("</b></font><br><br>"); //$NON-NLS-1$
    // show the build date
    message.append(Version.BUILD_DATE).append("<br>"); //$NON-NLS-1$
    // show a link to our web site
    message.append("<a href=\"").append(Geotag.WEBSITE).append("\">").append(//$NON-NLS-1$ //$NON-NLS-2$
        Geotag.WEBSITE).append("</a>"); //$NON-NLS-1$
    message.append("<br>"); //$NON-NLS-1$
    // show a copyright notice
    message.append(i18n.tr("Copyright")).append(' ') //$NON-NLS-1$
        .append('\u00a9').append(' '); // \u00a9 is the copyright symbol
    message.append("2007-2016").append(' '); //$NON-NLS-1$
    message.append("Andreas Schneider"); //$NON-NLS-1$
    message.append("<br><br></center>"); //$NON-NLS-1$
    header.setText(message.toString());
    // add the header to the top of the panel
    panel.add(header, BorderLayout.NORTH);
    // The license text looks hideous in a JTextPane - we use a label
    JLabel license = new JLabel();
    // with a plain font
    Font font = license.getFont();
    license.setFont(font.deriveFont(Font.PLAIN));
    message = new StringBuilder("<html><center>"); //$NON-NLS-1$
    List<String> licenseInfo = License.licenseInfo();
    for (String line : licenseInfo) {
      message.append(line).append("<br>"); //$NON-NLS-1$
    }
    message.append("</center></html>"); //$NON-NLS-1$
    license.setText(message.toString());
    // The license text is shown in the centre of the panel
    panel.add(license, BorderLayout.CENTER);
    // The footer is the link to the FSF web site
    JTextPane footer = new JTextPane();
    // as HTML because it's a hyperlink
    footer.setContentType("text/html"); //$NON-NLS-1$
    // not editable
    footer.setEditable(false);
    // with the right background colour
    footer.setBackground(getContentPane().getBackground());
    // get call back if the hyperlink is clicked
    footer.addHyperlinkListener(this);
    // the actual link
    footer
        .setText("<center><a href=\"http://www.gnu.org/licenses\">http://www.gnu.org/licenses</a></center>"); //$NON-NLS-1$
    panel.add(footer, BorderLayout.SOUTH);
    // a border, so the text doesn't sit right on the edge of the dialog
    panel.setBorder(BorderFactory.createEmptyBorder(0, BORDER_SIZE,
        BORDER_SIZE, BORDER_SIZE));
    // add the panel at the centre of the dialog
    add(panel, BorderLayout.CENTER);
    // Now a panel for the OK button to dismiss the dialog
    JPanel buttonPanel = new JPanel(new FlowLayout());
    String ok = i18n.tr("OK"); //$NON-NLS-1$
    JButton okButton = new JButton(ok);
    // give the button a more pleasing preferred size
    FontMetrics fontMetrics = okButton.getFontMetrics(okButton.getFont());
    int preferredWidth = fontMetrics.stringWidth(ok) * 3;
    Dimension preferredSize = okButton.getPreferredSize();
    preferredSize.width = preferredWidth;
    okButton.setPreferredSize(preferredSize);
    // get a call back when the button is clicked on
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    buttonPanel.add(okButton);
    AbstractButton flattrButton = new FlattrButton();
    buttonPanel.add(flattrButton);
    // add button at the bottom of the dialog
    add(buttonPanel, BorderLayout.SOUTH);
    // set the dialog size snugly fit everything
    pack();
    // and place the dialog
    if (owner != null) {
      setLocationRelativeTo(owner);
    }
  }

  /**
   * Launch web browser when a hyperlink is clicked.
   * 
   * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
   */
  @Override
  public void hyperlinkUpdate(HyperlinkEvent event) {
    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      BrowserLauncher.openURL(Settings.get(SETTING.BROWSER, null), event
          .getURL().toString());
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    (new AboutDialog(null, "About Geotag")).setVisible(true); //$NON-NLS-1$
  }
}
