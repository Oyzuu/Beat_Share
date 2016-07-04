package be.omnuzel.beatshare;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import be.omnuzel.beatshare.model.User;
import be.omnuzel.beatshare.db.DataAccessObject;
import be.omnuzel.beatshare.db.UserDAO;
import be.omnuzel.beatshare.fragments.LogInFragment;
import be.omnuzel.beatshare.fragments.SignUpFragment;

// TODO functional log in / sign up - almost done

public class MainActivity
        extends
            AppCompatActivity
        implements
            LogInFragment.ILoginFragment,
            SignUpFragment.ISignUpFragment {

    private UserDAO userDAO;

    private EditText
            nameEdit,
            passEdit,
            passConfirmEdit,
            mailEdit,
            mailConfirmEdit,
            loginNameEdit,
            loginPassEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDAO = new UserDAO(this);

        getFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_view, LogInFragment.getInstance())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loginNameEdit   = (EditText) findViewById(R.id.login_username);
        loginPassEdit   = (EditText) findViewById(R.id.login_password);
    }

    @Override
    public void logIn(View view) {
        boolean formIsOK = true;

        String name = loginNameEdit != null ? loginNameEdit.getText().toString() : "";
        String pass = loginPassEdit != null ? loginPassEdit.getText().toString() : "";

        if (name.equals("") || pass.equals("")) {
            snackThis(getString(R.string.input_error));
            Log.i("MAIN", "User input error");
            formIsOK = false;
        }

        if (!formIsOK)
            return;

        userDAO.open(DataAccessObject.READABLE);
        User user = userDAO.getByName(name);
        userDAO.close();

        if (user != null && user.getPassword().equals(pass)) {
            resetEditText(loginNameEdit, loginPassEdit);
            startActivity(new Intent(this, SequencerActivity.class));
        }
        else {
            snackThis(getString(R.string.input_error));
            Log.i("MAIN", "User input error");
        }
    }

    @Override
    public void toSignUp(View view) {
        getFragmentManager()
                .beginTransaction()
                .addToBackStack("")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_view, SignUpFragment.getInstance())
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
        // TODO trim the strings ! (auto-completion adds spaces)
        boolean formIsOK = true;

        String name        = nameEdit        != null ? nameEdit       .getText().toString() : "";
        String pass        = passEdit        != null ? passEdit       .getText().toString() : "";
        String passConfirm = passConfirmEdit != null ? passConfirmEdit.getText().toString() : "";
        String mail        = mailEdit        != null ? mailEdit       .getText().toString() : "";
        String mailConfirm = mailConfirmEdit != null ? mailConfirmEdit.getText().toString() : "";

        Log.i("MAIN", String.format("name : %s, pass : %s, mail : %s", name, pass, mail));

        if (name.equals("") || pass.equals("") || mail.equals("")) {
            snackThis(getString(R.string.input_error));
            Log.i("MAIN", "User input error");
            formIsOK = false;
        }
        
        if (!pass.equals(passConfirm)) {
            snackThis(getString(R.string.pass_confirm_error));
            Log.i("MAIN", "Password confirmation error");
            formIsOK = false;
        }
        
        if (!mail.equals(mailConfirm)) {
            snackThis(getString(R.string.mail_confirm_error));
            Log.i("MAIN", "Mail confirmation error");
            formIsOK = false;
        }
        
        if (!formIsOK)
            return;

        Log.i("MAIN", "Sign Up : " + name + " with mail : " + mail);

        User user = new User();
        user.setUserName(name);
        user.setEmail   (mail);
        user.setPassword(pass);

        userDAO.open  (DataAccessObject.WRITABLE);
        userDAO.create(user);
        userDAO.close ();

        cancel(view);

        startActivity(new Intent(this, SequencerActivity.class));
    }

    @Override
    public void cancel(View view) {
        resetEditText(nameEdit, passEdit, passConfirmEdit, mailEdit, mailConfirmEdit);

        onBackPressed();
    }

    /**
     * Sets back EditText in the array to and empty string
     * @param editTexts one or many EditText in need of a clean shave
     */
    private void resetEditText(EditText... editTexts) {
        for (EditText editText : editTexts) {
            if (editText != null) editText.setText("");
        }
    }

    // TODO move this elsewhere (maybe in a ToolBox class) --- IF TIME FOR IT
    /**
     * Displays a message in a Snackbar
     * @param message The string you want to display in a short-length Snackbar
     */
    private void snackThis(String message) {
        View view = findViewById(R.id.main_view);

        if (view != null)
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}