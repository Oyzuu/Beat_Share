package be.omnuzel.beatshare.controller.activities;

import android.content.Intent;
import android.media.SoundPool;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.Threads.PlaybackThread;
import be.omnuzel.beatshare.controller.utils.SoundBank;
import be.omnuzel.beatshare.model.User;

// TODO check AudioAttributes CONTENT_TYPE and USAGE
// TODO will probably need its own fragment
// TODO animate button activation --- IF TIME FOR IT

public class SequencerActivity extends AppCompatActivity implements SoundBank.ISoundBank{

    private SoundBank     soundBank;
    private User          user;
    private boolean       isPlaying,
                          isRecording,
                          recordingHasStarted;
    private StringBuilder soundBuffer;
    private long          startingTime,
                          elapsedTime,
                          endingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequencer);

        Bundle extras = getIntent().getExtras();

        isPlaying           = false;
        isRecording         = false;
        recordingHasStarted = false;

        if (extras != null) {
            user = (User) extras.get("user");
            Log.i("SEQUENCER", "Started for user : " + user.getUserName() + " - " + user.getEmail());
        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.sequencer_rootview);

        if (drawerLayout != null) {
            drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    TextView headerNameText = (TextView) findViewById(R.id.header_name);
                    if (headerNameText != null)
                        headerNameText.setText(user.getUserName());

                    TextView headerMailText = (TextView) findViewById(R.id.header_mail);
                    if (headerMailText != null)
                        headerMailText.setText(user.getEmail());
                }

                @Override
                public void onDrawerClosed(View drawerView) {

                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        }

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
     * Load the sounds in the SoundBank and give a visual feedback of button activation
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

    public void play(View view) {
        Button playButton = (Button) findViewById(R.id.seq_play_button);

//        Intent intent = new Intent(this, Debug.class);
//        intent.putExtra("debugInfo", soundBuffer.toString());
//        startActivity(intent);

        if (!isPlaying) {
            isPlaying = true;
            if (playButton != null)
                playButton.setText(getString(R.string.pause));

            PlaybackThread playbackThread =
                    new PlaybackThread(soundBuffer.toString(), soundBank.getSoundPool());
            new Thread(playbackThread).start();
        }
        else {
            isPlaying = false;
            if (playButton != null)
                playButton.setText(getString(R.string.play));
        }
    }

    public void stop(View view) {

    }

    public void record(View view) {
        Button recordButton = (Button) findViewById(R.id.seq_record_button);

        if (!isRecording) {
            isRecording = true;
            if (recordButton != null)
                recordButton.setText(getString(R.string.stop));
        }
        else {
            isRecording         = false;
            recordingHasStarted = false;
            if (recordButton != null)
                recordButton.setText(getString(R.string.record));
        }
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public boolean isRecording() {
        return isRecording;
    }

    @Override
    public boolean hasRecordingStarted() {
        return recordingHasStarted;
    }

    @Override
    public void startRecording() {
        recordingHasStarted = true;
        soundBuffer         = new StringBuilder();
        startingTime        = System.currentTimeMillis();
    }

    @Override
    public void writeSound(int soundId) {
        elapsedTime = System.currentTimeMillis() - startingTime;

        String stamp = String.format(
                "%s-%s,", elapsedTime, soundId
        );

        Log.i("SOUND_STAMP", stamp);

        soundBuffer.append(stamp);
    }
}
