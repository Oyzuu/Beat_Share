package be.omnuzel.beatshare;

import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.LinkedList;

import be.omnuzel.beatshare.classes.SoundBank;

public class SequencerActivity extends AppCompatActivity {

    // TODO Check AudioAttributes CONTENT_TYPE and USAGE
    // TODO Modify onLoadComplete() for Button / sample id

    private SoundBank          soundBank;
    private LinkedList<Button> buttons = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequencer);

        // For matching coherence on button / sample id - NO ZERO BUTTON !
        buttons.add(null);

        // Init all the buttons with a setActionTouch and store them in a list for onLoadComplete
        for (int i = 1; i <= 16; i++) {
            String buttonId = "seq_button_" + i;
            int    id       = getResources().getIdentifier(buttonId, "id", getPackageName());
            Button button   = (Button) findViewById(id);
            Log.i("SEQ_ONCREATE_INFO", "Button " + i + " created.");

            setActionOnTouch(button);
            Log.i("SEQ_ONCREATE_INFO", "Touch action set on button " + i);

            buttons.add(button);
        }

        initSounds();
    }

    public void initSounds() {
        soundBank = new SoundBank(this);

        soundBank.load(R.raw.iamm_c1_bass_drum,      R.id.seq_button_1);
        soundBank.load(R.raw.iamm_d1_acoustic_snare, R.id.seq_button_2);
        soundBank.load(R.raw.iamm_c3_hi_bongo,       R.id.seq_button_3);
        soundBank.load(R.raw.iamm_cd3_low_bongo,     R.id.seq_button_4);
        soundBank.load(R.raw.iamm_fd1_closed_hihat,  R.id.seq_button_5);
        soundBank.load(R.raw.iamm_ad1_open_hihat,    R.id.seq_button_6);
        soundBank.load(R.raw.iamm_e1_electric_snare, R.id.seq_button_7);

        // First sound seems to load instantly and thus is not "heard" on the listener - DUCT TAPE
        buttons.get(1).setBackground(getResources().getDrawable(R.drawable.sequencer_pad));

        soundBank.getSoundPool().setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.i("SEQ_LOADCOMPLETE", "Sample " + sampleId + " loaded !");
                buttons.get(sampleId)
                        .setBackground(getResources().getDrawable(R.drawable.sequencer_pad));
            }
        });
    }

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
