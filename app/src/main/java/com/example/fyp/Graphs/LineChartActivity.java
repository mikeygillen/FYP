package com.example.fyp.Graphs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fyp.Classes.Run;
import com.example.fyp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LineChartActivity extends AppCompatActivity {

    private static final String TAG = "BarChartActivity";

    private DatabaseReference mRunRef = FirebaseDatabase.getInstance().getReference("Runs");

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String currentUserId = mUser.getUid();
   // private DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
    private ArrayList<Run> runList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        LineChart graph = findViewById(R.id.lineChart);

        LineDataSet lds1 = null;
        Intent intent = getIntent();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        if (intent.hasExtra("user_distances")){
            lds1 = new LineDataSet(sortDistances(), "Distances Covered");
        }else if (intent.hasExtra("user_pace")){
            //lds1 = new LineDataSet(sortPace(), "Pace of Runs");
        }else if (intent.hasExtra("user_times")){
            //lds1 = new LineDataSet(sortTime(), "Duration of Runs");
        }else if (intent.hasExtra("user_days")){
            //lds1 = new LineDataSet(sortDays(), "Days of Runs");
        }

        //SETS GRAPH SPECIFICATIONS
        //graph.setBackgroundColor(Color.GREEN);
        graph.setNoDataText("NO DATA");
        //graph.setDrawGridBackground(true);

        //SETS LINE SPECIFICATIONS
        lds1.setLineWidth(3);
        lds1.setColor(Color.GRAY);
        lds1.setDrawCircles(true);
        //lds1.setDrawCircleHole(true);
        lds1.setCircleColor(Color.DKGRAY);
        lds1.setCircleRadius(4);
        lds1.setValueTextSize(15);
        lds1.setValueTextColor(Color.BLUE);
        //lds1.enableDashedLine();
        dataSets.add(lds1);

        //LEGEND SPECIFICATIONS
        Legend legend = graph.getLegend();
        legend.setEnabled(true);
        //legend.setTextColor(true);
        legend.setTextSize(12);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(5);
        legend.setEnabled(true);

        //SET DESCRIPTION FOR GRAPH
        Description description = new Description();
        description.setText("Dates");
        description.setTextSize(10);
        graph.setDescription(description);

        LineData lineData = new LineData(dataSets);
        graph.setData(lineData);
        graph.animateY(1000);
        graph.invalidate();

        /*
        YAxis leftAxis = graph.getAxisLeft();
        //leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        //leftAxis.setAxisMinimum(0f);
        //leftAxis.setAxisMaximum(30f);

        YAxis rightAxis = graph.getAxisRight();
        //rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        //rightAxis.setAxisMinimum(0f);
        //rightAxis.setAxisMaximum(30f);

        XAxis xAxis = graph.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setDrawGridLines(false);
        //xAxis.setAxisMinimum(0f);
        //xAxis.setLabelCount(5);
        //xAxis.setAxisMaximum(400f);
        //xAxis.setGranularity(1f);
        //xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new DateAxisValueFormatter(null));

         */
    }

    private List<Entry> sortDistances() {

            ArrayList<Entry> lineEntries = new ArrayList<>();
            Intent intent = getIntent();
            ArrayList<? extends Float> userDistances = intent.getParcelableArrayListExtra("user_distances");
            ArrayList<? extends String> userTimes = intent.getParcelableArrayListExtra("user_days");

            for(int i=0;i<userDistances.size();i++) {
                long startDate = 0;
                try {
                    String dateString = userTimes.get(i);
                    Log.d(TAG, "sortDistances: dateString = " + dateString);
                    SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy");
                    Date date = sdf.parse(dateString);
                    Log.d(TAG, "sortDistances: date = " + date);
                    startDate = date.getTime();
                    Log.d(TAG, "sortDistances: startDate = " + startDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                lineEntries.add(new Entry(i, userDistances.get(i)));
            }
        Log.d(TAG, "sortDistances: lineEntries = " + lineEntries);
            return lineEntries;
        }

    class DateAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        public DateAxisValueFormatter(String[] values) {
            this.mValues = values; }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            //creating Date from millisecond
            Date date = new Date((long) value);
            Log.d(TAG, "getFormattedValue: Date =" + date);

            //printing value of Date
            System.out.println("current Date: " + date);

            DateFormat df = new SimpleDateFormat("dd MMM");
            String d = df.format(date);

            //formatted value of current Date
            return d;

        }
    }

   /* private class MyAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float dateInMilliseconds, AxisBase axis) {
            Log.d(TAG, "getFormattedValue: Starting = " + dateInMilliseconds);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
                Log.d(TAG, "getFormattedValue: DATEINMILLISECONDS = " + dateInMilliseconds);
                Date date=new Date((long) dateInMilliseconds);
                String dateText = sdf.format(date);
                return dateText;

                //return sdf.format(new Date(Long.parseLong(String.valueOf(dateInMilliseconds))));
            } catch (Exception e) {
                return String.valueOf(dateInMilliseconds);
            }
        }
    }
   /*public class MyXAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {

       @Override
       public String getXValue(String dateInMillisecons, int index, ViewPortHandler viewPortHandler) {
           try {
               SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");

               return sdf.format(new Date(Long.parseLong(dateInMillisecons)));
           } catch (Exception e) {
               return dateInMillisecons;
           }

       }
   }*/

}