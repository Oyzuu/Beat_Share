package be.omnuzel.beatshare.controller.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.dialogs.ExitDialog;
import be.omnuzel.beatshare.controller.fragments.LogInFragment;
import be.omnuzel.beatshare.controller.fragments.SignUpFragment;
import be.omnuzel.beatshare.controller.utils.ChocolateSaltyBalls;
import be.omnuzel.beatshare.db.DataAccessObject;
import be.omnuzel.beatshare.db.RoleDAO;
import be.omnuzel.beatshare.db.UserDAO;
import be.omnuzel.beatshare.model.User;


// TODO !!! IMPORTANT !!! make models parcelable (Sequence ?)
// TODO ___ PUBLISH ___ Create developer account for Play Games Services

public class MainActivity
        extends
            AppCompatActivity
        implements
        LogInFragment .LoginListener,
        SignUpFragment.SignUpListener {

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

    @Override
    public void onBackPressed() {
        // Check if back order comes from the sign up fragment and then act accordingly
        if (signUpFragment.isVisible()) {
            flushForm();
            getFragmentManager().popBackStackImmediate();
        }
        else {
            new ExitDialog().show(getFragmentManager(), "quit");
        }

        // Ensure non null log in screen text fields
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

        if (user == null) {
            loginNameEdit.setError(getString(R.string.login_user_error));
            return;
        }

        String salt = userDAO.getSalt(user);
        userDAO.close();

        String hashedPassword = "";

        try {
            hashedPassword = ChocolateSaltyBalls.getInstance().hash(pass + salt);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (!user.getPassword().equals(hashedPassword)) {
            loginPassEdit.setError(getString(R.string.login_password_error));
            return;
        }

        String message = String.format("User : %s with roles : %s",
                user.getName(), user.getRoles());
        flushForm();
        Intent intent = new Intent(this, SequencerActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public void toSignUp(View view) {
        flushForm();

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
        user.setName(name);
        user.setEmail   (mail);
        user.setPassword(pass);

        try {
            userDAO.open(DataAccessObject.WRITABLE);
            userDAO.create(user);
            user= userDAO.getByName(name);

            cancel(new View(this));

            Intent intent = new Intent(this, SequencerActivity.class);
            intent.putExtra("user", user);

            flushForm();
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
        onBackPressed();
    }

    /**
     * Remove text and error from every EditText
     */
    @Override
    public void flushForm() {
        EditText[] editTexts = {loginNameEdit, loginPassEdit, nameEdit,
                                passEdit, passConfirmEdit,mailEdit, mailConfirmEdit};

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
}