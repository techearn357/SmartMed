package com.smartmed.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Medicine model representing a user's medicine.
 */
public class Medicine {

    @SerializedName("_id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("name")
    private String name;

    @SerializedName("dosage")
    private String dosage;

    @SerializedName("dosageUnit")
    private String dosageUnit;

    @SerializedName("frequency")
    private String frequency;

    @SerializedName("times")
    private List<String> times;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("duration")
    private int duration;

    @SerializedName("tabletsPerDose")
    private int tabletsPerDose;

    @SerializedName("totalTablets")
    private int totalTablets;

    @SerializedName("remainingTablets")
    private int remainingTablets;

    @SerializedName("instructions")
    private String instructions;

    @SerializedName("prescriptionId")
    private String prescriptionId;

    @SerializedName("active")
    private boolean active;

    @SerializedName("createdAt")
    private String createdAt;

    public Medicine() {
        this.active = true;
        this.tabletsPerDose = 1;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getDosageUnit() { return dosageUnit; }
    public void setDosageUnit(String dosageUnit) { this.dosageUnit = dosageUnit; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public List<String> getTimes() { return times; }
    public void setTimes(List<String> times) { this.times = times; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getTabletsPerDose() { return tabletsPerDose; }
    public void setTabletsPerDose(int tabletsPerDose) { this.tabletsPerDose = tabletsPerDose; }

    public int getTotalTablets() { return totalTablets; }
    public void setTotalTablets(int totalTablets) { this.totalTablets = totalTablets; }

    public int getRemainingTablets() { return remainingTablets; }
    public void setRemainingTablets(int remainingTablets) { this.remainingTablets = remainingTablets; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /**
     * Returns formatted dosage string (e.g., "500 mg").
     */
    public String getFormattedDosage() {
        return dosage + " " + (dosageUnit != null ? dosageUnit : "");
    }

    /**
     * Checks if stock is low based on threshold.
     */
    public boolean isLowStock(int threshold) {
        return remainingTablets > 0 && remainingTablets <= threshold;
    }

    /**
     * Returns number of daily doses based on frequency.
     */
    public int getDailyDoseCount() {
        if (times != null) return times.size();
        switch (frequency != null ? frequency : "") {
            case "once_daily": return 1;
            case "twice_daily": return 2;
            case "three_times_daily": return 3;
            case "four_times_daily": return 4;
            default: return 1;
        }
    }
}
