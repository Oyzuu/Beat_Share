package be.omnuzel.beatshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import fragments.LogInFragment;

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
    }

    @Override
    public void toSignUp(View view) {

    }
}
