package com.example.fyp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<UserAdapter.ViewHolder> mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DatabaseReference UserRefs = FirebaseDatabase.getInstance().getReference("Users");
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String currentUserId = mUser.getUid();
    private DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);


   // private OnFragmentInteractionListener mListener;

    Intent intent = getIntent();
    String distanceProgress = intent.getExtras().getString("distance_value");
    String paceProgress = intent.getExtras().getString("pace_value");
    String preference = intent.getExtras().getString("preference_value");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_user);
        userList.clear();
        String userKey =  UserRefs.push().getKey();

        Toast.makeText(this, "Dis - " + distanceProgress + "pace - " + paceProgress + "pre - " + preference, Toast.LENGTH_LONG).show();

        retrieveUsers();
        initRecyclerView(userList);

    }


    private void retrieveUsers() {
        UserRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot result : snapshot.getChildren()) {
                    String name = result.child("Name").getValue().toString();
                    double dis = new Double(Math. round(Float.parseFloat(snapshot.child("Total Distance").getValue().toString())));
                    int runs = new Integer(snapshot.child("Total Runs").getValue().toString());
                    double aDistance = Math. round(dis/runs);
                    double aPace = 5.4;

                    User user1 = new User(name, dis, aDistance, aPace, runs);
                    Log.d(TAG, "onDataChange: user1 - " + user1.getName());
                    userList.add(user1);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The Retrieve User Failed: ");
            }
        });
    }


    private void initRecyclerView(ArrayList<User> list){
        mRecyclerView = findViewById(R.id.userRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);  //CHANGED FROM getActivity()
        mAdapter = new UserAdapter(list, this);  //CHANGED FROM this

        //VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        //mRecyclerView.addItemDecoration(itemDecorator);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onUserClick(int position) {

    }

    @Override
    public void onClick(View v) {

    }
/*
    @Override
    public void onClick(View v) {
        Log.e(TAG, "onRouteClick clicked");
        //Intent intent = new Intent(getActivity(), HomePageActivity.class);
        //startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRouteClick(int position) {
        Log.d(TAG, "onRouteClick: ");
        Toast.makeText(this, "Item clicked" + userList.get(position).getName(), Toast.LENGTH_LONG).show();

    }

 */

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Log.e(TAG, "onSwiped swiped to view");

        }
    };
}
