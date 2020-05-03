package com.example.fyp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.Adapters.RunAdapter;
import com.example.fyp.Classes.Run;
import com.example.fyp.Graphs.PieChartActivity;
import com.example.fyp.Maps.HomePageActivity;
import com.example.fyp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment implements RunAdapter.OnRunListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "StatsFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Spinner filter1, filter2;
    private Button btnLineChart, btnPieChart;
    private TextView tDistanceView, aDistanceView, tRunsView, fRun, sRun, tCalories;
    private String furthest, shortest;

    View v;

    private Run mReadRuns = new Run();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<RunAdapter.ViewHolder> mRunAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mRunRef = FirebaseDatabase.getInstance().getReference("Runs");

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String currentUserId = mUser.getUid();
    private DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
    private ArrayList<Run> runList = new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    private double f=0, s=1000;


    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_stats, container, false);
        runList.clear();
        // String runKey =  mRunRef.push().getKey();

        tDistanceView = v.findViewById(R.id.text_total_distance);
        aDistanceView = v.findViewById(R.id.text_avg_distance);
        tRunsView = v.findViewById(R.id.text_total_runs);
        tCalories = v.findViewById(R.id.text_total_calories);
        fRun = v.findViewById(R.id.text_furthest_run);
        sRun = v.findViewById(R.id.text_shortest_run);

        filter1 = (Spinner) v.findViewById(R.id.text_filter);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.personal_filter, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter1.setAdapter(adapter1);

        retrieveUserInfo();
        retrieveRuns();
        setFurthestShortest();

        btnPieChart = v.findViewById(R.id.btnPieChart);

        btnPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String f;
                if (filter1 != null && filter1.getSelectedItem() != null) {
                    f = (String) filter1.getSelectedItem();
                } else {
                    f = "Null";
                }

                if (f.equals("Distances")) {
                    Intent intent = new Intent(getActivity(), PieChartActivity.class);
                    intent.putExtra("user_distances", getDistances());
                    startActivity(intent);
                } else if (f.equals("Pace")) {
                    Intent intent = new Intent(getActivity(), PieChartActivity.class);
                    intent.putExtra("user_pace", getPace());
                    startActivity(intent);
                } else if (f.equals("Run Times")) {
                    Intent intent = new Intent(getActivity(), PieChartActivity.class);
                    intent.putExtra("user_times", getTime());
                    startActivity(intent);
                } else if (f.equals("Days Ran")) {
                    Intent intent = new Intent(getActivity(), PieChartActivity.class);
                    intent.putExtra("user_days", getDays());
                    startActivity(intent);
                }
            }
        });

        return v;
    }

    private void setFurthestShortest() {
        double f=0,s=0;
        for (int i=0;i<runList.size();i++){
            if(f<runList.get(i).getDistance()){
                f = runList.get(i).getDistance();
            }else if(s>runList.get(i).getDistance()){
                s = runList.get(i).getDistance();
            }
        }
        furthest = String.valueOf(f);
        shortest = String.valueOf(s);
        fRun.setText(furthest);
        sRun.setText(shortest);
    }


    private ArrayList<String> getDays() {
        ArrayList<String> userDays = new ArrayList<>();
        for (int i = 0; i<runList.size(); i++){
            userDays.add(runList.get(i).getCreatedOn());
            Log.d(TAG, "getUserDay: Day Logged = " + runList.get(i).getCreatedOn());
        }
        return userDays;
    }

    private ArrayList<String> getTime() {
        ArrayList<String> userTime = new ArrayList<>();
        for (int i = 0; i<runList.size(); i++){
            userTime.add(runList.get(i).getDuration());
            Log.d(TAG, "getUserTime: Time Logged = " + runList.get(i).getDuration());
        }
        return userTime;
    }

    private ArrayList<Float> getPace() {
        ArrayList<Float> userPace = new ArrayList<>();
        for (int i = 0; i<runList.size(); i++){
            userPace.add((float) runList.get(i).getPace());
            Log.d(TAG, "getUserPace: Pace Logged = " + userPace.get(i));
        }
        return userPace;
    }

    private ArrayList<Float> getDistances() {
        ArrayList<Float> userDistances = new ArrayList<>();
        for (int i = 0; i<runList.size(); i++){
            userDistances.add((float) runList.get(i).getDistance());
            Log.d(TAG, "getUserDistances: Distance Logged = " + userDistances.get(i));
        }
        return userDistances;
    }

    private void retrieveUserInfo() {
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    double dis = new Double(Math. round(Float.parseFloat(snapshot.child("Total Distance").getValue().toString())));
                    int runs = new Integer(snapshot.child("Total Runs").getValue().toString());
                    double aDistance = Math. round(dis/runs);
                    double cal = new Double(Math. round(Float.parseFloat(snapshot.child("Total Calories").getValue().toString())));

                    tDistanceView.setText(dis + " Meter");
                    tRunsView.setText(snapshot.child("Total Runs").getValue().toString() + " Workouts");
                    aDistanceView.setText(aDistance + " Meter");
                    tCalories.setText(cal + " Calories");

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Unable to find User Statistics" , Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void retrieveRuns(){
        mRunRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot result : snapshot.getChildren()) {
                    try {
                        if(result.child("userId").getValue().toString().equalsIgnoreCase(currentUserId)) {
                            String duration = result.child("duration").getValue().toString();
                            double pace = new Double(result.child("pace").getValue().toString());
                            double distance = new Double(result.child("distance").getValue().toString());
                            if(f<distance){
                                f = distance;
                            }else if(s>distance){
                                s = distance;
                            }
                            String userId = result.child("userId").getValue().toString();
                            String createdOn = result.child("createdOn").getValue().toString();

                            Run run1 = new Run(duration, distance, pace, userId, createdOn);
                            runList.add(run1);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "onDataChange: FAILED" + e);
                    }
                }
                fRun.setText(Math.round(f) + "Km");
                sRun.setText(Math.round(s) + "Km");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The StatsFragment failed: ");
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onRunClick clicked");
        //Intent intent = new Intent(getActivity(), HomePageActivity.class);
        //startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRunClick(int position) {
        Log.d(TAG, "onRunClick: ");
        Toast.makeText(getActivity(), "Run clicked" + runList.get(position).getDistance(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getActivity(), HomePageActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
