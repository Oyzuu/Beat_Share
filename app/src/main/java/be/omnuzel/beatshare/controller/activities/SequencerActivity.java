package be.omnuzel.beatshare.controller.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.dialogs.AddSoundDialog;
import be.omnuzel.beatshare.controller.dialogs.SetBMPDialog;
import be.omnuzel.beatshare.controller.threads.PlaybackThread;
import be.omnuzel.beatshare.model.Bar;
import be.omnuzel.beatshare.model.Sequence;
import be.omnuzel.beatshare.model.User;

// TODO Option to remove bar / sound
// TODO modify drawer menu to fit app needs

public class SequencerActivity
        extends
            AppCompatActivity
        implements
            NavigationView.OnNavigationItemSelectedListener,
            AddSoundDialog.SoundDialogListener,
            SetBMPDialog  .BPMDialogListener,
            PlaybackThread.PlaybackListener {

    public static final int
            STOPPED = 0,
            PLAYING = 1,
            PAUSED  = 2;

    private User user;

    private int
            bpm,
            state,
            currentStep;

    private Sequence sequence;
    private Bar
            bar1,
            bar2,
            bar3,
            bar4;

    private android.support.v7.app.ActionBar actionBar;
    private Spinner spinner;

    private Bar    activeBar;
    private String activeSound;
    private int[]  activePads = {
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequencer);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = (User) extras.get("user");
        }

        // Left drawer init and event management
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.sequencer_rootview);

        if (drawerLayout != null) {
            drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {}

                @Override
                public void onDrawerOpened(View drawerView) {
                    if (getSupportActionBar() != null)
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                    if (user == null)
                        return;

                    TextView headerNameText = (TextView) findViewById(R.id.header_name);
                    if (headerNameText != null)
                        headerNameText.setText(user.getUserName());

                    TextView headerMailText = (TextView) findViewById(R.id.header_mail);
                    if (headerMailText != null)
                        headerMailText.setText(user.getEmail());
                }

                @Override
                public void onDrawerClosed(View drawerView) {}

                @Override
                public void onDrawerStateChanged(int newState) {}
            });
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.sequencer_drawer);
        if (navigationView != null) {
            navigationView.setItemIconTintList(null);
            navigationView.setNavigationItemSelectedListener(this);
        }

        // Sequence init
        sequence = new Sequence();
        bar1     = new Bar();
        bar2     = new Bar();
        bar3     = new Bar();
        bar4     = new Bar();

        sequence.addBar(bar1);

        // Sequencer init
        activeBar   = bar1;
        activeSound = "";
        bpm         = 60;
        state       = STOPPED;
        currentStep = 0;

        // Display BPM in action bar
        actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(bpm + " BPM");

        // Spinner init
        spinner = (Spinner) findViewById(R.id.sounds_spinner);
        refreshSpinner();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activeSound = activeBar.getActiveSoundsNames().get(position);
                Log.i("SPINNER ITEM SELECTED", activeSound);

                if (activeSound.equals("empty")) {
                    resetActivePads();
                }
                else {
                    activePads  = activeBar.getSoundMatrix(activeSound);
                    Log.i("SPINNER SELECT MATRIX", activeBar.getReadableMatrixFromSound(activeSound));
                }

                refreshButtons();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.sequencer_rootview);

        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void setBPM(int bpm) {
        this.bpm = bpm;
        if (actionBar != null)
            actionBar.setTitle(bpm + " BPM");
    }

    @Override
    public int getBPM() {
        return bpm;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public int getCurrentStep() {
        return currentStep;
    }

    @Override
    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    @Override
    public Sequence getSequence() {
        return sequence;
    }

    // TODO populate these
    public void toSettings() {
        new SetBMPDialog().show(getFragmentManager(), "set BPM");
    }
    public void toAccount() {
        snackThis("account");
    }
    public void importSequence() {
        snackThis("import");
    }
    public void exportSequence() {
        snackThis("export");
    }
    public void saveSequence() {
        snackThis("save");
    }

    public void play(View view) {
        if (state == STOPPED || state == PAUSED) {
            state = PLAYING;
            new PlaybackThread(this);
        }
        else {
            state = PAUSED;
        }

        refreshPlayButton(state);
    }

    public void stop(View view) {
        state       = STOPPED;
        currentStep = 0;
        refreshPlayButton(state);
    }

    public void addSound(View view) {
        new AddSoundDialog().show(getFragmentManager(), "set sound");
    }

    @Override
    public void setSound(String soundName) {
        if (activeBar.getActiveSoundsNames().get(0).equals("empty")) {
            activeBar.getActiveSoundsNames().remove(0);
        }

        int id = getFileId(soundName);
        activeBar.addSound(soundName, id);

        stop(new View(this));

        refreshSpinner();
        spinner.setSelection(activeBar.getActiveSoundsNames().indexOf(soundName));

//        Log.i("FILE_NAME", getResources().getString(id));
//        Log.i("new sound matrix", activeBar.getReadableMatrixFromSound(soundName));
    }

    private int getFileId(String soundName) {
        String fileName = soundName.replace(" ", "_");
        return getResources().getIdentifier(fileName, "raw", getPackageName());
    }

    public void addBar(View view) {
        Button addBarButton = (Button) findViewById(R.id.add_bar_button);

        switch (sequence.getTotalBars()) {
            case 1 : activeBar = bar2; break;
            case 2 : activeBar = bar3; break;
            case 3 : activeBar = bar4;
                if (addBarButton != null) addBarButton.setVisibility(View.GONE); break;
        }
        sequence.addBar(activeBar);

        String layoutName = "bar" + sequence.getTotalBars() + "_layout";
        int id = getResources().getIdentifier(layoutName, "id", getPackageName());
        LinearLayout barLayout = (LinearLayout) findViewById(id);

        if (barLayout != null) {
            barLayout.setVisibility(View.VISIBLE);
        }

        String name = "button_bar" + sequence.getTotalBars();
        id = getResources().getIdentifier(name, "id", getPackageName());
        View button = findViewById(id);
        resetActivePads();
        selectBar(button);
    }

    public void selectBar(View view) {
        for (int i = 1; i <= 4; i++) {
            String name = "button_bar" + i;
            int id = getResources().getIdentifier(name, "id", getPackageName());
            Button button = (Button) findViewById(id);
            if (button != null) {
                button.setTextColor (getResources().getColor(R.color.white));
            }
        }

        Button button = (Button) findViewById(view.getId());
        if (button != null) {
            button.setTextColor (getResources().getColor(R.color.colorAccent));
        }

        switch (view.getId()) {
            case R.id.button_bar1 : activeBar = bar1; break;
            case R.id.button_bar2 : activeBar = bar2; break;
            case R.id.button_bar3 : activeBar = bar3; break;
            case R.id.button_bar4 : activeBar = bar4; break;
        }

        refreshButtons();
        refreshSpinner();
    }

    public void activateButton(View view) {
        if (activeSound.equals("empty")) {
            snackThis("Add a sound");
            return;
        }

        Drawable active   = getResources().getDrawable(R.drawable.sequencer_pad);
        Drawable inactive = getResources().getDrawable(R.drawable.disabled_sequencer_pad);

        String[] buttonName = getResources().getResourceName(view.getId()).split("_");
        int buttonNumber    = Integer.parseInt(buttonName[2]);

        if (activePads[buttonNumber - 1] == 0) {
            view.setBackground(active);
            activePads[buttonNumber - 1] = 1;
        }
        else {
            view.setBackground(inactive);
            activePads[buttonNumber - 1] = 0;
        }

        activeBar.updateSound(activeSound, activePads);
//        Log.i("Active pads for sound", activeBar.getReadableMatrixFromSound(activeSound));
    }

    private void snackThis(String message) {
        Snackbar.make(findViewById(R.id.sequencer_rootview), message, Snackbar.LENGTH_SHORT).show();
    }

    private void refreshSpinner() {
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activeBar.getActiveSoundsNames());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    private void refreshButtons() {
        Drawable active   = getResources().getDrawable(R.drawable.sequencer_pad);
        Drawable inactive = getResources().getDrawable(R.drawable.disabled_sequencer_pad);

        for (int i = 0; i < activePads.length; i++) {
            String buttonId = "seq_button_" + (i+1);
            int    id       = getResources().getIdentifier(buttonId, "id", getPackageName());
            View view       = findViewById(id);

            if (activePads[i] == 1) {
                if (view != null) {
                    view.setBackground(active);
                }
            }
            else {
                if (view != null) {
                    view.setBackground(inactive);
                }
            }
        }
    }

    private void refreshPlayButton(int state) {
        Button playButton = (Button) findViewById(R.id.seq_play_button);

        if (playButton != null) {
            switch (state) {
                case STOPPED :
                case PAUSED  : playButton.setText(getString(R.string.play));  break;
                case PLAYING : playButton.setText(getString(R.string.pause)); break;
            }
        }
    }
    private void resetActivePads() {
        activePads = new int[16];
    }
}
