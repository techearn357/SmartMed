package com.smartmed.app.utils;

/**
 * Application-wide constants.
 */
public final class Constants {

    private Constants() {} // Prevent instantiation

    // API Base URLs - Deployed on Render
    public static final String BASE_URL = "https://smartmed-api.onrender.com/api/";
    public static final String AI_BASE_URL = "https://smartmed-mdml.onrender.com/";

    // SharedPreferences Keys
    public static final String PREF_NAME = "smartmed_prefs";
    public static final String PREF_AUTH_TOKEN = "auth_token";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USER_NAME = "user_name";
    public static final String PREF_USER_EMAIL = "user_email";
    public static final String PREF_FIREBASE_UID = "firebase_uid";
    public static final String PREF_IS_LOGGED_IN = "is_logged_in";
    public static final String PREF_FCM_TOKEN = "fcm_token";

    // Settings Preferences
    public static final String PREF_SNOOZE_DURATION = "snooze_duration";
    public static final String PREF_LOW_STOCK_THRESHOLD = "low_stock_threshold";
    public static final String PREF_MISSED_DOSE_THRESHOLD = "missed_dose_threshold";
    public static final String PREF_AI_REMINDERS_ENABLED = "ai_reminders_enabled";
    public static final String PREF_CAREGIVER_ALERTS_ENABLED = "caregiver_alerts_enabled";

    // Default Settings Values
    public static final int DEFAULT_SNOOZE_DURATION = 10; // minutes
    public static final int DEFAULT_LOW_STOCK_THRESHOLD = 5; // tablets
    public static final int DEFAULT_MISSED_DOSE_THRESHOLD = 3; // consecutive misses

    // OTP
    public static final int OTP_LENGTH = 6;
    public static final int OTP_RESEND_COOLDOWN = 45; // seconds
    public static final int OTP_EXPIRY_MINUTES = 5;
    public static final int OTP_MAX_ATTEMPTS = 5;

    // Notification Channels & Actions
    public static final String CHANNEL_MEDICINE_REMINDER = "medicine_reminder_channel";
    public static final String CHANNEL_LOW_STOCK = "low_stock_channel";
    public static final String CHANNEL_CAREGIVER_ALERT = "caregiver_alert_channel";

    public static final String ACTION_TAKEN = "com.smartmed.app.ACTION_TAKEN";
    public static final String ACTION_SNOOZE = "com.smartmed.app.ACTION_SNOOZE";
    public static final String ACTION_MISSED = "com.smartmed.app.ACTION_MISSED";

    // Intent Extras
    public static final String EXTRA_MEDICINE_ID = "medicine_id";
    public static final String EXTRA_MEDICINE_NAME = "medicine_name";
    public static final String EXTRA_MEDICINE_DOSAGE = "medicine_dosage";
    public static final String EXTRA_SCHEDULE_ID = "schedule_id";
    public static final String EXTRA_SCHEDULED_TIME = "scheduled_time";
    public static final String EXTRA_ALARM_TIME = "alarm_time";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    public static final String EXTRA_IS_SNOOZED = "is_snoozed";
    public static final String EXTRA_RAW_OCR_TEXT = "raw_ocr_text";
    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_OTP_PURPOSE = "otp_purpose";
    public static final String EXTRA_PRESCRIPTION_IMAGE_URI = "prescription_image_uri";
    public static final String EXTRA_OCR_RESULT = "ocr_result";

    // OTP Purposes
    public static final String PURPOSE_REGISTRATION = "registration";
    public static final String PURPOSE_PASSWORD_RESET = "password_reset";

    // Alarm Request Codes
    public static final int ALARM_REQUEST_CODE_BASE = 10000;

    // Notification IDs
    public static final int NOTIFICATION_REMINDER_BASE = 20000;
    public static final int NOTIFICATION_CAREGIVER_ALERT = 30000;
    public static final int NOTIFICATION_LOW_STOCK = 30001;

    // Medicine Status
    public static final String STATUS_TAKEN = "TAKEN";
    public static final String STATUS_MISSED = "MISSED";
    public static final String STATUS_SNOOZED = "SNOOZED";
    public static final String STATUS_LATE = "LATE";
    public static final String STATUS_PENDING = "PENDING";

    // Frequency
    public static final String FREQ_ONCE_DAILY = "once_daily";
    public static final String FREQ_TWICE_DAILY = "twice_daily";
    public static final String FREQ_THREE_TIMES = "three_times_daily";
    public static final String FREQ_FOUR_TIMES = "four_times_daily";
    public static final String FREQ_AS_NEEDED = "as_needed";
    public static final String FREQ_CUSTOM = "custom";

    // Adherence Thresholds
    public static final int ADHERENCE_EXCELLENT = 90;
    public static final int ADHERENCE_GOOD = 75;
    public static final int ADHERENCE_AVERAGE = 50;

    // Late Threshold (minutes after scheduled time)
    public static final int LATE_THRESHOLD_MINUTES = 30;

    // Date Formats
    public static final String DATE_FORMAT_DISPLAY = "MMM dd, yyyy";
    public static final String DATE_FORMAT_API = "yyyy-MM-dd";
    public static final String TIME_FORMAT_DISPLAY = "hh:mm a";
    public static final String TIME_FORMAT_24H = "HH:mm";
    public static final String DATETIME_FORMAT_DISPLAY = "MMM dd, yyyy hh:mm a";
    public static final String DATETIME_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
}
