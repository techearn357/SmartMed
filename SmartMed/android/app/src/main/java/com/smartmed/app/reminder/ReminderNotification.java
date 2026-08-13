package com.smartmed.app.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.smartmed.app.R;
import com.smartmed.app.utils.Constants;

/**
 * Creates rich notifications for medicine reminders with action buttons.
 */
public class ReminderNotification {

    public static void showMedicineReminder(Context context, String medicineId,
                                             String medicineName, String dosage,
                                             String time, boolean isSnoozed) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = (medicineId + "_" + time).hashCode() & 0x7FFFFFFF;

        // Build notification title/body
        String title = isSnoozed ? "⏰ Snoozed Reminder" : "💊 Time for your medicine!";
        String body = medicineName + (dosage != null ? " - " + dosage : "");

        // Taken action
        Intent takenIntent = new Intent(context, NotificationActionReceiver.class);
        takenIntent.setAction(Constants.ACTION_TAKEN);
        takenIntent.putExtra(Constants.EXTRA_MEDICINE_ID, medicineId);
        takenIntent.putExtra(Constants.EXTRA_MEDICINE_NAME, medicineName);
        takenIntent.putExtra(Constants.EXTRA_MEDICINE_DOSAGE, dosage);
        takenIntent.putExtra(Constants.EXTRA_ALARM_TIME, time);
        takenIntent.putExtra(Constants.EXTRA_NOTIFICATION_ID, notificationId);
        PendingIntent takenPI = PendingIntent.getBroadcast(context, notificationId * 10 + 1,
                takenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Snooze action
        Intent snoozeIntent = new Intent(context, NotificationActionReceiver.class);
        snoozeIntent.setAction(Constants.ACTION_SNOOZE);
        snoozeIntent.putExtra(Constants.EXTRA_MEDICINE_ID, medicineId);
        snoozeIntent.putExtra(Constants.EXTRA_MEDICINE_NAME, medicineName);
        snoozeIntent.putExtra(Constants.EXTRA_MEDICINE_DOSAGE, dosage);
        snoozeIntent.putExtra(Constants.EXTRA_ALARM_TIME, time);
        snoozeIntent.putExtra(Constants.EXTRA_NOTIFICATION_ID, notificationId);
        PendingIntent snoozePI = PendingIntent.getBroadcast(context, notificationId * 10 + 2,
                snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Missed action
        Intent missedIntent = new Intent(context, NotificationActionReceiver.class);
        missedIntent.setAction(Constants.ACTION_MISSED);
        missedIntent.putExtra(Constants.EXTRA_MEDICINE_ID, medicineId);
        missedIntent.putExtra(Constants.EXTRA_MEDICINE_NAME, medicineName);
        missedIntent.putExtra(Constants.EXTRA_MEDICINE_DOSAGE, dosage);
        missedIntent.putExtra(Constants.EXTRA_ALARM_TIME, time);
        missedIntent.putExtra(Constants.EXTRA_NOTIFICATION_ID, notificationId);
        PendingIntent missedPI = PendingIntent.getBroadcast(context, notificationId * 10 + 3,
                missedIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_MEDICINE_REMINDER)
                .setSmallIcon(android.R.drawable.ic_menu_agenda)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body + "\n\nTap an action below:"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .addAction(android.R.drawable.ic_menu_agenda, "✅ Taken", takenPI)
                .addAction(android.R.drawable.ic_menu_recent_history, "⏰ Snooze", snoozePI)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "❌ Missed", missedPI);

        manager.notify(notificationId, builder.build());
    }

    /**
     * Shows a low-stock warning notification.
     */
    public static void showLowStockWarning(Context context, String medicineName, int remaining) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = ("stock_" + medicineName).hashCode() & 0x7FFFFFFF;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_LOW_STOCK)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("⚠️ Low Medicine Stock")
                .setContentText(medicineName + " - Only " + remaining + " tablets remaining!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        manager.notify(notificationId, builder.build());
    }

    /**
     * Shows a caregiver alert notification.
     */
    public static void showCaregiverAlert(Context context, String medicineName, int missedCount) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = ("caregiver_" + medicineName).hashCode() & 0x7FFFFFFF;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_CAREGIVER_ALERT)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("🚨 Caregiver Alert Sent")
                .setContentText(medicineName + " has been missed " + missedCount + " consecutive times.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(notificationId, builder.build());
    }
}
