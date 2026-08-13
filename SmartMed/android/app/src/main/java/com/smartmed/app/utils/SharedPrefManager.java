package com.smartmed.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences wrapper for easy access to stored preferences.
 * Handles auth tokens, user info, and app settings.
 */
public class SharedPrefManager {

    private static SharedPrefManager instance;
    private final SharedPreferences prefs;

    private SharedPrefManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
    }

    public static synchronized SharedPrefManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SharedPrefManager not initialized. Call init() first.");
        }
        return instance;
    }

    // ========================= Auth =========================

    public void saveAuthToken(String token) {
        prefs.edit().putString(Constants.PREF_AUTH_TOKEN, token).apply();
    }

    public String getAuthToken() {
        return prefs.getString(Constants.PREF_AUTH_TOKEN, null);
    }

    public void saveUserId(String userId) {
        prefs.edit().putString(Constants.PREF_USER_ID, userId).apply();
    }

    public String getUserId() {
        return prefs.getString(Constants.PREF_USER_ID, null);
    }

    public void saveUserName(String name) {
        prefs.edit().putString(Constants.PREF_USER_NAME, name).apply();
    }

    public String getUserName() {
        return prefs.getString(Constants.PREF_USER_NAME, "User");
    }

    public void saveUserEmail(String email) {
        prefs.edit().putString(Constants.PREF_USER_EMAIL, email).apply();
    }

    public String getUserEmail() {
        return prefs.getString(Constants.PREF_USER_EMAIL, "");
    }

    public void saveFirebaseUid(String uid) {
        prefs.edit().putString(Constants.PREF_FIREBASE_UID, uid).apply();
    }

    public String getFirebaseUid() {
        return prefs.getString(Constants.PREF_FIREBASE_UID, null);
    }

    public void setLoggedIn(boolean loggedIn) {
        prefs.edit().putBoolean(Constants.PREF_IS_LOGGED_IN, loggedIn).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.PREF_IS_LOGGED_IN, false);
    }

    public void saveFcmToken(String token) {
        prefs.edit().putString(Constants.PREF_FCM_TOKEN, token).apply();
    }

    public String getFcmToken() {
        return prefs.getString(Constants.PREF_FCM_TOKEN, null);
    }

    // ========================= Settings =========================

    public int getSnoozeDuration() {
        return prefs.getInt(Constants.PREF_SNOOZE_DURATION, Constants.DEFAULT_SNOOZE_DURATION);
    }

    public void setSnoozeDuration(int minutes) {
        prefs.edit().putInt(Constants.PREF_SNOOZE_DURATION, minutes).apply();
    }

    public int getLowStockThreshold() {
        return prefs.getInt(Constants.PREF_LOW_STOCK_THRESHOLD, Constants.DEFAULT_LOW_STOCK_THRESHOLD);
    }

    public void setLowStockThreshold(int threshold) {
        prefs.edit().putInt(Constants.PREF_LOW_STOCK_THRESHOLD, threshold).apply();
    }

    public int getMissedDoseThreshold() {
        return prefs.getInt(Constants.PREF_MISSED_DOSE_THRESHOLD, Constants.DEFAULT_MISSED_DOSE_THRESHOLD);
    }

    public void setMissedDoseThreshold(int threshold) {
        prefs.edit().putInt(Constants.PREF_MISSED_DOSE_THRESHOLD, threshold).apply();
    }

    public boolean isAiRemindersEnabled() {
        return prefs.getBoolean(Constants.PREF_AI_REMINDERS_ENABLED, true);
    }

    public void setAiRemindersEnabled(boolean enabled) {
        prefs.edit().putBoolean(Constants.PREF_AI_REMINDERS_ENABLED, enabled).apply();
    }

    public boolean isCaregiverAlertsEnabled() {
        return prefs.getBoolean(Constants.PREF_CAREGIVER_ALERTS_ENABLED, true);
    }

    public boolean areCaregiverAlertsEnabled() {
        return isCaregiverAlertsEnabled();
    }

    public void setCaregiverAlertsEnabled(boolean enabled) {
        prefs.edit().putBoolean(Constants.PREF_CAREGIVER_ALERTS_ENABLED, enabled).apply();
    }

    public boolean isSoundEnabled() {
        return prefs.getBoolean("sound_enabled", true);
    }

    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply();
    }

    public boolean isVibrationEnabled() {
        return prefs.getBoolean("vibration_enabled", true);
    }

    public void setVibrationEnabled(boolean enabled) {
        prefs.edit().putBoolean("vibration_enabled", enabled).apply();
    }

    public boolean areNotificationsEnabled() {
        return prefs.getBoolean("notifications_enabled", true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply();
    }

    public int getSnoozeInterval() {
        return getSnoozeDuration();
    }

    public void setSnoozeInterval(int minutes) {
        setSnoozeDuration(minutes);
    }

    // ========================= Session =========================

    /**
     * Saves complete user session after login.
     */
    public void saveUserSession(String userId, String name, String email, String firebaseUid, String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PREF_USER_ID, userId);
        editor.putString(Constants.PREF_USER_NAME, name);
        editor.putString(Constants.PREF_USER_EMAIL, email);
        editor.putString(Constants.PREF_FIREBASE_UID, firebaseUid);
        editor.putString(Constants.PREF_AUTH_TOKEN, token);
        editor.putBoolean(Constants.PREF_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Clears all user data on logout.
     */
    public void clearSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.PREF_AUTH_TOKEN);
        editor.remove(Constants.PREF_USER_ID);
        editor.remove(Constants.PREF_USER_NAME);
        editor.remove(Constants.PREF_USER_EMAIL);
        editor.remove(Constants.PREF_FIREBASE_UID);
        editor.putBoolean(Constants.PREF_IS_LOGGED_IN, false);
        editor.apply();
    }
}
