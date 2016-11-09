package be.omnuzel.beatshare.controller.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import be.omnuzel.beatshare.R;

public class OverwriteSaveDialog extends DialogFragment {
    public interface OverwriteSaveListener {
        void overwriteSave();
    }

    private OverwriteSaveListener callback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.callback = (OverwriteSaveListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.ow_dialog)
                .setMessage(R.string.ow_message)
                .setPositiveButton(getString(R.string.ow_pos_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.overwriteSave();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        return builder.create();
    }
}
