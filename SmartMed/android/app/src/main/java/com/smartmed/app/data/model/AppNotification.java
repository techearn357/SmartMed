package com.smartmed.app.data.model;

import com.google.gson.annotations.SerializedName;

/** App notification model. */
public class AppNotification {
    @SerializedName("_id")
    private String id;
    @SerializedName("userId")
    private String userId;
    @SerializedName("type")
    private String type;
    @SerializedName("title")
    private String title;
    @SerializedName("message")
    private String message;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("read")
    private boolean read;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
