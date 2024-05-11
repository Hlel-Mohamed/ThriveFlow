package de.tekup.thriveflow;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import de.tekup.thriveflow.model.ToDoModel;
import de.tekup.thriveflow.utils.DatabaseHandler;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";

    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DatabaseHandler databaseHandler;

    private DialogCloseListener dialogCloseListener;
    public static AddNewTask newInstance(DialogCloseListener dialogCloseListener) {
        AddNewTask addNewTask = new AddNewTask();
        addNewTask.dialogCloseListener = dialogCloseListener;
        return addNewTask;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newTaskText = requireView().findViewById(R.id.newTaskEditText);
        newTaskSaveButton = requireView().findViewById(R.id.newTaskButton);

        databaseHandler = new DatabaseHandler(requireContext());
        databaseHandler.openDatabase();

        boolean isUpdate = false;
        final Bundle bundle = getArguments();

        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskText.setText(task);
            assert task != null;
            if (task.length() > 0)
                newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.holo_blue_dark));
        }
        newTaskText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                } else {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.holo_blue_dark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(v -> {
            if (!getActivity().isFinishing()) {
                String text = newTaskText.getText().toString();
                if (finalIsUpdate) {
                    databaseHandler.updateTask(bundle.getInt("id"), text);
                } else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    databaseHandler.insertTask(task);
                }
                getDialog().dismiss();
            }

        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (dialogCloseListener != null) {
            dialogCloseListener.handleDialogClose(dialog);
        }
    }

}
