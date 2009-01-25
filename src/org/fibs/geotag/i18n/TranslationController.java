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
package org.fibs.geotag.i18n;

import java.util.List;
import java.util.Locale;

import javax.swing.JTextArea;

import org.fibs.geotag.gui.TranslationWindow;

/**
 * @author andreas
 *
 */
@SuppressWarnings("all")
public class TranslationController {
  
  public interface Model {
    public Locale getRootLocale();
    public Locale getTranslationLocale();
    public List<Locale> getOtherLocales();
    public List<Locale> getKnownLocales();
    public String getTranslation(Locale locale, String className, String identifier);
    public void setTranslation(Locale locale, String className, String identifier, String translation);
  }
  public interface View {
    public JTextArea getEditor(Locale locale);
    public Locale getTranslationLocale();
  }
  private TranslationWindow window;
  private Locale rootLocale;
  private Locale translationLocale;
  private List<Locale> otherLocales;

  public TranslationController(View view) {
  }
}
