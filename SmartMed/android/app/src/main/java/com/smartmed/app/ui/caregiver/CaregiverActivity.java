package com.smartmed.app.ui.caregiver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smartmed.app.R;
import com.smartmed.app.data.model.Caregiver;
import com.smartmed.app.viewmodel.CaregiverViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying list of caregivers with add and delete options.
 */
public class CaregiverActivity extends AppCompatActivity implements CaregiverAdapter.OnCaregiverClickListener {

    private RecyclerView rvCaregivers;
    private CaregiverAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private CaregiverViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver);

        viewModel = new ViewModelProvider(this).get(CaregiverViewModel.class);

        rvCaregivers = findViewById(R.id.rvCaregivers);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        adapter = new CaregiverAdapter(new ArrayList<>(), this);
        rvCaregivers.setLayoutManager(new LinearLayoutManager(this));
        rvCaregivers.setAdapter(adapter);

        fab.setOnClickListener(v -> startActivity(new Intent(this, AddCaregiverActivity.class)));

        loadCaregivers();
    }

    private void loadCaregivers() {
        viewModel.getCaregivers().observe(this, resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    List<Caregiver> list = resource.getData();
                    if (list != null && !list.isEmpty()) {
                        adapter.updateList(list);
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
    public void onDeleteClick(Caregiver caregiver) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_caregiver)
                .setMessage(R.string.delete_caregiver_confirm)
                .setPositiveButton(R.string.delete, (d, w) -> {
                    viewModel.deleteCaregiver(caregiver.getId()).observe(this, resource -> {
                        if (resource.isSuccess()) {
                            Toast.makeText(this, R.string.caregiver_deleted, Toast.LENGTH_SHORT).show();
                            loadCaregivers();
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCaregivers();
    }
}
