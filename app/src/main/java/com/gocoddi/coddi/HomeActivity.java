package com.gocoddi.coddi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private List<String> dateList = new ArrayList<>();
    private List<String> dayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();

        dateList = dateHelper();
        dayList = dayHelper();

        int[] dayFragments = {R.id.day1Fragment, R.id.day2Fragment, R.id.day3Fragment, R.id.day4Fragment, R.id.day5Fragment, R.id.day6Fragment, R.id.day7Fragment};
        FragmentManager fragmentManager = getSupportFragmentManager();

        for(int i = 0; i < dateList.size(); ++i) {
            Bundle bundle = new Bundle();
            String date = dateList.get(i);
            bundle.putString("date", date);
            Fragment dayFrag = DayFragment.newInstance(dateList.get(i));
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(dayFragments[i], dayFrag);
            transaction.commit();
            System.out.println(i);
        }
        System.out.println("Executing the transactions now");
        fragmentManager.executePendingTransactions();
        System.out.println("Hiding the loading panel and intializing the days now!");
        findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
        initDays();
    }

    private void initDays() {
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.layout);
        ConstraintLayout.LayoutParams lparams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(54, 0, 0, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        for (int i = 1; i <= 7; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i-1);
            String day = sdf.format(calendar.getTime());
            int id = idHelper(i);
            TextView current = (TextView)findViewById(id);
            current.setText(day);
        }
    }

    private List<String> dateHelper() {
        List<String> dates = new ArrayList<String>();
        SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy");
        for (int i = 1; i <= 7; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i-1);
            String currentDate = date.format(calendar.getTime());
            System.out.println(currentDate);
            dates.add(currentDate);
        }
        return dates;
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

    public int idHelper(int id) {
        switch(id) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_btn:
                Intent add = new Intent(this,CreateActivity.class);
                this.startActivity(add);
                return true;
            case R.id.friends_btn:
                Intent friends = new Intent(this, FriendsActivity.class);
                this.startActivity(friends);
                return true;
            case R.id.friendsAdd_btn:
                Intent friendsAdd = new Intent(this, AddFriendsActivity.class);
                this.startActivity(friendsAdd);
                return true;
            case R.id.manageFriends_btn:
                Intent manageFriends = new Intent(this, ManageFriendsActivity.class);
                this.startActivity(manageFriends);
                return true;
            case R.id.logout_btn:
                auth.signOut();
                Intent signOut = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(signOut);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
