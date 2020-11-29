package com.example.myapplication;

public class FriendList {

    public String profileimage, name, email;

    public FriendList() {

    }

    public FriendList(String profileimage, String name, String email) {
        this.profileimage = profileimage;
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
