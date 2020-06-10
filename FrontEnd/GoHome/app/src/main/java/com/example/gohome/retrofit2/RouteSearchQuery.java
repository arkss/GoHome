package com.example.gohome.retrofit2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteSearchQuery {

    @SerializedName("lat_start")
    @Expose
    private double latStart;
    @SerializedName("lon_start")
    @Expose
    private double lonStart;
    @SerializedName("lat_end")
    @Expose
    private double latEnd;
    @SerializedName("lon_end")
    @Expose
    private double lonEnd;
    @SerializedName("include_bike")
    @Expose
    private String includeBike;
    @SerializedName("include_bus")
    @Expose
    private String includeBus;

    public RouteSearchQuery(double latStart, double lonStart, double latEnd, double lonEnd, String includeBike, String includeBus) {
        this.latStart = latStart;
        this.lonStart = lonStart;
        this.latEnd = latEnd;
        this.lonEnd = lonEnd;
        this.includeBike = includeBike;
        this.includeBus = includeBus;
    }

    public double getLatStart() {
        return latStart;
    }

    public void setLatStart(double latStart) {
        this.latStart = latStart;
    }

    public double getLonStart() {
        return lonStart;
    }

    public void setLonStart(double lonStart) {
        this.lonStart = lonStart;
    }

    public double getLatEnd() {
        return latEnd;
    }

    public void setLatEnd(double latEnd) {
        this.latEnd = latEnd;
    }

    public double getLonEnd() {
        return lonEnd;
    }

    public void setLonEnd(double lonEnd) {
        this.lonEnd = lonEnd;
    }

    public String getIncludeBike() {
        return includeBike;
    }

    public void setIncludeBike(String includeBike) {
        this.includeBike = includeBike;
    }

    public String getIncludeBus() {
        return includeBus;
    }

    public void setIncludeBus(String includeBus) {
        this.includeBus = includeBus;
    }

}