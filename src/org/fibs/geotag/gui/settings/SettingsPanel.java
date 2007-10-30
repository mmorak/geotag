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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;

/**
 * A panel to display and edit settings
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public abstract class SettingsPanel extends JPanel {

  /** the parent frame */
  JFrame parent;

  /** A panel containg the (optional) checkbox and the editor component */
  JPanel panel;

  /** The component used to edit/enter the settings value */
  Component editorComponent;

  /** the setting that indicates that the actual setting will be used */
  SETTING enablingSetting;

  /** Is the editor enabled or disabled by default */
  boolean defaultEnabled;

  /** The settings key */
  SETTING setting;

  /** The default value for this setting */
  String defaultValue;

  /** The title for this panel */
  String title;

  /** the optional checkbox if there is an enabling setting */
  JCheckBox checkbox;

  /**
   * @param parent
   * @param title
   * @param enablingSetting
   *          TODO
   * @param defaultEnabled
   * @param setting
   * @param defaultValue
   */
  public SettingsPanel(JFrame parent, String title, SETTING enablingSetting,
      boolean defaultEnabled, SETTING setting, String defaultValue) {
    super(new BorderLayout());
    TitledBorder border = BorderFactory.createTitledBorder(title);
    setBorder(border);
    this.parent = parent;
    this.title = title;
    this.enablingSetting = enablingSetting;
    this.defaultEnabled = defaultEnabled;
    this.setting = setting;
    this.defaultValue = defaultValue;
    panel = new JPanel(new BorderLayout());
    if (enablingSetting != null) {
      checkbox = new JCheckBox();
      checkbox.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (editorComponent != null) {
            editorComponent.setEnabled(checkbox.isSelected());
          }
        }
      });
      checkbox.setSelected(Settings.get(enablingSetting, defaultEnabled));
      panel.add(checkbox, BorderLayout.WEST);
    }
    add(panel, BorderLayout.NORTH);
  }

  /**
   * Add the component doing the editing Subclasses need to call this
   * 
   * @param component
   */
  public void addEditor(Component component) {
    if (enablingSetting != null) {
      boolean enabled = Settings.get(enablingSetting, defaultEnabled);
      component.setEnabled(enabled);
    }
    panel.add(component, BorderLayout.CENTER);
    editorComponent = component;
  }

  /**
   * @return the string value of this panel
   */
  public abstract String getValue();

  /**
   * If necessary save the new settings
   */
  public void save() {
    if (enablingSetting != null) {
      boolean enabled = checkbox.isSelected();
      if (enabled != Settings.get(enablingSetting, false)) {
        System.out.println(enablingSetting.toString() + '=' + enabled);
        Settings.put(enablingSetting, enabled);
      }
    }
    String value = getValue();
    if (!value.equals(Settings.get(setting, defaultValue))) {
      System.out.println(setting.toString() + '=' + value);
      Settings.put(setting, value);
    }
  }

  /**
   * @return the setting
   */
  public SETTING getSetting() {
    return setting;
  }

  /**
   * @return the defaultValue
   */
  public String getDefaultValue() {
    return defaultValue;
  }

  /**
   * Override toString as this is used by JTree
   * 
   * @see java.awt.Component#toString()
   */
  @Override
  public String toString() {
    return title;
  }
}
