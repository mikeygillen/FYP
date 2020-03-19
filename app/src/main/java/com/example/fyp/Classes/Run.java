package com.example.fyp.Classes;

public class Run {
    protected double pace;
    protected String userId, routeId, time;

    public Run() {
    }

    public Run(String time, double pace, String userId, String routeId) {
        this.time = time;
        this.pace = pace;
        this.userId = userId;
        this.routeId = routeId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getPace() {
        return pace;
    }

    public void setPace(double pace) {
        this.pace = pace;
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


