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

/**
 * EventAdapter is an ArrayAdapter that displays a list of events.
 * It provides functionality for deleting events.
 * It uses a DatabaseHandler for deleting an event from the database.
 */
public class EventAdapter extends ArrayAdapter<EventModel> {

    private final DatabaseHandler databaseHandler;

    /**
     * Constructor for the EventAdapter class.
     * It initializes the DatabaseHandler.
     *
     * @param context         The context for the adapter.
     * @param events          The list of events for the adapter.
     * @param databasehandler The DatabaseHandler for the adapter.
     */
    public EventAdapter(@NonNull Context context, List<EventModel> events, DatabaseHandler databasehandler) {
        super(context, 0, events);
        this.databaseHandler = databasehandler;
    }

    /**
     * This method is called to get a View that displays the data at the specified position in the data set.
     * It inflates the layout for the View if it is null, gets the event at the position, sets the event title to the TextView,
     * and sets an OnClickListener to the View to show a dialog for deleting the event when clicked.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
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