package com.gocoddi.coddi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.Batch;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    private static final String TAG = "YERRRR";
    private EditText title;
    private EditText description;
    private Spinner date;
    private String dateId;

    private DatabaseReference db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        db = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        title = (EditText)findViewById(R.id.title);
        description = (EditText)findViewById(R.id.description);
        date = (Spinner)findViewById(R.id.date);
        initDate();

        final List<String> dateList = dateHelper();
        date.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                dateId = dateList.get(position);//This will be the student id.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Just let it fall through.
            }

        });

        Button create = (Button)findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent(title.getText().toString(), description.getText().toString(), dateId);
            }
        });

    }

    private void initDate() {
        Spinner dateSelect = (Spinner) findViewById(R.id.date);
        List<String> dayList = dayHelper();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, dayList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSelect.setAdapter(dataAdapter);
        dateSelect.setSelection(0);
    }

    private List<String> dayHelper() {
        List<String> days = new ArrayList<String>();
        SimpleDateFormat day = new SimpleDateFormat("EEEE");
        for (int i = 1; i <= 7; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i-1);
            String currentDay = day.format(calendar.getTime());
            days.add(currentDay);
        }
        return days;
    }

    private List<String> dateHelper() {
        List<String> dates = new ArrayList<String>();
        SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy");
        for (int i = 1; i <= 7; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i-1);
            String currentDate = date.format(calendar.getTime());
            dates.add(currentDate);
        }
        return dates;
    }

    private void addEvent(String title, String description, String date) {
        System.out.println(date);
        Map<String, Object> info = new HashMap<>();
        info.put("title", title);
        info.put("description", description);
        info.put("date", date);
        String key = db.child("posts").child(user.getUid()).child(date).push().getKey();
        Event event = new Event(title, description, date);
        Map<String, Object> eventValues = event.toMap();
        db.child("events").child(user.getUid()).child(date);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" + user.getUid() + "/" + date + "/" + key, eventValues);
        db.updateChildren(childUpdates);
//        CollectionReference dateRef = db.collection("events").document(user.getUid()).collection(date);
//        dateRef
//                .add(info)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
        Intent i = new Intent(CreateActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}
