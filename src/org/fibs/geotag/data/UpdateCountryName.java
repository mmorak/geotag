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

package org.fibs.geotag.data;

import javax.swing.undo.AbstractUndoableEdit;

import org.fibs.geotag.GlobalUndoManager;
import org.fibs.geotag.data.ImageInfo.DATA_SOURCE;

/**
 * A class encapsulating country name updates.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class UpdateCountryName extends AbstractUndoableEdit {
  /** The {@link ImageInfo}. */
  private ImageInfo imageInfo;

  /** A copy of the previous country name. */
  private String oldCountry;

  /** A copy of the previous data source. */
  private DATA_SOURCE oldSource;

  /** The new country name. */
  private String newCountry;

  /** The new data source. */
  private DATA_SOURCE newSource;

  /**
   * @param imageInfo
   * @param newCountry
   * @param newSource
   */
  public UpdateCountryName(ImageInfo imageInfo, String newCountry,
      DATA_SOURCE newSource) {
    this.imageInfo = imageInfo;
    this.oldCountry = imageInfo.getCountryName();
    this.oldSource = imageInfo.getSource();
    imageInfo.setCountryName(newCountry, newSource);
    this.newCountry = imageInfo.getCountryName();
    this.newSource = imageInfo.getSource();
    GlobalUndoManager.getManager().addEdit(this);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#isSignificant()
   */
  @Override
  public boolean isSignificant() {
    // these individual updates are never significant
    return false;
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#redo()
   */
  @Override
  public void redo() {
    super.redo();
    imageInfo.setCountryName(newCountry, newSource);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#undo()
   */
  @Override
  public void undo() {
    super.undo();
    imageInfo.setCountryName(oldCountry, oldSource);
  }
}
