package com.smartmed.app.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.chip.ChipGroup;
import com.smartmed.app.R;
import com.smartmed.app.data.model.Adherence;
import com.smartmed.app.utils.Constants;
import com.smartmed.app.viewmodel.AdherenceViewModel;

/** Adherence Fragment with percentage display and period filters. */
public class AdherenceFragment extends Fragment {
    private TextView tvPercentage, tvMessage, tvTotal, tvTaken, tvMissed, tvLate, tvStreak;
    private ProgressBar progressBar, circularProgress;
    private ChipGroup chipGroup;
    private AdherenceViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adherence, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AdherenceViewModel.class);
        tvPercentage = view.findViewById(R.id.tvPercentage);
        tvMessage = view.findViewById(R.id.tvMessage);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvTaken = view.findViewById(R.id.tvTaken);
        tvMissed = view.findViewById(R.id.tvMissed);
        tvLate = view.findViewById(R.id.tvLate);
        tvStreak = view.findViewById(R.id.tvStreak);
        progressBar = view.findViewById(R.id.progressBar);
        chipGroup = view.findViewById(R.id.chipGroup);

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipDaily)) loadAdherence("today");
            else if (checkedIds.contains(R.id.chipWeekly)) loadAdherence("week");
            else if (checkedIds.contains(R.id.chipMonthly)) loadAdherence("month");
        });
        loadAdherence("week");
    }

    private void loadAdherence(String period) {
        viewModel.getSummary(period).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.getStatus()) {
                case LOADING: progressBar.setVisibility(View.VISIBLE); break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    if (resource.getData() != null) updateUI(resource.getData());
                    break;
                case ERROR: progressBar.setVisibility(View.GONE); break;
            }
        });
    }

    private void updateUI(Adherence data) {
        int pct = (int) data.getAdherencePercentage();
        tvPercentage.setText(pct + "%");
        tvTotal.setText(String.valueOf(data.getTotalDoses()));
        tvTaken.setText(String.valueOf(data.getTakenDoses()));
        tvMissed.setText(String.valueOf(data.getMissedDoses()));
        tvLate.setText(String.valueOf(data.getLateDoses()));
        tvStreak.setText(getString(R.string.days_streak, data.getCurrentStreak()));

        if (pct >= Constants.ADHERENCE_EXCELLENT) {
            tvMessage.setText(R.string.adherence_excellent);
            tvPercentage.setTextColor(getResources().getColor(R.color.adherence_excellent, null));
        } else if (pct >= Constants.ADHERENCE_GOOD) {
            tvMessage.setText(R.string.adherence_good);
            tvPercentage.setTextColor(getResources().getColor(R.color.adherence_good, null));
        } else if (pct >= Constants.ADHERENCE_AVERAGE) {
            tvMessage.setText(R.string.adherence_average);
            tvPercentage.setTextColor(getResources().getColor(R.color.adherence_average, null));
        } else {
            tvMessage.setText(R.string.adherence_poor);
            tvPercentage.setTextColor(getResources().getColor(R.color.adherence_poor, null));
        }
    }
}
