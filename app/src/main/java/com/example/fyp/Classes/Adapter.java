package com.example.fyp.Classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<Route> mRouteList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView dist;
        public TextView user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.routeImage);
            dist = itemView.findViewById(R.id.routeDistanceView);
            user = itemView.findViewById(R.id.createdById);
        }
    }

    //Passes data to be shown in recycler view through here
    public Adapter(ArrayList<Route> routeList) {
        mRouteList = routeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Route currentItem = mRouteList.get(position);

        //holder.imageView.setImageResource(currentItem.getRouteImage());
        holder.dist.setText(String.valueOf(currentItem.getDistance()));
        holder.user.setText(currentItem.getUserId());
    }

    @Override
    public int getItemCount() {
        return mRouteList.size();
    }


}
