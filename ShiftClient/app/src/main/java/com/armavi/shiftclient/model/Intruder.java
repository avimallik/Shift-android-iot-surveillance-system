package com.armavi.shiftclient.model;

public class Intruder {
    String time, date, image, id;

    public Intruder(){

    }

    public Intruder(String time, String date, String image, String id) {
        this.time = time;
        this.date = date;
        this.image = image;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
