package com.smartmed.app.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.smartmed.app.utils.Constants;

/**
 * BroadcastReceiver that fires when AlarmManager triggers a medicine reminder.
 * Shows notification with Take/Snooze/Miss actions and reschedules for next day.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineId = intent.getStringExtra(Constants.EXTRA_MEDICINE_ID);
        String medicineName = intent.getStringExtra(Constants.EXTRA_MEDICINE_NAME);
        String dosage = intent.getStringExtra(Constants.EXTRA_MEDICINE_DOSAGE);
        String time = intent.getStringExtra(Constants.EXTRA_ALARM_TIME);
        boolean isSnoozed = intent.getBooleanExtra(Constants.EXTRA_IS_SNOOZED, false);

        if (medicineId == null || medicineName == null) return;

        // Show notification
        ReminderNotification.showMedicineReminder(
                context, medicineId, medicineName, dosage, time, isSnoozed);

        // Reschedule for next day (only if not a snooze)
        if (!isSnoozed) {
            AlarmScheduler scheduler = new AlarmScheduler(context);
            scheduler.rescheduleForNextDay(medicineId, medicineName, dosage, time);
        }
    }
}
