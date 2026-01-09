package com.example.examchat;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("owner")
    private String owner;

    @SerializedName("text")
    private String text;

    @SerializedName("time")
    private String time;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
