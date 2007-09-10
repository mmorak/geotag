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

import javax.swing.JFrame;
import javax.swing.JTextField;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;

/**
 * @author Andreas Schneider
 *
 */
@SuppressWarnings("serial")
public class IntegerSettingsPanel extends SettingsPanel {

  /** The TextField to hold the value */
  JTextField textField;
  
  /**
   * @param parent
   * @param title
   * @param setting
   * @param defaultValue
   */
  public IntegerSettingsPanel(JFrame parent, String title, SETTING setting,
      String defaultValue) {
    super(parent, title, setting, defaultValue);
    textField = new JTextField(Settings.get(setting, defaultValue));
    add(textField, BorderLayout.NORTH);
  }

  /**
   * @see org.fibs.geotag.gui.settings.SettingsPanel#getValue()
   */
  @Override
  public String getValue() {
    return textField.getText();
  }

}
