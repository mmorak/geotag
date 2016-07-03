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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

/**
 * A collapsable JPanel
 * 
 * @author andreas
 * 
 */
@SuppressWarnings("serial")
public class CollapsablePanel extends JPanel {

  /** An ArrowButton used to collapse/expand the panel */
  ArrowButton arrowButton;

  /** The collapsable content of the panel */
  JPanel content;

  /**
   * Create a collapsed panel
   * 
   * @param title
   */
  public CollapsablePanel(String title) {
    setLayout(new BorderLayout());
    content = new JPanel();
    content.setVisible(false);
    arrowButton = new ArrowButton(title);
    arrowButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setExpanded(arrowButton.isSelected());
        revalidate();
      }
    });
    add(arrowButton, BorderLayout.NORTH);
    add(content, BorderLayout.CENTER);
  }

  /**
   * Allows stuff being put in the content panel
   * 
   * @return the content panel
   */
  public JPanel getContent() {
    return content;
  }

  /**
   * Set the content to be collapsed or expanded
   * 
   * @param expanded
   *          True for expanded, false for collapsed.
   */
  public void setExpanded(boolean expanded) {
    content.setVisible(expanded);
    arrowButton.setSelected(expanded);
    arrowButton.repaint();
  }

  /**
   * @return True if the panel is expanded
   */
  public boolean isExpanded() {
    return content.isVisible();
  }

}
