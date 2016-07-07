package be.omnuzel.beatshare.controller.activities;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.location.Criteria;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.fragments.LogInFragment;
import be.omnuzel.beatshare.controller.fragments.SignUpFragment;
import be.omnuzel.beatshare.controller.utils.Localizer;
import be.omnuzel.beatshare.db.DataAccessObject;
import be.omnuzel.beatshare.db.RoleDAO;
import be.omnuzel.beatshare.db.UserDAO;
import be.omnuzel.beatshare.model.Location;
import be.omnuzel.beatshare.model.Role;
import be.omnuzel.beatshare.model.User;

// TODO Regex for log in / sign up --- IF TIME FOR IT
// TODO make models parcelable (Sequence ?)
// TODO Create developer account for Play Games Services

public class MainActivity
        extends
            AppCompatActivity
        implements
            LogInFragment.ILoginFragment,
            SignUpFragment.ISignUpFragment {

    private UserDAO userDAO;
    private RoleDAO roleDAO;

    private EditText
            nameEdit,
            passEdit,
            passConfirmEdit,
            mailEdit,
            mailConfirmEdit,
            loginNameEdit,
            loginPassEdit;

    private LogInFragment  logInFragment;
    private SignUpFragment signUpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDAO = new UserDAO(this);
        roleDAO = new RoleDAO(this);

        logInFragment  = LogInFragment.getInstance();
        signUpFragment = SignUpFragment.getInstance();

        getFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_view, logInFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginNameEdit = (EditText) findViewById(R.id.login_username);
        loginPassEdit = (EditText) findViewById(R.id.login_password);
    }

    // Ensures non null log in screen text fields
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        loginNameEdit = (EditText) findViewById(R.id.login_username);
        loginPassEdit = (EditText) findViewById(R.id.login_password);
    }

    @Override
    public void logIn(View view) {
        boolean formIsOK = true;

        // non null verification with an empty string as fallback value
        String name = loginNameEdit != null ? loginNameEdit.getText().toString() : "";
        String pass = loginPassEdit != null ? loginPassEdit.getText().toString() : "";

        name = name.trim();
        pass = pass.trim();

        if (name.equals("")) {
            Log.i("MAIN", "User input error");
            loginNameEdit.setError(getString(R.string.user_input_error));
            formIsOK = false;
        }

        if (pass.equals("")) {
            Log.i("MAIN", "Password input error");
            loginPassEdit.setError(getString(R.string.password_input_error));
            formIsOK = false;
        }

        if (!formIsOK)
            return;

        userDAO.open(DataAccessObject.READABLE);
        User user = userDAO.getByName(name);
        userDAO.close();
        if (user == null) {
            loginNameEdit.setError(getString(R.string.login_user_error));
            return;
        }

        if (pass.equals(user.getPassword())) {
            loginPassEdit.setError(getString(R.string.login_password_error));
            return;
        }

        flushLogInForm();
        Intent intent = new Intent(this, SequencerActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public void toSignUp(View view) {
        flushLogInForm();

        getFragmentManager()
                .beginTransaction()
                .addToBackStack("")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_view, signUpFragment)
                .commit();

        // Force commit to avoid null on views
        getFragmentManager().executePendingTransactions();

        nameEdit        = (EditText) findViewById(R.id.signup_username);
        passEdit        = (EditText) findViewById(R.id.signup_password);
        passConfirmEdit = (EditText) findViewById(R.id.signup_confirm_password);
        mailEdit        = (EditText) findViewById(R.id.signup_mail);
        mailConfirmEdit = (EditText) findViewById(R.id.signup_confirm_mail);
    }

    @Override
    public void signUp(View view) {
        boolean formIsOK = true;

        // non null verification with an empty string as fallback value
        String name        = nameEdit        != null ? nameEdit       .getText().toString() : "";
        String pass        = passEdit        != null ? passEdit       .getText().toString() : "";
        String passConfirm = passConfirmEdit != null ? passConfirmEdit.getText().toString() : "";
        String mail        = mailEdit        != null ? mailEdit       .getText().toString() : "";
        String mailConfirm = mailConfirmEdit != null ? mailConfirmEdit.getText().toString() : "";

        name        = name       .trim();
        pass        = pass       .trim();
        passConfirm = passConfirm.trim();
        mail        = mail       .trim();
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
        user.setUserName(name);
        user.setEmail   (mail);
        user.setPassword(pass);

        userDAO.open(DataAccessObject.WRITABLE);

        try {
            userDAO.create(user);

            flushSignUpForm();

            Intent intent = new Intent(this, SequencerActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
        catch (SQLiteConstraintException e) {
            snackThis("SQLite Constraint error");
        }
        finally {
            userDAO.close ();
        }
    }

    @Override
    public void cancel(View view) {
        flushSignUpForm();
        onBackPressed();
    }

    /**
     * Remove text and error from every EditText in LogInFragment
     */
    @Override
    public void flushLogInForm() {
        EditText[] editTexts = {loginNameEdit, loginPassEdit};
        for (EditText editText : editTexts) {
            if (editText != null) {
                editText.setText("");
                editText.setError(null);
            }
        }
    }

    /**
     * Remove text and error from every EditText in SignUpFragment
     */
    @Override
    public void flushSignUpForm() {
        EditText[] editTexts = {nameEdit, passEdit, passConfirmEdit, mailEdit, mailConfirmEdit};
        for (EditText editText : editTexts) {
            if (editText != null) {
                editText.setText("");
                editText.setError(null);
            }
        }
    }

    /**
     * Display a message in a short-length Snackbar
     * @param message The string you want to display
     */
    private void snackThis(String message) {
        View view = findViewById(R.id.main_view);

        if (view != null)
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    // DEBUG METHODS

    public void allUsers(View view) {
        userDAO.open(DataAccessObject.READABLE);

        if (userDAO.getAll() == null) {
            snackThis("getAll() returns NULL");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (User user : userDAO.getAll()) {
            sb.append(user.toString() + "\n");
        }

        Intent intent = new Intent(this, Debug.class);
        intent.putExtra("debugInfo", sb.toString());
        startActivity(intent);
    }

    public void allRoles(View view) {
        roleDAO.open(DataAccessObject.READABLE);

        if (roleDAO.getAll() == null) {
            snackThis("getAll() returns NULL");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Role role : roleDAO.getAll()) {
            sb.append(role.toString() + "\n");
        }

        Intent intent = new Intent(this, Debug.class);
        intent.putExtra("debugInfo", sb.toString());
        startActivity(intent);
    }

    public void allUserRoles(View view) {
        userDAO.open(DataAccessObject.READABLE);

        if (userDAO.getAllUserRoles() == null) {
            snackThis("getAll() returns NULL");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String s : userDAO.getAllUserRoles()) {
            sb.append(s + "\n");
        }

        Intent intent = new Intent(this, Debug.class);
        intent.putExtra("debugInfo", sb.toString());
        startActivity(intent);
    }

    public void localizeMe(View view) {
        Localizer localizer = new Localizer(this);

        Location location   = localizer.getLocation(Criteria.ACCURACY_COARSE);

        if (location == null) {
            snackThis("NULL Location");
            return;
        }

        @SuppressLint("DefaultLocale")
        String locString    = String.format(
                "Location : %.4f, %.4f - %s, %s, %s",
                location.getLatitude(),
                location.getLongitude(),
                location.getNeighbourhood().getName(),
                location.getNeighbourhood().getCity().getName(),
                location.getNeighbourhood().getCity().getCountry().getName()
        );
        snackThis(locString);
    }
}