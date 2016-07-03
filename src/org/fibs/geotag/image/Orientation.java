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
package org.fibs.geotag.image;

import org.fibs.geotag.data.ImageInfo;

/**
 * the possible image orientations as defined by EXIF.
 */
public enum Orientation {
  /** Normal image orientation. */
  NORMAL(false),
  /** Flip image left-right. */
  FLIP_LEFT_RIGHT(false),
  /** Rotate image 180 degrees. */
  ROTATE_180(false),
  /** Flip image upside down. */
  FLIP_UP_DOWN(false),
  /** Rotate image 90 degrees clockwise, then flip left-right. */
  ROTATE_90_CLOCKWISE_FLIP_LEFT_RIGHT(true),
  /** Rotate image 90 degrees clockwise. */
  ROTATE_90_CLOCKWISE(true),
  /** Flip image upside-down, then rotate 80 degrees anti-clockwise. */
  FLIP_UP_DOWN_ROTATE_90_ANTICLOCK(true),
  /** Rotate image 90 degrees anti-clockwise. */
  ROTATE_90_ANTICLOCK(true);
  
  /** true if the images orientation switched width and height */
  private boolean changesAspect;

  /**
   * @param changesAspect
   */
  private Orientation(boolean changesAspect) {
    this.changesAspect = changesAspect;
  }
  
  /**
   * The orientation is stored in the {@link ImageInfo} as a String. This
   * methods converts it to this enum.
   * An exif value of 0 means unknown and we treat this
   * as normal (i.e. display as is). Unfortunately Google's
   * Picasa software sets the exif orientation to 0.
   * @param imageInfo 
   * 
   * @return The image orientation between 1 and 8
   */
  static Orientation getOrientation(ImageInfo imageInfo) {
    int exifvalue = 1;
    if (imageInfo != null) {
      try {
        exifvalue = Integer.parseInt(imageInfo.getOrientation());
      } catch (Exception e) {
        // just use the default
      }
    }
    if (exifvalue < 1 || exifvalue > Orientation.values().length) {
      return Orientation.NORMAL;
    }
    return Orientation.values()[exifvalue - 1];
  }
  
  /**
   * @return true if this orientation changes the image aspect
   */
  public boolean changesAspect() {
    return changesAspect;
  }
  
}
