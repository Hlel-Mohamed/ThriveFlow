package de.tekup.thriveflow.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * CalendarUtils is a utility class that provides methods for handling dates and times.
 * It includes methods for formatting dates and times, getting the month and year from a date, and getting the days in a month.
 */
public class CalendarUtils {
    // The selected date
    public static LocalDate selectedDate;

    /**
     * This method formats a date into a string in the format "dd MMMM yyyy".
     *
     * @param date The date to be formatted.
     * @return The formatted date string.
     */
    public static String formattedDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return date.format(formatter);
    }

    /**
     * This method formats a time into a string in the format "HH:mm".
     *
     * @param time The time to be formatted.
     * @return The formatted time string.
     */
    public static String formattedTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    /**
     * This method gets the month and year from a date and formats it into a string in the format "MMMM yyyy".
     *
     * @param date The date from which to get the month and year.
     * @return The formatted month and year string.
     */
    public static String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    /**
     * This method calculates and returns an ArrayList of LocalDate objects representing a full calendar month view.
     * The ArrayList includes dates from the previous month and the next month, if necessary, to fill a total of 42 days (6 weeks).
     * This is useful for populating a monthly calendar view, where each week is a row of 7 days.
     * <p>
     * The method uses the static selectedDate field to determine the month to display.
     *
     * @return An ArrayList of LocalDate objects for each day to be displayed in a monthly calendar view.
     */
    public static ArrayList<LocalDate> daysInMonthArray() {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();

        // Get the YearMonth object for the selected date
        YearMonth yearMonth = YearMonth.from(selectedDate);
        // Get the number of days in the selected month
        int daysInMonth = yearMonth.lengthOfMonth();

        // Get the previous and next months
        LocalDate prevMonth = selectedDate.minusMonths(1);
        LocalDate nextMonth = selectedDate.plusMonths(1);

        // Get the YearMonth object for the previous month and the number of days in the previous month
        YearMonth prevYearMonth = YearMonth.from(prevMonth);
        int prevDaysInMonth = prevYearMonth.lengthOfMonth();

        // Get the first day of the selected month and its day of the week (1 = Monday, 7 = Sunday)
        LocalDate firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        // Loop through a total of 42 days
        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek) {
                // If the current day number is less than or equal to the day of the week of the first day of the selected month,
                // add a date from the end of the previous month
                daysInMonthArray.add(LocalDate.of(prevMonth.getYear(), prevMonth.getMonth(), prevDaysInMonth + i - dayOfWeek));
            } else if (i > daysInMonth + dayOfWeek) {
                // If the current day number is greater than the sum of the number of days in the selected month and the day of the week of the first day of the selected month,
                // add a date from the beginning of the next month
                daysInMonthArray.add(LocalDate.of(nextMonth.getYear(), nextMonth.getMonth(), i - dayOfWeek - daysInMonth));
            } else {
                // Otherwise, add a date from the selected month
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));
            }
        }

        return daysInMonthArray;
    }

}