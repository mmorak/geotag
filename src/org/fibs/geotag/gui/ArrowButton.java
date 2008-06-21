package org.fibs.geotag.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

/**
 * A button like label with an arrow icon.
 * Used for collapsable panes
 * @author andreas
 *
 */
@SuppressWarnings("serial")
public class ArrowButton extends JLabel {
  
  /** A down arrow icon */
  ArrowIcon downArrow = new ArrowIcon(7,
      ArrowIcon.Orientation.DOWN);

  /** A right arrow icon*/
  ArrowIcon rightArrow = new ArrowIcon(7,
      ArrowIcon.Orientation.RIGHT);

  /** Keeps track of the selected state of the "button"*/
  boolean selected = false;
  
  /** Used to generate action event ids*/
  int clickCounter = 0;
  
  /** Listeners to be notified if the "button" is clicked*/
  List<ActionListener> listeners = new ArrayList<ActionListener>();

  /**
   * create a new ArrowButton. The state will be "not selected" and the default
   * icon is a right arrow
   * @param text
   */
  public ArrowButton(String text) {
    super(text);
    setIcon(rightArrow);
    addMouseListener(new MouseAdapter() {
      /**
       * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        clickCounter++;
        selected = ! selected;
        setIcon(selected ? downArrow : rightArrow);
        System.out.println(selected);
        for (ActionListener listener : listeners) {
          listener.actionPerformed(new ActionEvent(ArrowButton.this, clickCounter, "click")); //$NON-NLS-1$
        }
      }
      
    });
  }
  
  /**
   * @param listener
   */
  public void addActionListener(ActionListener listener) {
    listeners.add(listener);
  }
  
  /**
   * @param listener
   */
  public void removeActionListener(ActionListener listener) {
    listeners.remove(listener);
  }

  /**
   * @return the selected
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   * @param selected the selected to set
   */
  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}