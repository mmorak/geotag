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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.fibs.geotag.data.ImageInfo;
import org.fibs.geotag.googleearth.KmlExporter;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author Andreas Schneider
 * 
 */
public class GoogleEarthExportTask extends BackgroundTask<Void> {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(GoogleEarthExportTask.class);

  /** The images to be exported. */
  private List<ImageInfo> images;

  /** The file to export to. */
  private File file;

  /**
   * @param name
   * @param images
   * @param file
   */
  public GoogleEarthExportTask(String name, List<ImageInfo> images, File file) {
    super(name);
    this.images = images;
    this.file = file;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getCurrentProgress()
   */
  @Override
  public int getCurrentProgress() {
    return 0;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getMaxProgress()
   */
  @Override
  public int getMaxProgress() {
    return 1;
  }

  /**
   * @see org.fibs.geotag.tasks.BackgroundTask#getMinProgress()
   */
  @Override
  public int getMinProgress() {
    return 0;
  }

  /**
   * @see javax.swing.SwingWorker#doInBackground()
   */
  @Override
  protected String doInBackground() throws Exception {
    KmlExporter exporter = new KmlExporter(images);
    try {
      exporter.write(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    String message = String.format(i18n
        .tr("Finished exporting to %s."), file.getPath()); //$NON-NLS-1$
    return message;
  }

}
