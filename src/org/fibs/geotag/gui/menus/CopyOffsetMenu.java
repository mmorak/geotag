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
package org.fibs.geotag.gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.gui.menus.actions.CopyOffsetToAllAction;
import org.fibs.geotag.gui.menus.actions.CopyOffsetToSelectedAction;
import org.fibs.geotag.table.ImagesTable;

/**
 * @author andreas
 *
 */
@SuppressWarnings("serial")
public class CopyOffsetMenu extends JMenu implements MenuConstants,ActionListener {
  
  /** The menu item used to copy the offset to all pictures. */
  private JMenuItem copyOffsetToAllItem;

  /** The menu item used to copy the offset to selected pictures. */
  private JMenuItem copyOffsetToSelectedItem;
  
  /** The table containing the images */
  private ImagesTable imagesTable;
  
  /** The image under the mouse pointer, if any */
  private ImageInfo currentImage;
  
  /**
   * @param backgroundTask
   * @param imagesTable
   * @param currentImage
   */
  public CopyOffsetMenu(boolean backgroundTask, ImagesTable imagesTable, ImageInfo currentImage) {
    super(COPY_TIME_OFFSET);
    this.imagesTable = imagesTable;
    this.currentImage = currentImage;
    boolean itemEnabled;
    boolean menuEnabled = false;
    int[] selectedRows = imagesTable.getSelectedRows();

    copyOffsetToSelectedItem = new JMenuItem(COPY_TIME_OFFSET_SELECTED);
    // enable if there is no background task and there is a selection
    itemEnabled = !backgroundTask && selectedRows.length > 0;
    menuEnabled |= itemEnabled;
    copyOffsetToSelectedItem.setEnabled(itemEnabled);
    copyOffsetToSelectedItem.addActionListener(this);
    this.add(copyOffsetToSelectedItem);

    copyOffsetToAllItem = new JMenuItem(COPY_TIME_OFFSET_ALL);
    itemEnabled = !backgroundTask;
    menuEnabled |= itemEnabled;
    copyOffsetToAllItem.setEnabled(itemEnabled);
    copyOffsetToAllItem.addActionListener(this);
    this.add(copyOffsetToAllItem);
    this.setEnabled(menuEnabled);
  }
  
  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == copyOffsetToAllItem) {
      new CopyOffsetToAllAction(currentImage, imagesTable).perform();
    } else if (event.getSource() == copyOffsetToSelectedItem) {
      new CopyOffsetToSelectedAction(currentImage, imagesTable).perform();
    }
  }
}
