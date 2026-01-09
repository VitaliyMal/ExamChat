package com.example.examchat;

import com.google.gson.annotations.SerializedName;

public class MessageRequest {

    @SerializedName("owner")
    private String owner;

    @SerializedName("text")
    private String text;

    public MessageRequest(String owner, String text) {
        this.owner = owner;
        this.text = text;
    }

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
}
