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
        void setSound(String soundName);
    }

    private String[] sounds = {
        "acoustic_snare",
        "closed_hihat",
        "drum_kick",
        "electric_snare",
        "open_hihat",
        "ride_cymbal"
    };

    private SoundDialogListener callback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.callback = (SoundDialogListener) activity;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder .setTitle(getString(R.string.sounds_dialog))
                .setItems(sounds, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.setSound(sounds[which]);
                    }
                });

        return builder.create();
    }
}
