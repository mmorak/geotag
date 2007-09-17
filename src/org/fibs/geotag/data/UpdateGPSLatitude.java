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

package org.fibs.geotag.data;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.fibs.geotag.GlobalUndoManager;

/**
 * A class encapsulating latitude updates
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class UpdateGPSLatitude extends AbstractUndoableEdit {
  /** The {@link ImageInfo} */
  private ImageInfo imageInfo;

  /** A copy of the previous latitude value */
  private String oldLatitude;

  /** A copy of the previous data source value */
  private ImageInfo.DATA_SOURCE oldDataSource;

  /** The new latitude value */
  private String newLatitude;

  /** The value for the data source */
  private ImageInfo.DATA_SOURCE newDataSource;

  /**
   * @param imageInfo
   * @param newLatitude
   * @param newDataSource
   */
  public UpdateGPSLatitude(ImageInfo imageInfo, String newLatitude,
      ImageInfo.DATA_SOURCE newDataSource) {
    this.imageInfo = imageInfo;
    this.oldLatitude = imageInfo.getGPSLatitude();
    this.oldDataSource = imageInfo.getSource();
    imageInfo.setGPSLatitude(newLatitude, newDataSource);
    this.newLatitude = imageInfo.getGPSLatitude();
    this.newDataSource = imageInfo.getSource();
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
  public void redo() throws CannotRedoException {
    super.redo();
    imageInfo.setGPSLatitude(newLatitude, newDataSource);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#undo()
   */
  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    imageInfo.setGPSLatitude(oldLatitude, oldDataSource);
  }
}
