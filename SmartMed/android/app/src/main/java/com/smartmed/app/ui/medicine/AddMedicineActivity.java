package com.smartmed.app.ui.medicine;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.smartmed.app.utils.*;
import com.smartmed.app.viewmodel.MedicineViewModel;
import java.util.*;

/**
 * Activity for adding a new medicine with full form.
 * Creates medicine + schedule + alarm on save.
 */
public class AddMedicineActivity extends AppCompatActivity {
    private TextInputEditText etName, etDosage, etDuration, etTotalTablets, etTabletsPerDose, etInstructions;
    private Spinner spinnerUnit, spinnerFrequency;
    private LinearLayout llTimes;
    private MaterialButton btnAddTime, btnSave;
    private ProgressBar progressBar;
    private MedicineViewModel viewModel;
    private final List<String> selectedTimes = new ArrayList<>();
    private String startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        viewModel = new ViewModelProvider(this).get(MedicineViewModel.class);
        initViews();
        setupSpinners();
        setupListeners();
        startDate = DateUtils.formatDateForApi(new Date());
    }

    private void initViews() {
        etName = findViewById(R.id.etMedicineName);
        etDosage = findViewById(R.id.etDosage);
        etDuration = findViewById(R.id.etDuration);
        etTotalTablets = findViewById(R.id.etTotalTablets);
        etTabletsPerDose = findViewById(R.id.etTabletsPerDose);
        etInstructions = findViewById(R.id.etInstructions);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        llTimes = findViewById(R.id.llTimes);
        btnAddTime = findViewById(R.id.btnAddTime);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
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

    private void setupListeners() {
        btnAddTime.setOnClickListener(v -> showTimePicker());
        btnSave.setOnClickListener(v -> saveMedicine());

        findViewById(R.id.btnStartDate).setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, day);
                startDate = DateUtils.formatDateForApi(selected.getTime());
                ((MaterialButton) v).setText(DateUtils.formatDate(selected.getTime()));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void showTimePicker() {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            selectedTimes.add(time);
            addTimeChip(time);
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show();
    }

    private void addTimeChip(String time) {
        TextView tv = new TextView(this);
        tv.setText(DateUtils.formatTimeString(time) + "  ✕");
        tv.setTextSize(16);
        tv.setPadding(24, 12, 24, 12);
        tv.setBackgroundResource(R.drawable.bg_status_chip);
        tv.setTextColor(getColor(R.color.primary));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 8, 8, 8);
        tv.setLayoutParams(params);
        tv.setOnClickListener(v -> { selectedTimes.remove(time); llTimes.removeView(tv); });
        llTimes.addView(tv);
    }

    private void saveMedicine() {
        String name = getText(etName);
        String dosage = getText(etDosage);
        String duration = getText(etDuration);
        String totalTablets = getText(etTotalTablets);
        String tabletsPerDose = getText(etTabletsPerDose);

        String error = ValidationUtils.validateMedicine(name, dosage, spinnerFrequency.getSelectedItem().toString());
        if (error != null) { Toast.makeText(this, error, Toast.LENGTH_SHORT).show(); return; }
        if (selectedTimes.isEmpty()) { Toast.makeText(this, "Please add at least one time", Toast.LENGTH_SHORT).show(); return; }

        Medicine medicine = new Medicine();
        medicine.setName(name);
        medicine.setDosage(dosage);
        medicine.setDosageUnit(spinnerUnit.getSelectedItem().toString());
        medicine.setFrequency(getFrequencyKey(spinnerFrequency.getSelectedItemPosition()));
        medicine.setTimes(selectedTimes);
        medicine.setStartDate(startDate);
        medicine.setDuration(duration.isEmpty() ? 0 : Integer.parseInt(duration));
        medicine.setTotalTablets(totalTablets.isEmpty() ? 0 : Integer.parseInt(totalTablets));
        medicine.setRemainingTablets(totalTablets.isEmpty() ? 0 : Integer.parseInt(totalTablets));
        medicine.setTabletsPerDose(tabletsPerDose.isEmpty() ? 1 : Integer.parseInt(tabletsPerDose));
        medicine.setInstructions(getText(etInstructions));
        medicine.setActive(true);

        // Calculate end date if duration provided
        if (medicine.getDuration() > 0) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, medicine.getDuration());
            medicine.setEndDate(DateUtils.formatDateForApi(cal.getTime()));
        }

        setLoading(true);
        viewModel.createMedicine(medicine).observe(this, resource -> {
            switch (resource.getStatus()) {
                case LOADING: setLoading(true); break;
                case SUCCESS:
                    setLoading(false);
                    // Create schedules and set alarms
                    if (resource.getData() != null) {
                        createSchedulesAndAlarms(resource.getData());
                    }
                    Toast.makeText(this, R.string.medicine_saved, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case ERROR:
                    setLoading(false);
                    Toast.makeText(this, resource.getMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void createSchedulesAndAlarms(Medicine savedMedicine) {
        ScheduleRepository scheduleRepo = new ScheduleRepository();
        AlarmScheduler alarmScheduler = new AlarmScheduler(this);

        for (String time : selectedTimes) {
            // Create schedule on server
            Schedule schedule = new Schedule();
            schedule.setMedicineId(savedMedicine.getId());
            schedule.setMedicineName(savedMedicine.getName());
            schedule.setMedicineDosage(savedMedicine.getFormattedDosage());
            schedule.setScheduledTime(time);
            schedule.setRepeatPattern("daily");
            schedule.setActive(true);
            scheduleRepo.createSchedule(schedule);

            // Set local alarm
            alarmScheduler.scheduleAlarm(savedMedicine.getId(), savedMedicine.getName(),
                    savedMedicine.getFormattedDosage(), time);
        }
    }

    private String getFrequencyKey(int position) {
        String[] keys = {"once_daily", "twice_daily", "three_times_daily", "four_times_daily", "as_needed", "custom"};
        return position < keys.length ? keys[position] : "once_daily";
    }

    private String getText(TextInputEditText et) { return et.getText() != null ? et.getText().toString().trim() : ""; }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
    }
}
