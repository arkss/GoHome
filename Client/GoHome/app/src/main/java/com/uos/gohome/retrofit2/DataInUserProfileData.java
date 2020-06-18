package com.uos.gohome.retrofit2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataInUserProfileData {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("detail_address")
    @Expose
    private String detailAddress;
    @SerializedName("address_lat")
    @Expose
    private double addressLat;
    @SerializedName("address_log")
    @Expose
    private double addressLog;

    /**
     * No args constructor for use in serialization
     *
     */
    public DataInUserProfileData() {
    }

    /**
     *
     * @param address
     * @param addressLat
     * @param nickname
     * @param detailAddress
     * @param email
     * @param addressLog
     * @param username
     */
    public DataInUserProfileData(String username, String email, String nickname, String address, String detailAddress, double addressLat, double addressLog) {
        super();
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.address = address;
        this.detailAddress = detailAddress;
        this.addressLat = addressLat;
        this.addressLog = addressLog;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public double getAddressLat() {
        return addressLat;
    }

    public void setAddressLat(double addressLat) {
        this.addressLat = addressLat;
    }

    public double getAddressLog() {
        return addressLog;
    }

    public void setAddressLog(double addressLog) {
        this.addressLog = addressLog;
    }

}