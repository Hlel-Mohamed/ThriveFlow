package de.tekup.thriveflow.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.tekup.thriveflow.fragments.AddNewTask;
import de.tekup.thriveflow.MainActivity;
import de.tekup.thriveflow.R;
import de.tekup.thriveflow.models.ToDoModel;
import de.tekup.thriveflow.utils.DatabaseHandler;
import de.tekup.thriveflow.utils.DialogCloseListener;

/**
 * ToDoAdapter is a RecyclerView.Adapter that displays a list of tasks.
 * It provides functionality for editing and deleting tasks.
 * It uses a DatabaseHandler for updating the status of a task, editing a task, and deleting a task.
 */
public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> {

    private List<ToDoModel> taskList;
    private final DatabaseHandler databaseHandler;
    private final DialogCloseListener dialogCloseListener;

    /**
     * Constructor for the ToDoAdapter class.
     * It initializes the DatabaseHandler and MainActivity.
     *
     * @param databaseHandler The DatabaseHandler for the adapter.
     */
    public ToDoAdapter(DatabaseHandler databaseHandler, DialogCloseListener dialogCloseListener) {
        this.databaseHandler = databaseHandler;
        this.dialogCloseListener = dialogCloseListener;
    }

    /**
     * This method is called when a new ViewHolder gets created.
     * It inflates the layout for the ViewHolder and returns a new ToDoViewHolder.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ToDoViewHolder.
     */
    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ToDoViewHolder(itemView);
    }

    /**
     * This method is called by RecyclerView to display the data at the specified position.
     * It updates the contents of the ViewHolder to reflect the task at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ToDoViewHolder holder, int position) {
        databaseHandler.openDatabase();

        ToDoModel task = taskList.get(position);
        holder.task.setText(task.getTask());
        holder.task.setChecked(toBoolean(task.getStatus()));
        holder.task.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                databaseHandler.updateStatus(task.getId(), 1);
            } else {
                databaseHandler.updateStatus(task.getId(), 0);
            }
        });
    }

    /**
     * This method returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * This method converts an integer to a boolean.
     * It returns true if the integer is not 0, and false otherwise.
     *
     * @param n The integer to be converted.
     * @return The boolean representation of the integer.
     */
    private boolean toBoolean(int n) {
        return n != 0;
    }

    /**
     * This method sets the task list for the adapter and notifies the adapter that the data set has changed.
     *
     * @param taskList The new task list for the adapter.
     */
    public void setTaskList(List<ToDoModel> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    /**
     * This method is called to edit a task.
     * It gets the task at the given position, creates a bundle with the task id and task text, and shows the AddNewTask dialog with the bundle.
     *
     * @param position The position of the task in the adapter's data set.
     */
    public void editTask(int position) {
        ToDoModel task = taskList.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt("id", task.getId());
        bundle.putString("task", task.getTask());

        AddNewTask fragment = AddNewTask.newInstance(dialogCloseListener);
        fragment.setArguments(bundle);
        fragment.show(((Fragment) dialogCloseListener).getChildFragmentManager(), AddNewTask.TAG);
    }

    /**
     * This method is called to delete a task.
     * It gets the task at the given position, deletes the task from the database, removes the task from the task list, and notifies the adapter that the item has been removed.
     *
     * @param position The position of the task in the adapter's data set.
     */
    public void deleteTask(int position) {
        ToDoModel task = taskList.get(position);
        databaseHandler.deleteTask(task.getId());
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * This method returns the context for the adapter.
     *
     * @return The context for the adapter.
     */
    public Context getContext() {
        return ((Fragment) dialogCloseListener).getContext();
    }

    public void clear() {
        taskList.clear();
        notifyDataSetChanged();
    }

    /**
     * ToDoViewHolder is a RecyclerView.ViewHolder that displays a task.
     * It provides a user interface for viewing the task text and checking the task status.
     */
    public static class ToDoViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;

        /**
         * Constructor for the ToDoViewHolder class.
         * It initializes the CheckBox.
         *
         * @param itemView The view for the ViewHolder.
         */
        ToDoViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.taskCheckBox);
        }
    }
}