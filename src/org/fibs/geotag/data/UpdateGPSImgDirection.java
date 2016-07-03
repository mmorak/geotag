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
 * A class encapsulating image direction updates.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class UpdateGPSImgDirection extends AbstractUndoableEdit {
  /** The {@link ImageInfo} whose image direction will be changed. */
  private ImageInfo imageInfo;

  /** A copy of the previous direction value. */
  private String oldDirection;

  /** The old source of the update. */
  private DATA_SOURCE oldDataSource;

  /** The new value for the direction. */
  private String newDirection;

  /** the new source of the update. */
  private DATA_SOURCE newDataSource;

  /**
   * @param imageInfo
   * @param newDirection
   * @param source
   */
  public UpdateGPSImgDirection(ImageInfo imageInfo, String newDirection,
      DATA_SOURCE source) {
    this.imageInfo = imageInfo;
    this.oldDirection = imageInfo.getGpsImgDirection();
    this.oldDataSource = imageInfo.getSource();
    imageInfo.setGpsImgDirection(newDirection);
    imageInfo.setSource(source);
    this.newDirection = imageInfo.getGpsImgDirection();
    this.newDataSource = imageInfo.getSource();
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
  public void redo() {
    super.redo();
    imageInfo.setGpsImgDirection(newDirection);
    imageInfo.setSource(newDataSource);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#undo()
   */
  @Override
  public void undo() {
    super.undo();
    imageInfo.setGpsImgDirection(oldDirection);
    imageInfo.setSource(oldDataSource);
  }
}
