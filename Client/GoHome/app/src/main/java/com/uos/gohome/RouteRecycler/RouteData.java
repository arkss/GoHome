package com.uos.gohome.RouteRecycler;

public class RouteData {
    private int img;
    private String station;

    public RouteData(int img, String station) {
        this.img = img;
        this.station = station;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }
}
