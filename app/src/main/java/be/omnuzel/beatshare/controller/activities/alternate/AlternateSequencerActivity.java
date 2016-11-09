package be.omnuzel.beatshare.controller.activities.alternate;

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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.activities.ManagementActivity;
import be.omnuzel.beatshare.controller.dialogs.AddSoundDialog;
import be.omnuzel.beatshare.controller.dialogs.LogOutDialog;
import be.omnuzel.beatshare.controller.dialogs.OverwriteSaveDialog;
import be.omnuzel.beatshare.controller.dialogs.SetBMPDialog;
import be.omnuzel.beatshare.controller.threads.PlaybackThread;
import be.omnuzel.beatshare.controller.utils.Localizer;
import be.omnuzel.beatshare.model.Location;
import be.omnuzel.beatshare.model.Sequence;
import be.omnuzel.beatshare.model.User;
import be.omnuzel.beatshare.model.alternate.SugarBar;
import be.omnuzel.beatshare.model.alternate.SugarSequence;
import be.omnuzel.beatshare.model.alternate.SugarSound;

// TODO IF TIME FOR IT - - - Option to remove bar / sound
// TODO stop PlaybackThread when log off

public class AlternateSequencerActivity
        extends
        AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        AddSoundDialog.AddSoundDialogListener,
        SetBMPDialog.BPMDialogListener,
        PlaybackThread.PlaybackListener,
        OverwriteSaveDialog.OverwriteSaveListener,
        DrawerLayout.DrawerListener {

    public static void startActivity(Context context, User user) {
        Intent intent = new Intent(context, AlternateSequencerActivity.class);
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

    private SugarSequence sequence;
    private Sequence sequenceToOverwrite;

    private android.support.v7.app.ActionBar actionBar;
    private Spinner spinner;

    private int[] activePads;
    private int bpm, state, currentStep;

    private int activeSoundIndex, activeBarIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequencer);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            user = (User) extras.get("user");

        if (user != null && user.getRoles().get(0).getName().equals("admin"))
            ManagementActivity.startActivity(this, user);

        NavigationView navigationView = (NavigationView) findViewById(R.id.sequencer_drawer);
        if (navigationView != null) {
            navigationView.setItemIconTintList(null);
            navigationView.setNavigationItemSelectedListener(this);
        }

        // Sequence init

        sequence = new SugarSequence();
        sequence.init();

        // Sequencer init

        activeBarIndex = 0;
        activeSoundIndex = 0;
        currentStep = 0;
        state = STOPPED;
        activePads = new int[16];
        bpm = 60;

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
                if (sequence.getBars().get(activeBarIndex).getNames().get(0).equals("empty")) {
                    resetActivePads();
                    return;
                }

                activeSoundIndex = position;
                SugarSound sound = sequence.getBars().get(activeBarIndex).getSounds().get(activeSoundIndex);
                Log.i("SPINNER-TAP", "Sound matrix = " + Arrays.toString(sound.getMatrix()));
                activePads = sound.getMatrix();
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
//        return sequence;
        return new Sequence();
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
//        String seq_name = "test sequence";
//
//        sequence.setName(seq_name);
//        sequence.setGenre("default genre");
//        sequence.setBpm(this.bpm);
//        sequence.setAuthor(user.getName());
//        sequence.setLocation(location);
//
//        Toast.makeText(this, location.toString(), Toast.LENGTH_LONG).show();
//
//        sequenceDAO.open(DataAccessObject.WRITABLE);
//
//        long id = sequenceDAO.alreadyPossess(seq_name, user.getName());
//
//        if (id >= 0) {
//            sequenceToOverwrite = sequence;
//            sequenceToOverwrite.setId(id);
//            new OverwriteSaveDialog().show(getFragmentManager(), null);
//        } else {
//            String message = String.format(getString(R.string.save_string_format),
//                    sequence.getName());
//
//            snackThis(message);
//
//            sequenceDAO.create(sequence);
//            sequenceDAO.close();
//        }
    }

    @Override
    public void overwriteSave() {
//
//        // TODO !!! IMPORTANT !!! SHOULD BE UPDATE NOT DELETE
//        sequenceDAO.delete(sequenceToOverwrite.getId());
//        sequenceDAO.create(sequenceToOverwrite);
//        sequenceDAO.close();
//
//        String message = String.format(getString(R.string.ow_string_format),
//                sequenceToOverwrite.getName());
//
//        snackThis(message);
    }

    // Close activity if called from drawer or display a dialog if not
    public void logOut() {
        stop(new View(this));
        new LogOutDialog().show(getFragmentManager(), null);
    }

    // TODO IF TIME FOR - - - async tasks for progress feedback
    public void play(View view) {
        if (sequence.getBars().get(0).getNames().get(0).equals("empty"))
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
        SugarBar bar = sequence.getBars().get(activeBarIndex);
        if (bar.getNames().get(0).equals("empty")) {
            bar.getNames().remove(0);
        }

        int id = getFileId(soundName);
        SugarSound sound = new SugarSound();
        sound.setName(soundName);
        sound.setSoundID(id);
        sound.setMatrix(new int[16]);
        bar.getSounds().add(sound);

        stop(new View(this));

        int soundIndex = bar.getNames().indexOf(soundName);
        activeSoundIndex = soundIndex;
        refreshSpinner();
        spinner.setSelection(soundIndex);
    }

    private int getFileId(String soundName) {
        String fileName = soundName.replace(" ", "_");

        return getResources().getIdentifier(fileName, "raw", getPackageName());
    }

    public void addBar(View view) {
        Button addBarButton = (Button) findViewById(R.id.add_bar_button);

        Log.i("ADD-BAR", sequence.getBars().size() + "");

        switch (sequence.getBars().size()) {
            case 1:
                activeBarIndex = 1;
                break;
            case 2:
                activeBarIndex = 2;
                break;
            case 3:
                activeBarIndex = 3;
                if (addBarButton != null) addBarButton.setVisibility(View.GONE);
                break;
        }

        sequence.addBar();

        // Fetch layout linked to created bar by name and make it visible
        String name = "bar" + sequence.getBars().size() + "_layout";
        int id = getResources().getIdentifier(name, "id", getPackageName());
        LinearLayout barLayout = (LinearLayout) findViewById(id);

        if (barLayout != null)
            barLayout.setVisibility(View.VISIBLE);

        // Fetch bar button to select by name
        name = "button_bar" + sequence.getBars().size();
        id = getResources().getIdentifier(name, "id", getPackageName());
        View button = findViewById(id);

        resetActivePads();
        selectBar(button);
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
                activeBarIndex = 0;
                break;
            case R.id.button_bar2:
                activeBarIndex = 1;
                break;
            case R.id.button_bar3:
                activeBarIndex = 2;
                break;
            case R.id.button_bar4:
                activeBarIndex = 3;
                break;
        }

        activeSoundIndex = 0;

        refreshButtons();
        refreshSpinner();
    }

    public void activatePad(View view) {
        // Empty bar check

        SugarSound sound = sequence.getBars().get(activeBarIndex).getSounds().get(activeSoundIndex);
        if (sound.getName().equals("empty")) {
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

        Log.i("ACTIVE-PADS", Arrays.toString(activePads));

        sound.setMatrix(activePads);
        Log.i("ACTIVATE-PAD", "Sound matrix = " + Arrays.toString(sound.getMatrix()));
        sequence.getBars().get(activeBarIndex).getSounds().set(activeSoundIndex, sound);
    }

    private void snackThis(String message) {
        View view = findViewById(R.id.sequencer_rootview);

        if (view != null)
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    private void refreshSpinner() {
        SugarBar bar = sequence.getBars().get(activeBarIndex);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bar.getNames());

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

    // Drawer Listener

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
}
