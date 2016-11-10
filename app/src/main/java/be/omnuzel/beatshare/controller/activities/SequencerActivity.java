package be.omnuzel.beatshare.controller.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.dialogs.AddSoundDialog;
import be.omnuzel.beatshare.controller.dialogs.LogOutDialog;
import be.omnuzel.beatshare.controller.dialogs.OverwriteSaveDialog;
import be.omnuzel.beatshare.controller.dialogs.SetBMPDialog;
import be.omnuzel.beatshare.controller.threads.PlaybackThread;
import be.omnuzel.beatshare.controller.utils.Localizer;
import be.omnuzel.beatshare.db.DataAccessObject;
import be.omnuzel.beatshare.db.SequenceDAO;
import be.omnuzel.beatshare.model.Bar;
import be.omnuzel.beatshare.model.Location;
import be.omnuzel.beatshare.model.Sequence;
import be.omnuzel.beatshare.model.User;

// TODO IF TIME FOR IT - - - Option to remove bar / sound
// TODO stop PlaybackThread when log off

public class SequencerActivity
        extends
        AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        AddSoundDialog.AddSoundDialogListener,
        SetBMPDialog.BPMDialogListener,
        PlaybackThread.PlaybackListener,
        OverwriteSaveDialog.OverwriteSaveListener {

    public static void startActivity(Context context, User user) {
        Intent intent = new Intent(context, SequencerActivity.class);
        intent.putExtra("user", user);
        context.startActivity(intent);
    }

    // STATE CONSTANTS
    public static final int STOPPED = 0,
            PLAYING = 1,
            PAUSED = 2;

    @IntDef({STOPPED, PLAYING, PAUSED})
    @Retention(RetentionPolicy.SOURCE)
    private @interface SequencerStates {
    }

    private User user;

    // SEQUENCE
    private SequenceDAO sequenceDAO;
    private Sequence sequence;
    private Sequence sequenceToOverwrite;
    private Bar bar1, bar2, bar3, bar4;

    private android.support.v7.app.ActionBar actionBar;
    private Spinner spinner;

    // SEQUENCER STATE
    private Bar activeBar;
    private String activeSound;
    private int[] activePads;
    private int bpm, state, currentStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequencer);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            user = (User) extras.get("user");

        if (user != null && user.getRoles().get(0).getName().equals("admin"))
            ManagementActivity.startActivity(this, user);

        // Left drawer init and event management
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.sequencer_rootview);

        if (drawerLayout != null) {
            drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    if (user == null)
                        return;

                    TextView headerNameText = (TextView) findViewById(R.id.header_name);
                    if (headerNameText != null)
                        headerNameText.setText(user.getName());

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.sequencer_drawer);
        if (navigationView != null) {
            navigationView.setItemIconTintList(null);
            navigationView.setNavigationItemSelectedListener(this);
        }

        sequenceDAO = new SequenceDAO(this);

        // Sequence init
        sequence = new Sequence();
        bar1 = new Bar();
        bar2 = new Bar();
        bar3 = new Bar();
        bar4 = new Bar();

        sequence.addBar(bar1);

        // Sequencer init
        activeBar = bar1;
        activePads = new int[16];
        bpm = 60;
        state = STOPPED;
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

                // a freshly charged bar has no sound, only an "empty" string in activeSoundsNames
                if (activeSound.equals("empty"))
                    resetActivePads();
                else
                    activePads = activeBar.getSoundMatrix(activeSound);

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
            case R.id.bar_menu_settings:
                toSettings();
                break;
            case R.id.bar_menu_account:
                ManagementActivity.startActivity(this, user);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_account:
                ManagementActivity.startActivity(this, user);
                break;
            case R.id.drawer_settings:
                toSettings();
                break;
            case R.id.drawer_import:
                importSequence();
                break;
            case R.id.drawer_save:
                saveSequence();
                break;
            case R.id.drawer_logout:
                logOut();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.sequencer_rootview);
        if (drawer != null)
            drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.sequencer_rootview);

        // checking if the left drawer is open and act accordingly
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            logOut();
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

    public void toSettings() {
        new SetBMPDialog().show(getFragmentManager(), "set BPM");
    }

    // TODO !!! IMPORTANT !!! Importable sequence
    public void importSequence() {
        snackThis("import");
    }

    // TODO MockLocation for demo
    public void saveSequence() {
        Localizer localizer = new Localizer(this);
//        Location  location  = localizer.getLocation(Criteria.ACCURACY_COARSE);
//
//        if (location == null)
//            location = localizer.getMockLocation();

        Location location = localizer.getMockLocation();
        String seq_name = "test sequence";

        sequence.setName(seq_name);
        sequence.setGenre("default genre");
        sequence.setBpm(this.bpm);
        sequence.setAuthor(user.getName());
        sequence.setLocation(location);

        Toast.makeText(this, location.toString(), Toast.LENGTH_LONG).show();

        sequenceDAO.open(DataAccessObject.WRITABLE);

        long id = sequenceDAO.alreadyPossess(seq_name, user.getName());

        if (id >= 0) {
            sequenceToOverwrite = sequence;
            sequenceToOverwrite.setId(id);
            new OverwriteSaveDialog().show(getFragmentManager(), null);
        } else {
            String message = String.format(getString(R.string.save_string_format),
                    sequence.getName());

            snackThis(message);

            sequenceDAO.create(sequence);
            sequenceDAO.close();
        }
    }

    @Override
    public void overwriteSave() {

        // TODO !!! IMPORTANT !!! SHOULD BE UPDATE NOT DELETE
        sequenceDAO.delete(sequenceToOverwrite.getId());
        sequenceDAO.create(sequenceToOverwrite);
        sequenceDAO.close();

        String message = String.format(getString(R.string.ow_string_format),
                sequenceToOverwrite.getName());

        snackThis(message);
    }

    // Close activity if called from drawer or display a dialog if not
    public void logOut() {
        stop(new View(this));
        new LogOutDialog().show(getFragmentManager(), null);
    }

    // TODO IF TIME FOR - - - async tasks for progress feedback
    public void play(View view) {
        if (bar1.getActiveSoundsNames().get(0).equals("empty"))
            return;

        if (state == STOPPED || state == PAUSED) {
            state = PLAYING;
            new PlaybackThread(this);
        } else
            state = PAUSED;

        refreshPlayButton(state);
    }

    public void stop(View view) {
        state = STOPPED;
        currentStep = 0;
        refreshPlayButton(state);
    }

    public void openAddSoundDialog(View view) {
        new AddSoundDialog().show(getFragmentManager(), null);
    }

    @Override
    public void addSound(String soundName) {
        if (activeBar.getActiveSoundsNames().get(0).equals("empty"))
            activeBar.getActiveSoundsNames().remove(0);

        int id = getFileId(soundName);
        activeBar.addSound(soundName, id);

        stop(new View(this));

        refreshSpinner();
        spinner.setSelection(activeBar.getActiveSoundsNames().indexOf(soundName));
    }

    private int getFileId(String soundName) {
        String fileName = soundName.replace(" ", "_");

        return getResources().getIdentifier(fileName, "raw", getPackageName());
    }

    public void addBar(View view) {
        Button addBarButton = (Button) findViewById(R.id.add_bar_button);

        switch (sequence.getTotalBars()) {
            case 1:
                activeBar = bar2;
                break;
            case 2:
                activeBar = bar3;
                break;
            case 3:
                activeBar = bar4;
                if (addBarButton != null) addBarButton.setVisibility(View.GONE);
                break;
        }

        sequence.addBar(activeBar);


        // Setting button and progress bar visible
        String name = "button_bar" + sequence.getTotalBars();
        int id = getResources().getIdentifier(name, "id", getPackageName());
        View invisibleView = findViewById(id);

        if (invisibleView != null) {
            invisibleView.setVisibility(View.VISIBLE);
        }

        selectBar(invisibleView);
        resetActivePads();

        name = "progress_bar" + sequence.getTotalBars();
        id = getResources().getIdentifier(name, "id", getPackageName());
        invisibleView = findViewById(id);

        if (invisibleView != null) {
            invisibleView.setVisibility(View.VISIBLE);
        }
    }

    public void selectBar(View view) {
        // Reset bar buttons text color
        for (int i = 1; i <= 4; i++) {
            String name = "button_bar" + i;
            int id = getResources().getIdentifier(name, "id", getPackageName());

            Button button = (Button) findViewById(id);
            if (button != null)
                button.setTextColor(getResources().getColor(R.color.white));
        }

        // Change currently selected bar text color
        Button button = (Button) findViewById(view.getId());
        if (button != null)
            button.setTextColor(getResources().getColor(R.color.colorAccent));

        switch (view.getId()) {
            case R.id.button_bar1:
                activeBar = bar1;
                break;
            case R.id.button_bar2:
                activeBar = bar2;
                break;
            case R.id.button_bar3:
                activeBar = bar3;
                break;
            case R.id.button_bar4:
                activeBar = bar4;
                break;
        }

        refreshButtons();
        refreshSpinner();
    }

    public void activatePad(View view) {
        // Empty bar check
        if (activeSound.equals("empty")) {
            snackThis("Add a sound");
            return;
        }

        // Fetch button by name
        String[] buttonName = getResources().getResourceName(view.getId()).split("_");
        int buttonNumber = Integer.parseInt(buttonName[2]);
        Button button = (Button) view;

        if (activePads[buttonNumber - 1] == 0) {
            changeButtonBackground(button, true);
            activePads[buttonNumber - 1] = 1;
        } else {
            changeButtonBackground(button, false);
            activePads[buttonNumber - 1] = 0;
        }

        activeBar.updateSound(activeSound, activePads);
    }

    private void snackThis(String message) {
        View view = findViewById(R.id.sequencer_rootview);

        if (view != null)
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    private void refreshSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, activeBar.getActiveSoundsNames());

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    private void refreshButtons() {
        for (int i = 0; i < activePads.length; i++) {
            String buttonId = "seq_button_" + (i + 1);
            int id = getResources().getIdentifier(buttonId, "id", getPackageName());
            Button button = (Button) findViewById(id);

            if (activePads[i] == 1)
                changeButtonBackground(button, true);
            else
                changeButtonBackground(button, false);
        }
    }

    private void changeButtonBackground(Button button, boolean isActive) {
        Drawable active;
        Drawable inactive;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            active = getResources().getDrawable(R.drawable.sequencer_pad);
            inactive = getResources().getDrawable(R.drawable.disabled_sequencer_pad);
        } else {
            active = getResources().getDrawable(R.drawable.sequencer_pad, null);
            inactive = getResources().getDrawable(R.drawable.disabled_sequencer_pad, null);
        }

        if (isActive)
            button.setBackground(active);
        else
            button.setBackground(inactive);
    }

    private void refreshPlayButton(@SequencerStates int state) {
        Button playButton = (Button) findViewById(R.id.seq_play_button);

        if (playButton != null) {
            switch (state) {
                case STOPPED:
                case PAUSED:
                    playButton.setText(getString(R.string.play));
                    break;
                case PLAYING:
                    playButton.setText(getString(R.string.pause));
                    break;
            }
        }
    }

    private void resetActivePads() {
        activePads = new int[16];
    }
}
