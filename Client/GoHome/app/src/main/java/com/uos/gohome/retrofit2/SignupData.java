package com.uos.gohome.retrofit2;

import com.google.gson.annotations.SerializedName;

public class SignupData {
    @SerializedName("profile")
    private SignupProfile profile;

    public SignupData(String username, String password, String email, String nickname, String address, String detail_address, double address_lat, double address_log) {
        this.profile = new SignupProfile(username, password, email, nickname, address, detail_address, address_lat, address_log);
    }

    public SignupProfile getProfile() {
        return profile;
    }

    public void setProfile(SignupProfile profile) {
        this.profile = profile;
    }

    private class SignupProfile {
        @SerializedName("username")
        private String username;
        @SerializedName("password")
        private String password;
        @SerializedName("email")
        private String email;
        @SerializedName("nickname")
        private String nickname;
        @SerializedName("address")
        private String address;
        @SerializedName("detail_address")
        private String detail_address;
        @SerializedName("address_lat")
        private double address_lat;
        @SerializedName("address_log")
        private double address_log;

        public SignupProfile(String username, String password, String email, String nickname, String address, String detail_address, double address_lat, double address_log) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.nickname = nickname;
            this.address = address;
            this.detail_address = detail_address;
            this.address_lat = address_lat;
            this.address_log = address_log;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAddress() {
            return address;
        }

        public String getDetail_address() {
            return detail_address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setDetail_address(String detail_address) {
            this.detail_address = detail_address;
        }

        public double getAddress_lat() {
            return address_lat;
        }

        public void setAddress_lat(double address_lat) {
            this.address_lat = address_lat;
        }

        public double getaddress_log() {
            return address_log;
        }

        public void setaddress_log(double address_log) {
            this.address_log = address_log;
        }
    }
}
