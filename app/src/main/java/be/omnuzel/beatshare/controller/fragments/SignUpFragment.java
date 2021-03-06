package be.omnuzel.beatshare.controller.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.omnuzel.beatshare.R;

public class SignUpFragment extends Fragment {

    public interface SignUpListener {
        void signUp(View view);
        void cancel(View view);
        void flushForm();
    }


    public static SignUpFragment instance;

    public static SignUpFragment getInstance() {
        if (instance == null)
            instance = new SignUpFragment();

        return instance;
    }

    private SignUpListener callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.callback = (SignUpListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_signup, container, false);
    }
}
