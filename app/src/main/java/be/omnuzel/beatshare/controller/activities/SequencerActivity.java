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
import android.widget.Spinner;
import android.widget.TextView;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.dialogs.AddSoundDialog;
import be.omnuzel.beatshare.controller.dialogs.SetBMPDialog;
import be.omnuzel.beatshare.controller.threads.PlaybackThread;
import be.omnuzel.beatshare.controller.utils.SoundBank;
import be.omnuzel.beatshare.model.Bar;
import be.omnuzel.beatshare.model.Sequence;
import be.omnuzel.beatshare.model.User;

// TODO check AudioAttributes CONTENT_TYPE and USAGE
// TODO will probably need its own fragment
// TODO animate button activation --- IF TIME FOR IT

public class SequencerActivity
        extends
            AppCompatActivity
        implements
            NavigationView.OnNavigationItemSelectedListener,
            AddSoundDialog.SoundDialogListener,
            SetBMPDialog.BPMDialogListener,
            PlaybackThread.PlaybackListener {

    private SoundBank      soundBank;
    private User           user;
    private PlaybackThread playbackThread;

    private int
            bpm = 60,
            state = PlaybackThread.STOPPED;

    private Sequence sequence;
    private Bar
            bar1,
            bar2,
            bar3,
            bar4;

    private android.support.v7.app.ActionBar actionBar;
    private Spinner spinner;

    private Bar       activeBar;
    private String    activeSound;
    private int[]     activePads = {
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
            Log.i("SEQUENCER", "Started for user : " + user.getUserName() + " - " + user.getEmail());
        }

        actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(bpm + " BPM");

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
        bar1 = new Bar();
        bar2 = new Bar();
        bar3 = new Bar();
        bar4 = new Bar();
        sequence.addBar(bar1);
        sequence.addBar(bar2);
        sequence.addBar(bar3);
        sequence.addBar(bar4);
        activeBar   = bar1;
        activeSound = "";

        // First bar button init
        View firstBarButton = findViewById(R.id.button_bar1);
        if (firstBarButton != null)
            firstBarButton.setBackground(getResources().getDrawable(R.drawable.plain_button));

        // Spinner init
        spinner = (Spinner) findViewById(R.id.sounds_spinner);
        refreshSpinner();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activeSound = activeBar.getActiveSoundsNames().get(position);
                activePads  = activeBar.getSoundMatrix(activeSound);
                Log.i("SPINNER ITEM SELECTED", activeSound);
                Log.i("SPINNER SELECT MATRIX", activeBar.getReadableMatrixFromSound(activeSound));
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
        sequence.build();
        state = PlaybackThread.PLAYING;
        PlaybackThread playbackThread = new PlaybackThread(this, sequence);
        playbackThread.start();
    }

    public void stop(View view) {
        state = PlaybackThread.STOPPED;
    }

    public void addSound(View view) {
        new AddSoundDialog().show(getFragmentManager(), "set sound");
    }

    @Override
    public void setSound(String soundName) {
        int id = getFileId(soundName);
        activeBar.addSound(soundName, id);

        refreshSpinner();
        spinner.setSelection(activeBar.getActiveSoundsNames().indexOf(soundName));

        Log.i("FILE_NAME", getResources().getString(id));
        Log.i("new sound matrix", activeBar.getReadableMatrixFromSound(soundName));
    }

    private int getFileId(String soundName) {
        String fileName = soundName.replace(" ", "_");
        return getResources().getIdentifier(fileName, "raw", getPackageName());
    }

    public void addBar(View view) {}

    public void selectBar(View view) {
        switch (view.getId()) {
            case R.id.button_bar1 : activeBar = bar1; break;
            case R.id.button_bar2 : activeBar = bar2; break;
            case R.id.button_bar3 : activeBar = bar3; break;
            case R.id.button_bar4 : activeBar = bar4; break;
        }

        Button barButton1 = (Button) findViewById(R.id.button_bar1);
        if (barButton1 != null) {
            barButton1.setBackground(getResources().getDrawable(R.drawable.secondary_button_still));
        }
        Button barButton2 = (Button) findViewById(R.id.button_bar2);
        if (barButton2 != null) {
            barButton2.setBackground(getResources().getDrawable(R.drawable.secondary_button_still));
        }
        Button barButton3 = (Button) findViewById(R.id.button_bar3);
        if (barButton3 != null) {
            barButton3.setBackground(getResources().getDrawable(R.drawable.secondary_button_still));
        }
        Button barButton4 = (Button) findViewById(R.id.button_bar4);
        if (barButton4 != null) {
            barButton4.setBackground(getResources().getDrawable(R.drawable.secondary_button_still));
        }

        refreshButtons();
        refreshSpinner();
    }

    public void activateButton(View view) {
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
        Log.i("Active pads for sound", activeBar.getReadableMatrixFromSound(activeSound));
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
                if (view != null) view.setBackground(active);
            }
            else {
                if (view != null) view.setBackground(inactive);
            }
        }
    }

    private void resetActivePads() {
        for (int i = 0; i < activePads.length; i++) {
            activePads[i] = 0;
        }
    }
}
