package com.smartmed.app.service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.smartmed.app.reminder.ReminderNotification;
import com.smartmed.app.utils.SharedPrefManager;

import java.util.Map;

/**
 * Firebase Cloud Messaging Service for handling incoming push notifications
 * (e.g. Caregiver alerts, low stock warnings, AI reminder adjustments).
 */
public class SmartMedFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Save FCM token for sending to backend on login
        SharedPrefManager.getInstance().saveFcmToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        if (data.isEmpty()) return;

        String type = data.get("type");
        String title = data.get("title");
        String message = data.get("message");

        if ("CAREGIVER_ALERT".equals(type)) {
            String medName = data.get("medicineName");
            int missedCount = data.containsKey("missedCount") ? Integer.parseInt(data.get("missedCount")) : 3;
            ReminderNotification.showCaregiverAlert(this, medName != null ? medName : "Medication", missedCount);
        } else if ("LOW_STOCK".equals(type)) {
            String medName = data.get("medicineName");
            int remaining = data.containsKey("remaining") ? Integer.parseInt(data.get("remaining")) : 5;
            ReminderNotification.showLowStockWarning(this, medName != null ? medName : "Medication", remaining);
        }
    }
}
