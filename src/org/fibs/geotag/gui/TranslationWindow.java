/**
 * Geotag
 * Copyright (C) 2007,2008 Andreas Schneider
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
package org.fibs.geotag.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.fibs.geotag.i18n.Translations;

/**
 * The resource file editor window
 * @author andreas
 *
 */
@SuppressWarnings("serial")
public class TranslationWindow extends JFrame {
  /** The locale to be translated, or null if translating is not enabled */
  private static Locale locale = null;
  
  /** The only instance of this window ever created */
  private static TranslationWindow instance = null;
  
  /**
   * Private constructor for this Window. We only want one instance
   */
  private TranslationWindow() {
    super("Translation Window");
    createComponents();
  }
  
  private JPanel createClassPanel(final String className) {
    CollapsablePanel classPanel = new CollapsablePanel(className);
    classPanel.setBorder(new EmptyBorder(1,3,1,1));
    classPanel.setBackground(getBackground());
    Vector<String> identifiers = Translations.getKnownIdentifiers(className);
    classPanel.getContent().setLayout(new GridLayout(identifiers.size(),1));
    final JList identifierList = new JList(identifiers);
    for (String identifier : identifiers) {
      final JLabel identifierLabel = new JLabel(identifier);
      identifierLabel.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent event) {
          identifierLabel.setVisible(false);
        }
      });
      classPanel.getContent().add(identifierLabel);
    }
//    identifierLists.add(identifierList);
//    identifierList.setBackground(getBackground());
//    EmptyBorder border = new EmptyBorder(0, 20, 0, 0);
//    identifierList.setBorder(border);
//    identifierList.addListSelectionListener(new ListSelectionListener() {
//      @Override
//      public void valueChanged(ListSelectionEvent event) {
//        if (event.getValueIsAdjusting() == false) {
//          String identifier = (String) identifierList.getSelectedValue();
//          if (identifier != null) {
//            selectKey(identifierList,className+"."+identifier);
//          }
//        }
//      }
//    });
    classPanel.getContent().add(identifierList, BorderLayout.CENTER);
    return classPanel;
  }
  
  private JPanel createEditor(Locale locale, boolean editable) {
    JPanel editorPanel = new JPanel(new BorderLayout());
    editorPanel.setBorder(new LineBorder(Color.BLACK));
    String localeName = locale.equals(Locale.ROOT) ? "Default" : locale.getDisplayName();
    JLabel label = new JLabel(localeName);
    editorPanel.add(label, BorderLayout.NORTH);
    JTextArea textArea = new JTextArea();
    textArea.setLineWrap(true);
    textArea.setEditable(editable);
    editorPanel.add(textArea, BorderLayout.CENTER);
    return editorPanel;
  }
  
  private JScrollPane createEditors() {
    JPanel editorsPanel = new JPanel();
    BoxLayout layout = new BoxLayout(editorsPanel, BoxLayout.Y_AXIS);
    editorsPanel.setLayout(layout);
    editorsPanel.add(createEditor(getLocaleToTranslate(), true));
    editorsPanel.add(createEditor(Locale.ROOT, false));
    for (Locale locale : Translations.getKnownLocales()) {
      if (! locale.equals(Locale.ROOT) && ! locale.equals(getLocaleToTranslate())) {
        editorsPanel.add(createEditor(locale, false));
      }
    }
    return new JScrollPane(editorsPanel);
  }
  
  private void createComponents() {
    setLayout(new BorderLayout());
    setSize(600, 600);
    JPanel classNamesPanel = new JPanel();
    classNamesPanel.setBackground(getBackground());
    List<String> knownClasses = Translations.getKnownClasses();
    LayoutManager boxLayout = new BoxLayout(classNamesPanel, BoxLayout.Y_AXIS);
    classNamesPanel.setLayout(boxLayout);
    for (String className : knownClasses) {
      classNamesPanel.add(createClassPanel(className));
    }
    JScrollPane scrollPane = new JScrollPane(classNamesPanel);
    scrollPane.setBackground(getBackground());
    add(scrollPane, BorderLayout.WEST);
    add(createEditors(), BorderLayout.CENTER);
  }
  
//  void selectKey(JList selectedList, String key) {
//    for (JList list : identifierLists) {
//      if (list != selectedList) {
//        list.clearSelection();
//      } else {
//        System.out.println("Skipped a list");
//      }
//    }
//    selectedList.requestFocus();
//  }
  
  /**
   * Show the translation window instance
   */
  public static void showWindow() {
    if (instance == null) {
      instance = new TranslationWindow();
    }
    instance.setVisible(true);
    instance.toFront();
  }
  /**
   * Translating is enabled by setting the locale
   * @param locale
   */
  public static void setLocaleToTranslate(Locale locale) {
    TranslationWindow.locale = locale;
  }
  
  /**
   * @return The locale to translate or null if not enabled
   */
  public static Locale getLocaleToTranslate () {
    return locale;
  }
}
