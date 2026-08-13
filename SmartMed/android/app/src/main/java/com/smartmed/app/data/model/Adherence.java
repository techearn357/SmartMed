package com.smartmed.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Adherence summary model for medication tracking statistics.
 */
public class Adherence {

    @SerializedName("_id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("date")
    private String date;

    @SerializedName("totalDoses")
    private int totalDoses;

    @SerializedName("takenDoses")
    private int takenDoses;

    @SerializedName("missedDoses")
    private int missedDoses;

    @SerializedName("lateDoses")
    private int lateDoses;

    @SerializedName("adherencePercentage")
    private double adherencePercentage;

    @SerializedName("currentStreak")
    private int currentStreak;

    public Adherence() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getTotalDoses() { return totalDoses; }
    public void setTotalDoses(int totalDoses) { this.totalDoses = totalDoses; }

    public int getTakenDoses() { return takenDoses; }
    public void setTakenDoses(int takenDoses) { this.takenDoses = takenDoses; }

    public int getMissedDoses() { return missedDoses; }
    public void setMissedDoses(int missedDoses) { this.missedDoses = missedDoses; }

    public int getLateDoses() { return lateDoses; }
    public void setLateDoses(int lateDoses) { this.lateDoses = lateDoses; }

    public double getAdherencePercentage() { return adherencePercentage; }
    public void setAdherencePercentage(double adherencePercentage) { this.adherencePercentage = adherencePercentage; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    /**
     * Calculates adherence percentage from taken and total doses.
     */
    public double calculateAdherence() {
        if (totalDoses == 0) return 100.0;
        return (double) takenDoses / totalDoses * 100.0;
    }
}
