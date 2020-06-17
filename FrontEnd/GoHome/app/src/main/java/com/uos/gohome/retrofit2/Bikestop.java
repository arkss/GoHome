package com.uos.gohome.retrofit2;

import com.skt.Tmap.TMapPoint;

public class Bikestop {
    int rackTotCnt;
    String stationName;
    int parkingBikeTotCnt;
    int shared;
    double stationLatitude;
    double stationLongitude;
    String stationId;
    Object traveltime;

    public TMapPoint getPoint() {
        return new TMapPoint(stationLatitude, stationLongitude);
    }
}
