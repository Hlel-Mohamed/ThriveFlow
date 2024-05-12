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


public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {


    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;
    private Button previousMonth, nextMonth, newEventBtn;
    private List<EventModel> eventList;
    private EventAdapter eventAdapter;
    private DatabaseHandler databaseHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
        previousMonth = view.findViewById(R.id.previousMonthBtn);
        nextMonth = view.findViewById(R.id.nextMonthBtn);
        newEventBtn = view.findViewById(R.id.newEventBtn);
        eventListView = view.findViewById(R.id.eventListView);

        previousMonth.setOnClickListener(this::previousMonthAction);
        nextMonth.setOnClickListener(this::nextMonthAction);
        newEventBtn.setOnClickListener(this::newEventAction);

        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();

        databaseHandler = new DatabaseHandler(requireContext());
        databaseHandler.openDatabase();
        eventList = databaseHandler.getAllEvents();

        eventAdapter = new EventAdapter(requireContext(), eventList, databaseHandler);


        return view;
    }


    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        setEventAdapter();
    }

    public void previousMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setEventAdapter();
    }

    private void setEventAdapter() {
        DatabaseHandler dbHandler = new DatabaseHandler(requireContext());
        dbHandler.openDatabase();
        List<EventModel> dailyEvents = dbHandler.getAllEvents();
        dailyEvents.removeIf(event -> !event.getDate().equals(CalendarUtils.selectedDate));
        EventAdapter eventAdapter = new EventAdapter(requireContext(), dailyEvents, databaseHandler);
        eventListView.setAdapter(eventAdapter);

    }

    public void newEventAction(View view) {
        startActivity(new Intent(requireContext(), EventEditActivity.class));
    }


}