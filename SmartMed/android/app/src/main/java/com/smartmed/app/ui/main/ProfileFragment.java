package com.smartmed.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.smartmed.app.R;
import com.smartmed.app.data.repository.AuthRepository;
import com.smartmed.app.ui.auth.LoginActivity;
import com.smartmed.app.ui.caregiver.CaregiverActivity;
import com.smartmed.app.ui.settings.SettingsActivity;
import com.smartmed.app.utils.SharedPrefManager;

/** Profile Fragment showing user info, settings, caregiver, and logout. */
public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        tvName.setText(SharedPrefManager.getInstance().getUserName());
        tvEmail.setText(SharedPrefManager.getInstance().getUserEmail());

        view.findViewById(R.id.btnCaregivers).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CaregiverActivity.class)));
        view.findViewById(R.id.btnSettings).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.yes, (d, w) -> {
                    new AuthRepository().logout();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
