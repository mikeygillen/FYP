package com.example.fyp;

public class User {
    protected String name, height, weight, email, dob, gender;
    private long   totalTimeWalk;
    private float   distanceCovered, pace;

    public User(){
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String height, String weight, String dob, String gender) {
        this.height = height;
        this.weight = weight;
        this.dob = dob;
        this.gender = gender;
    }

    public User(String name, String height, String weight, String email, String dob, String gender, long totalTimeWalk, float distanceCovered, float pace) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.totalTimeWalk = totalTimeWalk;
        this.distanceCovered = distanceCovered;
        this.pace = pace;
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

    public long getTotalTimeWalk() {
        return totalTimeWalk;
    }

    public void setTotalTimeWalk(long totalTimeWalk) {
        this.totalTimeWalk = totalTimeWalk;
    }

    public float getDistanceCovered() {
        return distanceCovered;
    }

    public void setDistanceCovered(float distanceCovered) { this.distanceCovered = distanceCovered; }

    public float getPace() {
        return pace;
    }

    public void setPace(float pace) {
        this.pace = pace;
    }

    public void updateDistanceCovered(float distanceWalked) {this.distanceCovered = this.distanceCovered + distanceWalked; }

    public void updateTotalTimeWalk(long timeWalked) { this.totalTimeWalk = this.totalTimeWalk+timeWalked;
    }
}

