package com.smartmed.app.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.smartmed.app.data.api.ApiClient;
import com.smartmed.app.data.api.SmartMedApi;
import com.smartmed.app.data.model.ApiResponse;
import com.smartmed.app.data.model.Schedule;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Re-schedules all alarms after device reboot.
 * Registered in AndroidManifest for BOOT_COMPLETED.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) return;
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        // Fetch active schedules and re-create alarms
        SmartMedApi api = ApiClient.getApiService();
        api.getSchedules().enqueue(new Callback<ApiResponse<List<Schedule>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Schedule>>> call,
                                   Response<ApiResponse<List<Schedule>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess() && response.body().getData() != null) {
                    AlarmScheduler scheduler = new AlarmScheduler(context);
                    for (Schedule schedule : response.body().getData()) {
                        if (schedule.isActive()) {
                            scheduler.scheduleAlarm(
                                    schedule.getMedicineId(),
                                    schedule.getMedicineName(),
                                    schedule.getMedicineDosage(),
                                    schedule.getScheduledTime());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Schedule>>> call, Throwable t) {
                // Will sync when app is next opened
            }
        });
    }
}
