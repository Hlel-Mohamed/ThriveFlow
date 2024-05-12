package de.tekup.thriveflow.fragments;

import static de.tekup.thriveflow.utils.CalendarUtils.daysInMonthArray;
import static de.tekup.thriveflow.utils.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.tekup.thriveflow.EventEditActivity;
import de.tekup.thriveflow.R;
import de.tekup.thriveflow.adapters.CalendarAdapter;
import de.tekup.thriveflow.adapters.EventAdapter;
import de.tekup.thriveflow.models.EventModel;
import de.tekup.thriveflow.utils.CalendarUtils;
import de.tekup.thriveflow.utils.DatabaseHandler;

/**
 * CalendarFragment is a Fragment that displays a calendar and a list of events.
 * It provides functionality for navigating between months and adding new events.
 * It uses a RecyclerView for displaying the calendar and a ListView for displaying the events.
 */
public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;
    private DatabaseHandler databaseHandler;

    /**
     * Empty constructor required for Fragment subclasses.
     */
    public CalendarFragment() {
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
     * It initializes the RecyclerView, ListView, TextView, and DatabaseHandler, and sets up the RecyclerView, ListView, and Buttons.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
        Button previousMonth = view.findViewById(R.id.previousMonthBtn);
        Button nextMonth = view.findViewById(R.id.nextMonthBtn);
        Button newEventBtn = view.findViewById(R.id.newEventBtn);
        eventListView = view.findViewById(R.id.eventListView);

        previousMonth.setOnClickListener(this::previousMonthAction);
        nextMonth.setOnClickListener(this::nextMonthAction);
        newEventBtn.setOnClickListener(this::newEventAction);

        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();

        databaseHandler = new DatabaseHandler(requireContext());
        databaseHandler.openDatabase();
        List<EventModel> eventList = databaseHandler.getAllEvents();

        new EventAdapter(requireContext(), eventList, databaseHandler);


        return view;
    }

    /**
     * This method sets the month view.
     * It sets the month and year text, gets the days in the month, and sets the adapter for the RecyclerView.
     * It also calls the setEventAdapter method to set the adapter for the ListView.
     */
    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        setEventAdapter();
    }

    /**
     * This method is called when the previous month button is clicked.
     * It changes the selected date to the previous month and updates the month view.
     *
     * @param view The view that was clicked.
     */
    public void previousMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    /**
     * This method is called when the next month button is clicked.
     * It changes the selected date to the next month and updates the month view.
     *
     * @param view The view that was clicked.
     */
    public void nextMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    /**
     * This method is called when an item in the RecyclerView is clicked.
     * If the date is not null, it changes the selected date to the clicked date and updates the month view.
     *
     * @param position The position of the item in the RecyclerView.
     * @param date     The date of the item.
     */
    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    /**
     * This method is called when the fragment is resumed.
     * It calls the setEventAdapter method to set the adapter for the ListView.
     */
    @Override
    public void onResume() {
        super.onResume();
        setEventAdapter();
    }

    /**
     * This method sets the adapter for the ListView.
     * It gets all events from the database, filters the events for the selected date, and sets the adapter with the filtered events.
     */
    private void setEventAdapter() {
        DatabaseHandler dbHandler = new DatabaseHandler(requireContext());
        dbHandler.openDatabase();
        List<EventModel> dailyEvents = dbHandler.getAllEvents();
        dailyEvents.removeIf(event -> !event.getDate().equals(CalendarUtils.selectedDate));
        EventAdapter eventAdapter = new EventAdapter(requireContext(), dailyEvents, databaseHandler);
        eventListView.setAdapter(eventAdapter);

    }

    /**
     * This method is called when the new event button is clicked.
     * It starts the EventEditActivity.
     *
     * @param view The view that was clicked.
     */
    public void newEventAction(View view) {
        startActivity(new Intent(requireContext(), EventEditActivity.class));
    }
}