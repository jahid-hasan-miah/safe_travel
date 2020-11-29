package com.example.myapplication;

public class Posts {
    public String uid, time, date, postimage, carnumber, accidentspot, profileimage, name;

    public Posts() {

    }

    public Posts(String uid, String time, String date, String postimage, String carnumber, String profileimage, String name, String accidentspot) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postimage = postimage;
        this.carnumber = carnumber;
        this.profileimage = profileimage;
        this.name = name;
        this.accidentspot = accidentspot;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccidentspot() {
        return accidentspot;
    }

    public void setAccidentspot(String accidentspot) {
        this.accidentspot = "Accident Spot: " + accidentspot;
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

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getCarnumber() {
        return carnumber;
    }

    public void setCarnumber(String carnumber) {
        this.carnumber = "Car Number: " + carnumber;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
