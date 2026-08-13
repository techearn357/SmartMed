package com.smartmed.app.ui.medicine;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.smartmed.app.R;
import com.smartmed.app.data.model.Medicine;
import com.smartmed.app.utils.Constants;
import com.smartmed.app.utils.DateUtils;
import com.smartmed.app.reminder.AlarmScheduler;
import com.smartmed.app.viewmodel.MedicineViewModel;

/** Medicine Detail screen showing full info with edit/delete options. */
public class MedicineDetailActivity extends AppCompatActivity {
    private MedicineViewModel viewModel;
    private String medicineId;
    private Medicine currentMedicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);
        viewModel = new ViewModelProvider(this).get(MedicineViewModel.class);
        medicineId = getIntent().getStringExtra(Constants.EXTRA_MEDICINE_ID);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnDelete).setOnClickListener(v -> confirmDelete());

        loadMedicine();
    }

    private void loadMedicine() {
        viewModel.getMedicine(medicineId).observe(this, resource -> {
            if (resource.isSuccess() && resource.getData() != null) {
                currentMedicine = resource.getData();
                displayMedicine(currentMedicine);
            } else if (resource.isError()) {
                Toast.makeText(this, resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayMedicine(Medicine med) {
        ((TextView) findViewById(R.id.tvName)).setText(med.getName());
        ((TextView) findViewById(R.id.tvDosage)).setText(med.getFormattedDosage());
        ((TextView) findViewById(R.id.tvFrequency)).setText(med.getFrequency() != null ? med.getFrequency().replace("_", " ") : "");

        if (med.getTimes() != null && !med.getTimes().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String t : med.getTimes()) sb.append(DateUtils.formatTimeString(t)).append("  ");
            ((TextView) findViewById(R.id.tvTimes)).setText(sb.toString().trim());
        }

        ((TextView) findViewById(R.id.tvDuration)).setText(med.getDuration() > 0 ? med.getDuration() + " days" : "Ongoing");
        ((TextView) findViewById(R.id.tvTotalTablets)).setText(String.valueOf(med.getTotalTablets()));
        ((TextView) findViewById(R.id.tvRemainingTablets)).setText(String.valueOf(med.getRemainingTablets()));
        ((TextView) findViewById(R.id.tvInstructions)).setText(med.getInstructions() != null ? med.getInstructions() : "None");
        ((TextView) findViewById(R.id.tvStatus)).setText(med.isActive() ? "Active" : "Inactive");

        // Stock estimation
        if (med.getRemainingTablets() > 0 && med.getTabletsPerDose() > 0) {
            int daysLeft = DateUtils.calculateDaysRemaining(med.getRemainingTablets(), med.getTabletsPerDose(), med.getDailyDoseCount());
            ((TextView) findViewById(R.id.tvDaysLeft)).setText("≈ " + daysLeft + " days of supply left");
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_medicine)
                .setMessage(R.string.delete_medicine_confirm)
                .setPositiveButton(R.string.delete, (d, w) -> {
                    // Cancel all scheduled Android local alarms for this medicine
                    if (currentMedicine != null && currentMedicine.getTimes() != null) {
                        AlarmScheduler scheduler = new AlarmScheduler(this);
                        for (String time : currentMedicine.getTimes()) {
                            scheduler.cancelAlarm(medicineId, time);
                        }
                    }

                    viewModel.deleteMedicine(medicineId).observe(this, resource -> {
                        if (resource.isSuccess()) {
                            Toast.makeText(this, R.string.medicine_deleted, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
