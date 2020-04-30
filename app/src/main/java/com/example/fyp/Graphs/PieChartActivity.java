package com.example.fyp.Graphs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fyp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class PieChartActivity extends AppCompatActivity {
    private static final String TAG = "PieChartActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        PieChart pieChart = findViewById(R.id.piechart);

        PieDataSet pds = null;
         Intent intent = getIntent();
         if (intent.hasExtra("user_distances")){
             pds = new PieDataSet(sortDistances(), "");
         }else if (intent.hasExtra("user_pace")){
             pds = new PieDataSet(sortPace(), "Pace of Runs");
         }else if (intent.hasExtra("user_times")){
             pds = new PieDataSet(sortTime(), "Duration of Runs");
         }else if (intent.hasExtra("user_days")){
             pds = new PieDataSet(sortDays(), "Days of Runs");
         }

        pds.setColors(ColorTemplate.COLORFUL_COLORS);
         pieChart.setNoDataText("");

        //LEGEND SPECIFICATIONS
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(18);
        legend.setFormToTextSpace(1);
        legend.setStackSpace(10);
        legend.setFormSize(10);
        legend.setEnabled(true);

        PieData pd = new PieData(pds);
        pieChart.setData(pd);
        pds.setSliceSpace(3f);
        pds.setValueTextColor(Color.WHITE);
        pds.setValueTextSize(15f);
        pieChart.setData(pd);
        pieChart.animateXY(5000, 5000);
    }

    private ArrayList<PieEntry> sortDistances(){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        Intent intent = getIntent();
        ArrayList<? extends Float> userDistances = intent.getParcelableArrayListExtra("user_distances");
        int u=0, f=0, t=0, o=0;
        String under="<5k", five="5k - 10k", ten="10k - 20k", over="20k+";
        for (int i = 0; i<userDistances.size(); i++){
            if (userDistances.get(i) < 500){
                u = u + 1;
            }else if (userDistances.get(i) < 1000){
                f = f + 1;
            }else if (userDistances.get(i) < 2000){
                t = t + 1;
            }else{
                o = o + 1;
            }
        }
        pieEntries.add(new PieEntry(u, under));
        pieEntries.add(new PieEntry(f, five));
        pieEntries.add(new PieEntry(t, ten));
        pieEntries.add(new PieEntry(o, over));

        return pieEntries;
    }

    private ArrayList<PieEntry> sortPace(){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        Intent intent = getIntent();
        ArrayList<? extends Float> userPace = intent.getParcelableArrayListExtra("user_pace");

        Log.d(TAG, "sortPace: userPace = " + userPace);

        int u=0, f=0, t=0, o=0;
        String under="< 5min/Km", five="5-7", ten="7-10", over="10+";
        for (int i = 0; i<userPace.size(); i++){
            if (userPace.get(i) < 5){
                u = u + 1;
            }else if (userPace.get(i) < 7){
                f = f + 1;
            }else if (userPace.get(i) < 10){
                t = t + 1;
            }else{
                o = o + 1;
            }
        }
        pieEntries.add(new PieEntry(u, under));
        pieEntries.add(new PieEntry(f, five));
        pieEntries.add(new PieEntry(t, ten));
        pieEntries.add(new PieEntry(o, over));

        return pieEntries;
    }

    private ArrayList<PieEntry> sortTime(){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        Intent intent = getIntent();
        ArrayList<? extends String> userTimes = intent.getParcelableArrayListExtra("user_times");

        int u=0, f=0, t=0, o=0;
        String under="< 15min", ten="15-25min", twenty="25-45min", over="45min+";

        for (int i = 0; i<userTimes.size(); i++){
            String segments[] = userTimes.get(i).split(":");
            int minutes = Integer.parseInt(segments[segments.length - 2]);
            if (minutes < 15){
                u = u + 1;
            }else if (minutes < 25){
                f = f + 1;
            }else if (minutes < 45){
                t = t + 1;
            }else{
                o = o + 1;
            }

        }
        pieEntries.add(new PieEntry(u, under));
        pieEntries.add(new PieEntry(f, ten));
        pieEntries.add(new PieEntry(t, twenty));
        pieEntries.add(new PieEntry(o, over));

        return pieEntries;
    }

    private ArrayList<PieEntry> sortDays(){

            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            Intent intent = getIntent();
            ArrayList<? extends String> userDays = intent.getParcelableArrayListExtra("user_days");

        Log.d(TAG, "sortDays: userDays = " + userDays);

            int m=0, t=0, w=0, th=0, f=0, s=0, su=0;
            String monday="Monday", tuesday="Tuesday", wednesday="Wednesday", thursday="Thursday", friday="Friday", saturday="Saturday", sunday="Sunday";

            for (int i = 0; i<userDays.size(); i++){
                String segments[] = userDays.get(i).split(",");
                String day = segments[segments.length - 2];
                if (day.equalsIgnoreCase("Mon")){
                    m = m + 1;
                }else if (day.equalsIgnoreCase("Tue")){
                    t = t + 1;
                }else if (day.equalsIgnoreCase("Wed")){
                    w = w + 1;
                }else if (day.equalsIgnoreCase("Thu")){
                    th = th + 1;
                }else if (day.equalsIgnoreCase("Fri")){
                    f = f + 1;
                }else if (day.equalsIgnoreCase("Sat")) {
                    s = s + 1;
                }else{
                    su = su + 1;
                }
            }
            pieEntries.add(new PieEntry(m, monday));
            pieEntries.add(new PieEntry(t, tuesday));
            pieEntries.add(new PieEntry(w, wednesday));
            pieEntries.add(new PieEntry(th, thursday));
            pieEntries.add(new PieEntry(f, friday));
            pieEntries.add(new PieEntry(s, saturday));
            pieEntries.add(new PieEntry(su, sunday));

            return pieEntries;
        }

}