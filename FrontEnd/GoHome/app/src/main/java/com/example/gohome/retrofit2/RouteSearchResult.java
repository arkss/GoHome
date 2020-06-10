package com.example.gohome.retrofit2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteSearchResult {
    @SerializedName("result")
    @Expose
    private int result;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private RouteData data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RouteData getData() {
        return data;
    }

    public void setData(RouteData data) {
        this.data = data;
    }
}

class RouteData {
    @SerializedName("n")
    @Expose
    private int n;
    @SerializedName("routes")
    @Expose
    private Route[] routes = null;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public Route[] getRoutes() {
        return routes;
    }

    public void setRoutes(Route[] routes) {
        this.routes = routes;
    }
}