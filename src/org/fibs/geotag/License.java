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

package org.fibs.geotag;

import java.util.ArrayList;
import java.util.List;

import org.fibs.geotag.util.Util;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A class retrieving the localised text for the about dialog.
 * 
 * @author Andreas Schneider
 * 
 */
public final class License {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(License.class);

  /**
   * hide constructor.
   */
  private License() {
    // hide constructor
  }

  /** the maximum line width in characters. */
  private static final int MAX_LINE_LENGTH = 60;

  /**
   * @return license info as a List of strings
   */
  public static List<String> licenseInfo() {
    List<String> lines = new ArrayList<String>();
    // Translators: Leave the GPL untranslated
    String paragraph1 = i18n.tr("This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version."); //$NON-NLS-1$
    // Translators: Leave the GPL untranslated
    String paragraph2 = i18n.tr("This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details."); //$NON-NLS-1$
    // Translators: Leave the GPL untranslated
    String paragraph3 = i18n.tr("You should have received a copy of the GNU General Public License along with this program; if not, see"); //$NON-NLS-1$
    lines.addAll(Util.splitString(paragraph1, MAX_LINE_LENGTH));
    lines.add(""); //$NON-NLS-1$
    lines.addAll(Util.splitString(paragraph2, MAX_LINE_LENGTH));
    lines.add(""); //$NON-NLS-1$
    lines.addAll(Util.splitString(paragraph3, MAX_LINE_LENGTH));
    return lines;
  }
}
