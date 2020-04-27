package com.example.fyp.Classes;

public class Run {
    protected double pace, distance, calories;
    protected String userId, routeId, duration, createdOn;

    public Run(double pace, double distance, double calories, String duration, String userId, String routeId, String createdOn) {
        this.pace = 0;
        this.distance = 0;
        this.calories = 0;
        this.userId = "";
        this.routeId = "";
        this.duration = "";
        this.createdOn = "";
    }

    public Run() {

    }

    public Run(String duration, double distance, double pace, String routeId, String userId, String createdOn) {
        this.duration = duration;
        this.distance = distance;
        this.pace = pace;
        this.routeId = routeId;
        this.userId = userId;
        this.createdOn = createdOn;
    }


    public Run(String duration, double distance, double pace, String userId, String createdOn) {
        this.pace = pace;
        this.distance = distance;
        this.duration = duration;
        this.userId = userId;
        this.createdOn = createdOn;
    }

    public Run(String duration, double distance, double pace, double calories, String routeId, String userId, String createdOn) {
        this.duration = duration;
        this.distance = distance;
        this.pace = pace;
        this.calories = calories;
        this.routeId = routeId;
        this.userId = userId;
        this.createdOn = createdOn;
    }

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

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
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

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}


