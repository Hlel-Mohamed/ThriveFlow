package de.tekup.thriveflow.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.tekup.thriveflow.models.ToDoModel;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "thrive_flow";
    private static final String TASKS_TABLE_NAME = "tasks";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String CREATE_TASKS_TABLE = "CREATE TABLE " + TASKS_TABLE_NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK + " TEXT, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE_NAME);
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
}
