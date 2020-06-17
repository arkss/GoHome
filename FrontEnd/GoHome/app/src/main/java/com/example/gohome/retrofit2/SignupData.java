package com.example.gohome.retrofit2;

import com.google.gson.annotations.SerializedName;

public class SignupData {
    @SerializedName("profile")
    private SignupProfile profile;

    public SignupData(String username, String password, String email, String nickname) {
        this.profile = new SignupProfile(username, password, email, nickname);
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

        public SignupProfile(String username, String password, String email, String nickname) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.nickname = nickname;
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
    }
}
