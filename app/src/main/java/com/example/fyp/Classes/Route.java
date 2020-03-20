package com.example.fyp.Classes;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Route {

    private long routeImage;
    private double distance;
    private ArrayList<LatLng> locations = new ArrayList<>();
    private String userId;

    public Route() {
        this.distance = 0.0;
        this.locations = null;
        this.routeImage = 0;
        this.userId = "";
    }

    public Route(long routeImage, double distance, String userId) {
        this.routeImage = routeImage;
        this.distance = distance;
        this.userId = userId;
    }

    public Route(double distance, ArrayList<LatLng> locations, String userId) {
        this.distance = distance;
        this.locations = locations;
        this.userId = userId;
    }

    public Route( double distance, ArrayList<LatLng> locations, long routeImage,  String userId) {
        this.routeImage = routeImage;
        this.distance = distance;
        this.locations = locations;
        this.userId = userId;
    }

    public long getRouteImage() {
        return routeImage;
    }

    public void setRouteImage(long routeImage) {
        this.routeImage = routeImage;
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