package de.tekup.thriveflow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * SplashActivity is an activity that shows a splash screen for a certain duration before redirecting to the main activity.
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * This method is called when the activity is starting.
     * It sets the content view to the activity_splash layout and creates an intent to start the MainActivity.
     * It also sets a delay of 500 milliseconds before starting the MainActivity and finishing the SplashActivity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Intent intent = new Intent(this, MainActivity.class);
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 500);
    }
}