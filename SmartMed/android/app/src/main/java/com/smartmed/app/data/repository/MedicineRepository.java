package com.smartmed.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smartmed.app.data.api.ApiClient;
import com.smartmed.app.data.api.SmartMedApi;
import com.smartmed.app.data.model.ApiResponse;
import com.smartmed.app.data.model.Medicine;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for medicine CRUD operations.
 */
public class MedicineRepository {

    private final SmartMedApi api;

    public MedicineRepository() {
        this.api = ApiClient.getApiService();
    }

    public LiveData<Resource<List<Medicine>>> getMedicines() {
        MutableLiveData<Resource<List<Medicine>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        api.getMedicines().enqueue(new Callback<ApiResponse<List<Medicine>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Medicine>>> call, Response<ApiResponse<List<Medicine>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to load medicines", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Medicine>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Medicine>> getMedicine(String id) {
        MutableLiveData<Resource<Medicine>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        api.getMedicine(id).enqueue(new Callback<ApiResponse<Medicine>>() {
            @Override
            public void onResponse(Call<ApiResponse<Medicine>> call, Response<ApiResponse<Medicine>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to load medicine", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Medicine>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Medicine>> createMedicine(Medicine medicine) {
        MutableLiveData<Resource<Medicine>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        api.createMedicine(medicine).enqueue(new Callback<ApiResponse<Medicine>>() {
            @Override
            public void onResponse(Call<ApiResponse<Medicine>> call, Response<ApiResponse<Medicine>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to create medicine", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Medicine>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Medicine>> updateMedicine(String id, Medicine medicine) {
        MutableLiveData<Resource<Medicine>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        api.updateMedicine(id, medicine).enqueue(new Callback<ApiResponse<Medicine>>() {
            @Override
            public void onResponse(Call<ApiResponse<Medicine>> call, Response<ApiResponse<Medicine>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to update medicine", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Medicine>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Void>> deleteMedicine(String id) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        api.deleteMedicine(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(null));
                } else {
                    result.setValue(Resource.error("Failed to delete medicine", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
