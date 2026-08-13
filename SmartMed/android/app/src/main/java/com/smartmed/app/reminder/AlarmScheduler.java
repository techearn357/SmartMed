package com.smartmed.app.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.smartmed.app.utils.Constants;

import java.util.Calendar;

/**
 * Schedules exact alarms for medicine reminders using AlarmManager.
 * Handles alarm creation, cancellation, and snooze.
 */
public class AlarmScheduler {

    private final Context context;
    private final AlarmManager alarmManager;

    public AlarmScheduler(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Schedules a repeating daily alarm for a medicine at the specified time.
     */
    public void scheduleAlarm(String medicineId, String medicineName, String dosage, String time) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Constants.EXTRA_MEDICINE_ID, medicineId);
        intent.putExtra(Constants.EXTRA_MEDICINE_NAME, medicineName);
        intent.putExtra(Constants.EXTRA_MEDICINE_DOSAGE, dosage);
        intent.putExtra(Constants.EXTRA_ALARM_TIME, time);

        int requestCode = getRequestCode(medicineId, time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Parse time
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If time is in the past, schedule for tomorrow
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Schedule exact alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAlarmClock(
                        new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent),
                        pendingIntent);
            } else {
                // Fallback to inexact alarm
                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } else {
            alarmManager.setAlarmClock(
                    new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent),
                    pendingIntent);
        }
    }

    /**
     * Cancels a scheduled alarm.
     */
    public void cancelAlarm(String medicineId, String time) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        int requestCode = getRequestCode(medicineId, time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    /**
     * Snoozes an alarm by a specified number of minutes.
     */
    public void snoozeAlarm(String medicineId, String medicineName, String dosage, String time, int snoozeMinutes) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Constants.EXTRA_MEDICINE_ID, medicineId);
        intent.putExtra(Constants.EXTRA_MEDICINE_NAME, medicineName);
        intent.putExtra(Constants.EXTRA_MEDICINE_DOSAGE, dosage);
        intent.putExtra(Constants.EXTRA_ALARM_TIME, time);
        intent.putExtra(Constants.EXTRA_IS_SNOOZED, true);

        int requestCode = getRequestCode(medicineId, time) + 1000; // Different code for snooze

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L);

        alarmManager.setAlarmClock(
                new AlarmManager.AlarmClockInfo(triggerTime, pendingIntent),
                pendingIntent);
    }

    /**
     * Reschedules alarm for the next day (called after alarm fires).
     */
    public void rescheduleForNextDay(String medicineId, String medicineName, String dosage, String time) {
        scheduleAlarm(medicineId, medicineName, dosage, time);
    }

    /**
     * Generates a unique request code from medicine ID and time.
     */
    private int getRequestCode(String medicineId, String time) {
        return (medicineId + "_" + time).hashCode() & 0x7FFFFFFF;
    }
}
