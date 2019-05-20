package com.example.loginregistration.home;

public class HomeImageModel {
    private String images;
    private String video;

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "HomeImageModel{" +
                "images='" + images + '\'' +
                ", video='" + video + '\'' +
                '}';
    }
}
