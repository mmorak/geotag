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

package org.fibs.geotag.gui.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.fibs.geotag.Messages;
import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.gpsbabel.GPSBabel;
import org.fibs.geotag.util.FontUtil;

/**
 * A dialog for changing the settings of the program
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class SettingsDialog extends JDialog {

  /** The parent component, use to position this dialog */
  private JFrame parent;

  /**
   * @param parent
   */
  public SettingsDialog(JFrame parent) {
    // true == modal
    super(parent, Messages.getString("SettingsDialog.Settings"), true); //$NON-NLS-1$
    this.parent = parent;
    final List<SettingsPanel> panelList = new ArrayList<SettingsPanel>();
    Dimension minimumSize = parent.getSize();
    minimumSize.setSize(minimumSize.getWidth() / 2, 50);
    setMinimumSize(minimumSize);
    panelList.add(new FileSettingsPanel(parent, Messages
        .getString("SettingsDialog.Browser"), SETTING.BROWSER, "")); //$NON-NLS-1$//$NON-NLS-2$
    panelList
        .add(new FileSettingsPanel(
            parent,
            Messages.getString("SettingsDialog.ExiftoolPath"), SETTING.EXIFTOOL_PATH, "exiftool")); //$NON-NLS-1$ //$NON-NLS-2$
    panelList
        .add(new StringSettingsPanel(
            parent,
            Messages.getString("SettingsDialog.AdditionalExiftoolArguments"), SETTING.EXIFTOOL_ARGUMENTS, "")); //$NON-NLS-1$//$NON-NLS-2$
    panelList
        .add(new FileSettingsPanel(
            parent,
            Messages.getString("SettingsDialog.GPSBabelPath"), SETTING.GPSBABEL_PATH, "gpsbabel")); //$NON-NLS-1$//$NON-NLS-2$
    panelList
        .add(new StringSettingsPanel(
            parent,
            Messages.getString("SettingsDialog.GPSBabelProtocol"), SETTING.GPSBABEL_PROTOCOL, "garmin")); //$NON-NLS-1$//$NON-NLS-2$
    panelList
        .add(new StringSettingsPanel(
            parent,
            Messages.getString("SettingsDialog.GPSBabelDevice"), SETTING.GPSBABEL_DEVICE, GPSBabel.getDefaultDevice())); //$NON-NLS-1$
    panelList.add(new FileSettingsPanel(parent, Messages
        .getString("SettingsDialog.DcrawPath"), SETTING.DCRAW_PATH, "dcraw")); //$NON-NLS-1$ //$NON-NLS-2$
    panelList
        .add(new FontSettingsPanel(
            parent,
            Messages.getString("SettingsDialog.Font"), SETTING.FONT, FontUtil.fontToID(UIManager.getLookAndFeel().getDefaults().getFont("Table.font")))); //$NON-NLS-1$ //$NON-NLS-2$
    panelList
        .add(new BooleanSettingsPanel(
            parent,
            Messages.getString("SettingsDialog.XmpFilesOnly"), SETTING.XMP_FILES_ONLY, "false")); //$NON-NLS-1$//$NON-NLS-2$
    panelList
        .add(new BooleanSettingsPanel(
            parent,
            Messages.getString("SettingsDialog.CheckForUpdates"), SETTING.CHECK_FOR_NEW_VERSION, "true")); //$NON-NLS-1$//$NON-NLS-2$
    // Finally a Panel with OK and Cancel buttons
    // The flow layout looks very ugly, have to find something else
    JPanel buttonPanel = new JPanel(new FlowLayout());
    String ok = Messages.getString("SettingsDialog.OK"); //$NON-NLS-1$
    String cancel = Messages.getString("SettingsDialog.Cancel"); //$NON-NLS-1$
    JButton okButton = new JButton(ok);
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        for (SettingsPanel settingsPanel : panelList) {
          SETTING setting = settingsPanel.getSetting();
          String value = settingsPanel.getValue();
          if (!value.equals(Settings.get(setting, settingsPanel
              .getDefaultValue()))) {
            Settings.put(setting, value);
          }
        }
        Settings.flush();
        dispose();
      }
    });
    buttonPanel.add(okButton);
    JButton cancelButton = new JButton(cancel);
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // just close the dialog
        dispose();
      }
    });
    buttonPanel.add(cancelButton);
    FontMetrics fontMetrics = okButton.getFontMetrics(okButton.getFont());
    int preferredWidth = Math.max(fontMetrics.stringWidth(ok), fontMetrics
        .stringWidth(cancel)) * 2;
    Dimension preferredSize = okButton.getPreferredSize();
    preferredSize.width = preferredWidth;
    okButton.setPreferredSize(preferredSize);
    cancelButton.setPreferredSize(preferredSize);
    JPanel settingsPanel = new JPanel(new GridLayout(panelList.size(), 1));
    for (JPanel panel : panelList) {
      settingsPanel.add(panel);
    }
    setLayout(new BorderLayout());
    add(settingsPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * open the modal settings dialog
   */
  public void openDialog() {
    pack();
    setLocationRelativeTo(parent);
    setVisible(true);
  }
}
