package com.smartmed.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** AI suggestion response from the FastAPI service. */
public class AiSuggestion {
    @SerializedName("medicineId")
    private String medicineId;

    @SerializedName("medicineName")
    private String medicineName;

    @SerializedName("suggestedPreReminderTime")
    private String suggestedPreReminderTime;

    @SerializedName("suggestedPreReminderMinutes")
    private int suggestedPreReminderMinutes;

    @SerializedName("reason")
    private String reason;

    @SerializedName("confidence")
    private double confidence;

    @SerializedName("suggestions")
    private List<String> suggestions;

    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getSuggestedPreReminderTime() { return suggestedPreReminderTime; }
    public void setSuggestedPreReminderTime(String suggestedPreReminderTime) { this.suggestedPreReminderTime = suggestedPreReminderTime; }

    public int getSuggestedPreReminderMinutes() { return suggestedPreReminderMinutes; }
    public void setSuggestedPreReminderMinutes(int suggestedPreReminderMinutes) { this.suggestedPreReminderMinutes = suggestedPreReminderMinutes; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
}
