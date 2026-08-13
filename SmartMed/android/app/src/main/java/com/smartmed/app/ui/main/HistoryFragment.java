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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.smartmed.app.R;
import com.smartmed.app.data.model.MedicationHistory;
import com.smartmed.app.ui.medicine.HistoryAdapter;
import com.smartmed.app.utils.DateUtils;
import com.smartmed.app.viewmodel.HistoryViewModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Medication History Fragment with filter chips (Today/Week/Month). */
public class HistoryFragment extends Fragment {
    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ChipGroup chipGroup;
    private HistoryViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        rvHistory = view.findViewById(R.id.rvHistory);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        chipGroup = view.findViewById(R.id.chipGroup);

        adapter = new HistoryAdapter(new ArrayList<>());
        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHistory.setAdapter(adapter);

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipToday)) loadHistory(0);
            else if (checkedIds.contains(R.id.chipWeek)) loadHistory(7);
            else if (checkedIds.contains(R.id.chipMonth)) loadHistory(30);
            else loadHistory(0);
        });

        loadHistory(0); // Default to today
    }

    private void loadHistory(int daysBack) {
        String startDate = DateUtils.formatDateForApi(DateUtils.getDaysAgo(daysBack));
        String endDate = DateUtils.formatDateForApi(new Date());

        viewModel.getHistory(startDate, endDate, null).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.getStatus()) {
                case LOADING: progressBar.setVisibility(View.VISIBLE); tvEmpty.setVisibility(View.GONE); break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    List<MedicationHistory> list = resource.getData();
                    if (list != null && !list.isEmpty()) { adapter.updateList(list); tvEmpty.setVisibility(View.GONE); }
                    else { tvEmpty.setVisibility(View.VISIBLE); adapter.updateList(new ArrayList<>()); }
                    break;
                case ERROR: progressBar.setVisibility(View.GONE); tvEmpty.setVisibility(View.VISIBLE); break;
            }
        });
    }
}
