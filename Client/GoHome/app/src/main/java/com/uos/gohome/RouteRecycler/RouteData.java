package com.uos.gohome.RouteRecycler;

public class RouteData {
    private int img;
    private String stationStart;
    private String stationEnd;

    public RouteData(int img, String stationStart, String stationEnd) {
        this.img = img;
        this.stationStart = stationStart;
        this.stationEnd = stationEnd;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
    public String getStationStart() {
        return stationStart;
    }

    public void setStationStart(String stationStart) {
        this.stationStart = stationStart;
    }

    public String getStationEnd() {
        return stationEnd;
    }

    public void setStationEnd(String stationEnd) {
        this.stationEnd = stationEnd;
    }
}
