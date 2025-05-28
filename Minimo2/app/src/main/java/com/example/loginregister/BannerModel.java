package com.example.loginregister;

public class BannerModel {
    private String url = "";

    public BannerModel() {
    }

    public BannerModel(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "BannerModel{" +
                "url='" + url + '\'' +
                '}';
    }
}
