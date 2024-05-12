package de.tekup.thriveflow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import de.tekup.thriveflow.databinding.ActivityMainBinding;
import de.tekup.thriveflow.fragments.CalendarFragment;
import de.tekup.thriveflow.fragments.PrayersFragment;
import de.tekup.thriveflow.fragments.TodoListFragment;

/**
 * MainActivity is the main activity of the application.
 * It sets up the main layout and handles the navigation between different fragments.
 */
public class MainActivity extends AppCompatActivity {

    // Binding instance for the main activity
    ActivityMainBinding binding;

    /**
     * This method is called when the activity is starting.
     * It inflates the main activity layout and sets up the bottom navigation view.
     * It also loads the CalendarFragment as the initial fragment.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadFragment(new CalendarFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.calendar) {
                loadFragment(new CalendarFragment());
            } else if (item.getItemId() == R.id.todoList) {
                loadFragment(new TodoListFragment());
            } else if (item.getItemId() == R.id.prayers) {
                loadFragment(new PrayersFragment());
            }
            return true;
        });
    }

    /**
     * This method is used to load a new fragment into the frame layout.
     * It replaces the current fragment with the new one and commits the transaction.
     *
     * @param fragment The new fragment to be loaded.
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }
}