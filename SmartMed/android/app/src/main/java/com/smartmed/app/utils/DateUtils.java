package com.smartmed.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Date and time utility methods.
 */
public final class DateUtils {

    private DateUtils() {} // Prevent instantiation

    public static String getCurrentTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Formats a Date to display format (e.g., "Aug 10, 2026").
     */
    public static String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Formats a Date to API format (e.g., "2026-08-10").
     */
    public static String formatDateForApi(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_API, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Formats a Date to time display format (e.g., "08:00 AM").
     */
    public static String formatTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIME_FORMAT_DISPLAY, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Formats a time string from 24h to 12h format.
     */
    public static String formatTimeString(String time24h) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date date = input.parse(time24h);
            return date != null ? output.format(date) : time24h;
        } catch (ParseException e) {
            return time24h;
        }
    }

    /**
     * Formats a Date to full display format (e.g., "Aug 10, 2026 08:00 AM").
     */
    public static String formatDateTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATETIME_FORMAT_DISPLAY, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Parses an API date string to Date object.
     */
    public static Date parseApiDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATETIME_FORMAT_API, Locale.getDefault());
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_API, Locale.getDefault());
                return sdf.parse(dateStr);
            } catch (ParseException e2) {
                return null;
            }
        }
    }

    /**
     * Returns a greeting based on current time of day.
     */
    public static String getGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            return "Good Morning";
        } else if (hour < 17) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }

    /**
     * Returns today's date formatted for display.
     */
    public static String getTodayFormatted() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Checks if a date is today.
     */
    public static boolean isToday(Date date) {
        if (date == null) return false;
        Calendar today = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTime(date);
        return today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Returns the start of today as a Date.
     */
    public static Date getStartOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Returns the end of today as a Date.
     */
    public static Date getEndOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * Returns the start of N days ago.
     */
    public static Date getDaysAgo(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Creates a Calendar for a specific time today from a time string (HH:mm).
     */
    public static Calendar getCalendarForTime(String timeStr) {
        Calendar cal = Calendar.getInstance();
        try {
            String[] parts = timeStr.split(":");
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } catch (Exception e) {
            // Return current time if parsing fails
        }
        return cal;
    }

    /**
     * Calculates the number of days remaining given remaining tablets and tablets per dose per day.
     */
    public static int calculateDaysRemaining(int remainingTablets, int tabletsPerDose, int dosesPerDay) {
        if (tabletsPerDose <= 0 || dosesPerDay <= 0) return 0;
        int dailyConsumption = tabletsPerDose * dosesPerDay;
        return remainingTablets / dailyConsumption;
    }

    /**
     * Returns minutes difference between two times.
     */
    public static long getMinutesDifference(Date date1, Date date2) {
        if (date1 == null || date2 == null) return 0;
        long diffMs = Math.abs(date2.getTime() - date1.getTime());
        return TimeUnit.MILLISECONDS.toMinutes(diffMs);
    }

    /**
     * Gets a relative time string (e.g., "5 minutes ago", "in 2 hours").
     */
    public static String getRelativeTimeString(Date date) {
        if (date == null) return "";
        long now = System.currentTimeMillis();
        long time = date.getTime();
        long diff = now - time;

        if (diff > 0) {
            // Past
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            if (minutes < 1) return "Just now";
            if (minutes < 60) return minutes + " min ago";
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            if (hours < 24) return hours + " hr ago";
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else {
            // Future
            long minutes = TimeUnit.MILLISECONDS.toMinutes(-diff);
            if (minutes < 1) return "Now";
            if (minutes < 60) return "in " + minutes + " min";
            long hours = TimeUnit.MILLISECONDS.toHours(-diff);
            if (hours < 24) return "in " + hours + " hr";
            long days = TimeUnit.MILLISECONDS.toDays(-diff);
            return "in " + days + " day" + (days > 1 ? "s" : "");
        }
    }
}
