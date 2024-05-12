package de.tekup.thriveflow;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalTime;

import de.tekup.thriveflow.models.EventModel;
import de.tekup.thriveflow.utils.CalendarUtils;
import de.tekup.thriveflow.utils.DatabaseHandler;

public class EventEditActivity extends AppCompatActivity {
    private EditText eventNameET;
    private TextView eventDateTV, eventTimeTV;

    private LocalTime time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        time = LocalTime.now();
        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(time));
        eventTimeTV.setOnClickListener(v -> showTimePickerDialog());
    }

    private void initWidgets() {
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeTV = findViewById(R.id.eventTimeTV);
    }

    public void saveEventAction(View view) {
        String eventName = eventNameET.getText().toString();
        EventModel newEvent = new EventModel(eventName, CalendarUtils.selectedDate, time);
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        dbHandler.openDatabase();
        dbHandler.insertEvent(newEvent);
        finish();
    }

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
