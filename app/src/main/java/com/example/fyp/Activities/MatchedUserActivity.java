package com.example.fyp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.Adapters.UserAdapter;
import com.example.fyp.Classes.User;
import com.example.fyp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.google.android.material.floatingactionbutton.FloatingActionButton.OnClickListener;

public class MatchedUserActivity extends AppCompatActivity implements UserAdapter.OnUserListener, OnClickListener, Comparable<User> {
    private static final String TAG = "MatchedUserActivity";
    private static int distancePreferred, pacePreferred;
    private static long currentDisAvg, currentPaceAvg;
    private int best, worst;

    private ArrayList<User> userList = new ArrayList<>();
    private TextView noData, cPace, cTotalRuns, cDistance, iPace, iDistance;
    private Button buttonAll, buttonPace, buttonDistance;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<UserAdapter.ViewHolder> adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference UserRefs = FirebaseDatabase.getInstance().getReference("Users");
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String currentUserId = mUser.getUid();
    private DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
    private DatabaseReference mUserFollowing = mUserRef.child("Following");
    private String currentEmail;

    public static void setPreferenceValues(int seekDistanceValue, int seekPaceValue) {
        Log.d(TAG, "setPreferenceValues: dis = " + seekDistanceValue +  " pace = " + seekPaceValue);
        distancePreferred = seekDistanceValue;
        pacePreferred = seekDistanceValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_user);
        userList.clear();
        //String userKey =  UserRefs.push().getKey();

        buttonAll = (Button) findViewById(R.id.button_all_users);
        buttonDistance = (Button) findViewById(R.id.button_on_distance);
        buttonPace = (Button) findViewById(R.id.button_on_pace);

        noData = findViewById(R.id.empty_view);
        cDistance = findViewById(R.id.current_avg_distance);
        cPace = (TextView) findViewById(R.id.current_avg_pace);
        iDistance = findViewById(R.id.ideal_avg_distance);
        iPace = (TextView) findViewById(R.id.ideal_avg_pace);
        cTotalRuns = (TextView) findViewById(R.id.current_total_runs);

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentEmail = snapshot.child("Email").getValue().toString();
                double dis = new Double(Math.round(Float.parseFloat(snapshot.child("TotalDistance").getValue().toString())));
                double pace = new Double(Math.round(Float.parseFloat(snapshot.child("AvgPace").getValue().toString())));
                int runs = new Integer(snapshot.child("TotalRuns").getValue().toString());
                currentDisAvg = Math.round(dis / runs);
                currentPaceAvg = Math.round(pace / runs);

