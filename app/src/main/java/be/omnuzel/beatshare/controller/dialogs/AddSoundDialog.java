package be.omnuzel.beatshare.controller.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import be.omnuzel.beatshare.R;


public class AddSoundDialog extends DialogFragment {

    public interface SoundDialogListener {
        void addSound(String soundName);
    }

    private SoundDialogListener callback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.callback = (SoundDialogListener) activity;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String[] sounds = getResources().getStringArray(R.array.sounds);
        builder .setTitle(getString(R.string.sounds_dialog))
                .setItems(sounds, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.addSound(sounds[which]);
                    }
                });

        return builder.create();
    }
}
