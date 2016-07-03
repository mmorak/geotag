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

/**
 * A class encapsulating CameraDate updates.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class UpdateCameraDate extends AbstractUndoableEdit {
  /** the {@link ImageInfo} whose CameraDate will be updated. */
  private ImageInfo imageInfo;

  /** A copy of the previous CameraDate value. */
  private String oldCameraDate;

  /** The new CameraDate. */
  private String newCameraDate;

  /**
   * @param imageInfo
   * @param newCameraDate
   */
  public UpdateCameraDate(ImageInfo imageInfo, String newCameraDate) {
    this.imageInfo = imageInfo;
    this.oldCameraDate = imageInfo.getCameraDate();
    imageInfo.setCameraDate(newCameraDate);
    this.newCameraDate = imageInfo.getCameraDate();
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
    imageInfo.setCameraDate(newCameraDate);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#undo()
   */
  @Override
  public void undo() {
    super.undo();
    imageInfo.setCameraDate(oldCameraDate);
  }
}
