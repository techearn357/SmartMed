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

/** Reset Password screen after OTP verification. */
public class ResetPasswordActivity extends AppCompatActivity {
    private TextInputEditText etNewPassword, etConfirmPassword;
    private TextInputLayout tilNewPassword, tilConfirmPassword;
    private MaterialButton btnReset;
    private ProgressBar progressBar;
    private AuthViewModel viewModel;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        email = getIntent().getStringExtra(Constants.EXTRA_EMAIL);

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        btnReset = findViewById(R.id.btnReset);
        progressBar = findViewById(R.id.progressBar);

        btnReset.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String newPass = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        String confirmPass = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);

        if (!ValidationUtils.isValidPassword(newPass)) {
            tilNewPassword.setError(getString(R.string.error_short_password));
            return;
        }
        if (!ValidationUtils.doPasswordsMatch(newPass, confirmPass)) {
            tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
            return;
        }

        setLoading(true);
        // We pass empty OTP here since OTP was already verified in the previous screen
        // The backend uses a session/token to validate
        viewModel.resetPassword(email, "", newPass).observe(this, resource -> {
            switch (resource.getStatus()) {
                case LOADING: setLoading(true); break;
                case SUCCESS:
                    setLoading(false);
                    Toast.makeText(this, R.string.password_updated, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    break;
                case ERROR:
                    setLoading(false);
                    Toast.makeText(this, resource.getMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnReset.setEnabled(!loading);
    }
}
