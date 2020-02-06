package com.example.fyp;

import android.location.Location;

import java.util.ArrayList;

public class Route {
    protected String distance, time, pace;
    protected ArrayList<Location> locations = new ArrayList<>();

    public Route(){

    }

    public Route(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public Route(String distance, String time, String pace) {
        this.distance = distance;
        this.time = time;
        this.pace = pace;
    }

    public Route(String distance, String time, String pace, ArrayList<Location> locations) {
        this.distance = distance;
        this.time = time;
        this.pace = pace;
        this.locations = locations;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }
}