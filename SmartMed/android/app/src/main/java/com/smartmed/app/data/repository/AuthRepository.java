package com.smartmed.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smartmed.app.data.api.ApiClient;
import com.smartmed.app.data.api.SmartMedApi;
import com.smartmed.app.data.model.*;
import com.smartmed.app.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for authentication operations.
 * Handles Firebase Auth + Backend API registration/login/OTP.
 */
public class AuthRepository {

    private final SmartMedApi api;
    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        this.api = ApiClient.getApiService();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Registers a new user. Sends registration to backend which creates OTP.
     */
    public LiveData<Resource<String>> register(String name, String email, String password) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        RegisterRequest request = new RegisterRequest(name, email, password);
        api.register(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success("OTP sent to " + email));
                } else {
                    result.setValue(Resource.error(parseErrorMessage(response, "Registration failed"), null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Verifies OTP and creates the user account.
     */
    public LiveData<Resource<AuthResponse>> verifyOtp(String email, String otp, String purpose) {
        MutableLiveData<Resource<AuthResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        SmartMedApi.OtpVerifyRequest request = new SmartMedApi.OtpVerifyRequest(email, otp, purpose);
        api.verifyOtp(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthResponse authResponse = response.body().getData();
                    if (authResponse != null && authResponse.getUser() != null) {
                        // Save session
                        User user = authResponse.getUser();
                        SharedPrefManager.getInstance().saveUserSession(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getFirebaseUid(),
                                authResponse.getToken()
                        );

                        // Sign into Firebase on the client side
                        signIntoFirebase(email, authResponse.getToken());
                    }
                    result.setValue(Resource.success(authResponse));
                } else {
                    result.setValue(Resource.error(parseErrorMessage(response, "OTP verification failed"), null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Resends OTP to the given email.
     */
    public LiveData<Resource<String>> resendOtp(String email, String purpose) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        SmartMedApi.OtpResendRequest request = new SmartMedApi.OtpResendRequest(email, purpose);
        api.resendOtp(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success("OTP resent successfully"));
                } else {
                    result.setValue(Resource.error(parseErrorMessage(response, "Failed to resend OTP"), null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Logs in user via backend API.
     */
    public LiveData<Resource<AuthResponse>> login(String email, String password) {
        MutableLiveData<Resource<AuthResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        LoginRequest request = new LoginRequest(email, password, null);
        api.login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthResponse authResponse = response.body().getData();
                    if (authResponse != null && authResponse.getUser() != null) {
                        User user = authResponse.getUser();
                        SharedPrefManager.getInstance().saveUserSession(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getFirebaseUid(),
                                authResponse.getToken()
                        );
                    }
                    result.setValue(Resource.success(authResponse));
                } else {
                    result.setValue(Resource.error(parseErrorMessage(response, "Login failed"), null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                result.setValue(Resource.error("Backend connection error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Sends forgot password OTP.
     */
    public LiveData<Resource<String>> forgotPassword(String email) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        SmartMedApi.ForgotPasswordRequest request = new SmartMedApi.ForgotPasswordRequest(email);
        api.forgotPassword(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success("Password reset OTP sent to " + email));
                } else {
                    result.setValue(Resource.error(parseErrorMessage(response, "Failed to send OTP"), null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Resets password after OTP verification.
     */
    public LiveData<Resource<String>> resetPassword(String email, String otp, String newPassword) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        SmartMedApi.ResetPasswordRequest request = new SmartMedApi.ResetPasswordRequest(email, otp, newPassword);
        api.resetPassword(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success("Password updated successfully"));
                } else {
                    result.setValue(Resource.error(parseErrorMessage(response, "Password reset failed"), null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    private String parseErrorMessage(Response<?> response, String defaultMsg) {
        if (response.body() != null && response.body() instanceof ApiResponse) {
            ApiResponse<?> apiResp = (ApiResponse<?>) response.body();
            if (apiResp.getMessage() != null && !apiResp.getMessage().isEmpty()) {
                return apiResp.getMessage();
            }
        }
        if (response.errorBody() != null) {
            try {
                String errorJson = response.errorBody().string();
                org.json.JSONObject obj = new org.json.JSONObject(errorJson);
                if (obj.has("message")) {
                    return obj.getString("message");
                }
            } catch (Exception ignored) {}
        }
        return defaultMsg;
    }


    /**
     * Logs out the current user.
     */
    public void logout() {
        try { firebaseAuth.signOut(); } catch (Exception ignored) {}
        SharedPrefManager.getInstance().clearSession();
        ApiClient.reset();
    }

    /**
     * Checks if user is currently logged in.
     */
    public boolean isLoggedIn() {
        return SharedPrefManager.getInstance().isLoggedIn();
    }

    /**
     * Signs into Firebase using custom token or email.
     */
    private void signIntoFirebase(String email, String token) {
        // Firebase sign-in is handled during login flow
        // This is a placeholder for any additional Firebase setup needed
    }
}
