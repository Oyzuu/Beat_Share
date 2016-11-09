package be.omnuzel.beatshare.controller.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

    public static void startActivity(Context context, User user) {
        Intent intent = new Intent(context, ManagementActivity.class);
        intent.putExtra("user", user);
        context.startActivity(intent);
    }

    private User user;
    private UserDAO userDAO;

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

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.management_rootview, MemberManagementFragment.newInstance())
                .commit();
    }

    @Override
    public void save(View view) {

    }

    @Override
    public void cancel(View view) {
        onBackPressed();
    }
}
