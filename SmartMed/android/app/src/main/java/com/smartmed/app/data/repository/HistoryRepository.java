package com.smartmed.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.smartmed.app.data.api.ApiClient;
import com.smartmed.app.data.api.SmartMedApi;
import com.smartmed.app.data.model.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Repository for medication history operations. */
public class HistoryRepository {
    private final SmartMedApi api;

    public HistoryRepository() {
        this.api = ApiClient.getApiService();
    }

    public LiveData<Resource<MedicationHistory>> createHistory(MedicationHistory history) {
        MutableLiveData<Resource<MedicationHistory>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        api.createHistory(history).enqueue(new Callback<ApiResponse<MedicationHistory>>() {
            @Override
            public void onResponse(Call<ApiResponse<MedicationHistory>> call, Response<ApiResponse<MedicationHistory>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to record history", null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<MedicationHistory>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<MedicationHistory>>> getHistory(String startDate, String endDate, String medicineId) {
        MutableLiveData<Resource<List<MedicationHistory>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        api.getHistory(startDate, endDate, medicineId).enqueue(new Callback<ApiResponse<List<MedicationHistory>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MedicationHistory>>> call, Response<ApiResponse<List<MedicationHistory>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to load history", null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<MedicationHistory>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<MedicationHistory>>> getTodayHistory() {
        MutableLiveData<Resource<List<MedicationHistory>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        api.getTodayHistory().enqueue(new Callback<ApiResponse<List<MedicationHistory>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MedicationHistory>>> call, Response<ApiResponse<List<MedicationHistory>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to load today's history", null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<MedicationHistory>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return result;
    }
}
