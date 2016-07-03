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
 * A class encapsulating province name updates.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class UpdateProvinceName extends AbstractUndoableEdit {
  /** The {@link ImageInfo}. */
  private ImageInfo imageInfo;

  /** A copy of the previous province name. */
  private String oldProvince;

  /** A copy of the previous data source. */
  private DATA_SOURCE oldSource;

  /** The new province name. */
  private String newProvince;

  /** The new data source. */
  private DATA_SOURCE newSource;

  /**
   * @param imageInfo
   * @param newProvince
   * @param newSource
   */
  public UpdateProvinceName(ImageInfo imageInfo, String newProvince,
      DATA_SOURCE newSource) {
    this.imageInfo = imageInfo;
    this.oldProvince = imageInfo.getProvinceName();
    this.oldSource = imageInfo.getSource();
    imageInfo.setProvinceName(newProvince, newSource);
    this.newProvince = imageInfo.getProvinceName();
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
    imageInfo.setProvinceName(newProvince, newSource);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#undo()
   */
  @Override
  public void undo() {
    super.undo();
    imageInfo.setProvinceName(oldProvince, oldSource);
  }
}
