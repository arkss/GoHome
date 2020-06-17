package com.uos.gohome.retrofit2;

import com.google.gson.annotations.SerializedName;

public class BikestopData {
    @SerializedName("result")
    private int result;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private Data data;

    public BikestopData(int result, String message, Data data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public Bikestop[] getBikestops() { return data.bikestops; }
}

class Data {
    int n;
    Bikestop[] bikestops;
}

