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
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * The resource file editor window
 * @author andreas
 *
 */
@SuppressWarnings("serial")
public class TranslationWindow extends JFrame {
  /** The locale to be translated, or null if translating is not enabled */
  private static Locale locale = null;
  
  /** The only instance of this window ever created */
  private static TranslationWindow instance = new TranslationWindow();
  
  /**
   * Private constructor for this Window. We only want one instance
   */
  private TranslationWindow() {
    super("Translation Window");
    setLayout(new BorderLayout());
    JLabel label = new JLabel("ABC");
    add(label, BorderLayout.NORTH);
  }
  
  /**
   * Show the translation window instance
   */
  public static void showWindow() {
    instance.setVisible(true);
    instance.toFront();
  }
  /**
   * Translating is enabled by setting the locale
   * @param locale
   */
  public static void setLocaleToTranslate(Locale locale) {
    TranslationWindow.locale = locale;
  }
  
  /**
   * @return The locale to translate or null if not enabled
   */
  public static Locale getLocaleToTranslate () {
    return locale;
  }
}
