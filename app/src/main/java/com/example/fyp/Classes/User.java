package com.example.fyp.Classes;

import java.util.ArrayList;

public class User {

    protected String name, email, height, weight, dob, gender;
    protected double distanceCovered, distanceAvg, paceAvg, totalRunTime;
    protected int totalRuns;
    protected ArrayList<String> following;

    public User() {

    }

    public User(String name, String email, String height, String weight, String dob, String gender, double distanceCovered, double distanceAvg, double paceAvg, double totalRunTime, int totalRuns, ArrayList following) {
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
        this.following = new ArrayList<>();
    }

    public User(String name, String email, double distanceCovered, int totalRuns) {
        this.name = name;
        this.email = email;
        this.distanceCovered = distanceCovered;
        this.totalRuns = totalRuns;
    }

    public User(String height, String weight, String dob, String gender) {
        this.height = height;
        this.weight = weight;
        this.dob = dob;
        this.gender = gender;
    }

    public User(String name, String email, String height, String weight, String dob, String gender, double distanceCovered, double distanceAvg, double paceAvg, int totalRuns) {
        this.name = name;
        this.email = email;
        this.height = height;
        this.weight = weight;
        this.dob = dob;
        this.gender = gender;
        this.distanceCovered = distanceCovered;
        this.distanceAvg = distanceAvg;
        this.paceAvg = paceAvg;
        this.totalRuns = totalRuns;
    }

    public User(String name, double distanceCovered, double distanceAvg, double paceAvg, int totalRuns, String email) {
        this.name = name;
        this.distanceCovered = distanceCovered;
        this.distanceAvg = distanceAvg;
        this.paceAvg = paceAvg;
        this.totalRuns = totalRuns;
        this.email = email;
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

    public double getDistanceCovered() {
        return distanceCovered;
    }

    public void setDistanceCovered(double distanceCovered) { this.distanceCovered = distanceCovered; }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public double getDistanceAvg() {
        return distanceCovered / totalRuns;
    }

    public void setDistanceAvg(double distanceAvg) {
        this.distanceAvg = distanceAvg;
    }

    public double getPaceAvg() {
        return distanceCovered / totalRunTime;
    }

    public void setPaceAvg(double paceAvg) {
        this.paceAvg = paceAvg;
    }

    public double getTotalRunTime() { return totalRunTime; }

    public void setTotalRunTime(double totalRunTime) { this.totalRunTime = totalRunTime; }

    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = totalRuns;
    }

    public void updateDistanceCovered(float distanceWalked) {this.distanceCovered = this.distanceCovered + distanceWalked; }

    public void updateTotalRun() {this.totalRuns = this.totalRuns + 1;  }
}

