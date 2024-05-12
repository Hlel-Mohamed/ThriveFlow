package de.tekup.thriveflow.helpers;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import de.tekup.thriveflow.R;
import de.tekup.thriveflow.adapters.ToDoAdapter;

/**
 * RecyclerItemTouchHelper is a helper class that extends ItemTouchHelper.SimpleCallback.
 * It provides swipe functionality for a RecyclerView.
 * When an item is swiped left, it shows a dialog to confirm deletion of the task.
 * When an item is swiped right, it allows the task to be edited.
 */
public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private final ToDoAdapter adapter;

    /**
     * Constructor for the RecyclerItemTouchHelper class.
     * It calls the superclass constructor with the drag and swipe directions, and initializes the adapter.
     *
     * @param adapter The adapter for the RecyclerView.
     */
    public RecyclerItemTouchHelper(ToDoAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    /**
     * This method is called when an item is moved.
     * It returns false because item movement is not supported.
     *
     * @param recyclerView The RecyclerView which item is being moved.
     * @param viewHolder   The ViewHolder which is being moved.
     * @param target       The ViewHolder to which the item is moved.
     * @return false because item movement is not supported.
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * This method is called when an item is swiped.
     * If the item is swiped left, it shows a dialog to confirm deletion of the task.
     * If the item is swiped right, it allows the task to be edited.
     *
     * @param viewHolder The ViewHolder which is being swiped.
     * @param direction  The direction to which the ViewHolder is swiped.
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes", (dialog, which) -> adapter.deleteTask(position))
                    .setNegativeButton("No", (dialog, which) -> adapter.notifyItemChanged(position));
            builder.show();
        } else {
            adapter.editTask(position);
        }
    }

    /**
     * This method is called by ItemTouchHelper on RecyclerView's onDraw callback.
     * It draws the background and the icon of the item during the swipe.
     *
     * @param canvas            The canvas which is used for drawing.
     * @param recyclerView      The RecyclerView on which the drawing is performed.
     * @param viewHolder        The ViewHolder which is being swiped.
     * @param dX                The amount of horizontal displacement caused by user's action.
     * @param dY                The amount of vertical displacement caused by user's action.
     * @param actionState       The type of interaction on the View.
     * @param isCurrentlyActive True if this view is currently being controlled by the user or false it is simply animating back to its original state.
     */
    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        ColorDrawable background;
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        // If the item is being swiped to the right, set the icon to edit and the background to green.
        // Otherwise, set the icon to delete and the background to red.
        if (dX > 0) {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.baseline_mode_edit_outline_24);
            background = new ColorDrawable(Color.GREEN);
        } else {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.baseline_delete_24);
            background = new ColorDrawable(Color.RED);
        }

        // Calculate the margins and bounds for the icon.
        assert icon != null;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        // If the item is being swiped to the right, draw the icon and background on the left.
        // If the item is being swiped to the left, draw the icon and background on the right.
        // If the item is not being swiped, do not draw the background.
        if (dX > 0) {
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) {
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else {
            background.setBounds(0, 0, 0, 0);
        }

        // Draw the background and the icon on the canvas.
        background.draw(canvas);
        icon.draw(canvas);
    }

}