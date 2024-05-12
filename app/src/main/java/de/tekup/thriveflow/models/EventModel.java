package de.tekup.thriveflow.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class EventModel {
    private int id;
    private String name;
    private LocalDate date;
    private LocalTime time;

    public EventModel(String name, LocalDate date, LocalTime time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public static ArrayList<EventModel> eventsList = new ArrayList<>();

    public static ArrayList<EventModel> eventsForDate(LocalDate date) {
        ArrayList<EventModel> events = new ArrayList<>();

        for (EventModel event : eventsList) {
            if (event.getDate().equals(date))
                events.add(event);
        }

        return events;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
