/**
 * Geotag
 * Copyright (C) 2007-2009 Andreas Schneider
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

package org.fibs.geotag.image;

import java.io.File;
import java.util.List;

import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.i18n.Messages;

/**
 * A class to determine if a file is an image or not.
 * 
 * @author Andreas Schneider
 * 
 * @author Dominic Battre
 * 
 */
public abstract class ImageFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter{
  
  public static enum Type {
    ALL_IMAGES, JPEG, RAW, TIFF, CUSTOM_FILE_WITH_XMP
  }

  public abstract Type getType();

  private final static ImageFileFilter ALL_IMAGES_FILTER = new ImageFileFilter() {
    /*
     * Accept directories and image files.
     * 
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File file) {
      if (file.isDirectory()) {
        return true;
      }
      FileTypes fileType = FileTypes.fileType(file);
      switch (fileType) {
        case UNKOWN:
          return false;
        case JPEG:
          return true;
        case RAW_READ_ONLY:
          return true;
        case RAW_READ_WRITE:
          return true;
        case TIFF:
          return true;
        case XMP:
          return false;
        case CUSTOM_FILE_WITH_XMP:
          return true;
        default:
          return false;
      }
    }

    /**
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
      return Messages.getString("ImageFileFilter.ImageFiles"); //$NON-NLS-1$
    }

    @Override
    public Type getType() {
      return Type.ALL_IMAGES;
    }
  };

  /**
   * Accept JPG image files.
   * 
   * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
   */
  private final static ImageFileFilter JPEG_IMAGES_FILTER = new ImageFileFilter() {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory()) {
        return true;
      }
      return FileTypes.JPEG == FileTypes.fileType(file);
    }

    /**
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
      return Messages.getString("ImageFileFilter.ImageFiles_JPEG"); //$NON-NLS-1$
    }

    @Override
    public Type getType() {
      return Type.JPEG;
    }
  };

  /**
   * Accept JPG image files.
   * 
   * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
   */
  private final static ImageFileFilter RAW_IMAGES_FILTER = new ImageFileFilter() {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory()) {
        return true;
      }
      return FileTypes.RAW_READ_ONLY == FileTypes.fileType(file)
          || FileTypes.RAW_READ_WRITE == FileTypes.fileType(file);
    }

    /**
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
      return Messages.getString("ImageFileFilter.ImageFiles_RAW"); //$NON-NLS-1$
    }

    @Override
    public Type getType() {
      return Type.RAW;
    }
  };

  /**
   * - * @see javax.swing.filechooser.FileFilter#getDescription() Accept JPG
   * image files.
   * 
   * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
   */
  private final static ImageFileFilter TIFF_IMAGES_FILTER = new ImageFileFilter() {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory()) {
        return true;
      }
      return FileTypes.TIFF == FileTypes.fileType(file);
    }

    /**
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
      return Messages.getString("ImageFileFilter.ImageFiles_TIFF"); //$NON-NLS-1$
    }
    @Override
    public Type getType() {
      return Type.TIFF;
    }
  };

  /**
   * Accept custom files with XMP sidecars (as specified in user settings)
   * 
   * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
   */
  private final static ImageFileFilter CUSTOM_FILE_WITH_XMP_FILTER = new ImageFileFilter() {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory()) {
        return true;
      }
      return FileTypes.CUSTOM_FILE_WITH_XMP == FileTypes.fileType(file);
    }

    /**
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
      StringBuilder description = new StringBuilder();
      List<String> supportedFileTypes = FileTypes.getFileTypesSupportedByXmp();
      for (int index = 0; index < supportedFileTypes.size(); index++) {
        String fileType = supportedFileTypes.get(index);
        if (index > 0) {
          description.append(',');
        }
        description.append(fileType.toUpperCase());
      }
      description.append(' ');
      description.append(Messages
          .getString("ImageFileFilter.ImageFiles_CUSTOM_FILE_WITH_XMP")); //$NON-NLS-1$
      return description.toString();
    }

    @Override
    public Type getType() {
      return Type.CUSTOM_FILE_WITH_XMP;
    }
  };
  
  public static ImageFileFilter getFilter(Type type) {
    switch(type) {
      case ALL_IMAGES:
        return ALL_IMAGES_FILTER;
      case JPEG:
        return JPEG_IMAGES_FILTER;
      case RAW:
        return RAW_IMAGES_FILTER;
      case TIFF:
        return TIFF_IMAGES_FILTER;
      case CUSTOM_FILE_WITH_XMP:
        return CUSTOM_FILE_WITH_XMP_FILTER;
    }
    return ALL_IMAGES_FILTER;
  }
  
  public static void storeLastFilterUsed(ImageFileFilter filter) {
    Settings.put(SETTING.LAST_FILE_FILTER_SELECTED, filter.getType().toString());
    Settings.flush();
  }
 
  public static ImageFileFilter getLastFilterUsed() {
    String filterId = Settings.get(SETTING.LAST_FILE_FILTER_SELECTED, Type.ALL_IMAGES.toString());
    try {
      Type type = Type.valueOf(filterId);
      return getFilter(type);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ALL_IMAGES_FILTER;
  }

}
