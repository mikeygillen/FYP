package com.example.fyp.Classes;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
public class Route {
    protected double distance;
    protected ArrayList<LatLng> locations = new ArrayList<>();

    private String userId;

    public Route(double distance, ArrayList<LatLng> locations, String userId) {
        this.distance = distance;
        this.locations = locations;
        this.userId = userId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public ArrayList<LatLng> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<LatLng> locations) {
        this.locations = locations;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}