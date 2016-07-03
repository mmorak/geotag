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

package org.fibs.geotag.gui.menus.actions;

import java.util.ArrayList;
import java.util.List;

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.gui.menus.MenuConstants;
import org.fibs.geotag.table.ImagesTable;
import org.fibs.geotag.table.ImagesTableModel;
import org.fibs.geotag.tasks.SetOffsetTask;
import org.fibs.geotag.tasks.TaskExecutor;

/**
 * @author andreas
 * 
 */
public class CopyOffsetToAllAction implements MenuAction, MenuConstants {
  /** Image to copy from */
  private ImageInfo fromImage;

  /** The table of images */
  private ImagesTable imagesTable;

  /**
   * @param fromImage
   * @param imagesTable
   */
  public CopyOffsetToAllAction(ImageInfo fromImage, ImagesTable imagesTable) {
    this.fromImage = fromImage;
    this.imagesTable = imagesTable;
  }

  /**
   * @see org.fibs.geotag.gui.menus.actions.MenuAction#perform()
   */
  @Override
  public void perform() {
    int offset = fromImage.getOffset();
    List<ImageInfo> imageList = new ArrayList<ImageInfo>();
    ImagesTableModel tableModel = (ImagesTableModel) imagesTable.getModel();
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      ImageInfo image = tableModel.getImageInfo(index);
      imageList.add(image);
    }
    TaskExecutor.execute(new SetOffsetTask(COPY_TIME_OFFSET,
        COPY_TIME_OFFSET_ALL, tableModel, offset, imageList));
  }

}
