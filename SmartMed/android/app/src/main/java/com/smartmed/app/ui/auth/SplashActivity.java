package com.smartmed.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.smartmed.app.R;
import com.smartmed.app.data.repository.AuthRepository;
import com.smartmed.app.ui.main.MainActivity;

/**
 * Splash screen displayed on app launch.
 * Checks auth state and routes to Login or Dashboard.
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Animate elements
        TextView appName = findViewById(R.id.tvAppName);
        TextView tagline = findViewById(R.id.tvTagline);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);
        fadeIn.setStartOffset(300);

        AlphaAnimation taglineFade = new AlphaAnimation(0.0f, 1.0f);
        taglineFade.setDuration(1000);
        taglineFade.setStartOffset(800);

        appName.startAnimation(fadeIn);
        tagline.startAnimation(taglineFade);

        // Navigate after delay
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextScreen, SPLASH_DELAY);
    }

    private void navigateToNextScreen() {
        AuthRepository authRepo = new AuthRepository();
        Intent intent;

        if (authRepo.isLoggedIn()) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
