package com.example.fyp.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.Classes.User;
import com.example.fyp.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static final String TAG = "UsersRecyclerAdapter";

    private ArrayList<User> mAllUsers; //Shows all users
    private ArrayList<User> mFilterUsers; //Shows filtered users

    private OnUserListener mOnUserListener;

    //Passes data to be shown in recycler view through here
    public UserAdapter(ArrayList<User> userList, OnUserListener onUserListener) {
        this.mAllUsers = userList;
        mFilterUsers = (ArrayList<User>) mAllUsers.clone();

        this.mOnUserListener = onUserListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(v, mOnUserListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        User currentItem = mAllUsers.get(position);

        //holder.dist.setText(String.valueOf(Math.round(currentItem.getDistance())));  //Add /1000 when finished to get in km not m
        holder.name.setText(currentItem.getName());
        holder.tDis.setText(Math.round(currentItem.getDistanceCovered()) + "Km");
        holder.aDis.setText(Math.round(currentItem.getDistanceAvg()) + "Km per Run");
        holder.aPace.setText(Math.round(currentItem.getPaceAvg()) + "Min per Km");
        holder.tRuns.setText(String.valueOf(currentItem.getTotalRuns()));
    }

    @Override
    public int getItemCount() {
        return mAllUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, tDis, aDis, aPace, tRuns;
        OnUserListener onUserListener;

        public ViewHolder(View itemView, OnUserListener onUserListener) {
            super(itemView);
            name = itemView.findViewById(R.id.userNameView);
            tDis = itemView.findViewById(R.id.totalDistanceView);
            aDis = itemView.findViewById(R.id.avgDistanceView);
            aPace = itemView.findViewById(R.id.avgPaceView);
            tRuns = itemView.findViewById(R.id.totalRunsView);
            this.onUserListener = onUserListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: " + getAdapterPosition());
            mOnUserListener.onUserClick(getAdapterPosition());
        }
    }

    public interface OnUserListener{
        void onUserClick(int position);
    }
}
