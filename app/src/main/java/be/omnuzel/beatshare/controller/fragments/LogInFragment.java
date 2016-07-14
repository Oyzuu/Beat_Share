package be.omnuzel.beatshare.controller.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.omnuzel.beatshare.R;

public class LogInFragment extends Fragment {

    public interface LoginListener {
        void logIn(View view);
        void toSignUp(View view);
        void flushForm();
    }

    public static LogInFragment instance;

    public static LogInFragment getInstance() {
        if (instance == null)
            instance = new LogInFragment();

        return instance;
    }

    private LoginListener callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (LoginListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_login, container, false);
    }
}
