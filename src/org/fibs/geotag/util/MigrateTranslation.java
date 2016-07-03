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
package org.fibs.geotag.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author andreas
 *
 */
@SuppressWarnings("all")
public class MigrateTranslation {

  public static void main(String[] args) {
    try {
      migrateLocale("de", "German", "Germany");
      migrateLocale("da", "Danish", "Denmark");
      migrateLocale("fr", "French", "France");
      migrateLocale("en_GB", "English" , "United Kingdom");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * @param locale
   */
  private static void migrateLocale(String locale, String language, String country) throws Exception {
    System.out.println("Migrating locale "+locale);
    String rootDirectoryName = System.getProperty("user.dir"); //$NON-NLS-1$
    String poFileName = rootDirectoryName+"/i18n/"+locale+".po"; //$NON-NLS-1$
    File poFile = new File(poFileName);
    if (!poFile.exists()) {
      System.err.println("Can't find file "+poFileName);
    }
    String propertiesFileName = rootDirectoryName+"/res/org/fibs/geotag/geotag.properties";
    Map<String,String> originalMap = storeProperties(propertiesFileName);
    propertiesFileName = rootDirectoryName+"/res/org/fibs/geotag/geotag_"+locale+".properties";
    Map<String,String> localeMap = storeProperties(propertiesFileName);
    Map<String, String> translationMap = buildTranslation(originalMap, localeMap);
    applyTranslation(language, country, translationMap, poFile);
  }
  
  private static String MSGID = "msgid \"";
  private static String MSGSTR = "msgstr \"";
  
  private static void applyTranslation(String language, String country,Map<String,String> translation, File poFile) throws Exception {
    File tempFile = File.createTempFile(poFile.getName(), null);
    tempFile.deleteOnExit();
    System.out.println(tempFile);
    BufferedWriter tempFileWriter = new BufferedWriter(new FileWriter(tempFile));
    writeHeader(tempFileWriter, language, country);
    BufferedReader bufferedReader = new BufferedReader(new FileReader(poFile));
    String lineFromFile = null;
    StringBuilder msgId = null;
    while ((lineFromFile = bufferedReader.readLine())!=null) {
      if (lineFromFile.startsWith("#")) {
        tempFileWriter.write(lineFromFile);
        tempFileWriter.newLine();
      } else if (lineFromFile.startsWith(MSGID)) {
        msgId = new StringBuilder();
        msgId.append(lineFromFile.substring(MSGID.length(), lineFromFile.length()-1));
        tempFileWriter.write(lineFromFile);
        tempFileWriter.newLine();
      } else if (msgId != null && lineFromFile.startsWith("\"")) {
        msgId.append(lineFromFile.substring(1, lineFromFile.length() -1));
        tempFileWriter.write(lineFromFile);
        tempFileWriter.newLine();
      } else if (msgId != null && lineFromFile.startsWith(MSGSTR)) {
        String msgstr = translation.get(msgId.toString());
        if (msgstr != null) {
          writeMsgstr(tempFileWriter, msgstr);
        } else {
          tempFileWriter.write(lineFromFile);
          tempFileWriter.newLine();
        }
      } else {
        tempFileWriter.write(lineFromFile);
        tempFileWriter.newLine();
      }
    }
    bufferedReader.close();
    bufferedReader = null;
    tempFileWriter.close();
    overwrite(tempFile, poFile);
  }
  
  private static void writeHeader(BufferedWriter tempFileWriter,
      String language, String country) {
    try {
      tempFileWriter.write("msgid \"\"");
      tempFileWriter.newLine();
      tempFileWriter.write("msgstr \"\"");
      tempFileWriter.newLine();
      tempFileWriter.write("\"Project-Id-Version: Geotag\\n\"");
      tempFileWriter.newLine();
      tempFileWriter.write("\"Content-Type: text/plain; charset=utf-8\\n\"");
      tempFileWriter.newLine();
      tempFileWriter.write("\"Content-Transfer-Encoding: 8bit\\n\"");
      tempFileWriter.newLine();
      tempFileWriter.write("\"X-Poedit-Language: "+language+"\\n\"");
      tempFileWriter.newLine();
      tempFileWriter.write("\"X-Poedit-Country: "+country+"\\n\"");
      tempFileWriter.newLine();
      tempFileWriter.write("\"X-Poedit-SourceCharset: utf-8\\n\"");
      tempFileWriter.newLine();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
   
  }

  private static void writeMsgstr(BufferedWriter tempFileWriter, String msgstr) throws IOException {
     if (msgstr.length() < 800 - MSGSTR.length() - 1) {
       tempFileWriter.write(MSGSTR);
       for (int index = 0; index < msgstr.length(); index++) {
         char character = msgstr.charAt(index);
         if (character == '"') {
           tempFileWriter.write('\\');
         }
         tempFileWriter.write(character);
       }
       tempFileWriter.write("\"");
       tempFileWriter.newLine();
     }
  }
  
  private static void overwrite(File from, File to) {
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(from));
      BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(to));
      String line = null;
      while ((line = bufferedReader.readLine())!= null) {
        bufferedWriter.write(line);
        bufferedWriter.newLine();
      }
      bufferedReader.close();
      bufferedWriter.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private static Map<String,String> buildTranslation(Map<String,String> from, Map<String,String> to) {
    Map<String,String> translationMap = new HashMap<String, String>();
    Map<String,String> firstKeyMap = new HashMap<String, String>();
    Set<String> keys = from.keySet();
    for (String key : keys) {
      String originalText = from.get(key);
      String translatedText = to.get(key);
      if (translatedText!=null) {
        if (translationMap.containsKey(originalText)) {
          String knownTranslation = translationMap.get(originalText);
          if (knownTranslation == null
              || !knownTranslation.equals(translatedText)) {
            String firstKey = firstKeyMap.get(originalText);
            System.err.println(firstKey + "/" + key + " " + originalText + ": "
                + knownTranslation + "!=" + translatedText);
          }
        } else {
          translationMap.put(originalText, translatedText);
          firstKeyMap.put(originalText, key);
        }
        //System.out.println(originalText+"="+translatedText);;
      }
    }
    return translationMap;
  }
  
  private static Map<String,String> storeProperties(String fileName) {
    Map<String,String> map = new HashMap<String, String>();
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(fileName);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(0);
    }
    Properties properties = new Properties();
    try {
      properties.load(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    }
    Enumeration<Object> keys = properties.keys();
    while(keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      String value = properties.getProperty(key);
      //System.out.println(key+"="+value);
      if (map.containsKey(key)) {
        System.err.println("Dupicate "+value);
      }
      map.put(key,value);
    }
    return map;
  }
}
