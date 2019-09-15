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
import android.util.Log;
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

public class FriendsActivity extends AppCompatActivity {

    private static final String TAG = "YERRR";
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        initFriends();
        initRequests();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initFriends() {
        db.collection("users").document(user.getUid()).collection("friends").whereEqualTo("confirmed", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            TextView requests = (TextView)findViewById(R.id.friendRequestsText);
                            int previous = 0;
                            int count = 300;
                            if (task.getResult().isEmpty()) {
                                ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout);
                                ConstraintLayout.LayoutParams lparams = new ConstraintLayout.LayoutParams(
                                        ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                                lparams.setMargins(0, 0, 0, 0);
                                TextView friend = new TextView(FriendsActivity.this);
                                friend.setId(count);
                                friend.setText("You have no friends.");
                                friend.setLayoutParams(lparams);
                                layout.addView(friend);

                                ConstraintSet set = new ConstraintSet();
                                set.clone(layout);
                                set.connect(friend.getId(), ConstraintSet.TOP, R.id.friendsSubtitle, ConstraintSet.BOTTOM, 100);
                                set.connect(friend.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                                set.connect(friend.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                                set.connect(friend.getId(), ConstraintSet.BOTTOM, R.id.friendRequestsText, ConstraintSet.TOP, 100);
                                set.connect(requests.getId(), ConstraintSet.TOP, friend.getId(), ConstraintSet.BOTTOM, 100);
                                set.applyTo(layout);
                                ++count;
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    final String email = document.getString("email");
                                    final String id = document.getString("id");
                                    ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.layout);
                                    ConstraintLayout.LayoutParams lparams = new ConstraintLayout.LayoutParams(
                                            ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                                    lparams.setMargins(100, 0, 100, 0);
                                    Button friend = new Button(FriendsActivity.this);
                                    friend.setText(email);
                                    friend.setId(count);
                                    friend.setBackground(ContextCompat.getDrawable(FriendsActivity.this, R.drawable.transparent_bg_bordered_button));
                                    friend.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_right_light_grey, 0);
                                    friend.setLayoutParams(lparams);
                                    layout.addView(friend);

                                    friend.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            System.out.println("YOU CLOICKED ME");
                                            Intent i = new Intent(FriendsActivity.this, FriendsWeekActivity.class);
                                            i.putExtra("title", email);
                                            i.putExtra("id", id);
                                            startActivity(i);
                                        }
                                    });

                                    if (previous == 0) {
                                        ConstraintSet set = new ConstraintSet();
                                        set.clone(layout);
                                        set.connect(friend.getId(), ConstraintSet.TOP, R.id.friendsSubtitle, ConstraintSet.BOTTOM, 0);
                                        set.connect(friend.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                                        set.connect(friend.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                                        set.connect(R.id.friendRequestsText, ConstraintSet.TOP, friend.getId(), ConstraintSet.BOTTOM, 0);
                                        set.applyTo(layout);
                                    }
                                    else {
                                        ConstraintSet set = new ConstraintSet();
                                        set.clone(layout);
                                        set.connect(friend.getId(), ConstraintSet.TOP, previous, ConstraintSet.BOTTOM, 0);
                                        set.clear(previous, ConstraintSet.BOTTOM);
                                        set.connect(previous, ConstraintSet.BOTTOM, friend.getId(), ConstraintSet.TOP, 0);
                                        set.connect(friend.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                                        set.connect(friend.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                                        set.connect(friend.getId(), ConstraintSet.BOTTOM, R.id.friendRequestsText, ConstraintSet.TOP, 0);
                                        set.connect(R.id.friendRequestsText, ConstraintSet.TOP, friend.getId(), ConstraintSet.BOTTOM, 0);
                                        set.applyTo(layout);
                                    }
                                    previous = count;
                                    ++count;
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                };
        });
    }

    private void initRequests() {
        db.collection("users").document(user.getUid()).collection("friends").whereEqualTo("confirmed", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            TextView requests = (TextView)findViewById(R.id.friendRequestsText);
                            int previous = 0;
                            int count = 100;
                            if (task.getResult().isEmpty()) {
                                ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout);
                                ConstraintLayout.LayoutParams lparams = new ConstraintLayout.LayoutParams(
                                        ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                                lparams.setMargins(0, 0, 0, 0);
                                TextView request = new TextView(FriendsActivity.this);
                                request.setId(count);
                                request.setText("You have no requests.");
                                request.setLayoutParams(lparams);
                                layout.addView(request);

                                ConstraintSet set = new ConstraintSet();
                                set.clone(layout);
                                set.connect(request.getId(), ConstraintSet.TOP, requests.getId(), ConstraintSet.BOTTOM, 100);
                                set.connect(request.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                                set.connect(request.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                                set.applyTo(layout);
                                ++count;
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    final String email = document.getString("email");
                                    final String id = document.getString("id");
                                    ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.layout);
                                    ConstraintLayout.LayoutParams lparams = new ConstraintLayout.LayoutParams(
                                            ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                                    lparams.setMargins(100, 0, 100, 0);
                                    Button request = new Button(FriendsActivity.this);
                                    request.setText(email);
                                    request.setId(count);
                                    request.setBackground(ContextCompat.getDrawable(FriendsActivity.this, R.drawable.transparent_bg_bordered_button));
                                    request.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_right_light_grey, 0);
                                    request.setLayoutParams(lparams);
                                    layout.addView(request);

                                    request.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(getApplicationContext(), RequestDetailsActivity.class);
                                            i.putExtra("email", email);
                                            i.putExtra("id", id);
                                            startActivity(i);
                                        }
                                    });

                                    if (previous == 0) {
                                        ConstraintSet set = new ConstraintSet();
                                        set.clone(layout);
                                        set.connect(request.getId(), ConstraintSet.TOP, R.id.friendRequestsText, ConstraintSet.BOTTOM, 0);
                                        set.connect(request.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                                        set.connect(request.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                                        set.connect(R.id.friendRequestsText, ConstraintSet.BOTTOM, request.getId(), ConstraintSet.TOP, 0);
                                        set.applyTo(layout);
                                    }
                                    else {
                                        ConstraintSet set = new ConstraintSet();
                                        set.clone(layout);
                                        set.connect(request.getId(), ConstraintSet.TOP, previous, ConstraintSet.BOTTOM, 0);
                                        set.clear(previous, ConstraintSet.BOTTOM);
                                        set.connect(request.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                                        set.connect(request.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                                        set.applyTo(layout);
                                    }
                                    previous = count;
                                    ++count;
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    };
                });
    }
}
