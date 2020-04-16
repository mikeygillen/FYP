package com.example.fyp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class MatchedUserActivity extends AppCompatActivity implements UserAdapter.OnUserListener, OnClickListener {
    private static final String TAG = "MatchedUserActivity";

    private ArrayList<User> userList = new ArrayList<>();
    private int distancePreferred, pacePreferred;
    private String preference;
    private Button buttonAll;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<UserAdapter.ViewHolder> adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference UserRefs = FirebaseDatabase.getInstance().getReference("Users");
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String currentUserId = mUser.getUid();
    private DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
    private DatabaseReference mUserFollowing = mUserRef.child("Following");

    /*private OnFragmentInteractionListener mListener;

    public static void setPreferenceValues(int seekDistanceValue, int seekPaceValue, CharSequence text) {
        Log.d(TAG, "setPreferenceValues: dis = " + seekDistanceValue +  " pace = " + seekPaceValue +  " pref = " + text);
    }

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_user);
        //userList.clear();
        String userKey =  UserRefs.push().getKey();

        buttonAll = (Button) findViewById(R.id.button_all_users);

        retrieveUsers();

        buttonAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                initRecyclerView(userList);
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
                            Log.d(TAG, "onDataChange: snapshot = " + snapshot);
                            Log.d(TAG, "onDataChange: result = " + result);
                            String name = result.child("Name").getValue().toString();
                            String email = result.child("Email").getValue().toString();
                            double dis = new Double(Math.round(Float.parseFloat(result.child("Total Distance").getValue().toString())));
                            int runs = new Integer(result.child("Total Runs").getValue().toString());
                            double aDistance = Math.round(dis / runs);
                            double aPace = 5.4;

                            User user1 = new User(name, dis, aDistance, aPace, runs, email);
                            userList.add(user1);
                            Log.d(TAG, "onDataChange: userList - " + userList);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            User user1 = new User("Joe", 1000, 100, 5, 10, "blogs@gmail.com");
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


    private void initRecyclerView(ArrayList<User> list){
        Log.d(TAG, "initRecyclerView: Beginning");
        Log.d(TAG, "initRecyclerView: ArrayList = " + list);
        recyclerView = findViewById(R.id.userRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);  //CHANGED FROM getActivity()
        adapter = new UserAdapter(list, this);  //CHANGED FROM this

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


}
