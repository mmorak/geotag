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

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;

/**
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class BooleanSettingsPanel extends SettingsPanel {

  /** The check box to select the boolean value. */
  private JCheckBox checkBox;

  /**
   * @param parent
   * @param title
   * @param setting
   * @param defaultValue
   */
  public BooleanSettingsPanel(JFrame parent, String title, SETTING setting,
      boolean defaultValue) {
    // Boolean settings never have an enabling setting
    super(parent, title, null, true, setting, Boolean.toString(defaultValue));
    checkBox = new JCheckBox(title);
    boolean selelected = Settings.get(setting, defaultValue);
    checkBox.setSelected(selelected);
    addEditor(checkBox);
  }

  /**
   * @see org.fibs.geotag.gui.settings.SettingsPanel#getValue()
   */
  @Override
  public String getValue() {
    return Boolean.toString(checkBox.isSelected());
  }

}
