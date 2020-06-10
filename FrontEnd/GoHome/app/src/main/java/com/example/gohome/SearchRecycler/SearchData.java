package com.example.gohome.SearchRecycler;

/* data class */
public class SearchData {
    private String time;
    private String walkingTime;

    public SearchData(String time, String walkingTime) {
        this.time = time;
        this.walkingTime = walkingTime;
    }

    public String getTime() {
        return time;
    }

    public String getWalkingTime() {
        return walkingTime;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWalkingTime(String walkingTime) {
        this.walkingTime = walkingTime;
    }
}