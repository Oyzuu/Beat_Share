package be.omnuzel.beatshare.controller.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.activities.SequencerActivity;
import be.omnuzel.beatshare.controller.utils.ChocolateSaltyBalls;
import be.omnuzel.beatshare.db.DataAccessObject;
import be.omnuzel.beatshare.db.UserDAO;
import be.omnuzel.beatshare.model.User;

public class LogInFragment extends Fragment {

    public interface LogInFragmentListener {
        void toSignUp();
    }

    public static LogInFragment newInstance() {

        Bundle args = new Bundle();

        LogInFragment fragment = new LogInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private EditText loginNameEdit, loginPassEdit;
    private Context context;
    private LogInFragmentListener mCallback;

    public LogInFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.mCallback = (LogInFragmentListener) context;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Views instantiation

        Button signInButton = (Button) view.findViewById(R.id.sign_in_button);
        Button signUpButton = (Button) view.findViewById(R.id.sign_up_button);

        loginNameEdit = (EditText) view.findViewById(R.id.login_username);
        loginPassEdit = (EditText) view.findViewById(R.id.login_password);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.toSignUp();
            }
        });
    }

    private boolean checkResultEquality(EditText edittext, String expectedResult, String errorMessage) {
        if (edittext.getText().toString().trim().equals(expectedResult)) {
            edittext.setError(errorMessage);
            return false;
        }

        return true;
    }

    public void signIn() {

        String name = loginNameEdit != null ? loginNameEdit.getText().toString() : "";
        String pass = loginPassEdit != null ? loginPassEdit.getText().toString() : "";

        if (!checkResultEquality(loginNameEdit, "", getString(R.string.user_input_error)) ||
                !checkResultEquality(loginPassEdit, "", getString(R.string.password_input_error))) {
            return;
        }

        UserDAO userDAO = new UserDAO(context);

        userDAO.open(DataAccessObject.READABLE);
        User user = userDAO.getByName(name);

        if (user == null) {
            loginNameEdit.setError(getString(R.string.login_user_error));
            return;
        }

        String salt = userDAO.getSalt(user);
        userDAO.close();

        String hashedPassword = "";

        try {
            hashedPassword = ChocolateSaltyBalls.getInstance().hash(pass + salt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!user.getPassword().equals(hashedPassword)) {
            loginPassEdit.setError(getString(R.string.login_password_error));
            return;
        }

        SequencerActivity.startActivity(context, user);
    }

}
