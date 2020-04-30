package com.example.fyp.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.Classes.Run;
import com.example.fyp.R;

import java.util.ArrayList;

public class RunAdapter extends RecyclerView.Adapter<RunAdapter.ViewHolder> {
    private static final String TAG = "RunsRecyclerAdapter";

    private ArrayList<Run> mAllRuns; //Shows all runs
    private ArrayList<Run> mFilterRuns; //Shows filtered runs

    private OnRunListener mOnRunListener;

    //Passes data to be shown in recycler view through here
    public RunAdapter(ArrayList<Run> runList, OnRunListener onRunListener) {
        this.mAllRuns = runList;
        mFilterRuns = (ArrayList<Run>) mAllRuns.clone();

        Log.d(TAG, "RunAdapter: RunList = " + runList + ", mFilterList = " + mFilterRuns);

        this.mOnRunListener = onRunListener;
    }

    @NonNull
    @Override
    public RunAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.run_item, parent, false);
        return new ViewHolder(v, mOnRunListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try{
            Run currentItem = mAllRuns.get(position);

            holder.duration.setText(currentItem.getDuration());
            holder.dist.setText(Math. round(currentItem.getDistance() / 1000) + "Km");
            holder.pace.setText(currentItem.getPace() + " min/Km");
            holder.calories.setText(currentItem.getCalories() + " kcal");

        }catch (NullPointerException e){
            Log.e(TAG, "onBindViewHolder: Null Pointer: " + e.getMessage() );
        }
    }

    @Override
    public int getItemCount() {
        return mAllRuns.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView duration, dist, pace, calories;
        OnRunListener onRunListener;

        public ViewHolder(View itemView, OnRunListener onRunListener) {
            super(itemView);
            duration = itemView.findViewById(R.id.duration);
            dist = itemView.findViewById(R.id.runDistanceView);
            pace = itemView.findViewById(R.id.runPaceView);
            calories = itemView.findViewById(R.id.calorie_burned_view);
            this.onRunListener = onRunListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: " + getAdapterPosition());
            mOnRunListener.onRunClick(getAdapterPosition());
        }
    }

    public interface OnRunListener{
        void onRunClick(int position);
    }
}
