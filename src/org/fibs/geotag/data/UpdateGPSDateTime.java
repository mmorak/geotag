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
 * A class encapsulating GPSDateTime updates
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class UpdateGPSDateTime extends AbstractUndoableEdit {
  /** The {@link ImageInfo} whose GPSdateTime will be updated */
  private ImageInfo imageInfo;

  /** A copy of the previous value */
  private String oldDateTime;

  /** The new value */
  private String newDateTime;

  /**
   * @param imageInfo
   * @param newDateTime
   */
  public UpdateGPSDateTime(ImageInfo imageInfo, String newDateTime) {
    this.imageInfo = imageInfo;
    this.oldDateTime = imageInfo.getGPSDateTime();
    imageInfo.setGPSDateTime(newDateTime);
    this.newDateTime = imageInfo.getGPSDateTime();
    GlobalUndoManager.getManager().addEdit(this);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#isSignificant()
   */
  @Override
  public boolean isSignificant() {
    // these individual updates are not significant
    return false;
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#redo()
   */
  @Override
  public void redo() throws CannotRedoException {
    super.redo();
    imageInfo.setGPSDateTime(newDateTime);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#undo()
   */
  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    imageInfo.setGPSDateTime(oldDateTime);
  }
}
