package de.tekup.thriveflow.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import de.tekup.thriveflow.R;
import de.tekup.thriveflow.models.EventModel;
import de.tekup.thriveflow.utils.CalendarUtils;
import de.tekup.thriveflow.utils.DatabaseHandler;

public class EventAdapter extends ArrayAdapter<EventModel> {

    private final DatabaseHandler databaseHandler;

    public EventAdapter(@NonNull Context context, List<EventModel> events, DatabaseHandler databasehandler) {
        super(context, 0, events);
        this.databaseHandler = databasehandler;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        EventModel event = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_cell, parent, false);

        TextView eventCellTV = convertView.findViewById(R.id.eventCellTV);

        assert event != null;
        String eventTitle = event.getName() + " " + CalendarUtils.formattedTime(event.getTime());
        eventCellTV.setText(eventTitle);

        convertView.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    databaseHandler.deleteEvent(event.getId());
                    remove(getItem(position));
                    notifyDataSetChanged();
                })
                .setNegativeButton("No", null)
                .show());

        return convertView;
    }
}