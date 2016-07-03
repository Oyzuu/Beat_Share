package be.omnuzel.beatshare;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class SequencerActivity extends AppCompatActivity {

    // TODO Create a Sound object
    // TODO Create a SoundBank object extending SoundPool - will link Button and Sound

    private SoundPool soundPool;

    private int
            firstSound,
            secondSound,
            thirdSound,
            fourthSound,
            fifthSound,
            sixthSound;

    private Button
            button8,
            button9,
            button10,
            button12,
            button13,
            button14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequencer);

        button8   = (Button) findViewById(R.id.seq_button_8);
        button9   = (Button) findViewById(R.id.seq_button_9);
        button10  = (Button) findViewById(R.id.seq_button_10);
        button12  = (Button) findViewById(R.id.seq_button_12);
        button13  = (Button) findViewById(R.id.seq_button_13);
        button14  = (Button) findViewById(R.id.seq_button_14);


        setActionOnTouch(button8);
        setActionOnTouch(button9);
        setActionOnTouch(button10);
        setActionOnTouch(button12);
        setActionOnTouch(button13);
        setActionOnTouch(button14);

        initSounds();
    }

    public void initSounds() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }
        else {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage      (AudioAttributes.USAGE_GAME)
                    .build         ();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams     (6)
                    .setAudioAttributes(audioAttributes)
                    .build             ();
        }

        firstSound  = soundPool.load(this, R.raw.iamm_c1_bass_drum     , 0);
        secondSound = soundPool.load(this, R.raw.iamm_d1_acoustic_snare, 0);
        thirdSound  = soundPool.load(this, R.raw.iamm_e1_electric_snare, 0);
        fourthSound = soundPool.load(this, R.raw.iamm_ad1_open_hihat,    0);
        fifthSound  = soundPool.load(this, R.raw.iamm_cd3_low_bongo,     0);
        sixthSound  = soundPool.load(this, R.raw.iamm_gd3_low_agogo,     0);

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                switch (sampleId) {
                    case 1 :
                        button12.setBackground(getResources().getDrawable(R.drawable.sequencer_pad));
                        break;
                    case 2 :
                        button13.setBackground(getResources().getDrawable(R.drawable.sequencer_pad));
                        break;
                    case 3 :
                        button8 .setBackground(getResources().getDrawable(R.drawable.sequencer_pad));
                        break;
                    case 4 :
                        button9 .setBackground(getResources().getDrawable(R.drawable.sequencer_pad));
                        break;
                    case 5 :
                        button10.setBackground(getResources().getDrawable(R.drawable.sequencer_pad));
                        break;
                    case 6 :
                        button14.setBackground(getResources().getDrawable(R.drawable.sequencer_pad));
                        break;
                }
            }
        });
    }

    private void setActionOnTouch(Button button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    playSound(v);
                }
                return false;
            }
        });
    }

    public void playSound(View view) {
        switch (view.getId()) {
            case R.id.seq_button_8  : soundPool.play(thirdSound,  1, 1, 1, 0, 1); break;
            case R.id.seq_button_9  : soundPool.play(fourthSound, 1, 1, 1, 0, 1); break;
            case R.id.seq_button_10 : soundPool.play(fifthSound,  1, 1, 1, 0, 1); break;
            case R.id.seq_button_12 : soundPool.play(firstSound,  1, 1, 1, 0, 1); break;
            case R.id.seq_button_13 : soundPool.play(secondSound, 1, 1, 1, 0, 1); break;
            case R.id.seq_button_14 : soundPool.play(sixthSound,  1, 1, 1, 0, 1); break;
        }
    }
}
