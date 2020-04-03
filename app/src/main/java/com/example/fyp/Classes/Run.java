package com.example.fyp.Classes;

public class Run {
    protected double pace, distance;
    protected String userId, routeId, duration;

    public Run(double pace, double distance, String duration, String userId, String routeId) {
        this.pace = 0;
        this.distance = 0;
        this.userId = "";
        this.routeId = "";
        this.duration = "";
    }

    public Run() {

    }

    public Run(String duration, double distance, double pace, String routeId) {
        this.pace = pace;
        this.distance = distance;
        this.duration = duration;
        this.routeId = routeId;
    }

    /*public Run(double pace, double distance, String duration, String userId, String routeId) {
        this.pace = pace;
        this.distance = distance;
        this.duration = duration;
        this.userId = userId;
        this.routeId = routeId;
    }*/

    public double getPace() {
        return pace;
    }

    public void setPace(double pace) {
        this.pace = pace;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}


