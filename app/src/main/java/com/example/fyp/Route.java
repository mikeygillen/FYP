package com.example.fyp;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Route {
    protected LatLng startPos, endPos;
    protected String distance, time, pace;
    protected ArrayList<Location> locations = new ArrayList<>();

    public Route(){

    }

    public Route(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public Route(String distance, ArrayList<Location> locations) {
        this.distance = distance;
        this.locations = locations;
    }

    public Route(LatLng startPos, LatLng endPos, String distance, String time, String pace, ArrayList<Location> locations) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.distance = distance;
        this.time = time;
        this.pace = pace;
        this.locations = locations;
    }

    public LatLng getStartPos() {
        return startPos;
    }

    public void setStartPos(LatLng startPos) {
        this.startPos = startPos;
    }

    public LatLng getEndPos() {
        return endPos;
    }

    public void setEndPos(LatLng endPos) {
        this.endPos = endPos;
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