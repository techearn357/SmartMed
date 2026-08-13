package com.smartmed.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Prescription model representing an uploaded prescription.
 */
public class Prescription {

    @SerializedName("_id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("extractedText")
    private String extractedText;

    @SerializedName("extractedMedicines")
    private List<ExtractedMedicine> extractedMedicines;

    @SerializedName("createdAt")
    private String createdAt;

    public Prescription() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getExtractedText() { return extractedText; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }

    public List<ExtractedMedicine> getExtractedMedicines() { return extractedMedicines; }
    public void setExtractedMedicines(List<ExtractedMedicine> extractedMedicines) { this.extractedMedicines = extractedMedicines; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /**
     * Represents a medicine extracted from OCR text.
     */
    public static class ExtractedMedicine {
        @SerializedName("name")
        private String name;

        @SerializedName("dosage")
        private String dosage;

        @SerializedName("frequency")
        private String frequency;

        @SerializedName("duration")
        private String duration;

        @SerializedName("instructions")
        private String instructions;

        public ExtractedMedicine() {}

        public ExtractedMedicine(String name, String dosage, String frequency, String duration, String instructions) {
            this.name = name;
            this.dosage = dosage;
            this.frequency = frequency;
            this.duration = duration;
            this.instructions = instructions;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }

        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }

        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }

        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }
    }
}
