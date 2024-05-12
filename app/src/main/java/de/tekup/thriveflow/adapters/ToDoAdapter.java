package de.tekup.thriveflow.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.tekup.thriveflow.AddNewTask;
import de.tekup.thriveflow.MainActivity;
import de.tekup.thriveflow.R;
import de.tekup.thriveflow.models.ToDoModel;
import de.tekup.thriveflow.utils.DatabaseHandler;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> {

    private List<ToDoModel> taskList;
    private final MainActivity activity;
    private final DatabaseHandler databaseHandler;


    public ToDoAdapter(DatabaseHandler databaseHandler, MainActivity activity) {
        this.databaseHandler = databaseHandler;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = activity.getLayoutInflater()
                .inflate(R.layout.task_layout, parent, false);
        return new ToDoViewHolder(itemView);
    }


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

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    public void setTaskList(List<ToDoModel> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    public void editTask(int position) {
        ToDoModel task = taskList.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt("id", task.getId());
        bundle.putString("task", task.getTask());

        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public void deleteTask(int position) {
        ToDoModel task = taskList.get(position);
        databaseHandler.deleteTask(task.getId());
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    public Context getContext() {
        return activity;
    }

    public static class ToDoViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;

        ToDoViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.taskCheckBox);
        }
    }
}
