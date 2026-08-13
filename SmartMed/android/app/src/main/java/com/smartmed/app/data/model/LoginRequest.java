package com.smartmed.app.data.model;

import com.google.gson.annotations.SerializedName;

/** Login request DTO. */
public class LoginRequest {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("firebaseToken")
    private String firebaseToken;

    public LoginRequest(String email, String password, String firebaseToken) {
        this.email = email;
        this.password = password;
        this.firebaseToken = firebaseToken;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFirebaseToken() { return firebaseToken; }
}
