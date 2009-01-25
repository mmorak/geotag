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
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fibs.geotag.i18n.Translations;

/**
 * The resource file editor window
 * 
 * @author andreas
 * 
 */
@SuppressWarnings("all")
public class TranslationWindow extends JFrame {
  
  /** The only instance of this window ever created */
  private static TranslationWindow instance = null;
  
  /** The translations model */
  private Translations translations = new Translations();
  
  /** A map for one deditor per locale */
  private Map<Locale, JTextArea> editors = new HashMap<Locale, JTextArea>();

  /** Show all or untranslated items only */
  boolean showUntranslatedOnly = false;

  /** the collapsable panels for each class */
  List<ClassPanel> classPanels = new Vector<ClassPanel>();

  /**
   * Private constructor for this Window. We only want one instance
   */
  private TranslationWindow() {
    super("Translation Window");
    createMenuBar();
    createComponents();
    createController();
  }

  /**
   * Create the menubar and handle the actions from it
   */
  private void createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    menuBar.add(fileMenu);
    JMenu viewMenu = new JMenu("View");
    JMenuItem untranslatedItem = new JCheckBoxMenuItem("Untranslated only");
    untranslatedItem.setSelected(showUntranslatedOnly);
    untranslatedItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showUntranslatedOnly = !showUntranslatedOnly;
        for (ClassPanel classPanel : classPanels) {
          classPanel.setup(showUntranslatedOnly);
        }
      }
    });
    viewMenu.add(untranslatedItem);
    menuBar.add(viewMenu);
    setJMenuBar(menuBar);
  }

  /**
   * Create one editor for a goven locale
   * 
   * @param editorLocale
   *          The locale
   * @param editable
   *          Whether or not the editor actually allows editing
   * @return The editor for the locale
   */
  private JPanel createEditor(Locale editorLocale, boolean editable) {
    JPanel editorPanel = new JPanel(new BorderLayout());
    editorPanel.setBorder(new LineBorder(Color.BLACK));
    String localeName = editorLocale.equals(Locale.ROOT) ? "Default"
        : editorLocale.getDisplayName();
    JLabel label = new JLabel(localeName);
    label.setBorder(new LineBorder(Color.BLACK));
    editorPanel.add(label, BorderLayout.NORTH);
    JTextArea textArea = new JTextArea();
    textArea.setLineWrap(true);
    textArea.setEditable(editable);
    if (editable) {
      textArea.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void changedUpdate(DocumentEvent e) {
          System.out.println("change");
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
          System.out.println("insert");
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          System.out.println("remove");
        }

      });
    }
    editorPanel.add(textArea, BorderLayout.CENTER);
    editors.put(editorLocale, textArea);
    return editorPanel;
  }

  /**
   * @return Editors for all known locales, put in a scroll pane
   */
  private JScrollPane createEditors() {
    JPanel editorsPanel = new JPanel();
    BoxLayout layout = new BoxLayout(editorsPanel, BoxLayout.Y_AXIS);
    editorsPanel.setLayout(layout);
    editorsPanel.add(createEditor(translations.getTranslationLocale(), true));
    editorsPanel.add(createEditor(translations.getRootLocale(), false));
    for (Locale editorLocale : translations.getOtherLocales()) {
        editorsPanel.add(createEditor(editorLocale, false));
    }
    return new JScrollPane(editorsPanel);
  }

  /**
   * Setup all editors for the new identifier to edit
   * 
   * @param className
   * @param identifier
   */
  void initEditors(String className, String identifier) {
    for (Locale locale : translations.getKnownLocales()) {
      JTextArea textArea = editors.get(locale);
      String translation = translations.getTranslation(locale, className,
          identifier);
      textArea.setText(translation);
    }
  }

  /**
   * Create the components for the translation window
   */
  private void createComponents() {
    setLayout(new BorderLayout());
    setSize(600, 600);
    JPanel classNamesPanel = new JPanel();
    classNamesPanel.setBackground(getBackground());
    List<String> knownClasses = Translations.getKnownClasses();
    LayoutManager boxLayout = new BoxLayout(classNamesPanel, BoxLayout.Y_AXIS);
    classNamesPanel.setLayout(boxLayout);
    for (final String className : knownClasses) {
      ClassPanel classPanel = new ClassPanel(className);
      classPanels.add(classPanel);
      classPanel.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
          initEditors(className, event.getActionCommand());
        }
      });
      classPanel.setup(showUntranslatedOnly);
      classPanel.getContent().setBorder(new EmptyBorder(0, 20, 0, 0));
      classNamesPanel.add(classPanel);
    }
    JScrollPane scrollPane = new JScrollPane(classNamesPanel);
    scrollPane.setBackground(getBackground());
    add(scrollPane, BorderLayout.WEST);
    add(createEditors(), BorderLayout.CENTER);
  }
  
  private void createController() {
    //controller = new TranslationController(this, rootLocale, translationLocale, otherLocales); 
  }

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
   * A collapsable panel showing all identifiers for a given class name
   */
  public class ClassPanel extends CollapsablePanel {

    /** the class name */
    String className;

    /** Who to notify if the user clicks on an identifier */
    List<ActionListener> listeners = new Vector<ActionListener>();

    /**
     * Create a ClassPanel instance
     * 
     * @param className
     *          The class name for this panel
     */
    public ClassPanel(String className) {
      super(className);
      this.className = className;
    }

    /**
     * Add an action listener
     * 
     * @param listener
     */
    public void addActionListener(ActionListener listener) {
      listeners.add(listener);
    }

    /**
     * Remove an action listener
     * 
     * @param listener
     */
    public void removeActionListener(ActionListener listener) {
      listeners.remove(listener);
    }

    /**
     * Setup the panel. Can be called repeatedly to show/hide already translated
     * items
     * 
     * @param untranslatedOnly
     */
    public void setup(boolean untranslatedOnly) {
      int numShowing = 0;
      getContent().removeAll();
      setBorder(new EmptyBorder(1, 3, 1, 1));
      List<String> identifiers = Translations.getKnownIdentifiers(className);
      getContent().setLayout(new BoxLayout(getContent(), BoxLayout.Y_AXIS));
      for (final String identifier : identifiers) {
        String translation = translations.getTranslation(translations.getTranslationLocale(),
            className, identifier);
        if (!untranslatedOnly || (translation == null && untranslatedOnly)) {
          numShowing++;
          final JLabel identifierLabel = new JLabel(identifier);
          if (translation == null) {
            identifierLabel.setFont(identifierLabel.getFont().deriveFont(Font.BOLD));
          }
          identifierLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
              ActionEvent actionEvent = new ActionEvent(identifierLabel, 0,
                  identifier);
              for (ActionListener listener : listeners) {
                listener.actionPerformed(actionEvent);
              }
            }
          });
          getContent().add(identifierLabel);
        }
      }
      setExpanded(numShowing > 0);
    }
  }

}
