package de.tekup.thriveflow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import de.tekup.thriveflow.databinding.ActivityMainBinding;
import de.tekup.thriveflow.fragments.CalendarFragment;
import de.tekup.thriveflow.fragments.PrayersFragment;
import de.tekup.thriveflow.fragments.TodoListFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

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

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }
}