package com.gocoddi.coddi;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;

public class DayFragment extends Fragment {
    public static final String DATE = "";
    public static final String TAG = "";
    private String date;
    private DatabaseReference db;
    private DatabaseReference eventsRef;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private View root;

    public DayFragment() {
        // Required empty public constructor
    }

    public static DayFragment newInstance(String date) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putString(DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getArguments() != null) {
            date = this.getArguments().getString(DATE);
        }
        System.out.println(date);
        db = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (date != null) {
            eventsRef = db.child("events").child(user.getUid()).child(date);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_day, container, false);
        LinearLayout layout = (LinearLayout) root.findViewById(R.id.fragmentLayout);
        return root;
    }

    public void onStart() {
        super.onStart();
        if (date != null) {
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    System.out.println("The data changed!");
                    System.out.println(dataSnapshot);
                    System.out.println(dataSnapshot.getValue());
                    System.out.println("key " + dataSnapshot.getKey());
                    final int count = (int)Math.random() * 100;
                    LinearLayout layout = (LinearLayout) root.findViewById(R.id.fragmentLayout);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (dataSnapshot.getValue() != null) {
                        for(final DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                            final Event event = eventSnapshot.getValue(Event.class);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            Button eventButton = new Button(getActivity());
                            eventButton.setText(event.title);
                            eventButton.setId(count);
                            eventButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.transparent_bg_bordered_button));
                            eventButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_right_light_grey, 0);
                            eventButton.setLayoutParams(params);
                            layout.addView(eventButton);
                            eventButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(getActivity(), EventDetailsActivity.class);
                                    i.putExtra("title", event.title);
                                    i.putExtra("desc", event.description);
                                    i.putExtra("date", date);
                                    i.putExtra("id", eventSnapshot.getKey());
                                    startActivity(i);
                                }
                            });
                        }
                    } else {
                        TextView noEvents = new TextView(getActivity());
                        noEvents.setId((int)Math.random());
                        noEvents.setText("You haven't scheduled any events for this day");
                        noEvents.setGravity(Gravity.CENTER);
                        noEvents.setLayoutParams(params);
                        layout.addView(noEvents);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    Toast.makeText(getContext(), "Failed to load post.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            eventsRef.addValueEventListener(eventListener);
        }
    }

//    private void initEvents(final String date, final LinearLayout layout) {
//        db.collection("events").document(user.getUid()).collection(date)
//            .get()
//            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    int count = Integer.parseInt(date.substring(3,5)) * 100;
//                    if(task.getResult().isEmpty()) {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        TextView event = new TextView(getActivity());
//                        event.setId(count);
//                        event.setText("You haven't scheduled any events for this day");
//                        event.setGravity(Gravity.CENTER);
//                        event.setLayoutParams(params);
//                        if(layout != null) {
//                            layout.addView(event);
//                            System.out.println("it was added and there wasn't anything there");
//                        } else {
//                            System.out.println("it was null");
//                        }
//                    } else {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            final String title = document.getString("title");
//                            final String desc = document.getString("description");
//                            final String date = document.getString("date");
//                            final String id = document.getId();
//                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            layout.setOrientation(LinearLayout.VERTICAL);
//                            Button event = new Button(getActivity());
//                            event.setText(title);
//                            event.setId(count);
//                            event.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.transparent_bg_bordered_button));
//                            event.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_right_light_grey, 0);
//                            event.setLayoutParams(params);
//                            if(layout != null) {
//                                layout.addView(event);
//                                System.out.println("it was added and there was something there");
//                            } else {
//                                System.out.println("it was null");
//                            }
//
//                            event.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent i = new Intent(getActivity(), EventDetailsActivity.class);
//                                    i.putExtra("title", title);
//                                    i.putExtra("desc", desc);
//                                    i.putExtra("date", date);
//                                    i.putExtra("id", id);
//                                    startActivity(i);
//                                }
//                            });
//                        }
//                    }
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.getException());
//                }
//                }
//            });
//    }
}
