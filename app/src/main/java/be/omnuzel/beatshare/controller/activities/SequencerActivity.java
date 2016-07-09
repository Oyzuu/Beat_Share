package be.omnuzel.beatshare.controller.activities;

import android.graphics.drawable.Drawable;
import android.media.SoundPool;
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
    private boolean        isPlaying = false;
    private StringBuilder  soundBuffer;
    private long           startingTime,
                           elapsedTime;
    private PlaybackThread playbackThread;
    private int            bpm = 60;

    private int[] activePads = {
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

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.sequencer_rootview);

        if (drawerLayout != null) {
            drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {}

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

    // TODO populate these
    public void toSettings() {
        snackThis("settings");
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
    public void play(View view) {}
    public void stop(View view) {}

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
    }

    private void snackThis(String message) {
        Snackbar.make(findViewById(R.id.sequencer_rootview), message, Snackbar.LENGTH_SHORT).show();
    }
}
