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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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

        //XAxis xAxis = graph.getXAxis();
        YAxis yAxisLeft = graph.getAxisLeft();
        YAxis yAxisRight = graph.getAxisRight();
       // xAxis.setValueFormatter(new MyAxisValueFormatter());
       // xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
       // yAxisRight.setEnabled(false);

        Intent i = getIntent();
        ArrayList<? extends String> userTimes = i.getParcelableArrayListExtra("user_days");

        String[] values = new String[]{
                String.valueOf(userTimes)
        };

        XAxis xAxis = graph.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

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
            Log.d(TAG, "sortDistances: userDistances = " + userDistances);
            Log.d(TAG, "sortDistances: userTimes = " + userTimes);

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


    public class MyXAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }

        /** this is only needed if numbers are returned, else return 0 */
        //@Override
        //public int getDecimalDigits() { return 0; }
    }


        /*private class MyValueFormatter implements IValueFormatter{
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return value + "";
            }
        }
        private class MyAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter{
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "Day " + value;
            }
        }

         */
}