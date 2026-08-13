package com.smartmed.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * User model representing a registered user.
 */
public class User {

    @SerializedName("_id")
    private String id;

    @SerializedName("firebaseUid")
    private String firebaseUid;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("createdAt")
    private String createdAt;

    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirebaseUid() { return firebaseUid; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
