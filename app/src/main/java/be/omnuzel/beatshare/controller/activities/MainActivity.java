package be.omnuzel.beatshare.controller.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.dialogs.ExitDialog;
import be.omnuzel.beatshare.controller.fragments.LogInFragment;
import be.omnuzel.beatshare.controller.fragments.SignUpFragment;
import be.omnuzel.beatshare.model.DummySugarEntity;

// TODO ___ PUBLISH ___ Create developer account for Play Games Services

public class MainActivity extends AppCompatActivity implements LogInFragment.LogInFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DummySugarEntity dummySugarEntity = new DummySugarEntity();
        dummySugarEntity.setName("hey");
        dummySugarEntity.setCount(5);
        dummySugarEntity.save();

        getFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_view, LogInFragment.newInstance())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else {
            new ExitDialog().show(getFragmentManager(), "quit");
        }
    }

    @Override
    public void toSignUp() {
        getFragmentManager()
                .beginTransaction()
                .addToBackStack("")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_view, SignUpFragment.newInstance())
                .commit();
    }

    /**
     * Display a message in a short-length Snackbar
     *
     * @param message The string you want to display
     */
    private void snackThis(String message) {
        View view = findViewById(R.id.main_view);

        if (view != null)
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}