package com.example.examchat;

import com.google.gson.annotations.SerializedName;

public class AuthRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("login")
    private String login;

    @SerializedName("password")
    private String password;

    public AuthRequest(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
