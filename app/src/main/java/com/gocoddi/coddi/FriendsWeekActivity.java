package com.gocoddi.coddi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class FriendsWeekActivity extends AppCompatActivity {

    private static final String TAG = "YERRR";
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private List<String> dateList = new ArrayList<>();
    private List<String> dayList = new ArrayList<>();
    private String friendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_week);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        friendId =  getIntent().getStringExtra("id");
        System.out.println(friendId);


        initDays();

        dateList = dateHelper();
        dayList = dayHelper();

        for (int i = 0; i < dateList.size(); ++i) {
            initEvents(dateList.get(i));
        }
    }

    private void initEvents(final String date) {
        db.collection("events").document(friendId).collection(date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int previous = 0;
                            int count = Integer.parseInt(date.substring(3, 5)) * 100;
                            int dayOneId = idHelper(dateList.indexOf(date) + 1);
                            int dayTwoId = idHelper(dateList.indexOf(date) + 2);
                            if (task.getResult().isEmpty()) {
                                ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout);
                                ConstraintLayout.LayoutParams lparams = new ConstraintLayout.LayoutParams(
                                        ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                                lparams.setMargins(0, 0, 0, 0);
                                TextView event = new TextView(FriendsWeekActivity.this);
                                event.setId(count);
                                event.setText("Your friend hasn't scheduled any events for this day");
                                event.setLayoutParams(lparams);
                                layout.addView(event);

                                if (dayOneId != R.id.day7) {
                                    ConstraintSet set = new ConstraintSet();
                                    set.clone(layout);
                                    set.connect(event.getId(), ConstraintSet.TOP, dayOneId, ConstraintSet.BOTTOM, 0);
                                    set.connect(event.getId(), ConstraintSet.BOTTOM, dayTwoId, ConstraintSet.TOP, 0);
                                    set.connect(event.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                                    set.connect(event.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                                    set.connect(dayTwoId, ConstraintSet.TOP, event.getId(), ConstraintSet.BOTTOM, 0);
                                    set.applyTo(layout);
                                } else {
                                    ConstraintSet set = new ConstraintSet();
                                    set.clone(layout);
                                    set.connect(event.getId(), ConstraintSet.TOP, dayOneId, ConstraintSet.BOTTOM, 0);
                                    set.connect(event.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                                    set.connect(event.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                                    set.connect(dayTwoId, ConstraintSet.TOP, event.getId(), ConstraintSet.BOTTOM, 0);
                                    set.applyTo(layout);
                                }

                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    final String title = document.getString("title");
                                    final String desc = document.getString("description");
                                    final String date = document.getString("date");
                                    final String id = document.getId();
                                    ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout);
                                    ConstraintLayout.LayoutParams lparams = new ConstraintLayout.LayoutParams(
                                            ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                                    lparams.setMargins(100, 0, 100, 0);
                                    Button event = new Button(FriendsWeekActivity.this);
                                    event.setText(title);
                                    event.setId(count);
                                    event.setBackground(ContextCompat.getDrawable(FriendsWeekActivity.this, R.drawable.transparent_bg_bordered_button));
                                    event.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_right_light_grey, 0);
                                    event.setLayoutParams(lparams);
                                    layout.addView(event);

                                    event.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(getApplicationContext(), FriendsWeekEventActivity.class);
                                            i.putExtra("title", title);
                                            i.putExtra("desc", desc);
                                            i.putExtra("date", date);
                                            i.putExtra("id", friendId);
                                            startActivity(i);
                                        }
                                    });

                                    if (previous == 0 && dayOneId != R.id.day7) {
                                        ConstraintSet set = new ConstraintSet();
                                        set.clone(layout);
                                        set.connect(event.getId(), ConstraintSet.TOP, dayOneId, ConstraintSet.BOTTOM, 0);
                                        set.connect(event.getId(), ConstraintSet.BOTTOM, dayTwoId, ConstraintSet.TOP, 0);
                                        set.connect(event.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                                        set.connect(event.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                                        set.connect(dayTwoId, ConstraintSet.TOP, event.getId(), ConstraintSet.BOTTOM, 0);
                                        set.applyTo(layout);
                                    } else if (previous == 0 && dayOneId == R.id.day7) {
                                        ConstraintSet set = new ConstraintSet();
                                        set.clone(layout);
                                        set.connect(event.getId(), ConstraintSet.TOP, dayOneId, ConstraintSet.BOTTOM, 0);
                                        set.connect(event.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                                        set.connect(event.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                                        set.clear(dayTwoId, ConstraintSet.TOP);
                                        set.connect(dayTwoId, ConstraintSet.TOP, event.getId(), ConstraintSet.BOTTOM, 0);
                                        set.applyTo(layout);
                                    } else {
                                        ConstraintSet set = new ConstraintSet();
                                        set.clone(layout);
                                        set.connect(event.getId(), ConstraintSet.TOP, previous, ConstraintSet.BOTTOM, 0);
                                        set.clear(previous, ConstraintSet.BOTTOM);
                                        set.connect(event.getId(), ConstraintSet.BOTTOM, dayTwoId, ConstraintSet.TOP, 0);
                                        set.connect(event.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                                        set.connect(event.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                                        set.clear(dayTwoId, ConstraintSet.TOP);
                                        set.connect(dayTwoId, ConstraintSet.TOP, event.getId(), ConstraintSet.BOTTOM, 0);
                                        set.applyTo(layout);
                                    }
                                    previous = count;
                                    ++count;
                                }
                            }
                            if (dayOneId == R.id.day7) {
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initDays() {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout);
        ConstraintLayout.LayoutParams lparams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(54, 0, 0, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        for (int i = 1; i <= 7; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i - 1);
            String day = sdf.format(calendar.getTime());
            int id = idHelper(i);
            TextView current = (TextView) findViewById(id);
            current.setText(day);
        }
    }

    private List<String> dateHelper() {
        List<String> dates = new ArrayList<String>();
        SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy");
        for (int i = 1; i <= 7; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i - 1);
            String currentDate = date.format(calendar.getTime());
            dates.add(currentDate);
        }
        return dates;
    }

    private List<String> dayHelper() {
        List<String> days = new ArrayList<String>();
        SimpleDateFormat day = new SimpleDateFormat("EEEE");
        for (int i = 1; i <= 7; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i - 1);
            String currentDay = day.format(calendar.getTime());
            days.add(currentDay);
        }
        return days;
    }

    public int idHelper(int id) {
        switch (id) {
            case 1:
                return R.id.day1;
            case 2:
                return R.id.day2;
            case 3:
                return R.id.day3;
            case 4:
                return R.id.day4;
            case 5:
                return R.id.day5;
            case 6:
                return R.id.day6;
            case 7:
                return R.id.day7;
            default:
                return 0;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("id", friendId);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        friendId = savedInstanceState.getString("id");
    }
}
