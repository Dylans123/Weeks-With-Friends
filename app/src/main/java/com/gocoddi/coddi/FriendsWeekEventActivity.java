package com.gocoddi.coddi;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FriendsWeekEventActivity extends AppCompatActivity {

    private static final String TAG = "YERRR";
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private String title;
    private String date;
    private String desc;
    private String id;
    private TextView descText;
    private TextView dateText;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_week_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        date = getIntent().getStringExtra("date");
        id = getIntent().getStringExtra("id");

        titleText = (TextView)findViewById(R.id.title);
        descText = (TextView)findViewById(R.id.description);
        dateText = (TextView)findViewById(R.id.date);

        titleText.setText(title);
        descText.setText(desc);
        dateText.setText(date);
    }
}
