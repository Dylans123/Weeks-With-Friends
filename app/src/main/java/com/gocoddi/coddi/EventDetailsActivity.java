package com.gocoddi.coddi;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventDetailsActivity extends AppCompatActivity {

    private DatabaseReference db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private static final String TAG = "YERRR";

    private String title;
    private String desc;
    private String date;
    private String id;
    private TextView titleText;
    private TextView descText;
    private TextView dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseDatabase.getInstance().getReference();
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

        Button delete = (Button)findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            db.child("events").child(user.getUid()).child(date).child(id).removeValue();
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
