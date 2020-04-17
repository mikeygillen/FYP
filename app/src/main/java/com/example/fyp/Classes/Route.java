package com.example.fyp.Classes;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Route {

    private double distance;
    private ArrayList<LatLng> locations = new ArrayList<>();
    private String userId, createdOn, start, end;

    public Route() {
        this.distance = 0.0;
        this.locations = null;
        this.userId = "";
        this.createdOn = "";
    }

    public Route(double distance, String userId, String createdOn) {
        this.distance = distance;
        this.userId = userId;
        this.createdOn = createdOn;
    }

    public Route(double distance, ArrayList<LatLng> locations, String userId, String createdOn) {
        this.distance = distance;
        this.locations = locations;
        this.userId = userId;
        this.createdOn = createdOn;
    }

    public Route(double distance, ArrayList<LatLng> routePoints, String user, String created, String start, String end) {
        this.distance = distance;
        this.locations = routePoints;
        this.userId = user;
        this.createdOn = created;
        this.start = start;
        this.end = end;
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

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}