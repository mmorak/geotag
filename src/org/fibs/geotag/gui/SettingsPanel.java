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

package org.fibs.geotag.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.util.FontUtil;

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
    /** Setting type Boolean */
    BOOLEAN,
    /** Setting type File */
    FILE,
    /** Setting type Font */
    FONT
  }

  /** the parent frame */
  JFrame parent;

  /** The settings key */
  SETTING setting;

  /** The settings type - one of STRING, INTEGER or FILE */
  TYPE type;

  /** The default value for this setting */
  String defaultValue;

  /** The text field containing the value */
  JTextField textField;

  /** The check box for boolean values */
  JCheckBox checkBox;

  /**
   * @param parent
   * @param title
   * @param setting
   * @param defaultValue
   * @param type
   */
  public SettingsPanel(JFrame parent, String title, SETTING setting,
      String defaultValue, TYPE type) {
    super(new BorderLayout());
    TitledBorder border = BorderFactory.createTitledBorder(title);
    setBorder(border);
    this.parent = parent;
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
    } else if (type == TYPE.FONT) {
      JPanel fontPanel = new JPanel(new BorderLayout());
      textField = new JTextField(Settings.get(setting, defaultValue));
      fontPanel.add(textField, BorderLayout.CENTER);
      JButton browseButton = new JButton("..."); //$NON-NLS-1$
      browseButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Font font = FontUtil.fontFromID(Settings.get(
              SettingsPanel.this.setting, SettingsPanel.this.defaultValue));
          FontChooser fontChooser = new FontChooser(SettingsPanel.this.parent,
              font);
          if (fontChooser.showDialog() != null) {
            Font selectedFont = fontChooser.getSelectedFont();
            textField.setText(FontUtil.fontToID(selectedFont));
          }
        }
      });
      fontPanel.add(browseButton, BorderLayout.EAST);
      add(fontPanel, BorderLayout.NORTH);
    } else if (type == TYPE.BOOLEAN) {
      checkBox = new JCheckBox(title);
      boolean selelected = Boolean.parseBoolean(Settings.get(setting,
          defaultValue));
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
  public SETTING getSetting() {
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
