package be.omnuzel.beatshare.controller.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.fragments.MemberManagementFragment;
import be.omnuzel.beatshare.db.UserDAO;
import be.omnuzel.beatshare.model.User;

// TODO account modifications saved in database
// TODO !!! IMPORTANT !!! admin fragment and exit to main on back press

public class ManagementActivity
        extends
            AppCompatActivity
        implements
            MemberManagementFragment.MemberManagementListener {

    private User    user;
    private UserDAO userDAO;

    private MemberManagementFragment memberManagementFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            user = (User) extras.get("user");

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Account");

        userDAO = new UserDAO(this);
        memberManagementFragment = MemberManagementFragment.getInstance();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.management_rootview, memberManagementFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void save(View view) {

    }

    @Override
    public void cancel(View view) {
        finish();
    }
}
