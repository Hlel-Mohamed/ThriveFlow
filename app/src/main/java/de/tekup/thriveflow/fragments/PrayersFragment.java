package de.tekup.thriveflow.fragments;

import static android.icu.text.DateFormat.getDateInstance;

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

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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


public class PrayersFragment extends Fragment {


    private TextView fajrTime, dhuhrTime, asrTime, maghribTime, ishaTime, sunriseTime, date;
    private EditText searchCity;
    private TextInputLayout textInputLayout;

    String url = "";

    public PrayersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prayers, container, false);

        fajrTime = view.findViewById(R.id.textViewFajrTime);
        dhuhrTime = view.findViewById(R.id.textViewDhuhrTime);
        asrTime = view.findViewById(R.id.textViewAsrTime);
        maghribTime = view.findViewById(R.id.textViewMaghribTime);
        ishaTime = view.findViewById(R.id.textViewIshaTime);
        sunriseTime = view.findViewById(R.id.textViewSunriseTime);
        date = view.findViewById(R.id.textViewDate);
        searchCity = view.findViewById(R.id.editTextCity);
        textInputLayout = view.findViewById(R.id.textInputLayout);


        textInputLayout.setEndIconOnClickListener(v -> {
            String location = searchCity.getText().toString().trim();
            if (location != null) {
                //url = "https://muslimsalat.com/" + location + ".json?key=d82948f828527cc80c533f48af05333a";
                Geocoder geocoder = new Geocoder(getContext());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocationName(location, 5);
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
                    Log.d("Error 3", e.getMessage());
                }
            } else {
                Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        });

        retrieveData();

        return view;
    }

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
                Log.d("Error 1", e.getMessage());
            }
        }, error -> {
            Log.d("Error 2", error.getMessage());
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);

    }

    private void retrieveData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE);
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
                Log.d("Error 1", e.getMessage());
            }
        }
    }

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

    private void setPrayerAlarms() {
        setPrayerAlarm(fajrTime.getText().toString(), "Fajr");
        setPrayerAlarm(dhuhrTime.getText().toString(), "Dhuhr");
        setPrayerAlarm(asrTime.getText().toString(), "Asr");
        setPrayerAlarm(maghribTime.getText().toString(), "Maghrib");
        setPrayerAlarm(ishaTime.getText().toString(), "Isha");
    }
}