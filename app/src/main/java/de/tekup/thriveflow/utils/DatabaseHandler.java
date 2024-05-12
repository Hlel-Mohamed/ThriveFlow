package de.tekup.thriveflow.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import de.tekup.thriveflow.models.EventModel;
import de.tekup.thriveflow.models.ToDoModel;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "thrive_flow";
    private static final String TASKS_TABLE_NAME = "tasks";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String EVENTS_TABLE_NAME = "events";
    private static final String NAME = "name";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String CREATE_TASKS_TABLE = "CREATE TABLE " + TASKS_TABLE_NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK + " TEXT, "
            + STATUS + " INTEGER)";

    private static final String CREATE_EVENTS_TABLE = "CREATE TABLE " + EVENTS_TABLE_NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT, "
            + DATE + " TEXT, "
            + TIME + " TEXT)";
    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TASKS_TABLE);
        database.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE_NAME);
        onCreate(database);
    }

    public void openDatabase() {
        database = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK, task.getTask());
        contentValues.put(STATUS, 0);
        database.insert(TASKS_TABLE_NAME, null, contentValues);
    }

    @SuppressLint("Range")
    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        database.beginTransaction();
        try (Cursor cursor = database.query(TASKS_TABLE_NAME, null, null, null, null, null, null)) {

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        ToDoModel task = new ToDoModel();
                        task.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                        task.setTask(cursor.getString(cursor.getColumnIndex(TASK)));
                        task.setStatus(cursor.getInt(cursor.getColumnIndex(STATUS)));
                        taskList.add(task);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            database.endTransaction();
        }

        return taskList;
    }

    public void updateStatus(int id, int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, status);
        database.update(TASKS_TABLE_NAME, contentValues, ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void updateTask(int id, String task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK, task);
        database.update(TASKS_TABLE_NAME, contentValues, ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id) {
        database.delete(TASKS_TABLE_NAME, ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void insertEvent(EventModel event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, event.getName());
        contentValues.put(DATE, event.getDate().toString());
        contentValues.put(TIME, event.getTime().toString());
        database.insert(EVENTS_TABLE_NAME, null, contentValues);
    }

    @SuppressLint("Range")
    public List<EventModel> getAllEvents() {
        List<EventModel> eventList = new ArrayList<>();
        try (Cursor cursor = database.query(EVENTS_TABLE_NAME, null, null, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(ID));
                    String name = cursor.getString(cursor.getColumnIndex(NAME));
                    LocalDate date = LocalDate.parse(cursor.getString(cursor.getColumnIndex(DATE)));
                    LocalTime time = LocalTime.parse(cursor.getString(cursor.getColumnIndex(TIME)));
                    EventModel event = new EventModel(name, date, time);
                    event.setId(id);
                    eventList.add(event);
                } while (cursor.moveToNext());
            }
        }
        return eventList;
    }

    public void updateEvent(int id, String name, LocalDate date, LocalTime time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(DATE, date.toString());
        contentValues.put(TIME, time.toString());
        database.update(EVENTS_TABLE_NAME, contentValues, ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteEvent(int id) {
        try {
            int rowsAffected = database.delete(EVENTS_TABLE_NAME, ID + " = ?", new String[]{String.valueOf(id)});
            if (rowsAffected == 0) {
                Log.d("DatabaseHandler", "No event found with id: " + id);
            } else {
                Log.d("DatabaseHandler", "Event with id: " + id + " deleted successfully");
            }
        } catch (Exception e) {
            Log.e("DatabaseHandler", "Error while trying to delete event with id: " + id, e);
        }
    }
}
