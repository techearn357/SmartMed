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

/** Repository for schedule operations. */
public class ScheduleRepository {
    private final SmartMedApi api;

    public ScheduleRepository() {
        this.api = ApiClient.getApiService();
    }

    public LiveData<Resource<Schedule>> createSchedule(Schedule schedule) {
        MutableLiveData<Resource<Schedule>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        api.createSchedule(schedule).enqueue(new Callback<ApiResponse<Schedule>>() {
            @Override
            public void onResponse(Call<ApiResponse<Schedule>> call, Response<ApiResponse<Schedule>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to create schedule", null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Schedule>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<Schedule>>> getSchedules() {
        MutableLiveData<Resource<List<Schedule>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        api.getSchedules().enqueue(new Callback<ApiResponse<List<Schedule>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Schedule>>> call, Response<ApiResponse<List<Schedule>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to load schedules", null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Schedule>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<Schedule>>> getTodaySchedules() {
        MutableLiveData<Resource<List<Schedule>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        api.getTodaySchedules().enqueue(new Callback<ApiResponse<List<Schedule>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Schedule>>> call, Response<ApiResponse<List<Schedule>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to load today's schedules", null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Schedule>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return result;
    }
}
