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

package org.fibs.geotag.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.fibs.geotag.Settings;

/**
 * A panel to display and edit settings
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class SettingsPanel extends JPanel {
  
  /**
   * An enum for the type of setting 
   */
  public enum TYPE {
    /** Setting type String */
    STRING,
    /** Setting type Integer */
    INTEGER,
    /** Setting type File */
    FILE,
    /** Setting type Boolean */
    BOOLEAN
  }
  
//  /** The setting is a string value */
//  public static final int STRING = 1;
//
//  /** The setting is an integer */
//  public static final int INTEGER = 2;
//
//  /** The setting is a file name */
//  public static final int FILE = 3;
  
  
  /** The settings key */
  private String setting;

  /** The settings type - one of STRING, INTEGER or FILE */
  private TYPE type;

  /** The default value for this setting */
  private String defaultValue;

  /** The text field containing the value */
  JTextField textField;

  /** The check box for boolean values */
  JCheckBox checkBox;
  /**
   * @param title
   * @param setting
   * @param defaultValue
   * @param type
   */
  public SettingsPanel(String title, String setting, String defaultValue,
      TYPE type) {
    super(new BorderLayout());
    TitledBorder border = BorderFactory.createTitledBorder(title);
    setBorder(border);
    this.setting = setting;
    this.type = type;
    this.defaultValue = defaultValue;
    if (type == TYPE.FILE) {
      JPanel filePanel = new JPanel(new BorderLayout());
      textField = new JTextField(Settings.get(setting, defaultValue));
      filePanel.add(textField, BorderLayout.CENTER);
      JButton browseButton = new JButton("..."); //$NON-NLS-1$
      browseButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          JFileChooser fileChooser = new JFileChooser();
          if (fileChooser.showOpenDialog(SettingsPanel.this) == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            textField.setText(filename);
          }
        }
      });
      filePanel.add(browseButton, BorderLayout.EAST);
      add(filePanel, BorderLayout.NORTH);
    } else if (type == TYPE.BOOLEAN) {
      checkBox = new JCheckBox(title);
      boolean selelected = Boolean.parseBoolean(Settings.get(setting, defaultValue));
      checkBox.setSelected(selelected);
      add(checkBox, BorderLayout.NORTH);
    } else {
      // default: display text field
      textField = new JTextField(Settings.get(setting, defaultValue));
      add(textField, BorderLayout.NORTH);
    }
  }

  /**
   * @return the string value of this panel
   */
  public String getValue() {
    if (type == TYPE.BOOLEAN) {
      return Boolean.toString(checkBox.isSelected());
    }
    return textField.getText();
  }

  /**
   * @return the setting
   */
  public String getSetting() {
    return setting;
  }

  /**
   * @return the type
   */
  public TYPE getType() {
    return type;
  }

  /**
   * @return the defaultValue
   */
  public String getDefaultValue() {
    return defaultValue;
  }
}
