package com.example.fyp.Graphs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fyp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class BarChartActivity extends AppCompatActivity {

    private static final String TAG = "BarChartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        BarChart barChart = findViewById(R.id.barchart);

        Intent intent = getIntent();
        ArrayList<BarEntry> userDistances = intent.getParcelableArrayListExtra("user_distances");
        //userDistances.add(new BarEntry(600f, 2));
        Log.d(TAG, "onCreate: INTENT RECEIVED - " + userDistances);

        ArrayList<BarEntry> sample = new ArrayList<>();
        for (int i = 0; i<userDistances.size(); i++){
            sample.add(new BarEntry(userDistances.get(i).getX(), userDistances.get(i).getY()));
        }

        Log.d(TAG, "onCreate: sample List - " + sample);
       /*ArrayList<String> year = new ArrayList<>();
        year.add("2008");
        year.add("2009");
        year.add("2010");
        year.add("2011");
        year.add("2012");
        year.add("2013");
        year.add("2014");
        year.add("2015");
        year.add("2016");
        year.add("2017");*/


        BarDataSet barDataSet = new BarDataSet(sample, "Distances");
        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setData(barData);
        barChart.animateY(3000);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.invalidate();

    }
}