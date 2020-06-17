package com.uos.gohome.retrofit2;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;

    public LoginData(String username, String passward) {
        this.username = username;
        this.password = passward;
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
}
