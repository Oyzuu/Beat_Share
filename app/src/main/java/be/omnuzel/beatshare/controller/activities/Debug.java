package be.omnuzel.beatshare.controller.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import be.omnuzel.beatshare.R;

public class Debug extends AppCompatActivity {

    private String debugInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            debugInfo = extras.getString("debugInfo");
        }

        TextView tv = (TextView) findViewById(R.id.debug_text);
        if (tv != null)
            tv.setText(debugInfo);
    }
}
