package com.smartmed.app.ui.prescription;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.smartmed.app.R;
import com.smartmed.app.data.model.Medicine;
import com.smartmed.app.data.model.Schedule;
import com.smartmed.app.data.repository.ScheduleRepository;
import com.smartmed.app.reminder.AlarmScheduler;
import com.smartmed.app.utils.Constants;
import com.smartmed.app.utils.DateUtils;
import com.smartmed.app.utils.OcrParser;
import com.smartmed.app.viewmodel.MedicineViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Activity presenting extracted OCR prescription results.
 * Allows user to verify and edit the parsed medicine fields
 * before creating the medicine and automatic schedule.
 */
public class PrescriptionConfirmActivity extends AppCompatActivity {

    private TextInputEditText etRawText, etMedicineName, etDosage, etDuration, etInstructions;
    private Spinner spinnerUnit, spinnerFrequency;
    private MaterialButton btnConfirm;
    private ProgressBar progressBar;
    private MedicineViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_confirm);

        viewModel = new ViewModelProvider(this).get(MedicineViewModel.class);

        initViews();
        setupSpinners();

        String rawOcr = getIntent().getStringExtra(Constants.EXTRA_RAW_OCR_TEXT);
        if (rawOcr != null) {
            etRawText.setText(rawOcr);
            parseAndPopulate(rawOcr);
        }

        btnConfirm.setOnClickListener(v -> saveExtractedMedicine());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        etRawText = findViewById(R.id.etRawText);
        etMedicineName = findViewById(R.id.etMedicineName);
        etDosage = findViewById(R.id.etDosage);
        etDuration = findViewById(R.id.etDuration);
        etInstructions = findViewById(R.id.etInstructions);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        btnConfirm = findViewById(R.id.btnConfirm);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(this,
                R.array.dosage_units, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(this,
                R.array.frequencies, android.R.layout.simple_spinner_item);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequency.setAdapter(freqAdapter);
    }

    private void parseAndPopulate(String rawText) {
        OcrParser.ParsedPrescription parsed = OcrParser.parseText(rawText);

        if (parsed.getName() != null && !parsed.getName().isEmpty()) {
            etMedicineName.setText(parsed.getName());
        }

        if (parsed.getDosage() != null && !parsed.getDosage().isEmpty()) {
            etDosage.setText(parsed.getDosage());
        }

        if (parsed.getDurationDays() > 0) {
            etDuration.setText(String.valueOf(parsed.getDurationDays()));
        }

        if (parsed.getInstructions() != null && !parsed.getInstructions().isEmpty()) {
            etInstructions.setText(parsed.getInstructions());
        }
    }

    private void saveExtractedMedicine() {
        String name = etMedicineName.getText() != null ? etMedicineName.getText().toString().trim() : "";
        String dosage = etDosage.getText() != null ? etDosage.getText().toString().trim() : "";
        String duration = etDuration.getText() != null ? etDuration.getText().toString().trim() : "";

        if (name.isEmpty()) {
            etMedicineName.setError("Medicine name is required");
            return;
        }

        Medicine medicine = new Medicine();
        medicine.setName(name);
        medicine.setDosage(dosage.isEmpty() ? "1" : dosage);
        medicine.setDosageUnit(spinnerUnit.getSelectedItem().toString());
        medicine.setFrequency("once_daily");
        medicine.setStartDate(DateUtils.formatDateForApi(new Date()));
        medicine.setDuration(duration.isEmpty() ? 7 : Integer.parseInt(duration));
        medicine.setInstructions(etInstructions.getText() != null ? etInstructions.getText().toString().trim() : "");
        medicine.setActive(true);

        List<String> defaultTimes = new ArrayList<>();
        defaultTimes.add("08:00"); // 8:00 AM default
        medicine.setTimes(defaultTimes);

        setLoading(true);

        viewModel.createMedicine(medicine).observe(this, resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    if (resource.getData() != null) {
                        createScheduleAndAlarm(resource.getData());
                    }
                    Toast.makeText(this, "Medicine & Schedule created from OCR!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case ERROR:
                    setLoading(false);
                    Toast.makeText(this, resource.getMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void createScheduleAndAlarm(Medicine medicine) {
        ScheduleRepository scheduleRepo = new ScheduleRepository();
        AlarmScheduler alarmScheduler = new AlarmScheduler(this);

        for (String time : medicine.getTimes()) {
            Schedule schedule = new Schedule();
            schedule.setMedicineId(medicine.getId());
            schedule.setMedicineName(medicine.getName());
            schedule.setMedicineDosage(medicine.getFormattedDosage());
            schedule.setScheduledTime(time);
            schedule.setRepeatPattern("daily");
            schedule.setActive(true);
            scheduleRepo.createSchedule(schedule);

            alarmScheduler.scheduleAlarm(medicine.getId(), medicine.getName(), medicine.getFormattedDosage(), time);
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnConfirm.setEnabled(!loading);
    }
}
