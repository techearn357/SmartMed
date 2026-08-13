package com.smartmed.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.smartmed.app.R;
import com.smartmed.app.ui.main.MainActivity;
import com.smartmed.app.utils.Constants;
import com.smartmed.app.utils.ValidationUtils;
import com.smartmed.app.viewmodel.AuthViewModel;

/**
 * OTP Verification screen with 6-digit input and countdown timer.
 */
public class OtpVerificationActivity extends AppCompatActivity {

    private TextInputEditText etOtp;
    private MaterialButton btnVerify;
    private TextView tvResend, tvCountdown, tvEmail;
    private ProgressBar progressBar;
    private AuthViewModel viewModel;
    private String email;
    private String purpose;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        email = getIntent().getStringExtra(Constants.EXTRA_EMAIL);
        purpose = getIntent().getStringExtra(Constants.EXTRA_OTP_PURPOSE);

        initViews();
        setupListeners();
        startCountdown();
    }

    private void initViews() {
        etOtp = findViewById(R.id.etOtp);
        btnVerify = findViewById(R.id.btnVerify);
        tvResend = findViewById(R.id.tvResend);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvEmail = findViewById(R.id.tvEmail);
        progressBar = findViewById(R.id.progressBar);

        tvEmail.setText(email);
        tvResend.setEnabled(false);
    }

    private void setupListeners() {
        btnVerify.setOnClickListener(v -> verifyOtp());

        tvResend.setOnClickListener(v -> {
            tvResend.setEnabled(false);
            viewModel.resendOtp(email, purpose).observe(this, resource -> {
                if (resource.isSuccess()) {
                    Toast.makeText(this, R.string.otp_resent, Toast.LENGTH_SHORT).show();
                    startCountdown();
                } else if (resource.isError()) {
                    Toast.makeText(this, resource.getMessage(), Toast.LENGTH_LONG).show();
                    tvResend.setEnabled(true);
                }
            });
        });
    }

    private void verifyOtp() {
        String otp = etOtp.getText() != null ? etOtp.getText().toString().trim() : "";

        if (!ValidationUtils.isValidOtp(otp)) {
            etOtp.setError(getString(R.string.error_invalid_otp_length));
            return;
        }

        setLoading(true);

        viewModel.verifyOtp(email, otp, purpose).observe(this, resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    if (Constants.PURPOSE_REGISTRATION.equals(purpose)) {
                        navigateToDashboard();
                    } else {
                        navigateToResetPassword();
                    }
                    break;
                case ERROR:
                    setLoading(false);
                    Toast.makeText(this, resource.getMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void startCountdown() {
        tvCountdown.setVisibility(View.VISIBLE);
        tvResend.setEnabled(false);

        if (countDownTimer != null) countDownTimer.cancel();

        countDownTimer = new CountDownTimer(Constants.OTP_RESEND_COOLDOWN * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tvCountdown.setText(getString(R.string.resend_otp_in, seconds));
            }

            @Override
            public void onFinish() {
                tvCountdown.setVisibility(View.GONE);
                tvResend.setEnabled(true);
            }
        }.start();
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToResetPassword() {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        intent.putExtra(Constants.EXTRA_EMAIL, email);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnVerify.setEnabled(!loading);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
