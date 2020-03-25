package com.example.fyp.Classes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private static final String TAG = "RoutesRecyclerAdapter";

    private ArrayList<Route> mAllRoutes; //Shows all routes
    private ArrayList<Route> mFilterRoutes; //Shows filtered routes

    private OnRouteListener mOnRouteListener;

    //Passes data to be shown in recycler view through here
    public Adapter(ArrayList<Route> routeList, OnRouteListener onRouteListener) {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try{
            Route currentItem = mAllRoutes.get(position);
            //DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(currentItem.getUserId());
            holder.dist.setText(String.valueOf(Math. round(currentItem.getDistance() * 100.0) / 100.0));
            //holder.user.setText(mUserRef.getClass());
            holder.user.setText(currentItem.getUserId());
            holder.created.setText(currentItem.getCreatedOn());
        }catch (NullPointerException e){
            Log.e(TAG, "onBindViewHolder: Null Pointer: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mAllRoutes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView dist, user, created;
        OnRouteListener onRouteListener;

        public ViewHolder(View itemView, OnRouteListener onRouteListener) {
            super(itemView);
            dist = itemView.findViewById(R.id.routeDistanceView);
            user = itemView.findViewById(R.id.createdById);
            created = itemView.findViewById(R.id.createdOnDate);
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

    /*@Override
    public int getItemCount() {
        return mFilterRoutes.size();
    }

    public void noFilter() {
        mFilterRoutes = mAllRoutes;
        notifyDataSetChanged();
    }

    private void filterCurrent(String userId) {
        mFilterRoutes = new ArrayList<>();
        for(Route route : mAllRoutes) {
            if(route.getUserId().equals(userId)) {
                mFilterRoutes.add(route);
            }
        }
        notifyDataSetChanged();
    }

    private void filterOther(String userId) {
        mFilterRoutes = new ArrayList<>();
        for(Route route : mAllRoutes) {
            if(!userId.equals(route.getUserId())) {
                mFilterRoutes.add(route);
            }
        }
        notifyDataSetChanged();
    }*/


}
