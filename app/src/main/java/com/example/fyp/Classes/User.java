package com.example.fyp.Classes;

public class User {

    protected String name, email, height, weight, dob, gender, preferredTime;
    protected float distanceCovered, distanceAvg, paceAvg, totalRunTime;
    protected int totalRuns;

    public User(String name, String email, String height, String weight, String dob, String gender, float distanceCovered, float distanceAvg, float paceAvg, float totalRunTime, int totalRuns) {
        this.name = "";
        this.email = "";
        this.height = "";
        this.weight = "";
        this.dob = "";
        this.gender = "";
        this.distanceCovered = 0;
        this.distanceAvg = 0;
        this.paceAvg = 0;
        this.totalRunTime = 0;
        this.totalRuns = 0;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String height, String weight, String dob, String gender, String preferredTime) {
        this.height = height;
        this.weight = weight;
        this.dob = dob;
        this.gender = gender;
        this.preferredTime = preferredTime;
    }

    public User(String name, String email, String height, String weight, String dob, String gender, String preferredTime, float distanceCovered, float distanceAvg, float paceAvg, int totalRuns) {
        this.name = name;
        this.email = email;
        this.height = height;
        this.weight = weight;
        this.dob = dob;
        this.gender = gender;
        this.preferredTime = preferredTime;
        this.distanceCovered = distanceCovered;
        this.distanceAvg = distanceAvg;
        this.paceAvg = paceAvg;
        this.totalRuns = totalRuns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public float getDistanceCovered() {
        return distanceCovered;
    }

    public void setDistanceCovered(float distanceCovered) { this.distanceCovered = distanceCovered; }

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

    public float getDistanceAvg() {
        return distanceCovered / totalRuns;
    }

    public void setDistanceAvg(float distanceAvg) {
        this.distanceAvg = distanceAvg;
    }

    public float getPaceAvg() {
        return distanceCovered / totalRunTime;
    }

    public void setPaceAvg(float paceAvg) {
        this.paceAvg = paceAvg;
    }

    public float getTotalRunTime() { return totalRunTime; }

    public void setTotalRunTime(float totalRunTime) { this.totalRunTime = totalRunTime; }

    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = totalRuns;
    }

    public void updateDistanceCovered(float distanceWalked) {this.distanceCovered = this.distanceCovered + distanceWalked; }

    public void updateTotalRun() {this.totalRuns = this.totalRuns + 1;  }
}

