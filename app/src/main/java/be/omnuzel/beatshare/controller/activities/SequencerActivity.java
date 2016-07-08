package be.omnuzel.beatshare.controller.activities;

import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.Threads.PlaybackThread;
import be.omnuzel.beatshare.controller.utils.SoundBank;
import be.omnuzel.beatshare.model.User;

// TODO check AudioAttributes CONTENT_TYPE and USAGE
// TODO will probably need its own fragment
// TODO animate button activation --- IF TIME FOR IT

public class SequencerActivity
        extends
            AppCompatActivity
        implements
            SoundBank.ISoundBank,
            NavigationView.OnNavigationItemSelectedListener {

    private SoundBank      soundBank;
    private User           user;
    private boolean        isPlaying,
                           isRecording,
                           recordingHasStarted,
                           threadWaiting;
    private StringBuilder  soundBuffer;
    private long           startingTime,
                           elapsedTime,
                           endingTime;
    private PlaybackThread playbackThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequencer);

        Bundle extras = getIntent().getExtras();

        isPlaying           = false;
        isRecording         = false;
        recordingHasStarted = false;
        threadWaiting       = false;
        soundBuffer         = new StringBuilder("");

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

                    if (getSupportActionBar() != null)
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    if (getSupportActionBar() != null)
                        getSupportActionBar().setIcon(getResources()
                                .getDrawable(R.drawable.ic_menu_white_24dp));
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.sequencer_drawer);
        if (navigationView != null) {
            navigationView.setItemIconTintList(null);
            navigationView.setNavigationItemSelectedListener(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bar_menu_settings : toSettings(); break;
            case R.id.bar_menu_account  : toAccount();  break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_account  : toAccount();      break;
            case R.id.drawer_settings : toSettings();     break;
            case R.id.drawer_import   : importSequence(); break;
            case R.id.drawer_export   : exportSequence(); break;
            case R.id.drawer_save     : saveSequence();   break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.sequencer_rootview);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
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
        startingTime        = System.currentTimeMillis();
    }

    @Override
    public void writeInSequence(int soundId) {
        elapsedTime = System.currentTimeMillis() - startingTime;

        String stamp = String.format(
                "%s-%s,", elapsedTime, soundId
        );

        Log.i("SOUND_STAMP", stamp);

        soundBuffer.append(stamp);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.sequencer_rootview);

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    // TODO populate this
    public void toSettings() {}
    public void toAccount() {}
    public void importSequence() {}
    public void exportSequence() {}
    public void saveSequence() {}

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

        // TODO Remove this in prod
//        Intent intent = new Intent(this, Debug.class);
//        intent.putExtra("debugInfo", soundBuffer.toString());
//        startActivity(intent);

        if (!isPlaying) {
            isPlaying = true;
            if (playButton != null)
                playButton.setText(getString(R.string.pause));

            playbackThread = new PlaybackThread(soundBuffer.toString(), soundBank.getSoundPool());
            playbackThread.start();
        }
        else {
            isPlaying = false;
            if (playButton != null)
                playButton.setText(getString(R.string.play));
        }
    }

    public void stop(View view) {}

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
}
