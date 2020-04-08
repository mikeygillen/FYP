package com.example.fyp.Graphs;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fyp.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class LineChartActivity extends AppCompatActivity {

    private static final String TAG = "BarChartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        GraphView graph = findViewById(R.id.lineChart);

        /*
        LineGraphSeries<DataPoint> series = null;
        Intent intent = getIntent();
        if (intent.hasExtra("user_distances")){
            series = new LineGraphSeries<>(sortDistances());
            //pds = new PieDataSet(sortDistances(), "Distances Covered");
        }else if (intent.hasExtra("user_pace")){
            series = new LineGraphSeries<>(sortPace());
           // pds = new PieDataSet(sortPace(), "Pace of Runs");
        }else if (intent.hasExtra("user_times")){
            series = new LineGraphSeries<>(sortTime());
           // pds = new PieDataSet(sortTime(), "Duration of Runs");
        }else if (intent.hasExtra("user_days")){
            series = new LineGraphSeries<>(sortDays());
           // pds = new PieDataSet(sortDays(), "Days of Runs");
        }
         */


        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(getDataPoint());
        graph.addSeries(series);

        series.setColor(Color.rgb(0,0,100));
        series.setThickness(10);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.rgb(0,100,0));
        series.setDrawDataPoints(true);
    }

    /*
    private DataPoint[] sortDays() {
    }

    private DataPoint[] sortTime() {
    }

    private DataPoint[] sortPace() {
    }*/

    private DataPoint[] getDataPoint() {

        DataPoint[] dp = new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(2, 5),
                new DataPoint(3, 1),
                new DataPoint(5, 6),
                new DataPoint(8, 3)};
        return (dp);
    }



    /*private DataPoint[] sortDistances(){

        Intent intent = getIntent();
        ArrayList<? extends Float> userDistances = intent.getParcelableArrayListExtra("user_distances");
        int userDisSize = userDistances.size();

        DataPoint[] dataPoints = new DataPoint[userDisSize];
        for (int i=0; i<userDisSize; i++){

        }

        return dataPoints;
    }

    /*SerializableList<MoneyMeterData> moneyMeterDataList = MoneyMeterListService.getInstance().GetActiveMoneyMeter().GetMoneyMeterDataList();
    int moneyMeterDataListSize = moneyMeterDataList.getSize();
    DataPoint[] dataPoints = new DataPoint[moneyMeterDataListSize];

    Date firstDate = new Date();
    Date lastDate = new Date();

    for (int index = 0; index < moneyMeterDataListSize; index++) {
        SerializableDate saveDate = moneyMeterDataList.getValue(index).GetSaveDate();
        Date date = new Date(saveDate.Year(), saveDate.Month() - 1, saveDate.DayOfMonth());
        dataPoints[index] = new DataPoint(date, moneyMeterDataList.getValue(index).GetAmount());
        if (index == 0) {
            firstDate = date;
        } else if (index == moneyMeterDataListSize - 1) {
            lastDate = date;
        }
    }*/

}