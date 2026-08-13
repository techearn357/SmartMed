package com.smartmed.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Schedule model representing a medicine reminder schedule.
 */
public class Schedule {

    @SerializedName("_id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("medicineId")
    private String medicineId;

    @SerializedName("medicineName")
    private String medicineName;

    @SerializedName("medicineDosage")
    private String medicineDosage;

    @SerializedName("scheduledTime")
    private String scheduledTime;

    @SerializedName("repeatPattern")
    private String repeatPattern;

    @SerializedName("remainingTablets")
    private int remainingTablets;

    @SerializedName("isTomorrow")
    private boolean isTomorrow;

    @SerializedName("active")
    private boolean active;

    public Schedule() {
        this.active = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getMedicineDosage() { return medicineDosage; }
    public void setMedicineDosage(String medicineDosage) { this.medicineDosage = medicineDosage; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getRepeatPattern() { return repeatPattern; }
    public void setRepeatPattern(String repeatPattern) { this.repeatPattern = repeatPattern; }

    public int getRemainingTablets() { return remainingTablets; }
    public void setRemainingTablets(int remainingTablets) { this.remainingTablets = remainingTablets; }

    public boolean isTomorrow() { return isTomorrow; }
    public void setTomorrow(boolean tomorrow) { isTomorrow = tomorrow; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}


