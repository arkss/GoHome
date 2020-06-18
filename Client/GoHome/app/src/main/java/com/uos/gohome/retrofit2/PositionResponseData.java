package com.uos.gohome.retrofit2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PositionResponseData {
    @SerializedName("response")
    private String response;
    @SerializedName("data")
    private List<RouteData> data;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<RouteData> getData() {
        return data;
    }

    public void setData(List<RouteData> data) {
        this.data = data;
    }

    public class RouteData {
        @SerializedName("route")
        private Object route;
        @SerializedName("lat")
        private float lat;
        @SerializedName("log")
        private float log;

        public Object getRoute() {
            return route;
        }

        public void setRoute(Object route) {
            this.route = route;
        }

        public float getLat() {
            return lat;
        }

        public void setLat(float lat) {
            this.lat = lat;
        }

        public float getLog() {
            return log;
        }

        public void setLog(float log) {
            this.log = log;
        }
    }
}
