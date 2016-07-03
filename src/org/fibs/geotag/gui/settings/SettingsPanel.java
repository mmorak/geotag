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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A panel to display and edit settings.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public abstract class SettingsPanel extends JPanel {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(SettingsPanel.class);

  /** Text for checkbox if selected. */
  static final String YES = i18n.tr("Yes"); //$NON-NLS-1$

  /** text for checkbox if not selected. */
  static final String NO = i18n.tr("No"); //$NON-NLS-1$

  /** A panel containg the (optional) checkbox and the editor component. */
  private JPanel panel;
  
  /** The parent JFrame. */
  private JFrame parentFrame;

  /** The component used to edit/enter the settings value. */
  private Component editorComponent;

  /** the setting that indicates that the actual setting will be used. */
  private SETTING enablingSetting;

  /** Is the editor enabled or disabled by default. */
  private boolean defaultEnabled;

  /** The settings key. */
  private SETTING setting;

  /** The default value for this setting. */
  private String defaultValue;

  /** The title for this panel. */
  private String title;

  /** the optional checkbox if there is an enabling setting. */
  private JCheckBox checkbox;

  /**
   * @param parent
   * @param title
   * @param enablingSetting
   * @param defaultEnabled
   * @param setting
   * @param defaultValue
   */
  public SettingsPanel(JFrame parent, String title, SETTING enablingSetting,
      boolean defaultEnabled, SETTING setting, String defaultValue) {
    super(new BorderLayout());
    TitledBorder border = BorderFactory.createTitledBorder(title);
    setBorder(border);
    this.parentFrame = parent;
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
          if (getEditorComponent() != null) {
            getEditorComponent().setEnabled(getCheckbox().isSelected());
            getCheckbox().setText(getCheckbox().isSelected() ? YES : NO);
          }
        }
      });
      checkbox.setSelected(Settings.get(enablingSetting, defaultEnabled));
      checkbox.setText(checkbox.isSelected() ? YES : NO);
      panel.add(checkbox, BorderLayout.WEST);
    }
    add(panel, BorderLayout.NORTH);
  }

  /**
   * Add the component doing the editing Subclasses need to call this.
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
   * If necessary save the new settings.
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
   * Override toString as this is used by JTree.
   * 
   * @see java.awt.Component#toString()
   */
  @Override
  public String toString() {
    return title;
  }

  /**
   * @return the editorComponent
   */
  Component getEditorComponent() {
    return editorComponent;
  }

  /**
   * @return the checkbox
   */
  JCheckBox getCheckbox() {
    return checkbox;
  }

  /**
   * @return the parentFrame
   */
  protected JFrame getParentFrame() {
    return parentFrame;
  }
}
