package com.smartmed.app.utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Input validation utility methods.
 */
public final class ValidationUtils {

    private ValidationUtils() {} // Prevent instantiation

    /**
     * Validates email format.
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validates password meets minimum requirements.
     * At least 6 characters.
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    /**
     * Validates passwords match.
     */
    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        return !TextUtils.isEmpty(password) && password.equals(confirmPassword);
    }

    /**
     * Validates name is not empty and has reasonable length.
     */
    public static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && name.trim().length() >= 2;
    }

    /**
     * Validates OTP is exactly 6 digits.
     */
    public static boolean isValidOtp(String otp) {
        return !TextUtils.isEmpty(otp) && otp.length() == Constants.OTP_LENGTH && otp.matches("\\d+");
    }

    /**
     * Validates medicine name.
     */
    public static boolean isValidMedicineName(String name) {
        return !TextUtils.isEmpty(name) && name.trim().length() >= 2;
    }

    /**
     * Validates dosage is a positive number.
     */
    public static boolean isValidDosage(String dosage) {
        if (TextUtils.isEmpty(dosage)) return false;
        try {
            double value = Double.parseDouble(dosage);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates a positive integer.
     */
    public static boolean isPositiveInteger(String value) {
        if (TextUtils.isEmpty(value)) return false;
        try {
            int intValue = Integer.parseInt(value);
            return intValue > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates phone number (basic check).
     */
    public static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && phone.matches("\\+?\\d{10,15}");
    }

    /**
     * Returns a validation error message or null if valid.
     */
    public static String validateRegistration(String name, String email, String password, String confirmPassword) {
        if (!isValidName(name)) return "Please enter your full name";
        if (!isValidEmail(email)) return "Please enter a valid email";
        if (!isValidPassword(password)) return "Password must be at least 6 characters";
        if (!doPasswordsMatch(password, confirmPassword)) return "Passwords do not match";
        return null; // Valid
    }

    /**
     * Returns a validation error message for login or null if valid.
     */
    public static String validateLogin(String email, String password) {
        if (!isValidEmail(email)) return "Please enter a valid email";
        if (TextUtils.isEmpty(password)) return "Please enter your password";
        return null; // Valid
    }

    /**
     * Returns a validation error for medicine form or null if valid.
     */
    public static String validateMedicine(String name, String dosage, String frequency) {
        if (!isValidMedicineName(name)) return "Please enter a valid medicine name";
        if (!isValidDosage(dosage)) return "Please enter a valid dosage";
        if (TextUtils.isEmpty(frequency)) return "Please select a frequency";
        return null; // Valid
    }
}
