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

import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;

/**
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class IntegerSettingsPanel extends SettingsPanel {

  /** The spinner model holding the selected value. */
  private SpinnerNumberModel spinnerModel;

  /**
   * @param parent
   * @param title
   * @param setting
   * @param defaultValue
   * @param minimum
   * @param maximum
   * @param stepSize
   */
  public IntegerSettingsPanel(JFrame parent, String title, SETTING setting,
      int defaultValue, int minimum, int maximum, int stepSize) {
    this(parent, title, null, true, setting, defaultValue, minimum, maximum,
        stepSize);
  }

  /**
   * @param parent
   * @param title
   * @param enablingSetting
   * @param defaultEnabled
   * @param setting
   * @param defaultValue
   * @param minimum
   * @param maximum
   * @param stepSize
   */
  public IntegerSettingsPanel(JFrame parent, String title,
      SETTING enablingSetting, boolean defaultEnabled, SETTING setting,
      int defaultValue, int minimum, int maximum, int stepSize) {
    super(parent, title, enablingSetting, defaultEnabled, setting, Integer
        .toString(defaultValue));
    int value = Settings.get(setting, defaultValue);
    spinnerModel = new SpinnerNumberModel(value, minimum, maximum, stepSize);
    JSpinner spinner = new JSpinner(spinnerModel);
    addEditor(spinner);
  }

  /**
   * @see org.fibs.geotag.gui.settings.SettingsPanel#getValue()
   */
  @Override
  public String getValue() {
    return spinnerModel.getNumber().toString();
  }

}
