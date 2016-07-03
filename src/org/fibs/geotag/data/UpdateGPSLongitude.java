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
 * A class encapsulating longitude updates.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class UpdateGPSLongitude extends AbstractUndoableEdit {
  /** the {@link ImageInfo} whose longitude will be updated. */
  private ImageInfo imageInfo;

  /** A copy of the previous longitude value. */
  private String oldLongitude;

  /** The previous data source value. */
  private ImageInfo.DATA_SOURCE oldDataSource;

  /** The new longitude value. */
  private String newLongitude;

  /** The new data source value. */
  private ImageInfo.DATA_SOURCE newDataSource;

  /**
   * @param imageInfo
   * @param newLongitude
   * @param newDataSource
   */
  public UpdateGPSLongitude(ImageInfo imageInfo, String newLongitude,
      ImageInfo.DATA_SOURCE newDataSource) {
    this.imageInfo = imageInfo;
    this.oldLongitude = imageInfo.getGpsLongitude();
    this.oldDataSource = imageInfo.getSource();
    imageInfo.setGpsLongitude(newLongitude, newDataSource);
    this.newLongitude = imageInfo.getGpsLongitude();
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
  public void redo() {
    super.redo();
    imageInfo.setGpsLongitude(newLongitude, newDataSource);
  }

  /**
   * @see javax.swing.undo.AbstractUndoableEdit#undo()
   */
  @Override
  public void undo() {
    super.undo();
    imageInfo.setGpsLongitude(oldLongitude, oldDataSource);
  }
}
