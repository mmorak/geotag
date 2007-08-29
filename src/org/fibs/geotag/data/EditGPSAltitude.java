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
 * A class encapsulating altitude updates
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class EditGPSAltitude extends AbstractUndoableEdit {
  /** The {@link ImageInfo} whose altitude will be changed */
  private ImageInfo imageInfo;

  /** A copy of the previous altitude value */
  private String oldAltitude;

  /** A copy of the previous data source value */
  private ImageInfo.DATA_SOURCE oldDataSource;

  /** The new value for the altitude */
  private String newAltitude;

  /** The new value for the data source */
  private ImageInfo.DATA_SOURCE newDataSource;

  /**
   * @param imageInfo
   * @param newAltitude
   * @param newDataSource
   */
  public EditGPSAltitude(ImageInfo imageInfo, String newAltitude,
      ImageInfo.DATA_SOURCE newDataSource) {
    this.imageInfo = imageInfo;
    this.oldAltitude = imageInfo.getGPSAltitude();
    this.oldDataSource = imageInfo.getSource();
    imageInfo.setGPSAltitude(newAltitude, newDataSource);
    this.newAltitude = imageInfo.getGPSAltitude();
    this.newDataSource = imageInfo.getSource();
    GlobalUndoManager.getManager().addEdit(this);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#isSignificant()
   */
  @Override
  public boolean isSignificant() {
    // individual updates ar not significant
    return false;
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#redo()
   */
  @Override
  public void redo() throws CannotRedoException {
    super.redo();
    imageInfo.setGPSAltitude(newAltitude, newDataSource);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#undo()
   */
  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    imageInfo.setGPSAltitude(oldAltitude, oldDataSource);
  }
}
