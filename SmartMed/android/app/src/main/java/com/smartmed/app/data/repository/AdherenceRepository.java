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

/** Repository for adherence data. */
public class AdherenceRepository {
    private final SmartMedApi api;

    public AdherenceRepository() {
        this.api = ApiClient.getApiService();
    }

    public LiveData<Resource<Adherence>> getAdherenceSummary(String period) {
        MutableLiveData<Resource<Adherence>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        api.getAdherenceSummary(period).enqueue(new Callback<ApiResponse<Adherence>>() {
            @Override
            public void onResponse(Call<ApiResponse<Adherence>> call, Response<ApiResponse<Adherence>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to load adherence", null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Adherence>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<Adherence>>> getAdherenceHistory(String period) {
        MutableLiveData<Resource<List<Adherence>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        api.getAdherence(period).enqueue(new Callback<ApiResponse<List<Adherence>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Adherence>>> call, Response<ApiResponse<List<Adherence>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to load adherence history", null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Adherence>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return result;
    }
}
