package com.smartmed.app.data.model;

import com.google.gson.annotations.SerializedName;

/** Auth response DTO containing user data and token. */
public class AuthResponse {
    @SerializedName("token")
    private String token;
    @SerializedName("user")
    private User user;
    @SerializedName("message")
    private String message;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
