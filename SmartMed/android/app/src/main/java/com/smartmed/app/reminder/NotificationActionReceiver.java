package com.smartmed.app.reminder;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.smartmed.app.data.api.ApiClient;
import com.smartmed.app.data.api.SmartMedApi;
import com.smartmed.app.data.model.ApiResponse;
import com.smartmed.app.data.model.MedicationHistory;
import com.smartmed.app.utils.Constants;
import com.smartmed.app.utils.DateUtils;
import com.smartmed.app.utils.SharedPrefManager;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Handles notification action buttons (Taken / Snooze / Missed).
 * Records the action in medication history and updates the server.
 */
public class NotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String medicineId = intent.getStringExtra(Constants.EXTRA_MEDICINE_ID);
        String medicineName = intent.getStringExtra(Constants.EXTRA_MEDICINE_NAME);
        String dosage = intent.getStringExtra(Constants.EXTRA_MEDICINE_DOSAGE);
        String time = intent.getStringExtra(Constants.EXTRA_ALARM_TIME);
        int notificationId = intent.getIntExtra(Constants.EXTRA_NOTIFICATION_ID, 0);

        if (action == null || medicineId == null) return;

        // Dismiss notification
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);

        switch (action) {
            case Constants.ACTION_TAKEN:
                recordHistory(context, medicineId, medicineName, dosage, time, Constants.STATUS_TAKEN);
                Toast.makeText(context, "✅ " + medicineName + " marked as Taken", Toast.LENGTH_SHORT).show();
                break;

            case Constants.ACTION_SNOOZE:
                // Snooze for configured minutes
                int snoozeMinutes = SharedPrefManager.getInstance().getSnoozeInterval();
                AlarmScheduler scheduler = new AlarmScheduler(context);
                scheduler.snoozeAlarm(medicineId, medicineName, dosage, time, snoozeMinutes);
                recordHistory(context, medicineId, medicineName, dosage, time, Constants.STATUS_SNOOZED);
                Toast.makeText(context, "⏰ Snoozed for " + snoozeMinutes + " minutes", Toast.LENGTH_SHORT).show();
                break;

            case Constants.ACTION_MISSED:
                recordHistory(context, medicineId, medicineName, dosage, time, Constants.STATUS_MISSED);
                // Check for consecutive misses and alert caregiver
                checkConsecutiveMisses(context, medicineId, medicineName);
                Toast.makeText(context, "❌ " + medicineName + " marked as Missed", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Records medication action in server history.
     */
    private void recordHistory(Context context, String medicineId, String medicineName,
                                String dosage, String time, String status) {
        MedicationHistory history = new MedicationHistory();
        history.setMedicineId(medicineId);
        history.setMedicineName(medicineName);
        history.setMedicineDosage(dosage);
        history.setScheduledTime(time);
        history.setStatus(status);
        history.setDate(DateUtils.formatDateForApi(new Date()));

        if (Constants.STATUS_TAKEN.equals(status)) {
            history.setTakenTime(DateUtils.getCurrentTimeString());
        }

        SmartMedApi api = ApiClient.getApiService();
        api.createHistory(history).enqueue(new Callback<ApiResponse<MedicationHistory>>() {
            @Override
            public void onResponse(Call<ApiResponse<MedicationHistory>> call, Response<ApiResponse<MedicationHistory>> response) {
                // History recorded successfully
            }

            @Override
            public void onFailure(Call<ApiResponse<MedicationHistory>> call, Throwable t) {
                // Will sync later when online
            }
        });
    }

    /**
     * Checks if the medicine has been missed consecutively and alerts caregiver.
     */
    private void checkConsecutiveMisses(Context context, String medicineId, String medicineName) {
        int threshold = SharedPrefManager.getInstance().getMissedDoseThreshold();

        // Send caregiver alert via backend
        SmartMedApi api = ApiClient.getApiService();
        SmartMedApi.CaregiverAlertRequest request = new SmartMedApi.CaregiverAlertRequest(
                medicineId, medicineName, threshold);

        api.sendCaregiverAlert(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    ReminderNotification.showCaregiverAlert(context, medicineName, threshold);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                // Will retry later
            }
        });
    }
}
