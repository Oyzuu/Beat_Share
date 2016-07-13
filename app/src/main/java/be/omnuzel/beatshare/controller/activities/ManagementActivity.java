package be.omnuzel.beatshare.controller.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import be.omnuzel.beatshare.R;
import be.omnuzel.beatshare.controller.fragments.MemberManagementFragment;
import be.omnuzel.beatshare.model.User;

// TODO profile management

public class ManagementActivity
        extends
            AppCompatActivity
        implements
            MemberManagementFragment.MemberManagementListener {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            user = (User) extras.get("user");

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.management_rootview, MemberManagementFragment.getInstance())
                .commit();
    }
}
