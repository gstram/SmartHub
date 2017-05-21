package com.example.stram.smartmirror;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by stram on 2/12/2016.
 * creates an error dialog
 */
public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.error)
                .setMessage(R.string.error2)
                .setPositiveButton(R.string.error_ok_text, null);

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
