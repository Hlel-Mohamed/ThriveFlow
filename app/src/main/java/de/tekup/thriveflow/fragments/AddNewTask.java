package de.tekup.thriveflow.fragments;

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

import de.tekup.thriveflow.R;
import de.tekup.thriveflow.models.ToDoModel;
import de.tekup.thriveflow.utils.DatabaseHandler;
import de.tekup.thriveflow.utils.DialogCloseListener;

/**
 * AddNewTask is a BottomSheetDialogFragment that allows the user to add a new task or update an existing task.
 * It provides a user interface for entering the task text and a button for saving the task.
 * It uses a DatabaseHandler for inserting the new task into the database or updating the existing task.
 */
public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";

    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DatabaseHandler databaseHandler;

    private DialogCloseListener dialogCloseListener;

    /**
     * This method creates a new instance of the AddNewTask fragment.
     *
     * @param dialogCloseListener The listener for the dialog close event.
     * @return A new instance of AddNewTask.
     */
    public static AddNewTask newInstance(DialogCloseListener dialogCloseListener) {
        AddNewTask addNewTask = new AddNewTask();
        addNewTask.dialogCloseListener = dialogCloseListener;
        return addNewTask;
    }

    /**
     * This method is called when the fragment is first created.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    /**
     * This method is called to have the fragment instantiate its user interface view.
     * It inflates the layout for the fragment and sets the soft input mode for the window.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    /**
     * This method is called after onCreateView() when the view hierarchy has been created.
     * It initializes the EditText, Button, and DatabaseHandler, and sets up the EditText and Button.
     *
     * @param view               The view returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
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

    /**
     * This method is called when the dialog is dismissed.
     * It calls the handleDialogClose method of the dialogCloseListener if it is not null.
     *
     * @param dialog The dialog that was dismissed.
     */
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (dialogCloseListener != null) {
            dialogCloseListener.handleDialogClose(dialog);
        }
    }

}