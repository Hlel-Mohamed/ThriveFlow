package de.tekup.thriveflow.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import de.tekup.thriveflow.R;
import de.tekup.thriveflow.receivers.PrayerAlarmReceiver;

/**
 * PrayersFragment is a Fragment that displays prayer times.
 * It provides functionality for searching for prayer times by city.
 * It uses Volley to make a network request to an API and retrieve the prayer times.
 * It also sets alarms for the prayer times using AlarmManager.
 */
public class PrayersFragment extends Fragment {

    private TextView[] alarmTextViews = new TextView[6];
    private PendingIntent[] pendingIntents = new PendingIntent[5];
    private AlarmManager alarmManager;
    private TextView date;
    private EditText searchCity;
    private SwitchCompat alarmSwitch;

    /**
     * Empty constructor required for Fragment subclasses.
     */
    public PrayersFragment() {
        // Required empty public constructor
    }

    /**
     * This method is called when the fragment is first created.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This method is called to have the fragment instantiate its user interface view.
     * It initializes the TextViews and EditText, and sets an OnClickListener to the end icon of the TextInputLayout.
     * When the end icon is clicked, it gets the location from the EditText, gets the latitude and longitude of the location using Geocoder,
     * constructs the API url with the latitude and longitude, and calls the getData method with the url and location.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prayers, container, false);

        // Initialize the TextViews and EditText
        alarmTextViews[0] = view.findViewById(R.id.Fajr);
        alarmTextViews[1] = view.findViewById(R.id.Dhuhr);
        alarmTextViews[2] = view.findViewById(R.id.Asr);
        alarmTextViews[3] = view.findViewById(R.id.Maghrib);
        alarmTextViews[4] = view.findViewById(R.id.Isha);
        alarmTextViews[5] = view.findViewById(R.id.Sunrise);
        date = view.findViewById(R.id.textViewDate);
        searchCity = view.findViewById(R.id.editTextCity);
        TextInputLayout textInputLayout = view.findViewById(R.id.textInputLayout);
        alarmSwitch = view.findViewById(R.id.switchAlarm);

        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        // Set an OnClickListener to the end icon of the TextInputLayout
        textInputLayout.setEndIconOnClickListener(v -> {
            searchLocationAndGetData();
        });

        // Retrieve the prayer times data
        retrieveData();

        // Set an OnCheckedChangeListener to the alarm switch
        alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the switch state when it changes
            handleAlarmWithSwitch(isChecked);
        });

        // Retrieve the switch state when the fragment is created
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE);
        boolean switchState = sharedPreferences.getBoolean("alarmSwitchState", false);
        alarmSwitch.setChecked(switchState);

        return view;
    }

    /**
     * This method handles the alarm switch state changes.
     * It saves the switch state to SharedPreferences when it changes.
     * If the switch is checked, it sets the alarms and shows a toast message.
     * If the switch is not checked, it cancels the alarms and shows a toast message.
     *
     * @param isChecked The new state of the switch.
     */
    private void handleAlarmWithSwitch(boolean isChecked) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("alarmSwitchState", isChecked);
        editor.apply();

