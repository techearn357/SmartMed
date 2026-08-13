package com.smartmed.app.data.api;

import com.smartmed.app.data.model.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Retrofit API interface defining all SmartMed backend endpoints.
 */
public interface SmartMedApi {

    // ========================= Auth =========================

    @POST("auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("auth/verify-otp")
    Call<ApiResponse<AuthResponse>> verifyOtp(@Body OtpVerifyRequest request);

    @POST("auth/resend-otp")
    Call<ApiResponse<Void>> resendOtp(@Body OtpResendRequest request);

    @POST("auth/forgot-password")
    Call<ApiResponse<Void>> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/reset-password")
    Call<ApiResponse<Void>> resetPassword(@Body ResetPasswordRequest request);

    // ========================= Users =========================

    @GET("users/{id}")
    Call<ApiResponse<User>> getUser(@Path("id") String userId);

    @PUT("users/{id}")
    Call<ApiResponse<User>> updateUser(@Path("id") String userId, @Body User user);

    // ========================= Medicines =========================

    @POST("medicines")
    Call<ApiResponse<Medicine>> createMedicine(@Body Medicine medicine);

    @GET("medicines")
    Call<ApiResponse<List<Medicine>>> getMedicines();

    @GET("medicines/{id}")
    Call<ApiResponse<Medicine>> getMedicine(@Path("id") String medicineId);

    @PUT("medicines/{id}")
    Call<ApiResponse<Medicine>> updateMedicine(@Path("id") String medicineId, @Body Medicine medicine);

    @DELETE("medicines/{id}")
    Call<ApiResponse<Void>> deleteMedicine(@Path("id") String medicineId);

    // ========================= Schedules =========================

    @POST("schedules")
    Call<ApiResponse<Schedule>> createSchedule(@Body Schedule schedule);

    @GET("schedules")
    Call<ApiResponse<List<Schedule>>> getSchedules();

    @GET("schedules/today")
    Call<ApiResponse<List<Schedule>>> getTodaySchedules();

    @DELETE("schedules/{id}")
    Call<ApiResponse<Void>> deleteSchedule(@Path("id") String scheduleId);

    // ========================= History =========================

    @POST("history")
    Call<ApiResponse<MedicationHistory>> createHistory(@Body MedicationHistory history);

    @GET("history")
    Call<ApiResponse<List<MedicationHistory>>> getHistory(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("medicineId") String medicineId
    );

    @GET("history/today")
    Call<ApiResponse<List<MedicationHistory>>> getTodayHistory();

    // ========================= Prescriptions =========================

    @POST("prescriptions")
    Call<ApiResponse<Prescription>> createPrescription(@Body Prescription prescription);

    @GET("prescriptions")
    Call<ApiResponse<List<Prescription>>> getPrescriptions();

    // ========================= Adherence =========================

    @GET("adherence")
    Call<ApiResponse<List<Adherence>>> getAdherence(
            @Query("period") String period
    );

    @GET("adherence/summary")
    Call<ApiResponse<Adherence>> getAdherenceSummary(
            @Query("period") String period
    );

    // ========================= Caregivers =========================

    @POST("caregivers")
    Call<ApiResponse<Caregiver>> createCaregiver(@Body Caregiver caregiver);

    @GET("caregivers")
    Call<ApiResponse<List<Caregiver>>> getCaregivers();

    @PUT("caregivers/{id}")
    Call<ApiResponse<Caregiver>> updateCaregiver(@Path("id") String caregiverId, @Body Caregiver caregiver);

    @DELETE("caregivers/{id}")
    Call<ApiResponse<Void>> deleteCaregiver(@Path("id") String caregiverId);

    // ========================= Notifications =========================

    @POST("notifications/caregiver-alert")
    Call<ApiResponse<Void>> sendCaregiverAlert(@Body CaregiverAlertRequest request);

    @GET("notifications")
    Call<ApiResponse<List<AppNotification>>> getNotifications();

    // ========================= AI =========================
    // These go to the AI service, not the Node.js backend

    // ========================= Inner DTOs =========================

    class OtpVerifyRequest {
        private String email;
        private String otp;
        private String purpose;

        public OtpVerifyRequest(String email, String otp, String purpose) {
            this.email = email;
            this.otp = otp;
            this.purpose = purpose;
        }
    }

    class OtpResendRequest {
        private String email;
        private String purpose;

        public OtpResendRequest(String email, String purpose) {
            this.email = email;
            this.purpose = purpose;
        }
    }

    class ForgotPasswordRequest {
        private String email;

        public ForgotPasswordRequest(String email) {
            this.email = email;
        }
    }

    class ResetPasswordRequest {
        private String email;
        private String otp;
        private String newPassword;

        public ResetPasswordRequest(String email, String otp, String newPassword) {
            this.email = email;
            this.otp = otp;
            this.newPassword = newPassword;
        }
    }

    class CaregiverAlertRequest {
        private String medicineId;
        private String medicineName;
        private int missedCount;

        public CaregiverAlertRequest(String medicineId, String medicineName, int missedCount) {
            this.medicineId = medicineId;
            this.medicineName = medicineName;
            this.missedCount = missedCount;
        }
    }
}
