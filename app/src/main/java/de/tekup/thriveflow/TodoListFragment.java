package de.tekup.thriveflow;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tekup.thriveflow.adapter.ToDoAdapter;
import de.tekup.thriveflow.model.ToDoModel;
import de.tekup.thriveflow.utils.DatabaseHandler;


public class TodoListFragment extends Fragment implements DialogCloseListener {

    private RecyclerView tasksRecyclerView;
    private FloatingActionButton floatingActionButton;
    private ToDoAdapter tasksAdapter;
    private List<ToDoModel> taskLists;
    private DatabaseHandler databaseHandler;

    public TodoListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        databaseHandler = new DatabaseHandler(getContext());
        databaseHandler.openDatabase();

        taskLists = new ArrayList<>();

        tasksRecyclerView = view.findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksAdapter = new ToDoAdapter(databaseHandler, (MainActivity) getActivity());
        tasksRecyclerView.setAdapter(tasksAdapter);

        floatingActionButton = view.findViewById(R.id.tasksFloatingActionButton);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        taskLists = databaseHandler.getAllTasks();
        Collections.reverse(taskLists);
        tasksAdapter.setTaskList(taskLists);

        floatingActionButton.setOnClickListener(v -> {
            AddNewTask.newInstance(this).show(getChildFragmentManager(), AddNewTask.TAG);
        });

        return view;

    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskLists = databaseHandler.getAllTasks();
        Collections.reverse(taskLists);
        tasksAdapter.setTaskList(taskLists);
        tasksAdapter.notifyDataSetChanged();
    }
}