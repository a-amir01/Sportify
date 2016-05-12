package gspot.com.sportify.Model;

import java.util.ArrayList;
import java.util.List;

/** GSpot Calendar
 * Represents a calendar indicating a user's
 * times of availability.
 * Created by patrickhayes on 5/3/16.
 */
public class GspotCalendar {

    /* Represents the times that the user is available */
    private List<List<Boolean>> calendarGrid;

    /* Magic Numbers */
    private static final int NUM_TIMES_OF_DAY = 4;
    private static final int NUM_DAYS_OF_WEEK = 7;

    /* Create GSpot Calendar with default values */
    public GspotCalendar() {
        calendarGrid = new ArrayList<List<Boolean>>(7);

        /* For all time cells, the user is available */
        for( int i= 0; i < NUM_DAYS_OF_WEEK; ++i) {
            calendarGrid.add(new ArrayList<Boolean>(4));
            for (int j=0; j < NUM_TIMES_OF_DAY; ++j) {
                calendarGrid.get(i).add(true);
            }
        }
    }

    /* Get the calendar data */
    public List<List<Boolean>> getCalendarGrid() {
        return calendarGrid;
    }

    /** Toggle Time Method
     * Toggles the time to available/busy at a specific time of
     * the day and a specific day of the week.
     * @param timeOfDay specified time of the day to toggle (0-3)
     * @param dayOfWeek specified day of the week to toggle (0-6)
     */
    public void toggleTime(int dayOfWeek, int timeOfDay) {

        /* See if user is available at this time and day */
        Boolean available = calendarGrid.get(dayOfWeek).get(timeOfDay);

        /* Toggle the availability at this time and day */
        calendarGrid.get(dayOfWeek).set(timeOfDay, !available);
    }

    /** Set Availability Method
     * Sets the a specified time to be set as
     * 'available' or 'busy'.
     * @param availability specified time of the day to set
     * @param timeOfDay specified time of the day to set (0-3)
     * @param dayOfWeek specified day of the week to toggle (0-6)
     */
    public void setAvailability(boolean availability, int dayOfWeek, int timeOfDay) {
        calendarGrid.get(dayOfWeek).set(timeOfDay, availability);
    }

    /** Get Availability Method
     * Gets the a specified time to be set as
     * 'available' or 'busy'.
     * @params dayOfWeek - the day of the week (0-6)
     * @params timeOfDay - the time of the day (0-3)
     */
    public boolean getAvailability(int dayOfWeek, int timeOfDay) {
        return calendarGrid.get(dayOfWeek).get(timeOfDay);
    }

}