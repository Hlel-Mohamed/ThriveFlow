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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView fajrTime, dhuhrTime, asrTime, maghribTime, ishaTime, sunriseTime, date;
    private EditText searchCity;

    String url = "";

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
        fajrTime = view.findViewById(R.id.textViewFajrTime);
        dhuhrTime = view.findViewById(R.id.textViewDhuhrTime);
        asrTime = view.findViewById(R.id.textViewAsrTime);
        maghribTime = view.findViewById(R.id.textViewMaghribTime);
        ishaTime = view.findViewById(R.id.textViewIshaTime);
        sunriseTime = view.findViewById(R.id.textViewSunriseTime);
        date = view.findViewById(R.id.textViewDate);
        searchCity = view.findViewById(R.id.editTextCity);
        TextInputLayout textInputLayout = view.findViewById(R.id.textInputLayout);

        // Set an OnClickListener to the end icon of the TextInputLayout
        textInputLayout.setEndIconOnClickListener(v -> {
            String location = searchCity.getText().toString().trim();
            Geocoder geocoder = new Geocoder(requireContext());
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocationName(location, 5);
                assert addresses != null;
                if (addresses.size() > 0) {
                    double latitude = addresses.get(0).getLatitude();
                    double longitude = addresses.get(0).getLongitude();
                    url = "https://api.aladhan.com/v1/calendar?latitude=" + latitude + "&longitude=" + longitude;
                    getData(url, location);

                } else {
                    Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Error 3", Objects.requireNonNull(e.getMessage()));
            }
        });

        // Retrieve the prayer times data
        retrieveData();

        return view;
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

                fajrTime.setText(timings.getString("Fajr"));
                sunriseTime.setText(timings.getString("Sunrise"));
                dhuhrTime.setText(timings.getString("Dhuhr"));
                asrTime.setText(timings.getString("Asr"));
                maghribTime.setText(timings.getString("Maghrib"));
                ishaTime.setText(timings.getString("Isha"));
                date.setText(dateObject.getString("readable"));
                searchCity.setText(location);

                setPrayerAlarms();

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Error 1", Objects.requireNonNull(e.getMessage()));
            }
        }
    }

    /**
     * This method sets a prayer alarm with the given prayer time and prayer name.
     * It cleans the prayer time, splits it into hour and minute, sets a calendar with the hour and minute,
     * creates an Intent with the prayer name, creates a PendingIntent with the Intent, gets the AlarmManager,
     * and sets a repeating alarm with the AlarmManager, PendingIntent, and calendar time.
     *
     * @param prayerTime The time for the prayer.
     * @param prayerName The name of the prayer.
     */
    private void setPrayerAlarm(String prayerTime, String prayerName) {
        String cleanPrayerTime = prayerTime.replaceAll("[^\\d:]", "");

        String[] timeParts = cleanPrayerTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(getContext(), PrayerAlarmReceiver.class);
        intent.putExtra("prayerName", prayerName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.d("Alarm", "Alarm set for " + prayerName + " at " + cleanPrayerTime);
    }

    /**
     * This method sets prayer alarms for each prayer.
     * It calls the setPrayerAlarm method with the time and name of each prayer.
     */
    private void setPrayerAlarms() {
        setPrayerAlarm(fajrTime.getText().toString(), "Fajr");
        setPrayerAlarm(dhuhrTime.getText().toString(), "Dhuhr");
        setPrayerAlarm(asrTime.getText().toString(), "Asr");
        setPrayerAlarm(maghribTime.getText().toString(), "Maghrib");
        setPrayerAlarm(ishaTime.getText().toString(), "Isha");
    }
}