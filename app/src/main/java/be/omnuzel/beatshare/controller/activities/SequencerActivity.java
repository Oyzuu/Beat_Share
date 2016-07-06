package be.omnuzel.beatshare.controller.activities;

import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.utils.SoundBank;

// TODO check AudioAttributes CONTENT_TYPE and USAGE
// TODO will probably need its own fragment
// TODO animate button activation --- IF TIME FOR IT

public class SequencerActivity extends AppCompatActivity {

    private SoundBank soundBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequencer);

        // Init all the buttons with a setActionTouch
        for (int i = 1; i <= 16; i++) {
            String buttonId = "seq_button_" + i;
            int    id       = getResources().getIdentifier(buttonId, "id", getPackageName());
            Button button   = (Button) findViewById(id);
            Log.i("SEQUENCER", "Button " + i + " created.");

            setActionOnTouch(button);
            Log.i("SEQUENCER", "Touch action set on button " + i);
        }

        initSounds();
    }

    /**
     * Load the sounds in the SoundBank
     */
    public void initSounds() {
        soundBank = SoundBank.getInstance(this);

        if (!soundBank.getLoadingState()) {
            soundBank.load(R.raw.iamm_c1_bass_drum,      R.id.seq_button_1);
            soundBank.load(R.raw.iamm_d1_acoustic_snare, R.id.seq_button_2);
            soundBank.load(R.raw.iamm_c3_hi_bongo,       R.id.seq_button_3);
            soundBank.load(R.raw.iamm_cd3_low_bongo,     R.id.seq_button_4);
            soundBank.load(R.raw.iamm_fd1_closed_hihat,  R.id.seq_button_5);
            soundBank.load(R.raw.iamm_ad1_open_hihat,    R.id.seq_button_6);
            soundBank.load(R.raw.iamm_e1_electric_snare, R.id.seq_button_7);
            soundBank.load(R.raw.iamm_dd2_ride_cymbal_1, R.id.seq_button_8);
            soundBank.load(R.raw.iamm_gd3_low_agogo,     R.id.seq_button_9);
        }

        if (soundBank.getLoadingState()) {
            activateButtons();
        }

        // TODO move this elsewhere (SoundBank -> callback.activateButtons()) --- IF TIME FOR IT
        // Ensures that the last sound is loaded before calling activateButtons() for the first time
        // Afterwards, notifies the sound bank that all the sounds are loaded
        soundBank.getSoundPool().setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (sampleId == soundBank.getMaxSoundId()) {
                    soundBank.setLoadingState(true);

                    activateButtons();
                }
            }
        });
    }

    /**
     * Set a background color for activated pads
     */
    private void activateButtons() {
        for (int id : soundBank.getLoadedButtons())  {
            Button button = (Button) findViewById(id);

            if (button != null) {
                button.setBackground(getResources().getDrawable(R.drawable.sequencer_pad));
            }
        }
    }

    /**
     * Force a button to react on touch instead of on release
     * @param button the button to set up
     */
    private void setActionOnTouch(Button button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    soundBank.play(view.getId());
                }

                return false;
            }
        });
    }
}
