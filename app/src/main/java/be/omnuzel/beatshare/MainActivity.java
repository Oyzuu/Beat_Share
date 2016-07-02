package be.omnuzel.beatshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import be.omnuzel.beatshare.fragments.LogInFragment;

public class MainActivity
        extends
            AppCompatActivity
        implements
            LogInFragment.ILoginFragment {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_view, LogInFragment.getInstance())
                .commit();
    }

    @Override
    public void logIn(View view) {
        startActivity(new Intent(this, SequencerActivity.class));
    }

    @Override
    public void toSignUp(View view) {

    }
}
