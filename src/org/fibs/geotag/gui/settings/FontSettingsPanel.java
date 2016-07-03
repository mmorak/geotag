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

package org.fibs.geotag.gui.settings;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.gui.FontChooser;
import org.fibs.geotag.util.FontUtil;

/**
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class FontSettingsPanel extends SettingsPanel {

  /** The TextField for the Font name. */
  private JTextField textField;
  
  /**
   * @param parent
   * @param title
   * @param setting
   * @param defaultValue
   */
  public FontSettingsPanel(JFrame parent, String title, SETTING setting,
      String defaultValue) {
    this(parent, title, null, true, setting, defaultValue);
  }

  /**
   * @param parent
   * @param title
   * @param enablingSetting
   * @param defaultEnabled
   * @param setting
   * @param defaultValue
   */
  public FontSettingsPanel(JFrame parent, String title,
      SETTING enablingSetting, boolean defaultEnabled, SETTING setting,
      String defaultValue) {
    super(parent, title, enablingSetting, defaultEnabled, setting, defaultValue);
    JPanel fontPanel = new JPanel(new BorderLayout());
    textField = new JTextField(Settings.get(setting, defaultValue));
    fontPanel.add(textField, BorderLayout.CENTER);
    JButton browseButton = new JButton("..."); //$NON-NLS-1$
    browseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Font font = FontUtil.fontFromID(Settings
            .get(getSetting(),
                getDefaultValue()));
        FontChooser fontChooser = new FontChooser(
            getParentFrame(), font);
        if (fontChooser.showDialog() != null) {
          Font selectedFont = fontChooser.getSelectedFont();
          getTextField().setText(FontUtil.fontToID(selectedFont));
        }
      }
    });
    fontPanel.add(browseButton, BorderLayout.EAST);
    addEditor(fontPanel);
  }

  /**
   * @see org.fibs.geotag.gui.settings.SettingsPanel#getValue()
   */
  @Override
  public String getValue() {
    return textField.getText();
  }

  /**
   * @return the textField
   */
  JTextField getTextField() {
    return textField;
  }

}
