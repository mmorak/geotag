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
 * A class encapsulating longitude updates
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class EditGPSLongitude extends AbstractUndoableEdit {
  /** the {@link ImageInfo} whose longitude will be updated */
  private ImageInfo imageInfo;

  /** A copy of the previous longitude value */
  private String oldLongitude;

  /** The previous data source value */
  private ImageInfo.DATA_SOURCE oldDataSource;

  /** The new longitude value */
  private String newLongitude;

  /** The new data source value */
  private ImageInfo.DATA_SOURCE newDataSource;

  /**
   * @param imageInfo
   * @param newLongitude
   * @param newDataSource
   */
  public EditGPSLongitude(ImageInfo imageInfo, String newLongitude,
      ImageInfo.DATA_SOURCE newDataSource) {
    this.imageInfo = imageInfo;
    this.oldLongitude = imageInfo.getGPSLongitude();
    this.oldDataSource = imageInfo.getSource();
    imageInfo.setGPSLongitude(newLongitude, newDataSource);
    this.newLongitude = imageInfo.getGPSLongitude();
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
    imageInfo.setGPSLongitude(newLongitude, newDataSource);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#undo()
   */
  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    imageInfo.setGPSLongitude(oldLongitude, oldDataSource);
  }
}
