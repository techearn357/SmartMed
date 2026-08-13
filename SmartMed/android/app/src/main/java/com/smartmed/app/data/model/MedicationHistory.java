package com.smartmed.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Medication history entry model.
 */
public class MedicationHistory {

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

    @SerializedName("takenTime")
    private String takenTime;

    @SerializedName("status")
    private String status;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("date")
    private String date;

    public MedicationHistory() {}

    // Getters and Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

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

    public String getTakenTime() { return takenTime; }
    public void setTakenTime(String takenTime) { this.takenTime = takenTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
