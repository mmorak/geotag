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

import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;

/**
 * @author Andreas Schneider
 */
@SuppressWarnings("serial")
public class ChoiceSettingsPanel extends SettingsPanel {

  /** The combo box to display them in. */
  private JComboBox<?> comboBox;

  /**
   * @param parent
   * @param title
   * @param setting
   * @param choices
   * @param defaultIndex
   */
  public ChoiceSettingsPanel(JFrame parent, String title, SETTING setting,
      String[] choices, int defaultIndex) {
    this(parent, title, null, true, setting, choices, defaultIndex);
  }

  /**
   * @param parent
   * @param title
   * @param enablingSetting
   * @param defaultEnabled
   * @param setting
   * @param choices
   * @param defaultIndex
   */
  public ChoiceSettingsPanel(JFrame parent, String title,
      SETTING enablingSetting, boolean defaultEnabled, SETTING setting,
      String[] choices, int defaultIndex) {
    super(parent, title, enablingSetting, defaultEnabled, setting, Integer
        .toString(defaultIndex));
    comboBox = new JComboBox<Object>(choices);
    int selectIndex = Settings.get(setting, defaultIndex);
    comboBox.setSelectedIndex(selectIndex);
    addEditor(comboBox);
  }

  /**
   * @see org.fibs.geotag.gui.settings.SettingsPanel#getValue()
   */
  @Override
  public String getValue() {
    return Integer.toString(comboBox.getSelectedIndex());
  }

}
