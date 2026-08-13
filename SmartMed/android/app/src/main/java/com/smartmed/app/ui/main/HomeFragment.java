package com.smartmed.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.smartmed.app.R;
import com.smartmed.app.data.model.MedicationHistory;
import com.smartmed.app.data.model.Schedule;
import com.smartmed.app.ui.medicine.AddMedicineActivity;
import com.smartmed.app.ui.prescription.ScanPrescriptionActivity;
import com.smartmed.app.utils.DateUtils;
import com.smartmed.app.utils.SharedPrefManager;
import com.smartmed.app.viewmodel.HomeViewModel;

import java.util.List;

/**
 * Home Dashboard Fragment showing greeting, today's schedule,
 * statistics, and quick actions.
 */
public class HomeFragment extends Fragment {

    private TextView tvGreeting, tvDate, tvNextMedicine, tvNextTime, tvNextDosage;
    private TextView tvTotalDoses, tvTaken, tvMissed, tvAdherence, tvTodayRemindersHeader;
    private MaterialCardView cardNextReminder;
    private LinearLayout llQuickActions;
    private RecyclerView rvTodayReminders;
    private ProgressBar progressBar;
    private HomeViewModel viewModel;
    private TodayScheduleAdapter scheduleAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        initViews(view);
        loadData();
    }

    private MaterialButton btnMarkTaken;

    private void initViews(View view) {
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvDate = view.findViewById(R.id.tvDate);
        tvNextMedicine = view.findViewById(R.id.tvNextMedicine);
        tvNextTime = view.findViewById(R.id.tvNextTime);
        tvNextDosage = view.findViewById(R.id.tvNextDosage);
        tvTotalDoses = view.findViewById(R.id.tvTotalDoses);
        tvTaken = view.findViewById(R.id.tvTaken);
        tvMissed = view.findViewById(R.id.tvMissed);
        tvAdherence = view.findViewById(R.id.tvAdherence);
        cardNextReminder = view.findViewById(R.id.cardNextReminder);
        btnMarkTaken = view.findViewById(R.id.btnMarkTaken);
        tvTodayRemindersHeader = view.findViewById(R.id.tvTodayRemindersHeader);
        rvTodayReminders = view.findViewById(R.id.rvTodayReminders);
        progressBar = view.findViewById(R.id.progressBar);

        if (rvTodayReminders != null) {
            rvTodayReminders.setLayoutManager(new LinearLayoutManager(requireContext()));
        }

        // Set greeting
        String userName = SharedPrefManager.getInstance().getUserName();
        tvGreeting.setText(DateUtils.getGreeting() + ", " + userName);
        tvDate.setText(DateUtils.getTodayFormatted());

        // Quick actions
        view.findViewById(R.id.btnAddMedicine).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddMedicineActivity.class)));
        view.findViewById(R.id.btnScanPrescription).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ScanPrescriptionActivity.class)));
    }

    private void loadData() {
        // Load today's schedules
        viewModel.loadTodaySchedules().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    updateSchedules(resource.getData());
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    break;
            }
        });

        // Load today's adherence
        viewModel.loadTodayAdherence().observe(getViewLifecycleOwner(), resource -> {
            if (resource.isSuccess() && resource.getData() != null) {
                tvTotalDoses.setText(String.valueOf(resource.getData().getTotalDoses()));
                tvTaken.setText(String.valueOf(resource.getData().getTakenDoses()));
                tvMissed.setText(String.valueOf(resource.getData().getMissedDoses()));
                int adherence = (int) resource.getData().getAdherencePercentage();
                tvAdherence.setText(adherence + "%");
            }
        });
    }

    private void updateSchedules(List<Schedule> schedules) {
        if (schedules == null || schedules.isEmpty()) {
            cardNextReminder.setVisibility(View.GONE);
            if (tvTodayRemindersHeader != null) tvTodayRemindersHeader.setVisibility(View.GONE);
            if (rvTodayReminders != null) rvTodayReminders.setVisibility(View.GONE);
            return;
        }

        Schedule next = schedules.get(0);
        cardNextReminder.setVisibility(View.VISIBLE);

        if (next.isTomorrow()) {
            tvNextMedicine.setText("🎉 All Doses Taken Today!");
            String stockText = next.getRemainingTablets() > 0 ? " • 📦 " + next.getRemainingTablets() + " left" : "";
            tvNextDosage.setText("Next Reminder: " + next.getMedicineName() + " (" + (next.getMedicineDosage() != null ? next.getMedicineDosage() : "") + ")" + stockText);
            tvNextTime.setText("Tomorrow at " + DateUtils.formatTimeString(next.getScheduledTime()));
            btnMarkTaken.setVisibility(View.GONE);

            // Display ALL of tomorrow's schedules in the list
            if (tvTodayRemindersHeader != null && rvTodayReminders != null) {
                tvTodayRemindersHeader.setText("Tomorrow's Reminders");
                tvTodayRemindersHeader.setVisibility(View.VISIBLE);
                rvTodayReminders.setVisibility(View.VISIBLE);

                if (scheduleAdapter == null) {
                    scheduleAdapter = new TodayScheduleAdapter(schedules, this::loadData);
                    rvTodayReminders.setAdapter(scheduleAdapter);
                } else {
                    scheduleAdapter.updateList(schedules);
                }
            }
            return;
        }

        btnMarkTaken.setVisibility(View.VISIBLE);
        tvNextMedicine.setText(next.getMedicineName());

        String stockText = next.getRemainingTablets() > 0 ? " • 📦 " + next.getRemainingTablets() + " left" : " • ⚠️ Low Stock";
        tvNextDosage.setText((next.getMedicineDosage() != null ? next.getMedicineDosage() : "") + stockText);
        tvNextTime.setText(DateUtils.formatTimeString(next.getScheduledTime()));

        btnMarkTaken.setOnClickListener(v -> {
            btnMarkTaken.setEnabled(false);
            MedicationHistory history = new MedicationHistory();
            history.setMedicineId(next.getMedicineId() != null ? next.getMedicineId() : "med_general");
            history.setMedicineName(next.getMedicineName());
            history.setMedicineDosage(next.getMedicineDosage());
            history.setScheduledTime(next.getScheduledTime());
            history.setStatus("TAKEN");
            history.setDate(DateUtils.formatDateForApi(new java.util.Date()));
            history.setTakenTime(DateUtils.getCurrentTimeString());

            com.smartmed.app.data.api.ApiClient.getApiService().createHistory(history).enqueue(
                    new retrofit2.Callback<com.smartmed.app.data.model.ApiResponse<MedicationHistory>>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.smartmed.app.data.model.ApiResponse<MedicationHistory>> call,
                                               retrofit2.Response<com.smartmed.app.data.model.ApiResponse<MedicationHistory>> response) {
                            if (isAdded()) {
                                btnMarkTaken.setEnabled(true);
                                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                    int remaining = next.getRemainingTablets() > 0 ? next.getRemainingTablets() - 1 : 0;
                                    android.widget.Toast.makeText(requireContext(),
                                            "✅ " + next.getMedicineName() + " marked as Taken! (" + remaining + " tablets remaining)",
                                            android.widget.Toast.LENGTH_LONG).show();
                                    loadData();
                                } else {
                                    android.widget.Toast.makeText(requireContext(), "Failed to record history", android.widget.Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<com.smartmed.app.data.model.ApiResponse<MedicationHistory>> call, Throwable t) {
                            if (isAdded()) {
                                btnMarkTaken.setEnabled(true);
                                android.widget.Toast.makeText(requireContext(), "Network error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        // Populate all today's schedules list below
        if (rvTodayReminders != null && tvTodayRemindersHeader != null) {
            tvTodayRemindersHeader.setText("All Today's Reminders");
            tvTodayRemindersHeader.setVisibility(View.VISIBLE);
            rvTodayReminders.setVisibility(View.VISIBLE);

            if (scheduleAdapter == null) {
                scheduleAdapter = new TodayScheduleAdapter(schedules, this::loadData);
                rvTodayReminders.setAdapter(scheduleAdapter);
            } else {
                scheduleAdapter.updateList(schedules);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        loadData(); // Refresh on return
    }
}

