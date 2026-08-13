package com.smartmed.app.ui.caregiver;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.smartmed.app.R;
import com.smartmed.app.data.model.Caregiver;
import com.smartmed.app.utils.ValidationUtils;
import com.smartmed.app.viewmodel.CaregiverViewModel;

/** Activity to add a new caregiver contact. */
public class AddCaregiverActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone;
    private Spinner spinnerRelationship;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private CaregiverViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caregiver);

        viewModel = new ViewModelProvider(this).get(CaregiverViewModel.class);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        spinnerRelationship = findViewById(R.id.spinnerRelationship);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.relationships, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRelationship.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveCaregiver());
    }

    private void saveCaregiver() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";

        if (!ValidationUtils.isValidName(name)) {
            etName.setError(getString(R.string.error_empty_name));
            return;
        }

        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            return;
        }

        if (email.isEmpty() && phone.isEmpty()) {
            Toast.makeText(this, "Please provide an email or phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        Caregiver caregiver = new Caregiver();
        caregiver.setName(name);
        caregiver.setEmail(email);
        caregiver.setPhone(phone);
        caregiver.setRelationship(spinnerRelationship.getSelectedItem().toString());

        setLoading(true);

        viewModel.addCaregiver(caregiver).observe(this, resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    Toast.makeText(this, R.string.caregiver_saved, Toast.LENGTH_SHORT).show();
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
        btnSave.setEnabled(!loading);
    }
}
