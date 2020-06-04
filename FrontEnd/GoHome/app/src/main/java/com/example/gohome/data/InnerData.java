package com.example.gohome.data;

/* data class */
public class InnerData {
    int imgResource;
    String transName;
    public InnerData(int imgResource, String transName) {
        this.imgResource = imgResource;
        this.transName = transName;
    }

    int getImgResource() {
        return imgResource;
    }
    String getTransName() {
        return transName;
    }
}