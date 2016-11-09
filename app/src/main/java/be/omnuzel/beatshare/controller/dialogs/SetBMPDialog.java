package be.omnuzel.beatshare.controller.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import be.omnuzel.beatshare.R;

public class SetBMPDialog extends DialogFragment {

    public interface BPMDialogListener {
        void setBPM(int bpm);

        int getBPM();
    }

    private BPMDialogListener callback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.callback = (BPMDialogListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from((Context) callback);
        View view = inflater.inflate(R.layout.bpm_edit, null);
        final EditText editText = (EditText) view;

        editText.setText(callback.getBPM() + "");

        builder.setTitle(R.string.bpm_dialog)
                .setView(editText)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int bpm = Integer.parseInt(editText.getText().toString());
                        callback.setBPM(bpm);
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
