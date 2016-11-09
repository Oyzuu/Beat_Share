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

    public interface SignUpListener {
        void toSignIn();
    }

    public static SignUpFragment newInstance() {

        Bundle args = new Bundle();

        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private SignUpListener callback;
    private Context context;
    private EditText nameEdit, passEdit, passConfirmEdit, mailEdit, mailConfirmEdit;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.callback = (SignUpListener) context;
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
                callback.toSignIn();
            }
        });

        nameEdit = (EditText) view.findViewById(R.id.signup_username);
        passEdit = (EditText) view.findViewById(R.id.signup_password);
        passConfirmEdit = (EditText) view.findViewById(R.id.signup_confirm_password);
        mailEdit = (EditText) view.findViewById(R.id.signup_mail);
        mailConfirmEdit = (EditText) view.findViewById(R.id.signup_confirm_mail);
    }

    public void signUp() {
        boolean formIsOK = true;

        // non null verification with an empty string as fallback value
        String name = nameEdit != null ? nameEdit.getText().toString() : "";
        String pass = passEdit != null ? passEdit.getText().toString() : "";
        String passConfirm = passConfirmEdit != null ? passConfirmEdit.getText().toString() : "";
        String mail = mailEdit != null ? mailEdit.getText().toString() : "";
        String mailConfirm = mailConfirmEdit != null ? mailConfirmEdit.getText().toString() : "";

        name = name.trim();
        pass = pass.trim();
        passConfirm = passConfirm.trim();
        mail = mail.trim();
        mailConfirm = mailConfirm.trim();

        Log.i("MAIN", String.format("name : %s, pass : %s, mail : %s", name, pass, mail));

        if (name.equals("") || pass.equals("") || mail.equals("")) {
            Log.i("MAIN", "User input error");
            formIsOK = false;
        }

        if (name.equals("")) {
            Log.i("MAIN", "Name input error");
            nameEdit.setError(getString(R.string.user_input_error));
            formIsOK = false;
        }

        if (pass.equals("")) {
            Log.i("MAIN", "Password input error");
            passEdit.setError(getString(R.string.pass_confirm_error));
            formIsOK = false;
        }

        if (mail.equals("")) {
            Log.i("MAIN", "mail input error");
            mailEdit.setError(getString(R.string.mail_input_error));
            formIsOK = false;
        }

        if (!formIsOK)
            return;

        if (!pass.equals(passConfirm)) {
            Log.i("MAIN", "Password confirmation error");
            passConfirmEdit.setError(getString(R.string.pass_confirm_error));
            formIsOK = false;
        }

        if (!mail.equals(mailConfirm)) {
            Log.i("MAIN", "Mail confirmation error");
            mailConfirmEdit.setError(getString(R.string.mail_confirm_error));
            formIsOK = false;
        }

        if (!formIsOK)
            return;

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

            // CANCEL

            SequencerActivity.startActivity(context, user);
        } catch (SQLiteConstraintException e) {
            Log.i("SIGNUP-ERROR", "SQLite Constraint error");
        } finally {
            userDAO.close();
        }
    }
}
