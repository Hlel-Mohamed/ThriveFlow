package de.tekup.thriveflow;

import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.time.LocalTime;

import de.tekup.thriveflow.models.EventModel;
import de.tekup.thriveflow.utils.CalendarUtils;
import de.tekup.thriveflow.utils.DatabaseHandler;

/**
 * EventEditActivity is an activity that allows the user to create and save a new event.
 * It provides a user interface for entering the event name, selecting the event date and time.
 */
public class EventEditActivity extends AppCompatActivity {
    private EditText eventNameET;
    private TextView eventDateTV, eventTimeTV;

    private LocalTime time;

    /**
     * This method is called when the activity is starting.
     * It initializes the widgets, sets the current date and time, and sets up the event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        time = LocalTime.now();
        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(time));
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.baseline_access_time_24);
        eventTimeTV.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        eventTimeTV.setOnClickListener(v -> showTimePickerDialog());
    }

    /**
     * This method initializes the widgets used in the activity.
     */
    private void initWidgets() {
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeTV = findViewById(R.id.eventTimeTV);
    }

    /**
     * This method is called when the save event button is clicked.
     * It creates a new event with the entered name, selected date and time, and saves it to the database.
     *
     * @param view The view that was clicked.
     */
    public void saveEventAction(View view) {
        String eventName = eventNameET.getText().toString();
        EventModel newEvent = new EventModel(eventName, CalendarUtils.selectedDate, time);
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        dbHandler.openDatabase();
        dbHandler.insertEvent(newEvent);
        finish();
    }

    /**
     * This method shows a time picker dialog for selecting the event time.
     * When a time is selected, it updates the time variable and the eventTimeTV text view.
     */
    private void showTimePickerDialog() {
        int hour = time.getHour();
        int minute = time.getMinute();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    time = LocalTime.of(hourOfDay, minute1);
                    eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(time));
                }, hour, minute, true);

        timePickerDialog.show();
    }
}