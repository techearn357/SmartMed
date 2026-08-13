package com.smartmed.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.smartmed.app.R;
import com.smartmed.app.utils.Constants;
import com.smartmed.app.utils.ValidationUtils;
import com.smartmed.app.viewmodel.AuthViewModel;

/**
 * Registration screen with name, email, password, and confirm password.
 * On successful registration, navigates to OTP verification.
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirmPassword;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.tvSignIn).setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String name = getText(etName);
        String email = getText(etEmail);
        String password = getText(etPassword);
        String confirmPassword = getText(etConfirmPassword);

        // Clear errors
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Validate
        String error = ValidationUtils.validateRegistration(name, email, password, confirmPassword);
        if (error != null) {
            if (!ValidationUtils.isValidName(name)) tilName.setError(getString(R.string.error_empty_name));
            else if (!ValidationUtils.isValidEmail(email)) tilEmail.setError(getString(R.string.error_invalid_email));
            else if (!ValidationUtils.isValidPassword(password)) tilPassword.setError(getString(R.string.error_short_password));
            else tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
            return;
        }

        setLoading(true);

        viewModel.register(name, email, password).observe(this, resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    navigateToOtp(email);
                    break;
                case ERROR:
                    setLoading(false);
                    Toast.makeText(this, resource.getMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void navigateToOtp(String email) {
        Intent intent = new Intent(this, OtpVerificationActivity.class);
        intent.putExtra(Constants.EXTRA_EMAIL, email);
        intent.putExtra(Constants.EXTRA_OTP_PURPOSE, Constants.PURPOSE_REGISTRATION);
        startActivity(intent);
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
    }
}
