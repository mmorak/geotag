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

package org.fibs.geotag.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A font chooser component.
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class FontChooser extends JDialog {
  
  /** Create i18n support */
  private static final I18n i18n = I18nFactory.getI18n(FontChooser.class);

  /** the smallest font size selectable. */
  private static final int SMALLEST_FONT_SIZE = 6;

  /** the biggest font size selectable. */
  private static final int BIGGEST_FONT_SIZE = 30;

  /** The Font currently displayed in this FontChooser. */
  private Font displayedFont;

  /**
   * construct a FontChooser.
   * 
   * @param parent
   * @param initialFont
   */
  public FontChooser(Frame parent, Font initialFont) {
    super(parent, i18n.tr("Select Font"), true); //$NON-NLS-1$

    // initially display the font specified in the constructor
    displayedFont = initialFont;
    if (displayedFont == null) {
      displayedFont = getFont();
    }
    System.out.println();

    // The best place to get the font family names is...
    String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getAvailableFontFamilyNames();

    // we are going to add three panels
    // setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
    setLayout(new BorderLayout());

    // Next we add a custom label to show off the font's looks
    // This goes in the centre of the layout

    // The text displayed to demonstrate the font
    final String sampleText = "AaBbCcXxYyZz 0123456789"; //$NON-NLS-1$

    final JLabel sampleTextLabel = new JLabel(sampleText) {
      @Override
      public Dimension getMinimumSize() {
        Font largestFont = new Font(getDisplayedFont().getFamily(), Font.PLAIN,
            BIGGEST_FONT_SIZE);
        // we want the component taller than the required font height
        FontMetrics fontMetrics = getFontMetrics(largestFont);
        int minumumWidth = fontMetrics.stringWidth(getText());
        // Need the same space again above and below the strings and 2 strings
        final int factor = 5;
        int minimumHeight = fontMetrics.getHeight() * factor;
        return new Dimension(minumumWidth, minimumHeight);
      }

      @Override
      public Dimension getPreferredSize() {
        return getMinimumSize();
      }

      @Override
      public void paint(Graphics graphics) {
        // super.paint(g);
        int width = getWidth();
        int height = getHeight();
        int centreLine = height / 2;
        FontMetrics fontMetrics = getFontMetrics(getDisplayedFont());
        int stringWidth = fontMetrics.stringWidth(sampleText);
        // x and y coordinates refer to the baseline of the leftmost character
        int x = (width - stringWidth) / 2;
        // plain font - one and a half font heights above centre line
        int y = (int) (centreLine - fontMetrics.getHeight() / 2.0);
        graphics.setColor(getForeground());
        graphics.drawString(sampleText, x, y);
        Font boldFont = getDisplayedFont().deriveFont(Font.BOLD);
        graphics.setFont(boldFont);
        fontMetrics = getFontMetrics(boldFont);
        stringWidth = fontMetrics.stringWidth(sampleText);
        // x and y coordinates refer to the baseline of the leftmost character
        x = (width - stringWidth) / 2;
        // plain font - one and a half font heights above centre line
        y = (int) (centreLine + fontMetrics.getHeight() * (3.0 / 2.0));
        graphics.drawString(sampleText, x, y);
      }
    };
    sampleTextLabel.setFont(displayedFont);
    add(sampleTextLabel, BorderLayout.CENTER);

    // first a panel for selection font name and size
    // it will have two elements in one row
    JPanel fontSelectionPanel = new JPanel(new GridLayout(1, 2));

    // It contains one combo box for the font family name:
    final JComboBox<?> fontNamesComboBox = new JComboBox<Object>(fontList);
    fontNamesComboBox.setSelectedItem(displayedFont.getFamily());
    fontSelectionPanel.add(fontNamesComboBox);

    SpinnerNumberModel spinnerModel = new SpinnerNumberModel(displayedFont
        .getSize(), SMALLEST_FONT_SIZE, BIGGEST_FONT_SIZE, 1);
    final JSpinner fontSizesSpinner = new JSpinner(spinnerModel);
    fontSelectionPanel.add(fontSizesSpinner);

    // Whenever the selection in the combo boxe or the spinner changes,
    // we need to update the displayedFont and repaint the sample text
    ItemListener itemListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        String fontName = (String) fontNamesComboBox.getSelectedItem();
        String fontSize = ((Integer) fontSizesSpinner.getValue()).toString();
        setDisplayedFont(new Font(fontName, Font.PLAIN, Integer
            .parseInt(fontSize)));
        // now re-display the sample text
        sampleTextLabel.setFont(getDisplayedFont());
        sampleTextLabel.repaint();
      }
    };
    fontNamesComboBox.addItemListener(itemListener);

    ChangeListener changeListener = new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        String fontName = (String) fontNamesComboBox.getSelectedItem();
        String fontSize = ((Integer) fontSizesSpinner.getValue()).toString();
        setDisplayedFont(new Font(fontName, Font.PLAIN, Integer
            .parseInt(fontSize)));
        // now re-display the sample text
        sampleTextLabel.setFont(getDisplayedFont());
        sampleTextLabel.repaint();
      }
    };
    fontSizesSpinner.addChangeListener(changeListener);

    add(fontSelectionPanel, BorderLayout.NORTH);

    JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
    JButton okButton = new JButton(i18n.tr("OK")); //$NON-NLS-1$
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    buttonPanel.add(okButton);

    JButton cancelButton = new JButton(i18n.tr("Cancel")); //$NON-NLS-1$
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setDisplayedFont(null);
        dispose();
      }
    });
    buttonPanel.add(cancelButton);
    // add(buttonPanel);
    add(buttonPanel, BorderLayout.SOUTH);

    // now we select the correct
    pack();
    setSize(getPreferredSize());
    setLocationRelativeTo(parent);
  }

  /**
   * Show the modal dialog and return Font when done.
   * 
   * @return The selected font or null if cancelled
   */
  public Font showDialog() {
    setVisible(true);
    return getSelectedFont();
  }

  /**
   * @return the selected font or null if cancelled
   */
  public Font getSelectedFont() {
    return displayedFont;
  }

  /**
   * @return the displayed font
   */
  Font getDisplayedFont() {
    return displayedFont;
  }

  /**
   * @param displayedFont
   *          the displayedFont to set
   */
  void setDisplayedFont(Font displayedFont) {
    this.displayedFont = displayedFont;
  }

}
