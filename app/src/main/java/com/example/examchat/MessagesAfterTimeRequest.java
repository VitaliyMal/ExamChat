package com.example.examchat;

import com.google.gson.annotations.SerializedName;

public class MessagesAfterTimeRequest {

    @SerializedName("login")
    private String login;

    @SerializedName("time")
    private String time;

    public MessagesAfterTimeRequest(String login, String time) {
        this.login = login;
        this.time = time;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
