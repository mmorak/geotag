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

package org.fibs.geotag.i18n;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class is an interesting cheat. It allows us to specify a .po file on the
 * command line without compiling it into a Messages_xx.class file first. If a
 * po file is specified on the command line, we tell this class to read the
 * contents of that .po file, then we set the the default locale to "po". "po"
 * is not a valid language, but setting it as the default locale will cause I18n
 * to consult this class. Neat.
 * 
 * @author andreas
 * 
 */
public class Messages_po extends ResourceBundle {

  /** The resources by id */
  static Map<String, String> resourceMap = new HashMap<String, String>();

  /** Constant to recognise msgid from po file */
  private static String MSGID = "msgid \""; //$NON-NLS-1$

  /** Constant to recognise msgstr from po file*/
  private static String MSGSTR = "msgstr \""; //$NON-NLS-1$

  /**
    Process po file with that name
   * @param fileName The PO file name
   */
  public static void processPoFile(String fileName) {
    File poFile = new File(fileName);
    if (!poFile.exists()) {
      System.out.println("Can't find file " + fileName); //$NON-NLS-1$
      return;
    }
    String lineFromFile = null;
    StringBuilder msgId = null;
    StringBuilder msgStr = null;
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(poFile),"UTF-8")); //$NON-NLS-1$
      while ((lineFromFile = bufferedReader.readLine()) != null) {
        if (lineFromFile.startsWith(MSGID)) {
          msgId = new StringBuilder();
          msgId.append(lineFromFile.substring(MSGID.length(), lineFromFile
              .length() - 1));
        } else if (lineFromFile.startsWith("\"")) { //$NON-NLS-1$
          String content = lineFromFile.substring(1, lineFromFile.length() - 1);
          if (msgStr != null) {
            msgStr.append(content);
          } else if (msgId != null) {
            msgId.append(content);
          }
        } else if (lineFromFile.startsWith(MSGSTR)) {
          msgStr = new StringBuilder();
          msgStr.append(lineFromFile.substring(MSGSTR.length(), lineFromFile
              .length() - 1));
        } else {
          if (msgId != null && msgId.length() > 0 && msgStr != null
              && msgStr.length() > 0) {
            System.out.println(msgId.toString() + "=" + msgStr.toString()); //$NON-NLS-1$
            resourceMap.put(msgId.toString(), msgStr.toString());
          }
          msgId = null;
          msgStr = null;
        }
      }
      bufferedReader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Constructor
   */
  public Messages_po() {
  }

  @Override
  public Object handleGetObject(String key) throws MissingResourceException {
    String value = resourceMap.get(key);
    return value;
  }

  @Override
  public Enumeration<String> getKeys() {
    final Iterator<String> iterator = resourceMap.keySet().iterator();
    return new Enumeration<String>() {

      @Override
      public boolean hasMoreElements() {
        return iterator.hasNext();
      }

      @Override
      public String nextElement() {
        return iterator.next();
      }

    };
  }

  /**
   * @return the parent ResourceBundle
   */
  public ResourceBundle getParent() {
    return super.parent;
  }

}
