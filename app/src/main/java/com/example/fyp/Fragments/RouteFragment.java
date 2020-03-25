package com.example.fyp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fyp.Activities.HomePageActivity;
import com.example.fyp.Classes.Adapter;
import com.example.fyp.Classes.Route;
import com.example.fyp.Interface.Interface;
import com.example.fyp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.google.android.material.floatingactionbutton.FloatingActionButton.*;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RouteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RouteFragment extends Fragment implements Interface, Adapter.OnRouteListener, OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "RouteFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button buttonAll, buttonCurrent, buttonOther;

    View v;

    private Route mReadRoutes = new Route();

    private ArrayList<Route> routeList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<Adapter.ViewHolder> mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Routes");

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String currentUserId = mUser.getUid();

    private OnFragmentInteractionListener mListener;

    public RouteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RouteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RouteFragment newInstance(String param1, String param2) {
        RouteFragment fragment = new RouteFragment();
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_routes, container, false);
        routeList.clear();
        String routeKey =  mRef.push().getKey();

        buttonAll = (Button) v.findViewById(R.id.button_all_routes);
        buttonCurrent = (Button) v.findViewById(R.id.button_my_routes);
        buttonOther = (Button) v.findViewById(R.id.button_other_routes);

        retrieveRoutes();
        initRecyclerView();


        //v.findViewById(R.id.fab).setOnClickListener(this);

       // mNoteRepository = new NoteRepository(this);

        //insertFakeNotes();

        //setSupportActionBar((Toolbar)findViewById(R.id.notes_toolbar));
        //setTitle("Notes");

        return v;
    }

    public void retrieveRoutes(){
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot result: snapshot.getChildren()){
                    String user = result.child("userId").getValue().toString();
                    String created = result.child("createdOn").getValue().toString();
                    double distance = (Double) result.child("distance").getValue();
                    ArrayList<LatLng> locations = (ArrayList<LatLng>) result.child("locations").getValue();

                    Log.d(TAG, "onDataChange: " + locations);
                    for (int i=0; i<result.child("locations").getChildrenCount(); i++){
                        double latitude = (double) result.child("locations").child(String.valueOf(i)).child("latitude").getValue();
                        double longitude = (double) result.child("locations").child(String.valueOf(i)).child("longitude").getValue();

                        routePoints.add(new LatLng(latitude, longitude));
                    }

                    Route route1 = new Route(distance, routePoints, user, created);
                    //Route route1 = new Route(distance, user, created);
                    routeList.add(route1);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The RouteFragment failed: ");
            }
        });
    }

    /*public void getPolyline(){

        polylineOptions = new PolylineOptions().clickable(true);
        // Create polyline options with existing LatLng ArrayList
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Routes");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot result : snapshot.getChildren()) {
                    routePoints = (ArrayList<LatLng>) result.child("locations").getValue();
                    Log.d(TAG, "onDataChange: " + routePoints);
                    for (int i = 0; i < result.child("locations").getChildrenCount(); i++) {
                        double latitude = (double) result.child("locations").child(String.valueOf(i)).child("latitude").getValue();
                        double longitude = (double) result.child("locations").child(String.valueOf(i)).child("longitude").getValue();

                        polylineOptions.add(new LatLng(latitude, longitude));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



        polylineOptions.width(5).color(Color.RED).geodesic(true);
        mMap.addPolyline(polylineOptions);
    }*/

            private void initRecyclerView(){
        mRecyclerView = v.findViewById(R.id.routeRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new Adapter(routeList, this);


        //VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        //mRecyclerView.addItemDecoration(itemDecorator);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }



    @Override
    public void onClick(View v) {
        Log.e(TAG, "onRouteClick clicked");
        //Intent intent = new Intent(getContext(), HomePageActivity.class);
        //startActivity(intent);
    }

    @Override
    public void onRouteClick(int position) {
        Log.e(TAG, "onRouteClick clicked position - " + position);

        Log.d(TAG, "onRouteClick: " + routeList.get(position).getLocations());


        HomePageActivity.mapRoute(routeList.get(position).getLocations());
        //Intent intent = new Intent(this, NoteActivity.class);
        //intent.putExtra("selected_note", mNotes.get(position));


    }

    /*private void deleteRoute(Route route) {
        routeList.remove(route);
        mAdapter.notifyDataSetChanged();

        //Delete from firebase here
        mNoteRepository.deleteNoteTask(note);
    }*/

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Log.e(TAG, "onSwiped swiped to delete");
            //deleteRoute(routeList.get(viewHolder.getAdapterPosition()));
        }
    };

    private void filterCurrent() {
        Log.d(TAG, "filterCurrent Begin");
    }
    private void filterOther() {
        Log.d(TAG, "filterOther Begin");

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
