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

/** Forgot Password screen - sends OTP to email. */
public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputEditText etEmail;
    private TextInputLayout tilEmail;
    private MaterialButton btnSendOtp;
    private ProgressBar progressBar;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etEmail = findViewById(R.id.etEmail);
        tilEmail = findViewById(R.id.tilEmail);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.tvBackToLogin).setOnClickListener(v -> finish());

        btnSendOtp.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            tilEmail.setError(null);
            if (!ValidationUtils.isValidEmail(email)) {
                tilEmail.setError(getString(R.string.error_invalid_email));
                return;
            }
            setLoading(true);
            viewModel.forgotPassword(email).observe(this, resource -> {
                switch (resource.getStatus()) {
                    case LOADING: setLoading(true); break;
                    case SUCCESS:
                        setLoading(false);
                        Toast.makeText(this, resource.getData(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, OtpVerificationActivity.class);
                        intent.putExtra(Constants.EXTRA_EMAIL, email);
                        intent.putExtra(Constants.EXTRA_OTP_PURPOSE, Constants.PURPOSE_PASSWORD_RESET);
                        startActivity(intent);
                        break;
                    case ERROR:
                        setLoading(false);
                        Toast.makeText(this, resource.getMessage(), Toast.LENGTH_LONG).show();
                        break;
                }
            });
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSendOtp.setEnabled(!loading);
    }
}
