/**
 * Geotag
 * Copyright (C) 2007 Andreas Schneider
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

package org.fibs.geotag.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fibs.geotag.Messages;

/**
 * A dialog that lets the user select a date and a time
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class DateTimeChooser extends JDialog {

  /** The parent JFrame */
  private JFrame parent;

  /** The Date displayed by the component */
  Calendar displayedDate;

  /** The labels for the month names - one per month */
  private CalendarLabel[] monthLabels = new CalendarLabel[12];

  /**
   * The labels for showing the days of the month - we need 6 rows of 7 days
   * each
   */
  private CalendarLabel[][] dayLabels = new CalendarLabel[6][7];

  /** One label for displaying the year */
  private CalendarLabel yearLabel;

  /** A panel containing the year and the increment/decrement labels */
  private JPanel yearPanel;

  /** A panel containing the short month names */
  private JPanel monthsPanel;

  /** A panel showing the days in the month */
  private JPanel daysPanel;

  /** A panel containing the yearPanel, monthsPanel and daysPanel */
  private JPanel calendarPanel;

  /** A panel for choosing the time */
  private JPanel timePanel;

  /** A panel for the OK, Cancel and Today buttons */
  private JPanel buttonPanel;

  /** The currently selected time zone */
  TimeZone currentZone;

  /**
   * Create a {@link DateTimeChooser}
   * 
   * @param parent
   *          The parent component
   * @param title
   *          The title
   * @param date
   *          The initial date displayed
   * @param hasNowButton
   *          controls whether or not this component has a 'Now' button
   */
  public DateTimeChooser(JFrame parent, String title, Calendar date,
      boolean hasNowButton) {
    super(parent, title, true);
    this.parent = parent;
    // store the date
    displayedDate = date;
    // we use a BorderLayout
    Container container = getContentPane();
    container.setLayout(new BorderLayout());
    // the content pane holds 3 JPanels. From top to bottom:
    // one for the the calendar, one for the time and one for the buttons
    // First we create the panel for the calendar
    calendarPanel = new JPanel();
    calendarPanel.setLayout(new BorderLayout());
    calendarPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
    // the calendar panel contains 3 panels
    // The panel for the year is at the top
    yearPanel = new JPanel();
    yearPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    // it contains 3 labels in a GridLayout
    yearPanel.setLayout(new GridLayout(1, 3));
    // A big and bold decrement label for the year
    CalendarLabel decrement = new CalendarLabel("-", true, true); //$NON-NLS-1$
    decrement.setHorizontalAlignment(SwingConstants.RIGHT);
    decrement.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        // go one year back
        displayedDate.set(Calendar.YEAR, displayedDate.get(Calendar.YEAR) - 1);
        updateDateShown();
      }
    });
    // add as the first label in the panel
    yearPanel.add(decrement);
    // make the year label bold (the text won't be displayed)
    yearLabel = new CalendarLabel("Year", true, false); //$NON-NLS-1$
    // The year is always the selected one, so we highlight it
    yearLabel.setHighlighted(true);
    // add it to the right of the decrement button
    yearPanel.add(yearLabel);
    // make the increment label big and bold
    CalendarLabel increment = new CalendarLabel("+", true, true); //$NON-NLS-1$
    increment.setHorizontalAlignment(SwingConstants.LEFT);
    increment.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        // go one year forward
        displayedDate.set(Calendar.YEAR, displayedDate.get(Calendar.YEAR) + 1);
        updateDateShown();
      }
    });
    // add to the right of the year label
    yearPanel.add(increment);
    // add the lot to the top of the CalendarPanel
    calendarPanel.add(yearPanel, BorderLayout.NORTH);
    // Below the yearsPanel we have a Panel for the month names
    monthsPanel = new JPanel();
    monthsPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    // we display them in 2 rows with six month names each
    monthsPanel.setLayout(new GridLayout(2, 6));
    // get the month symbols Locale independent
    String[] monthSymbols = new DateFormatSymbols().getShortMonths();
    for (int month = 0; month < 12; month++) {
      // for each month we create one label showing its name
      monthLabels[month] = new CalendarLabel(monthSymbols[month], false, false);
      // and have the month number [0..11] associated with it
      monthLabels[month].setNumber(month);
      monthLabels[month].addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
          // user clicked on a month
          int selectedMonth = ((CalendarLabel) event.getSource()).getNumber();
          // change the month of the date displayed
          // note that when showing Mar 31, clicking on April will select May
          // 1st
          // (as there is no day 31 in April)
          displayedDate.set(Calendar.MONTH, selectedMonth);
          updateDateShown();
        }
      });
      // add the label to the panel
      monthsPanel.add(monthLabels[month]);
    }
    // the months are at the centre of the calendar panel
    calendarPanel.add(monthsPanel, BorderLayout.CENTER);

    // A panel displaying the days of the month
    // is shown at the bottom of the calendar panel
    daysPanel = new JPanel();
    daysPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    // there are 7 rows and 7 columns
    // the first row displays the weekday names, the other display
    // the numbers representing the days of the month
    daysPanel.setLayout(new GridLayout(7, 7));
    // The weekday names are retrieved Locale independent
    // They are stored in locations 1..7 of the array
    String[] WeekdayNames = new DateFormatSymbols().getShortWeekdays();
    // first fill in the labels for the weekday names
    for (int day = 1; day <= 7; day++) {
      // make the weekday labels bold
      CalendarLabel label = new CalendarLabel(WeekdayNames[getNthDayOfWeek(
          null, day)], true, false);
      daysPanel.add(label);
    }
    // next initialise the days of the week themselves
    for (int row = 0; row < 6; row++) {
      for (int col = 0; col < 7; col++) {
        dayLabels[row][col] = new CalendarLabel("X", false, false); //$NON-NLS-1$
        dayLabels[row][col].addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent event) {
            // which day did the user click on
            CalendarLabel daySelected = (CalendarLabel) event.getSource();
            int day = daySelected.getNumber();
            if (day > 0) {
              // change the date to the selected value
              displayedDate.set(Calendar.DATE, day);
              updateDateShown();
            }
          }
        });
        // add the label to the button
        daysPanel.add(dayLabels[row][col]);
      }
    }
    // That's the daysPanel done - add it at the bottom of the calendarPanel
    calendarPanel.add(daysPanel, BorderLayout.SOUTH);
    // The calendarPanel goes at the top the container
    container.add(calendarPanel, BorderLayout.NORTH);
    // at the centre of the container is the timePanel
    timePanel = new JPanel();
    // there are two components in there one for the time and one for the time
    // zone
    timePanel.setLayout(new GridLayout(2, 1));
    // create a SpinnerTimeModel
    final SpinnerDateModel timeModel = new SpinnerDateModel();
    // and set it to the displayed time
    timeModel.setValue(displayedDate.getTime());
    // create a time spinner with that SpinnerDateModel
    final JSpinner timeSpinner = new JSpinner(timeModel);
    // we specify an editor with a time only format
    final JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner,
        "HH:mm:ss"); //$NON-NLS-1$
    timeSpinner.setEditor(timeEditor);
    // TODO: The following line is what I want, but doesn't do what it should do
    JFormattedTextField textField = timeEditor.getTextField();
    textField.setHorizontalAlignment(SwingConstants.CENTER);
    // now we want to listen to changes in the time
    timeSpinner.addChangeListener(new ChangeListenerAdapter() {
      @Override
      public void stateChanged(ChangeEvent event) {
        super.stateChanged(event);
        JSpinner spinner = (JSpinner) event.getSource();
        try {
          // we accept the changes
          spinner.commitEdit();
          // create a new Calendar object with the correct time zone
          Calendar newDate = Calendar.getInstance(displayedDate.getTimeZone());
          // use the time from the spinner
          newDate.setTime((Date) spinner.getValue());
          // and the date from the displayedDate
          newDate.set(displayedDate.get(Calendar.YEAR), displayedDate
              .get(Calendar.MONTH), displayedDate.get(Calendar.DATE));
          // make sure the displayedDate is correct
          displayedDate = newDate;
        } catch (ParseException e) {
          e.printStackTrace();
        }
      }

    });
    timePanel.add(timeSpinner);
    // now for the time zone chooser
    ArrayList<String> timeZoneNames = new ArrayList<String>();
    currentZone = displayedDate.getTimeZone();
    timeZoneNames.add(currentZone.getID());
    for (int offset = -12; offset <= 12; offset++) {
      String name = "GMT"; //$NON-NLS-1$
      if (offset >= 0) {
        name += '+';
      }
      name += offset;
      timeZoneNames.add(TimeZone.getTimeZone(name).getID());
    }
    final JComboBox timezoneComboBox = new JComboBox(timeZoneNames.toArray()) {
      @Override
      public void actionPerformed(ActionEvent event) {
        currentZone = TimeZone.getTimeZone((String) getSelectedItem());
        Calendar newDisplayedDate = Calendar.getInstance(currentZone);
        newDisplayedDate.set(Calendar.DATE, displayedDate.get(Calendar.DATE));
        newDisplayedDate.set(Calendar.MONTH, displayedDate.get(Calendar.MONTH));
        newDisplayedDate.set(Calendar.YEAR, displayedDate.get(Calendar.YEAR));
        newDisplayedDate.set(Calendar.HOUR_OF_DAY, displayedDate
            .get(Calendar.HOUR_OF_DAY));
        newDisplayedDate.set(Calendar.MINUTE, displayedDate
            .get(Calendar.MINUTE));
        newDisplayedDate.set(Calendar.SECOND, displayedDate
            .get(Calendar.SECOND));
        displayedDate = newDisplayedDate;
        // make sure the formatter knows about the new time zone
        timeEditor.getFormat().setTimeZone(currentZone);
        // and update the display
        timeEditor.getTextField().setValue(displayedDate.getTime());
      }
    };
    timezoneComboBox.setSelectedIndex(0);
    timezoneComboBox.addActionListener(timezoneComboBox);
    timePanel.add(timezoneComboBox);
    // finally add the timePanel to the centre of the container
    container.add(timePanel, BorderLayout.CENTER);
    // at the bottom of the dialog we place two or three buttons
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, hasNowButton ? 3 : 2));
    buttonPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
    // A button to confirm the selected date/time
    JButton okButton = new JButton(Messages.getString("DateTimeChooser.OK")); //$NON-NLS-1$
    okButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        // store the time from the spinner
        Calendar displayedTime = Calendar.getInstance();
        displayedTime.setTimeZone(currentZone);
        displayedTime.setTime(timeModel.getDate());
        displayedDate.set(Calendar.HOUR_OF_DAY, displayedTime
            .get(Calendar.HOUR_OF_DAY));
        displayedDate.set(Calendar.MINUTE, displayedTime.get(Calendar.MINUTE));
        displayedDate.set(Calendar.SECOND, displayedTime.get(Calendar.SECOND));
        // close the chooser
        dispose();
      }
    });
    buttonPanel.add(okButton);
    JButton cancelButton = new JButton(Messages
        .getString("DateTimeChooser.Cancel")); //$NON-NLS-1$
    cancelButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        // set the date to null indicating no date was selected
        displayedDate = null;
        // close the chooser
        dispose();
      }
    });
    buttonPanel.add(cancelButton);
    JButton nowButton = new JButton(Messages.getString("DateTimeChooser.Now")); //$NON-NLS-1$
    nowButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        // set the date to 'now'
        displayedDate.setTime(new Date());
        timeModel.setValue(displayedDate.getTime());
        updateDateShown();
      }
    });
    if (hasNowButton) {
      buttonPanel.add(nowButton);
    }
    // add the buttonPanel at the bottom of the container
    container.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Return the n-th day of the week based on the correct first day of the week
   * of a given calendar
   * 
   * @param calendar
   *          The calendar used to determine the first day of the week (can be
   *          null)
   * @param n
   *          day of the week [1..7]
   * @return The {@link Calendar} constant representing that day
   */
  private int getNthDayOfWeek(Calendar calendar, int n) {
    int days[] = { Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
        Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY,
        Calendar.SATURDAY, Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
        Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY };
    int firstDayOfWeek = calendar == null ? Calendar.getInstance()
        .getFirstDayOfWeek() : calendar.getFirstDayOfWeek();
    switch (firstDayOfWeek) {
      case Calendar.SUNDAY:
        return days[n - 1];
      case Calendar.MONDAY:
        return days[n];
      case Calendar.TUESDAY:
        return days[n + 1];
      case Calendar.WEDNESDAY:
        return days[n + 2];
      case Calendar.THURSDAY:
        return days[n + 3];
      case Calendar.FRIDAY:
        return days[n + 4];
      case Calendar.SATURDAY:
        return days[n + 5];
    }
    // some default
    return n;
  }

  /**
   * Determine how many days into the week a given day is
   * 
   * @param calendar
   *          Calendar used to determine the first day of the week - can be null
   * @param day
   *          The day constant as defined by the {@link Calendar} class
   * @return the number (between 1 and 7) of how many days into the week we find
   *         this day
   */
  private int getDayOfWeek(Calendar calendar, int day) {
    for (int n = 1; n <= 7; n++) {
      if (getNthDayOfWeek(calendar, n) == day) {
        return n;
      }
    }
    return day;
  }

  /**
   * Update the display to represent the selected date
   */
  public void updateDateShown() {
    if (displayedDate == null) {
      displayedDate = Calendar.getInstance();
    }
    // Display the year
    yearLabel.setText(Integer.toString(displayedDate.get(Calendar.YEAR)));
    // highlight the correct month, don't highlight the others
    for (int month = 0; month < 12; month++) {
      if (month == displayedDate.get(Calendar.MONTH)) {
        monthLabels[month].setHighlighted(true);
      } else {
        monthLabels[month].setHighlighted(false);
      }
    }
    // determine the weekday of the first day of the month
    // Clone the date so we can fiddle with it
    Calendar firstDayOfMonth = (Calendar) displayedDate.clone();
    firstDayOfMonth.set(Calendar.DATE, 1);
    int dayOfWeek = getDayOfWeek(firstDayOfMonth, firstDayOfMonth
        .get(Calendar.DAY_OF_WEEK));
    // this tells us how many labels in the first row don't represent a day for
    // this month
    for (int col = 0; col < dayOfWeek - 1; col++) {
      dayLabels[0][col].setText(""); //$NON-NLS-1$
      dayLabels[0][col].setNumber(0); // non number stored with this label
    }
    int actualDaysInMonth = displayedDate
        .getActualMaximum(Calendar.DAY_OF_MONTH);
    // now we set up all the labels for the days in the month
    for (int day = 1; day <= actualDaysInMonth; day++) {
      int row = (dayOfWeek + day - 2) / 7;
      int column = (dayOfWeek + day - 2) % 7;
      dayLabels[row][column].setText(Integer.toString(day));
      // store the day number with the label
      // highlight the correct date, but not the others
      dayLabels[row][column].setNumber(day);
      if (day == displayedDate.get(Calendar.DATE)) {
        dayLabels[row][column].setHighlighted(true);
      } else {
        dayLabels[row][column].setHighlighted(false);
      }
    }
    // clear the remaining labels
    for (int position = dayOfWeek + actualDaysInMonth - 1; position < 6 * 7; position++) {
      int row = position / 7;
      int column = position % 7;
      dayLabels[row][column].setText(""); //$NON-NLS-1$
      // don't store a day number with this label
      dayLabels[row][column].setNumber(0);
    }
  }

  /**
   * Pop up the {@link DateTimeChooser} dialog initially displaying the given
   * date. Named after the method in {@link JFileChooser}
   * 
   * @param date
   *          The initial date
   * @return The selected date and time or null if none selected
   */
  public Calendar openChooser(Calendar date) {
    displayedDate = date;
    return openChooser();
  }

  /**
   * Pop up the {@link DateTimeChooser} dialog,.
   * 
   * @return The selected date and time or null if none selected
   */
  public Calendar openChooser() {
    updateDateShown();
    pack();
    setResizable(false);
    setLocationRelativeTo(parent);
    setVisible(true);
    return displayedDate;
  }

  /**
   * @author Andreas Schneider
   * 
   */
  class ChangeListenerAdapter implements ChangeListener {

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
      // override in instances
    }

  }

  /**
   * A custom label class that has a few extras
   * 
   * @author Andreas Schneider
   * 
   */
  @SuppressWarnings("serial")
  class CalendarLabel extends JLabel {
    /** This represents the day, month or year displayed by the label */
    private int number;

    /** The original default foreground colour of the label */
    private Color defaultForegroundColour;

    /**
     * Create a new {@link CalendarLabel}
     * 
     * @param text
     *          The initial text for the label
     * @param bold
     *          Make the label use a bold font or not
     * @param big
     *          Make the label use a bigger font or not
     */
    public CalendarLabel(String text, boolean bold, boolean big) {
      super(text, SwingConstants.CENTER);
      setFontStyle(bold ? Font.BOLD : Font.PLAIN);
      defaultForegroundColour = this.getForeground();
      if (big) {
        int size = this.getFont().getSize();
        this.setFont(this.getFont().deriveFont((float) (size * 1.5)));
      }
      this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }

    /**
     * Change the {@link Font} to one with the selected style
     * 
     * @param style
     *          the Font style (e.g Font.PLAIN or Font.BOLD)
     */
    private void setFontStyle(int style) {
      this.setFont(this.getFont().deriveFont(style));
    }

    /**
     * Highlight this label by settings its foreground colour to red or back to
     * the default foreground colour if not highlighted.
     * 
     * @param highlighted
     */
    public void setHighlighted(boolean highlighted) {
      this.setForeground(highlighted ? Color.RED : defaultForegroundColour);
      setFontStyle(highlighted ? Font.BOLD : Font.PLAIN);
    }

    /**
     * @return The number associated with this label
     */
    public int getNumber() {
      return number;
    }

    /**
     * @param number
     *          The number to be associated with this label
     */
    public void setNumber(int number) {
      this.number = number;
    }
  }

}
