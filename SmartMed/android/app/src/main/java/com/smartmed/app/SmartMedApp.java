package com.smartmed.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.FirebaseApp;
import com.smartmed.app.utils.SharedPrefManager;

/**
 * SmartMed Application class.
 * Initializes Firebase, creates notification channels, and sets up global state.
 */
public class SmartMedApp extends Application {

    public static final String CHANNEL_REMINDER = "medicine_reminders";
    public static final String CHANNEL_ALERT = "medication_alerts";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase safely
        try {
            FirebaseApp.initializeApp(this);
        } catch (Exception e) {
            android.util.Log.w("SmartMedApp", "Firebase initialization skipped: " + e.getMessage());
        }

        // Initialize SharedPreferences manager
        SharedPrefManager.init(this);

        // Create notification channels
        createNotificationChannels();
    }

    /**
     * Creates notification channels for medicine reminders and alerts.
     * Required for Android 8.0 (API 26) and above.
     */
    private void createNotificationChannels() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            // Medicine Reminder Channel
            NotificationChannel reminderChannel = new NotificationChannel(
                    com.smartmed.app.utils.Constants.CHANNEL_MEDICINE_REMINDER,
                    "Medicine Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            reminderChannel.setDescription("Notifications and alarms for scheduled medication doses");
            reminderChannel.enableVibration(true);
            reminderChannel.setVibrationPattern(new long[]{0, 500, 200, 500});
            reminderChannel.enableLights(true);
            reminderChannel.setShowBadge(true);

            // Low Stock Channel
            NotificationChannel stockChannel = new NotificationChannel(
                    com.smartmed.app.utils.Constants.CHANNEL_LOW_STOCK,
                    "Low Medicine Stock",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            // Caregiver Alert Channel
            NotificationChannel alertChannel = new NotificationChannel(
                    com.smartmed.app.utils.Constants.CHANNEL_CAREGIVER_ALERT,
                    "Caregiver Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );

            manager.createNotificationChannel(reminderChannel);
            manager.createNotificationChannel(stockChannel);
            manager.createNotificationChannel(alertChannel);
        }
    }
}
