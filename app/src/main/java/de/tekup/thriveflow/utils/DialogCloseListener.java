package de.tekup.thriveflow.utils;

import android.content.DialogInterface;

/**
 * DialogCloseListener is an interface that provides a method to handle the closing of a dialog.
 * It is used to perform actions when a dialog is closed.
 */
public interface DialogCloseListener {

    /**
     * This method is called when a dialog is closed.
     * It should contain the actions to be performed when the dialog is closed.
     *
     * @param dialog The dialog that was closed.
     */
    void handleDialogClose(DialogInterface dialog);
}