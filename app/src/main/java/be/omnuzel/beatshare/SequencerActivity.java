package be.omnuzel.beatshare;

import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.LinkedList;

import be.omnuzel.beatshare.classes.SoundBank;

public class SequencerActivity extends AppCompatActivity {

    // TODO Check AudioAttributes CONTENT_TYPE and USAGE
    // TODO Modify onLoadComplete() for Button / sample id

    private SoundBank soundBank;
    private LinkedList<Button> buttons = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequencer);

        for (int i = 0; i < 16; i++) {
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

        soundBank.load(R.raw.iamm_c1_bass_drum, R.id.seq_button_0);

        soundBank.getSoundPool().setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                buttons.get(sampleId - 1)
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
