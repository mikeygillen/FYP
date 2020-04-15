package com.example.fyp.Helper;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;


public class Helper {

    public static final String ACTION_NAME_SPACE = "com.example.fyp";
    public static final String INTENT_EXTRA_RESULT_CODE = "resultCode";
    public static final String INTENT_USER_LAT_LNG = "userLatLng";

    public static AlertDialog displayMessageToUser(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }

    public static ProgressDialog displayProgressDialog(Context context, boolean cancelable, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(cancelable);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public static Intent getIntent(Context context, Class<?> goToActivity) {
        Intent intent = new Intent(context, goToActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    public static int secondToMinuteConverter(long seconds) {
        return (int) seconds / 60;
    }

    public static float meterToMileConverter(float meter) {
        return meter / 1609;
    }

    public static int getNumberOfMilestones(float meter) {
        return (int) meter / 305; // 1000 feet -> 304.8
    }

    public static double calculatePace(long seconds, double distance) {
        return (16.666666667 / (distance/seconds)); // Should return min/Km
    }

    public static long elapsedTime(long startTime) {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    public static String secondToHHMMSS(long secondsCount) {
        long seconds = secondsCount % 60;
        secondsCount -= seconds;
        long minutesCount = secondsCount / 60;
        long minutes = minutesCount % 60;
        minutesCount -= minutes;
        long hoursCount = minutesCount / 60;
        return "" + hoursCount + ":" + minutes + ":" + seconds;
    }


    public static String seekbarPercentageConverter(int progress) {
        if(100 < progress) {
            int percent = (progress-100)/2;
            return "Partner averages " + percent + "% more in this category";
        } else if(progress == 100) {
            return "Partner will run Similar times ";
        } else{
            int percent = (progress-100)/2;
            return "Partner averages " + percent + "% less in this category";
        }
    }
}