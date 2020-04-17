package com.example.fyp.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.Classes.Route;
import com.example.fyp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder> {
    private static final String TAG = "RoutesRecyclerAdapter";

    private ArrayList<Route> mAllRoutes; //Shows all routes
    private ArrayList<Route> mFilterRoutes; //Shows filtered routes

    private OnRouteListener mOnRouteListener;

    //Passes data to be shown in recycler view through here
    public RouteAdapter(ArrayList<Route> routeList, OnRouteListener onRouteListener) {
        this.mAllRoutes = routeList;
        mFilterRoutes = (ArrayList<Route>) mAllRoutes.clone();

        this.mOnRouteListener = onRouteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_item, parent, false);
        return new ViewHolder(v, mOnRouteListener);


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Route currentItem = mAllRoutes.get(position);

            DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(currentItem.getUserId());
            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String name = snapshot.child("Name").getValue().toString();
                        Log.d(TAG, "onDataChange: Name = " + name);
                        holder.user.setText(name);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String name = "Anonymous User";
                        holder.user.setText(name);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            holder.dist.setText(Math.round(currentItem.getDistance()) + "Km");  //Add /1000 when finished to get in km not m
            holder.created.setText(currentItem.getCreatedOn());

        holder.start.setText(currentItem.getStart());
        holder.end.setText(currentItem.getEnd());
    }

    @Override
    public int getItemCount() {
        return mAllRoutes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView dist, user, created, start, end;
        OnRouteListener onRouteListener;

        public ViewHolder(View itemView, OnRouteListener onRouteListener) {
            super(itemView);
            dist = itemView.findViewById(R.id.routeDistanceView);
            user = itemView.findViewById(R.id.createdById);
            created = itemView.findViewById(R.id.createdOnDate);
            start = itemView.findViewById(R.id.startPointView);
            end = itemView.findViewById(R.id.endPointView);
            this.onRouteListener = onRouteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: " + getAdapterPosition());
            mOnRouteListener.onRouteClick(getAdapterPosition());
        }
    }

    public interface OnRouteListener{
        void onRouteClick(int position);
    }
}
