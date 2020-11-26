package com.example.myapplication;

public class trip {

    private  String time;
    private String name,uid;
    public String profileimage;

    public trip() {
    }

    public trip(String time,String uid, String name, String profileimage) {
        this.time = time;
        this.name = name;
        this.uid = uid;
        this.profileimage = profileimage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }
}
