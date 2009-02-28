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

package org.fibs.geotag.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Our own implementation of a property resource bundle. The default
 * implementation doesn't do what we want and the properties in it are private,
 * so we need to re-invent the wheel.
 */
public class EditableResourceBundle extends ResourceBundle {

  /** This is where we store the properties */
  private Map<Object, Object> resources;

  /**
   * Constructor
   * 
   * @param stream
   * @throws IOException
   */
  EditableResourceBundle(InputStream stream) throws IOException {
    Properties properties = new Properties();
    properties.load(stream);
    resources = new HashMap<Object, Object>(properties);
  }

  /**
   * @see java.util.ResourceBundle#getKeys()
   */
  @SuppressWarnings("unchecked")
  @Override
  public Enumeration getKeys() {
    ResourceBundle parentBundle = this.parent;
    Enumeration<Object> parentKeys = null;
    if (parentBundle != null) {
      parentBundle.getKeys();
    }
    Set<Object> bundleKeys = resources.keySet();
    return new KeyEnumeration(parentKeys, bundleKeys);
  }

  /**
   * Unlike getKeys() this returns the keys for just this bundle, not
   * considering any parent bundles
   * 
   * @return the key map
   */
  public Map<Object, Object> getKeyMap() {
    return resources;
  }

  /**
   * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
   */
  @Override
  protected Object handleGetObject(String key) {
    if (key == null) {
      throw new NullPointerException();
    }
    return resources.get(key);
  }

  /**
   * @param key
   * @return The value stored for this bundle only, without looking at parent
   *         bundles
   */
  public String getValue(String key) {
    return (String) resources.get(key);
  }

  /**
   * This allows editing the resources
   * 
   * @param key
   * @param value
   */
  public void putResource(Object key, Object value) {
    resources.put(key, value);
  }

  /**
   * The parent keys are available as an Enumeration, whereas the HashMap has a
   * key set with an Iterator. this class combines the two to create an
   * Enumeration as required by getKeys().
   */
  private class KeyEnumeration implements Enumeration<Object> {

    /** The Enumeration of the parent bundle's keys */
    private Enumeration<Object> parentKeys;

    /** The iterator for the bundle keys */
    private Iterator<Object> bundleKeys;

    /**
     * @param parentKeys
     * @param bundleKeys
     */
    public KeyEnumeration(Enumeration<Object> parentKeys, Set<Object> bundleKeys) {
      this.parentKeys = parentKeys;
      this.bundleKeys = bundleKeys.iterator();
    }

    /**
     * @see java.util.Enumeration#hasMoreElements()
     */
    @Override
    public boolean hasMoreElements() {
      if (parentKeys != null) {
        if (parentKeys.hasMoreElements()) {
          return true;
        }
        parentKeys = null;
      }
      return bundleKeys.hasNext();
    }

    /**
     * @see java.util.Enumeration#nextElement()
     */
    @Override
    public Object nextElement() {
      if (parentKeys != null) {
        return parentKeys.nextElement();
      }
      return bundleKeys.next();
    }
  }

}