        if (isChecked) {
            setAlarms();
            Toast.makeText(getContext(), "Alarms set", Toast.LENGTH_SHORT).show();
        } else {
            cancelAlarms();
            Toast.makeText(getContext(), "Alarms canceled", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method gets the location from the EditText, gets the latitude and longitude of the location using Geocoder,
     * constructs the API url with the latitude and longitude, and calls the getData method with the url and location.
     * If the location is not found, it shows a toast message.
     */
    private void searchLocationAndGetData() {
        String location = searchCity.getText().toString().trim();
        Geocoder geocoder = new Geocoder(requireContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(location, 5);
            assert addresses != null;
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                getData("https://api.aladhan.com/v1/calendar?latitude=" + latitude + "&longitude=" + longitude, location);
            } else {
                Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error 3", Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * This method makes a network request to the API with the given url and retrieves the prayer times data.
     * It creates a JsonObjectRequest with the url, and when the request is successful, it saves the response and location to SharedPreferences,
     * and calls the retrieveData method.
     *
     * @param url      The url for the API request.
     * @param location The location for the prayer times.
     */
    private void getData(String url, String location) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("data", response.toString());
                editor.putString("location", location);
                editor.apply();
                retrieveData();
                if (alarmSwitch.isChecked()) {
                    setAlarms();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Error 1", Objects.requireNonNull(e.getMessage()));
            }
        }, error -> Log.d("Error 2", Objects.requireNonNull(error.getMessage())));

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * This method retrieves the prayer times data from SharedPreferences.
     * It gets the data and location from SharedPreferences, and if the data is not null, it parses the data into a JSONObject,
     * gets the timings and date from the JSONObject, sets the timings and date to the TextViews, and calls the setPrayerAlarms method.
     */
    private void retrieveData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE);
        String data = sharedPreferences.getString("data", null);
        String location = sharedPreferences.getString("location", null);
        if (data != null) {
            try {
                JSONObject response = new JSONObject(data);
                Calendar calendar = Calendar.getInstance();
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH) - 1;

                JSONObject timings = response.getJSONArray("data").getJSONObject(dayOfMonth).getJSONObject("timings");
                JSONObject dateObject = response.getJSONArray("data").getJSONObject(dayOfMonth).getJSONObject("date");

                alarmTextViews[0].setText(timings.getString("Fajr"));
                alarmTextViews[5].setText(timings.getString("Sunrise"));
                alarmTextViews[1].setText(timings.getString("Dhuhr"));
                alarmTextViews[2].setText(timings.getString("Asr"));
                alarmTextViews[3].setText(timings.getString("Maghrib"));
                alarmTextViews[4].setText(timings.getString("Isha"));
                date.setText(dateObject.getString("readable"));
                searchCity.setText(location);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Error 1", Objects.requireNonNull(e.getMessage()));
            }
        }
    }

    /**
     * This method sets the alarms for the prayer times.
     * It gets the prayer times from the TextViews, splits the times into hours and minutes, and calls the setAlarm method with the hours, minutes, and prayer names.
     */
    private void setAlarms() {
        for (int i = 0; i < alarmTextViews.length - 1; i++) {
            String alarmTime = alarmTextViews[i].getText().toString();
            String cleanAlarmTime = alarmTime.replaceAll("[^\\d:]", "");
            String[] timeParts = cleanAlarmTime.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            setAlarm(hour, minute, i, getResources().getResourceName(alarmTextViews[i].getId()));
        }
    }

    /**
     * This method sets an alarm for a prayer time.
     * It creates an Intent with the prayer name, checks if the PendingIntent already exists, and if it doesn't, it creates a PendingIntent with the Intent.
     * It sets the alarm time with the hour and minute, and if the alarm time is after the current time, it sets the alarm with the PendingIntent.
     *
     * @param hour        The hour of the alarm.
     * @param minute      The minute of the alarm.
     * @param requestCode The request code for the PendingIntent.
     * @param prayerName  The name of the prayer.
     */
    private void setAlarm(int hour, int minute, int requestCode, String prayerName) {
        Intent intent = new Intent(requireContext(), PrayerAlarmReceiver.class);
        String cleanPrayerName = prayerName.split("/")[1];
        intent.putExtra("prayerName", cleanPrayerName);

        // Check if the PendingIntent already exists
        PendingIntent existingIntent = PendingIntent.getBroadcast(requireContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE);
        if (existingIntent != null) return;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Check if the alarm time is before the current time
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            // If it is, skip setting the alarm
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                // Request SCHEDULE_EXACT_ALARM permission.
                Intent permissionIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                permissionIntent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                startActivity(permissionIntent);
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        pendingIntents[requestCode] = pendingIntent;
    }

    /**
     * This method cancels the alarms.
     * It goes through the PendingIntent array and cancels each PendingIntent.
     */
    private void cancelAlarms() {
        for (PendingIntent pendingIntent : pendingIntents) {
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }

}