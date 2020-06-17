
package com.uos.gohome.retrofit2;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Section {

    @SerializedName("time")
    @Expose
    private int time;

    @SerializedName("distance")
    @Expose
    private int distance;

    @SerializedName("points")
    @Expose
    private List<List<Double>> points = null;

    @SerializedName("stationNameStart")
    @Expose
    private String stationNameStart;

    @SerializedName("stationNameEnd")
    @Expose
    private String stationNameEnd;

    @SerializedName("stationLatitudeStart")
    @Expose
    private double stationLatitudeStart;

    @SerializedName("stationLongitudeStart")
    @Expose
    private double stationLongitudeStart;

    @SerializedName("stationLatitudeEnd")
    @Expose
    private double stationLatitudeEnd;

    @SerializedName("stationLongitudeEnd")
    @Expose
    private double stationLongitudeEnd;

    @SerializedName("type")
    @Expose
    private int type;

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

    public List<List<Double>> getPoints() {
        return points;
    }

    public void setPoints(List<List<Double>> points) {
        this.points = points;
    }

    public String getStationNameStart() {
        return stationNameStart;
    }

    public void setStationNameStart(String stationNameStart) {
        this.stationNameStart = stationNameStart;
    }

    public String getStationNameEnd() {
        return stationNameEnd;
    }

    public void setStationNameEnd(String stationNameEnd) {
        this.stationNameEnd = stationNameEnd;
    }

    public double getStationLatitudeStart() {
        return stationLatitudeStart;
    }

    public void setStationLatitudeStart(double stationLatitudeStart) {
        this.stationLatitudeStart = stationLatitudeStart;
    }

    public double getStationLongitudeStart() {
        return stationLongitudeStart;
    }

    public void setStationLongitudeStart(double stationLongitudeStart) {
        this.stationLongitudeStart = stationLongitudeStart;
    }

    public double getStationLatitudeEnd() {
        return stationLatitudeEnd;
    }

    public void setStationLatitudeEnd(double stationLatitudeEnd) {
        this.stationLatitudeEnd = stationLatitudeEnd;
    }

    public double getStationLongitudeEnd() {
        return stationLongitudeEnd;
    }

    public void setStationLongitudeEnd(double stationLongitudeEnd) {
        this.stationLongitudeEnd = stationLongitudeEnd;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
