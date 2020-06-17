package com.uos.gohome.retrofit2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Route {
    @SerializedName("time")
    @Expose
    private int time;
    @SerializedName("distance")
    @Expose
    private int distance;
    @SerializedName("brief_list")
    @Expose
    private String[] briefList = null;
    @SerializedName("section_time")
    @Expose
    private int[] sectionTime = null;
    @SerializedName("section_distance")
    @Expose
    private int[] sectionDistance = null;
    @SerializedName("points")
    @Expose
    private double[][] points = null;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String[] getBriefList() {
        return briefList;
    }

    public void setBriefList(String[] briefList) {
        this.briefList = briefList;
    }

    public int[] getSectionTime() {
        return sectionTime;
    }

    public void setSectionTime(int[] sectionTime) {
        this.sectionTime = sectionTime;
    }

    public int[] getSectionDistance() {
        return sectionDistance;
    }

    public void setSectionDistance(int[] sectionDistance) {
        this.sectionDistance = sectionDistance;
    }

    public double[][] getPoints() {
        return points;
    }

    public void setPoints(double[][] points) {
        this.points = points;
    }
}