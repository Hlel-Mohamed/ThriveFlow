package de.tekup.thriveflow.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tekup.thriveflow.utils.DialogCloseListener;
import de.tekup.thriveflow.MainActivity;
import de.tekup.thriveflow.R;
import de.tekup.thriveflow.helpers.RecyclerItemTouchHelper;
import de.tekup.thriveflow.adapters.ToDoAdapter;
import de.tekup.thriveflow.models.ToDoModel;
import de.tekup.thriveflow.utils.DatabaseHandler;

/**
 * TodoListFragment is a Fragment that displays a list of tasks.
 * It provides functionality for adding new tasks and editing or deleting existing tasks.
 * It uses a RecyclerView for displaying the tasks and a FloatingActionButton for adding new tasks.
 */
public class TodoListFragment extends Fragment implements DialogCloseListener {

    private ToDoAdapter tasksAdapter;
    private List<ToDoModel> taskLists;
    private DatabaseHandler databaseHandler;

    /**
     * Empty constructor required for Fragment subclasses.
     */
    public TodoListFragment() {
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
     * It initializes the RecyclerView, FloatingActionButton, and DatabaseHandler, and sets up the RecyclerView and FloatingActionButton.
     * It also retrieves all tasks from the database, reverses the list, and sets the list to the adapter.
     * Finally, it sets an OnClickListener to the FloatingActionButton to show the AddNewTask dialog when clicked.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        // Initialize the DatabaseHandler and open the database
        databaseHandler = new DatabaseHandler(getContext());
        databaseHandler.openDatabase();

        // Initialize the task list
        taskLists = new ArrayList<>();

        // Initialize the RecyclerView and set its layout manager and adapter
        RecyclerView tasksRecyclerView = view.findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksAdapter = new ToDoAdapter(databaseHandler, this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        // Initialize the FloatingActionButton
        FloatingActionButton floatingActionButton = view.findViewById(R.id.tasksFloatingActionButton);

        // Initialize the ItemTouchHelper and attach it to the RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        // Retrieve all tasks from the database, reverse the list, and set the list to the adapter
        taskLists = databaseHandler.getAllTasks();
        Collections.reverse(taskLists);
        tasksAdapter.setTaskList(taskLists);

        // Set an OnClickListener to the FloatingActionButton to show the AddNewTask dialog when clicked
        floatingActionButton.setOnClickListener(v -> AddNewTask.newInstance(this).show(getChildFragmentManager(), AddNewTask.TAG));

        // Return the fragment view
        return view;
    }

    /**
     * This method is called when a dialog is closed.
     * It updates the task list and notifies the adapter of the changes.
     *
     * @param dialog The dialog that was closed.
     */
    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskLists = databaseHandler.getAllTasks();
        Collections.reverse(taskLists);
        tasksAdapter.clear();
        tasksAdapter.setTaskList(taskLists);
        tasksAdapter.notifyDataSetChanged();
    }
}