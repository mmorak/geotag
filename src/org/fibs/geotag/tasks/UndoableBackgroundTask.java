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

package org.fibs.geotag.tasks;

import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import org.fibs.geotag.GlobalUndoManager;

/**
 * A class for undo-able background tasks. See the javadoc for
 * AbstractUndoableEdit for details of most methods. We can't inherit
 * from it, so me must replicate the behaviour.
 * 
 * @author Andreas Schneider
 * 
 * @param <V>
 *          Type for passing on intermediate results by the publish() and
 *          process() methods
 */
public abstract class UndoableBackgroundTask<V> extends BackgroundTask<V>
    implements UndoableEdit {

  /** Key to get undo text from UIManager */
  private static final String UNDO_TEXT_KEY = "AbstractUndoableEdit.undoText"; //$NON-NLS-1$

  /** Key to get redo text from UIManager */
  private static final String REDO_TEXT_KEY = "AbstractUndoableEdit.redoText"; //$NON-NLS-1$

  /** Indicates if this has been done or undone. */
  private boolean hasBeenDone = true;

  /** True if this task is still alive. */
  private boolean alive = true;

  /** The optional (may be null) name of the task group. */
  private String group;

  /** A vector of {@link UndoableEdit}s that are part of this edit. */
  private Vector<UndoableEdit> edits = new Vector<UndoableEdit>();

  /**
   * Create an undo-able background task.
   * 
   * @param group
   *          The optional name of the task group
   * @param name
   *          The name of the task
   */
  public UndoableBackgroundTask(String group, String name) {
    super(name);
    this.group = group;
    GlobalUndoManager.getManager().addEdit(this);
  }

  /**
   * This default implementation returns false.
   * 
   * @see javax.swing.undo.UndoableEdit#addEdit(javax.swing.undo.UndoableEdit)
   */
  @Override
  public boolean addEdit(UndoableEdit anEdit) {
    if (anEdit.isSignificant()) {
      return false;
    }
    edits.add(anEdit);
    return true;
  }

  /**
   * Returns true if this edit is alive and hasBeenDone is false.
   * 
   * @see javax.swing.undo.UndoableEdit#canRedo()
   */
  @Override
  public boolean canRedo() {
    return alive && !hasBeenDone;
  }

  /**
   * Returns true if this edit is alive and hasBeenDone is true.
   * 
   * @see javax.swing.undo.UndoableEdit#canUndo()
   */
  @Override
  public boolean canUndo() {
    return alive && hasBeenDone;
  }

  /**
   * Sets alive to false.
   * 
   * @see javax.swing.undo.UndoableEdit#die()
   */
  @Override
  public void die() {
    alive = false;
  }

  /**
   * @see javax.swing.undo.UndoableEdit#getPresentationName()
   */
  @Override
  public String getPresentationName() {
    String presentationName = ""; //$NON-NLS-1$
    if (group != null) {
      presentationName += group + " - "; //$NON-NLS-1$
    }
    presentationName += getName();
    return presentationName;
  }

  /**
   * @see javax.swing.undo.UndoableEdit#getRedoPresentationName()
   */
  @Override
  public String getRedoPresentationName() {
    String presentationName = UIManager
        .getString(REDO_TEXT_KEY)
        + ' ' + getPresentationName();
    return presentationName;
  }

  /**
   * @see javax.swing.undo.UndoableEdit#getUndoPresentationName()
   */
  @Override
  public String getUndoPresentationName() {
    String presentationName = UIManager
        .getString(UNDO_TEXT_KEY)
        + ' ' + getPresentationName();
    return presentationName;
  }

  /**
   * @see javax.swing.undo.UndoableEdit#isSignificant()
   */
  @Override
  public boolean isSignificant() {
    // Background task are significant, where as individual edit for images
    // aren't
    return true;
  }

  /**
   * Throws CannotRedoException if canRedo returns false. Sets hasBeenDone to
   * true. Subclasses should override to redo the operation represented by this
   * edit. Override should begin with a call to super
   * 
   * @see javax.swing.undo.UndoableEdit#redo()
   */
  @Override
  public void redo() {
    if (!canRedo()) {
      throw new CannotRedoException();
    }
    // redo all edits
    for (UndoableEdit edit : edits) {
      edit.redo();
    }
    hasBeenDone = true;
  }

  /**
   * This default implementation returns false.
   * 
   * @see javax.swing.undo.UndoableEdit#replaceEdit(javax.swing.undo.UndoableEdit)
   */
  @Override
  public boolean replaceEdit(UndoableEdit anEdit) {
    return false;
  }

  /**
   * Throws CannotUndoException if canUndo returns false. Sets hasBeenDone to
   * false. Subclasses should override to undo the operation represented by this
   * edit. Override should begin with a call to super.
   * 
   * @see javax.swing.undo.UndoableEdit#undo()
   */
  @Override
  public void undo() {
    if (!canUndo()) {
      throw new CannotUndoException();
    }
    // undo all edits in reverse order
    for (int i = edits.size() - 1; i >= 0; i--) {
      edits.get(i).undo();
    }
    hasBeenDone = false;
  }

}
