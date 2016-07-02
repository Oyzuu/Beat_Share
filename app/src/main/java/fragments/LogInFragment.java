package fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.omnuzel.beatshare.R;

/**
 * Created by Gilbert on 02-07-16.
 */
public class LogInFragment extends Fragment {

    public interface ILoginFragment {
        void logIn(View view);
        void toSignUp(View view);
    }

    public static LogInFragment instance;

    public static LogInFragment getInstance() {
        if (instance == null)
            instance = new LogInFragment();
        return instance;
    }

    private ILoginFragment callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (ILoginFragment) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_login, container, false);
    }
}
