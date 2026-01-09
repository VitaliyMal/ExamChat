package com.example.examchat;

import com.google.gson.annotations.SerializedName;

public class SimpleResponse {

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
