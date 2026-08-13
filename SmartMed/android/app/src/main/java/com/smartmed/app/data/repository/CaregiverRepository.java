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

/** Repository for caregiver operations. */
public class CaregiverRepository {
    private final SmartMedApi api;
    public CaregiverRepository() { this.api = ApiClient.getApiService(); }

    public LiveData<Resource<List<Caregiver>>> getCaregivers() {
        MutableLiveData<Resource<List<Caregiver>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        api.getCaregivers().enqueue(new Callback<ApiResponse<List<Caregiver>>>() {
            @Override public void onResponse(Call<ApiResponse<List<Caregiver>>> call, Response<ApiResponse<List<Caregiver>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    String msg = (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Failed to load caregivers";
                    result.setValue(Resource.error(msg, null));
                }
            }
            @Override public void onFailure(Call<ApiResponse<List<Caregiver>>> call, Throwable t) { result.setValue(Resource.error("Network error: " + t.getMessage(), null)); }
        });
        return result;
    }

    public LiveData<Resource<Caregiver>> createCaregiver(Caregiver caregiver) {
        MutableLiveData<Resource<Caregiver>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        api.createCaregiver(caregiver).enqueue(new Callback<ApiResponse<Caregiver>>() {
            @Override public void onResponse(Call<ApiResponse<Caregiver>> call, Response<ApiResponse<Caregiver>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    String msg = (response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Failed to add caregiver";
                    result.setValue(Resource.error(msg, null));
                }
            }
            @Override public void onFailure(Call<ApiResponse<Caregiver>> call, Throwable t) { result.setValue(Resource.error("Network error: " + t.getMessage(), null)); }
        });
        return result;
    }

    public LiveData<Resource<Void>> deleteCaregiver(String id) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        api.deleteCaregiver(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) result.setValue(Resource.success(null));
                else result.setValue(Resource.error("Failed to delete caregiver", null));
            }
            @Override public void onFailure(Call<ApiResponse<Void>> call, Throwable t) { result.setValue(Resource.error("Network error: " + t.getMessage(), null)); }
        });
        return result;
    }

    public LiveData<Resource<Void>> sendCaregiverAlert(String medicineId, String medicineName, int missedCount) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        SmartMedApi.CaregiverAlertRequest request = new SmartMedApi.CaregiverAlertRequest(medicineId, medicineName, missedCount);
        api.sendCaregiverAlert(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) result.setValue(Resource.success(null));
                else result.setValue(Resource.error("Failed to send alert", null));
            }
            @Override public void onFailure(Call<ApiResponse<Void>> call, Throwable t) { result.setValue(Resource.error("Network error: " + t.getMessage(), null)); }
        });
        return result;
    }
}
