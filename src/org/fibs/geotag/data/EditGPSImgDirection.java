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
 * A class encapsulating image direction updates
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class EditGPSImgDirection extends AbstractUndoableEdit {
  /** The {@link ImageInfo} whose image direction will be changed */
  private ImageInfo imageInfo;

  /** A copy of the previous direction value */
  private String oldDirection;

  /** The new value for the direction */
  private String newDirection;

  /**
   * @param imageInfo
   * @param newDirection
   */
  public EditGPSImgDirection(ImageInfo imageInfo, String newDirection) {
    this.imageInfo = imageInfo;
    this.oldDirection = imageInfo.getGPSImgDirection();
    imageInfo.setGPSImgDirection(newDirection);
    this.newDirection = imageInfo.getGPSImgDirection();
    GlobalUndoManager.getManager().addEdit(this);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#isSignificant()
   */
  @Override
  public boolean isSignificant() {
    // individual updates are not significant
    return false;
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#redo()
   */
  @Override
  public void redo() throws CannotRedoException {
    super.redo();
    imageInfo.setGPSImgDirection(newDirection);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#undo()
   */
  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    imageInfo.setGPSImgDirection(oldDirection);
  }
}
