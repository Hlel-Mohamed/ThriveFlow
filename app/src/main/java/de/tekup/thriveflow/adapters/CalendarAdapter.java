package de.tekup.thriveflow.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

import de.tekup.thriveflow.R;
import de.tekup.thriveflow.utils.CalendarUtils;

/**
 * CalendarAdapter is a RecyclerView.Adapter that displays a list of dates.
 * It provides functionality for clicking on a date.
 * It uses an OnItemListener for handling date clicks.
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;

    /**
     * Constructor for the CalendarAdapter class.
     * It initializes the list of dates and the OnItemListener.
     *
     * @param days           The list of dates for the adapter.
     * @param onItemListener The OnItemListener for the adapter.
     */
    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    /**
     * This method is called when a new ViewHolder gets created.
     * It inflates the layout for the ViewHolder and returns a new CalendarViewHolder.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new CalendarViewHolder.
     */
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);

        return new CalendarViewHolder(view, onItemListener, days);
    }

    /**
     * This method is called by RecyclerView to display the data at the specified position.
     * It updates the contents of the ViewHolder to reflect the date at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final LocalDate date = days.get(position);

        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));

        if (date.equals(CalendarUtils.selectedDate))
            holder.parentView.setBackgroundColor(Color.LTGRAY);

        if (date.getMonth().equals(CalendarUtils.selectedDate.getMonth()))
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.dayOfMonth.getContext(), R.color.light_blue));
        else
            holder.dayOfMonth.setTextColor(Color.LTGRAY);
    }

    /**
     * This method returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return days.size();
    }

    /**
     * OnItemListener is an interface that provides a method to handle date clicks.
     */
    public interface OnItemListener {
        void onItemClick(int position, LocalDate date);
    }

    /**
     * CalendarViewHolder is a RecyclerView.ViewHolder that displays a date.
     * It provides a user interface for viewing the date and clicking on the date.
     */
    public static class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ArrayList<LocalDate> days;
        public final View parentView;
        public final TextView dayOfMonth;
        private final CalendarAdapter.OnItemListener onItemListener;

        /**
         * Constructor for the CalendarViewHolder class.
         * It initializes the View, TextView, OnItemListener, and list of dates, and sets an OnClickListener to the View.
         *
         * @param itemView       The view for the ViewHolder.
         * @param onItemListener The OnItemListener for the ViewHolder.
         * @param days           The list of dates for the ViewHolder.
         */
        public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days) {
            super(itemView);
            parentView = itemView.findViewById(R.id.parentView);
            dayOfMonth = itemView.findViewById(R.id.cellDayText);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
            this.days = days;
        }

        /**
         * This method is called when the View is clicked.
         * It calls the onItemClick method of the OnItemListener with the adapter position and the date at the position.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition()));
        }
    }
}