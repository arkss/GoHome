package com.uos.gohome.retrofit2;

import com.google.gson.annotations.SerializedName;

public class PostRouteData {
    @SerializedName("response")
    private String response;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private _Data data;

    public PostRouteData(String response, String message, _Data data) {
        this.response = response;
        this.message = message;
        this.data = data;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public _Data getData() {
        return data;
    }

    public void setData(_Data data) {
        this.data = data;
    }

    public int getProfile() {
        return data.getProfile();
    }

    public int getRoute_Id() {
        return data.getRoute_id();
    }

    class _Data {
        @SerializedName("profile")
        private int profile;
        @SerializedName("route_id")
        private int route_id;

        public _Data(int profile, int route_id) {
            this.profile = profile;
            this.route_id = route_id;
        }

        public int getProfile() {
            return profile;
        }

        public void setProfile(int profile) {
            this.profile = profile;
        }

        public int getRoute_id() {
            return route_id;
        }

        public void setRoute_id(int route_id) {
            this.route_id = route_id;
        }
    }

}
