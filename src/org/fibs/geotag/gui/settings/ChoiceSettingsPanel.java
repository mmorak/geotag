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

import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;

/**
 * @author Andreas Schneider
 */
@SuppressWarnings("serial")
public class ChoiceSettingsPanel extends SettingsPanel {

  /** The choices to display */
  String[] choices;
  
  /** The combo box to display them in */
  JComboBox comboBox;
  
  /**
   * @param parent
   * @param title
   * @param setting
   * @param choices
   * @param defaultValue
   */
  public ChoiceSettingsPanel(JFrame parent, String title, SETTING setting,
      String[] choices, String defaultValue) {
    super(parent, title, setting, defaultValue);
    this.choices = choices;
    comboBox = new JComboBox(choices);
    //comboBox.set
    int defaultIndex = 0;
    for (int index = 0; index < choices.length; index++) {
      if (choices[index].equals(defaultValue)) {
        defaultIndex = index;
        break;
      }
    }
    int selectIndex = Settings.get(setting, defaultIndex);
    comboBox.setSelectedIndex(selectIndex);
    add(comboBox, BorderLayout.NORTH);
  }

  /**
   * @see org.fibs.geotag.gui.settings.SettingsPanel#getValue()
   */
  @Override
  public String getValue() {
    return Integer.toString(comboBox.getSelectedIndex());
  }

}
