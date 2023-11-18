package com.example.iat359_final_project;

public class LogModel {
    private int id;
    private double distance;
    private String location;
    private int steps;

    public LogModel(int id, double distance, String location, int steps) {
        this.id = id;
        this.distance = distance;
        this.location = location;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}

