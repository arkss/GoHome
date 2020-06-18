package com.uos.gohome.retrofit2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileData {

    @SerializedName("response")
    @Expose
    private String response;
    @SerializedName("data")
    @Expose
    private DataInUserProfileData data;

    /**
     * No args constructor for use in serialization
     *
     */
    public UserProfileData() {
    }

    /**
     *
     * @param data
     * @param response
     */
    public UserProfileData(String response, DataInUserProfileData data) {
        super();
        this.response = response;
        this.data = data;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public DataInUserProfileData getData() {
        return data;
    }

    public void setData(DataInUserProfileData data) {
        this.data = data;
    }

}