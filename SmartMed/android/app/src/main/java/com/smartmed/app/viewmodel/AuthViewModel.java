package com.smartmed.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.smartmed.app.data.model.AuthResponse;
import com.smartmed.app.data.repository.AuthRepository;
import com.smartmed.app.data.repository.Resource;

/**
 * ViewModel for authentication operations.
 */
public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;

    public AuthViewModel() {
        this.repository = new AuthRepository();
    }

    public LiveData<Resource<String>> register(String name, String email, String password) {
        return repository.register(name, email, password);
    }

    public LiveData<Resource<AuthResponse>> verifyOtp(String email, String otp, String purpose) {
        return repository.verifyOtp(email, otp, purpose);
    }

    public LiveData<Resource<String>> resendOtp(String email, String purpose) {
        return repository.resendOtp(email, purpose);
    }

    public LiveData<Resource<AuthResponse>> login(String email, String password) {
        return repository.login(email, password);
    }

    public LiveData<Resource<String>> forgotPassword(String email) {
        return repository.forgotPassword(email);
    }

    public LiveData<Resource<String>> resetPassword(String email, String otp, String newPassword) {
        return repository.resetPassword(email, otp, newPassword);
    }

    public boolean isLoggedIn() {
        return repository.isLoggedIn();
    }

    public void logout() {
        repository.logout();
    }
}
