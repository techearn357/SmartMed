package com.smartmed.app.ui.settings;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.smartmed.app.R;
import com.smartmed.app.utils.SharedPrefManager;

/**
 * Settings screen for configuring app preferences:
 * - Sound / Vibration toggles
 * - Snooze duration slider
 * - Missed dose threshold
 * - Low stock alert threshold
 */
public class SettingsActivity extends AppCompatActivity {

    private SwitchMaterial switchSound, switchVibrate, switchNotifications, switchCaregiverAlerts;
    private Slider sliderSnooze, sliderMissedThreshold, sliderStockThreshold;
    private MaterialButton btnSave;
    private SharedPrefManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = SharedPrefManager.getInstance();

        initViews();
        loadSettings();
        setupListeners();
    }

    private void initViews() {
        switchSound = findViewById(R.id.switchSound);
        switchVibrate = findViewById(R.id.switchVibrate);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchCaregiverAlerts = findViewById(R.id.switchCaregiverAlerts);

        sliderSnooze = findViewById(R.id.sliderSnooze);
        sliderMissedThreshold = findViewById(R.id.sliderMissedThreshold);
        sliderStockThreshold = findViewById(R.id.sliderStockThreshold);

        btnSave = findViewById(R.id.btnSave);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadSettings() {
        switchSound.setChecked(prefs.isSoundEnabled());
        switchVibrate.setChecked(prefs.isVibrationEnabled());
        switchNotifications.setChecked(prefs.areNotificationsEnabled());
        switchCaregiverAlerts.setChecked(prefs.areCaregiverAlertsEnabled());

        sliderSnooze.setValue(prefs.getSnoozeInterval());
        sliderMissedThreshold.setValue(prefs.getMissedDoseThreshold());
        sliderStockThreshold.setValue(prefs.getLowStockThreshold());
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> {
            prefs.setSoundEnabled(switchSound.isChecked());
            prefs.setVibrationEnabled(switchVibrate.isChecked());
            prefs.setNotificationsEnabled(switchNotifications.isChecked());
            prefs.setCaregiverAlertsEnabled(switchCaregiverAlerts.isChecked());

            prefs.setSnoozeInterval((int) sliderSnooze.getValue());
            prefs.setMissedDoseThreshold((int) sliderMissedThreshold.getValue());
            prefs.setLowStockThreshold((int) sliderStockThreshold.getValue());

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
