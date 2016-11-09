package be.omnuzel.beatshare.controller.fragments;

import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.activities.SequencerActivity;
import be.omnuzel.beatshare.db.DataAccessObject;
import be.omnuzel.beatshare.db.UserDAO;
import be.omnuzel.beatshare.model.User;

public class SignUpFragment extends Fragment {

    public static SignUpFragment newInstance() {

        Bundle args = new Bundle();

        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Context context;
    private EditText nameEdit, passEdit, passConfirmEdit, mailEdit, mailConfirmEdit;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_signup, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button confirmButton = (Button) view.findViewById(R.id.signup_confirm_button);
        Button cancelButton = (Button) view.findViewById(R.id.signup_cancel_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        nameEdit = (EditText) view.findViewById(R.id.signup_username);
        passEdit = (EditText) view.findViewById(R.id.signup_password);
        passConfirmEdit = (EditText) view.findViewById(R.id.signup_confirm_password);
        mailEdit = (EditText) view.findViewById(R.id.signup_mail);
        mailConfirmEdit = (EditText) view.findViewById(R.id.signup_confirm_mail);
    }

    private boolean checkResultEquality(EditText edittext, String expectedResult, String errorMessage) {
        if (edittext.getText().toString().trim().equals(expectedResult)) {
            edittext.setError(errorMessage);
            return true;
        }

        return false;
    }

    public void signUp() {

        // non null verification with an empty string as fallback value
        String name = nameEdit != null ? nameEdit.getText().toString().trim() : "";
        String pass = passEdit != null ? passEdit.getText().toString().trim() : "";
        String mail = mailEdit != null ? mailEdit.getText().toString().trim() : "";

        if (checkResultEquality(nameEdit, "", getString(R.string.user_input_error)) ||
                checkResultEquality(passEdit, "", getString(R.string.password_input_error)) ||
                checkResultEquality(mailEdit, "", getString(R.string.mail_input_error)) ||
                !checkResultEquality(passConfirmEdit, pass, getString(R.string.pass_confirm_error)) ||
                !checkResultEquality(mailConfirmEdit, mail, getString(R.string.mail_confirm_error))) {
            return;
        }

        Log.i("MAIN", "Sign Up : " + name + " with mail : " + mail);

        User user = new User();
        user.setName(name);
        user.setEmail(mail);
        user.setPassword(pass);

        UserDAO userDAO = new UserDAO(context);

        try {
            userDAO.open(DataAccessObject.WRITABLE);
            userDAO.create(user);
            user = userDAO.getByName(name);

            getActivity().onBackPressed();
            SequencerActivity.startActivity(context, user);
        } catch (SQLiteConstraintException e) {
            Log.i("SIGNUP-ERROR", "SQLite Constraint error");
        } finally {
            userDAO.close();
        }
    }

}
