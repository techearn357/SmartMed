package com.smartmed.app.data.api;

import com.smartmed.app.data.model.AiSuggestion;
import com.smartmed.app.data.model.ApiResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * Retrofit interface for the AI FastAPI service.
 */
public interface AiApi {

    @POST("api/suggestions")
    Call<ApiResponse<List<AiSuggestion>>> getSuggestions(@Body Map<String, String> request);

    @POST("api/analyze-pattern")
    Call<ApiResponse<AiSuggestion>> analyzePattern(@Body AnalyzeRequest request);

    class AnalyzeRequest {
        private String userId;
        private String medicineId;

        public AnalyzeRequest(String userId, String medicineId) {
            this.userId = userId;
            this.medicineId = medicineId;
        }

        public String getUserId() { return userId; }
        public String getMedicineId() { return medicineId; }
    }
}
