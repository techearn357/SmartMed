package com.smartmed.app.ui.prescription;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.smartmed.app.R;
import com.smartmed.app.utils.Constants;

import java.io.File;
import java.io.IOException;

/**
 * Activity to capture or select a doctor prescription image
 * and perform OCR using Google ML Kit Text Recognition.
 */
public class ScanPrescriptionActivity extends AppCompatActivity {

    private Uri photoUri;
    private TextRecognizer textRecognizer;
    private ProgressBar progressBar;
    private TextView tvStatus;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && photoUri != null) {
                    processImage(photoUri);
                }
            });

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    processImage(uri);
                }
            });

    private final ActivityResultLauncher<String> cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required to take photo", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_prescription);

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        MaterialButton btnCamera = findViewById(R.id.btnCamera);
        MaterialButton btnGallery = findViewById(R.id.btnGallery);

        btnCamera.setOnClickListener(v -> checkPermissionAndLaunchCamera());
        btnGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));
    }

    private void checkPermissionAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            File photoFile = File.createTempFile("prescription_", ".jpg", getCacheDir());
            photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            cameraLauncher.launch(intent);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
        }
    }

    private void processImage(Uri uri) {
        setLoading(true, "Scanning prescription text...");

        try {
            InputImage image = InputImage.fromFilePath(this, uri);
            textRecognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        setLoading(false, "");
                        String rawText = visionText.getText();
                        if (rawText.trim().isEmpty()) {
                            Toast.makeText(this, "No text detected in image. Please try a clearer picture.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Open Confirmation screen with extracted text
                        Intent intent = new Intent(this, PrescriptionConfirmActivity.class);
                        intent.putExtra(Constants.EXTRA_RAW_OCR_TEXT, rawText);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        setLoading(false, "");
                        Toast.makeText(this, "OCR failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } catch (IOException e) {
            setLoading(false, "");
            Toast.makeText(this, "Error reading image file", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLoading(boolean loading, String status) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        tvStatus.setVisibility(loading ? View.VISIBLE : View.GONE);
        tvStatus.setText(status);
    }
}
