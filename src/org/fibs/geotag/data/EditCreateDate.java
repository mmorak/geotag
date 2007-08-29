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
 * A class encapsulating CreateDate updates
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class EditCreateDate extends AbstractUndoableEdit {
  /** the {@link ImageInfo} whose CreateDate will be updated */
  private ImageInfo imageInfo;

  /** A copy of the previous CreateDate value */
  private String oldCreateDate;

  /** The new CreateDate */
  private String newCreateDate;

  /**
   * @param imageInfo
   * @param newCreateDate
   */
  public EditCreateDate(ImageInfo imageInfo, String newCreateDate) {
    this.imageInfo = imageInfo;
    this.oldCreateDate = imageInfo.getCreateDate();
    imageInfo.setCreateDate(newCreateDate);
    this.newCreateDate = imageInfo.getCreateDate();
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
    imageInfo.setCreateDate(newCreateDate);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#undo()
   */
  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    imageInfo.setCreateDate(oldCreateDate);
  }
}