                cDistance.setText("Avg. Distance = " + currentDisAvg + "Km");
                cPace.setText("Avg Pace = " + currentPaceAvg + "min/Km");
                cTotalRuns.setText("Total Runs = " + runs);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The Retrieve User Failed: ");
            }
        });
        retrieveUsers();

        buttonAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                sortPreference();
                sortPreferencePace();
                initRecyclerView(userList);
            }
        });

        buttonDistance.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                sortDistance();
            }
        });
        buttonPace.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                sortPace();
            }
        });
    }

    private void retrieveUsers() {
        Log.d(TAG, "retrieveUsers: beginning");
            UserRefs.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot result : snapshot.getChildren()) {
                        try {
                            if (!result.child("Email").getValue().toString().equalsIgnoreCase(currentEmail)) {
                                String name = result.child("Name").getValue().toString();
                                String email = result.child("Email").getValue().toString();
                                double dis = new Double(Math.round(Float.parseFloat(result.child("TotalDistance").getValue().toString())));
                                double aPace = new Double(Math.round(Float.parseFloat(result.child("AvgPace").getValue().toString())));;
                                int runs = new Integer(result.child("TotalRuns").getValue().toString());
                                double aDistance = Math.round(dis / runs);

                                Log.d(TAG, "onDataChange: aPace = " + aPace);

                                User user1 = new User(name, email, dis, aDistance, aPace, runs);
                                userList.add(user1);
                                Log.d(TAG, "onDataChange: Pace - " + user1.getPaceAvg());
                            }
                        }catch (NumberFormatException e) {
                            e.printStackTrace();
                            User user1 = new User("Joe", "blogs@gmail.com", 1000, 100, 5, 10);
                            Log.d(TAG, "onDataChange: user1 - " + user1.getName());
                            userList.add(user1);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("The Retrieve User Failed: ");
                }
            });
        initRecyclerView(userList);
        Log.d(TAG, "retrieveUsers: ending");

    }


    private void sortPreference() {
        Log.d(TAG, "sortPreference: disPref = " + distancePreferred);
        Log.d(TAG, "sortPreference: currentDisAvg = " + currentDisAvg);
        double percent = ((distancePreferred-100)/2);
        if(100 < distancePreferred) {
            best = (int) (currentDisAvg * (1+ (percent/100)));
            worst = (int) currentDisAvg;
        } else if(distancePreferred == 100) {
            best = (int) ((int) currentDisAvg * 1.1);
            worst = (int) ((int) currentDisAvg * 0.9);
        } else{
            best = (int) (currentDisAvg * (percent/100));
            worst = (int) currentDisAvg;
        }
        iDistance.setText(worst + " - " + best + " Km");
        Log.d(TAG, "sortPreference: Best = " + best + "\n Worst = " + worst);
    }
    private void sortPreferencePace() {
        Log.d(TAG, "sortPreference: pacePref = " + pacePreferred);
        Log.d(TAG, "sortPreference: currentPaceAvg = " + currentPaceAvg);
        double percent = ((pacePreferred-100)/2);
        if(100 < pacePreferred) {
            best = (int) (currentPaceAvg * (1+ (percent/100)));
            worst = (int) currentPaceAvg;
        } else if(pacePreferred == 100) {
            best = (int) ((int) currentPaceAvg * 1.1);
            worst = (int) ((int) currentPaceAvg * 0.9);
        } else{
            best = (int) (currentPaceAvg * (percent/100));
            worst = (int) currentPaceAvg;
        }
        iPace.setText(worst + " - " + best + " min/Km");
        Log.d(TAG, "sortPreferencePace: Best = " + best + "\n Worst = " + worst);
    }

    private void sortDistance() {
        Log.d(TAG, "filterDistance Begin");
        ArrayList<User> mFilterList = new ArrayList<>();
        Log.d(TAG, "sortPreference: Best = " + best + "\n Worst = " + worst);

        for(User user : userList){
            Log.d(TAG, "sortDistance: User Avg. = " + user.getDistanceAvg());
            if (worst <= user.getDistanceAvg()  && user.getDistanceAvg() <= best){
                mFilterList.add(user);
                Log.d(TAG, "sortDistance: mFilterList = " + mFilterList);
            }
        }
        initRecyclerView(mFilterList);
    }

    private void sortPace() {
        Log.d(TAG, "filterPace Begin");
        ArrayList<User> mFilterList = new ArrayList<>();
        Log.d(TAG, "sortPreferencePace: Best = " + best + "\n Worst = " + worst);

        for(User user : userList){
            Log.d(TAG, "sortDistance: User Avg. = " + user.getPaceAvg());
            if (worst <= user.getPaceAvg() && user.getPaceAvg() <= best){
                mFilterList.add(user);
            }
        }
        initRecyclerView(mFilterList);
    }



    private void initRecyclerView(ArrayList<User> list){
        Log.d(TAG, "initRecyclerView: Beginning");
        Log.d(TAG, "initRecyclerView: ArrayList = " + list);
        recyclerView = findViewById(R.id.userRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new UserAdapter(list, this);

        //VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        //mRecyclerView.addItemDecoration(itemDecorator);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "initRecyclerView: Beginning");
    }


    @Override
    public void onUserClick(int position) {
        Toast.makeText(this, "Item clicked" + userList.get(position).getName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Log.e(TAG, "onSwiped swiped to view");
            int position = viewHolder.getPosition();

            final String newFollower = userList.get(position).getEmail();
            mUserRef.child("Following").setValue(newFollower);

            Toast.makeText(getApplication(), newFollower + " Followed", Toast.LENGTH_LONG).show();
        }
    };


    @Override
    public int compareTo(User o) {
        return 0;
        // usually toString should not be used,
        // instead one of the attributes or more in a comparator chain
        //return toString().compareTo(o.toString());
    }
}
