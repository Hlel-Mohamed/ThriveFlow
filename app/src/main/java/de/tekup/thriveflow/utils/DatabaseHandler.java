package de.tekup.thriveflow.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import de.tekup.thriveflow.models.EventModel;
import de.tekup.thriveflow.models.ToDoModel;

/**
 * DatabaseHandler is a helper class that manages the database operations.
 * It extends SQLiteOpenHelper and provides methods for creating, updating, and managing the database.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // Database version, name, and table names
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "thrive_flow";
    private static final String TASKS_TABLE_NAME = "tasks";
    private static final String EVENTS_TABLE_NAME = "events";

    // Column names for the tasks and events tables
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String NAME = "name";
    private static final String DATE = "date";
    private static final String TIME = "time";

    // SQL statements for creating the tasks and events tables
    private static final String CREATE_TASKS_TABLE = "CREATE TABLE " + TASKS_TABLE_NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK + " TEXT, "
            + STATUS + " INTEGER)";

    private static final String CREATE_EVENTS_TABLE = "CREATE TABLE " + EVENTS_TABLE_NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT, "
            + DATE + " TEXT, "
            + TIME + " TEXT)";

    // SQLiteDatabase instance
    private SQLiteDatabase database;

    /**
     * Constructor for the DatabaseHandler class.
     * It calls the superclass constructor with the context, database name, factory, and version.
     *
     * @param context The context to use for opening or creating the database.
     */
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This method is called when the database is created for the first time.
     * It creates the tasks and events tables.
     *
     * @param database The database.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TASKS_TABLE);
        database.execSQL(CREATE_EVENTS_TABLE);
    }

    /**
     * This method is called when the database needs to be upgraded.
     * It drops the existing tasks and events tables and creates new ones.
     *
     * @param database   The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE_NAME);
        onCreate(database);
    }

    /**
     * This method opens the database for writing.
     */
    public void openDatabase() {
        database = this.getWritableDatabase();
    }

    /**
     * This method inserts a new task into the tasks table.
     *
     * @param task The task to be inserted.
     */
    public void insertTask(ToDoModel task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK, task.getTask());
        contentValues.put(STATUS, 0);
        database.insert(TASKS_TABLE_NAME, null, contentValues);
    }

    /**
     * This method retrieves all tasks from the tasks table.
     *
     * @return A list of all tasks.
     */
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

    /**
     * This method updates the status of a task in the tasks table.
     *
     * @param id     The id of the task.
     * @param status The new status of the task.
     */
    public void updateStatus(int id, int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, status);
        database.update(TASKS_TABLE_NAME, contentValues, ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * This method updates the task in the tasks table.
     *
     * @param id   The id of the task.
     * @param task The new task.
     */
    public void updateTask(int id, String task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK, task);
        database.update(TASKS_TABLE_NAME, contentValues, ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * This method deletes a task from the tasks table.
     *
     * @param id The id of the task.
     */
    public void deleteTask(int id) {
        database.delete(TASKS_TABLE_NAME, ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * This method inserts a new event into the events table.
     *
     * @param event The event to be inserted.
     */
    public void insertEvent(EventModel event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, event.getName());
        contentValues.put(DATE, event.getDate().toString());
        contentValues.put(TIME, event.getTime().toString());
        database.insert(EVENTS_TABLE_NAME, null, contentValues);
    }

    /**
     * This method retrieves all events from the events table.
     *
     * @return A list of all events.
     */
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

    /**
     * This method updates an event in the events table.
     *
     * @param id   The id of the event.
     * @param name The new name of the event.
     * @param date The new date of the event.
     * @param time The new time of the event.
     */
    public void updateEvent(int id, String name, LocalDate date, LocalTime time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(DATE, date.toString());
        contentValues.put(TIME, time.toString());
        database.update(EVENTS_TABLE_NAME, contentValues, ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * This method deletes an event from the events table.
     *
     * @param id The id of the event.
     */
    public void deleteEvent(int id) {
        database.delete(EVENTS_TABLE_NAME, ID + " = ?", new String[]{String.valueOf(id)});
    }
}