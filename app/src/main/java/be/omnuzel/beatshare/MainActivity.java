package be.omnuzel.beatshare;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import be.omnuzel.beatshare.fragments.LogInFragment;
import be.omnuzel.beatshare.fragments.SignUpFragment;

public class MainActivity
        extends
            AppCompatActivity
        implements
            LogInFragment.ILoginFragment,
            SignUpFragment.ISignUpFragment {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_view, LogInFragment.getInstance())
                .commit();
    }

    @Override
    public void logIn(View view) {
        startActivity(new Intent(this, SequencerActivity.class));
    }

    @Override
    public void toSignUp(View view) {
        getFragmentManager()
                .beginTransaction()
                .addToBackStack("")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_view, SignUpFragment.getInstance())
                .commit();
    }

    @Override
    public void signUp(View view) {

    }

    @Override
    public void cancel(View view) {

    }
}
