package com.smartmed.app.ui.main;

import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smartmed.app.R;
import com.smartmed.app.data.model.Medicine;
import com.smartmed.app.ui.medicine.AddMedicineActivity;
import com.smartmed.app.ui.medicine.MedicineAdapter;
import com.smartmed.app.ui.medicine.MedicineDetailActivity;
import com.smartmed.app.utils.Constants;
import com.smartmed.app.viewmodel.MedicineViewModel;
import java.util.ArrayList;
import java.util.List;

/** Fragment displaying the user's medicine list with search and FAB. */
public class MedicinesFragment extends Fragment implements MedicineAdapter.OnMedicineClickListener {
    private RecyclerView rvMedicines;
    private MedicineAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private MedicineViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medicines, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MedicineViewModel.class);

        rvMedicines = view.findViewById(R.id.rvMedicines);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        FloatingActionButton fab = view.findViewById(R.id.fabAdd);

        adapter = new MedicineAdapter(new ArrayList<>(), this);
        rvMedicines.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMedicines.setAdapter(adapter);

        fab.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddMedicineActivity.class)));

        loadMedicines();
    }

    private void loadMedicines() {
        viewModel.getMedicines().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    List<Medicine> meds = resource.getData();
                    if (meds != null && !meds.isEmpty()) {
                        adapter.updateList(meds);
                        tvEmpty.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText(resource.getMessage());
                    break;
            }
        });
    }

    @Override
    public void onMedicineClick(Medicine medicine) {
        Intent intent = new Intent(requireContext(), MedicineDetailActivity.class);
        intent.putExtra(Constants.EXTRA_MEDICINE_ID, medicine.getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMedicines();
    }
}
