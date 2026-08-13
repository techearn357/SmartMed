package com.smartmed.app.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for parsing text extracted from prescription OCR images.
 */
public class OcrParser {

    public static class ParsedPrescription {
        private String name;
        private String dosage;
        private int durationDays;
        private String instructions;

        public ParsedPrescription() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }

        public int getDurationDays() { return durationDays; }
        public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }
    }

    public static ParsedPrescription parseText(String rawText) {
        ParsedPrescription parsed = new ParsedPrescription();
        if (rawText == null || rawText.trim().isEmpty()) {
            return parsed;
        }

        String[] lines = rawText.split("\n");
        if (lines.length > 0) {
            parsed.setName(lines[0].trim());
        }

        Pattern dosagePattern = Pattern.compile("(\\d+\\s*(?:mg|ml|g|tablets?|capsules?))", Pattern.CASE_INSENSITIVE);
        Matcher dosageMatcher = dosagePattern.matcher(rawText);
        if (dosageMatcher.find()) {
            parsed.setDosage(dosageMatcher.group(1));
        }

        Pattern durationPattern = Pattern.compile("(\\d+)\\s*(?:days?|weeks?|months?)", Pattern.CASE_INSENSITIVE);
        Matcher durationMatcher = durationPattern.matcher(rawText);
        if (durationMatcher.find()) {
            try {
                parsed.setDurationDays(Integer.parseInt(durationMatcher.group(1)));
            } catch (NumberFormatException ignored) {}
        } else {
            parsed.setDurationDays(7);
        }

        parsed.setInstructions("Take as prescribed");
        return parsed;
    }
}
