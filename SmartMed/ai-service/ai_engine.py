"""
AI Engine for SmartMed - Analyzes medication history patterns
and calculates optimal pre-reminder offset times (5-30 mins early)
for users who consistently miss or take doses late.
"""

def generate_personalized_suggestions(user_id: str, medicine_id: str = None):
    # Simulated pattern recognition algorithm based on historical logs
    return [
        {
            "medicineId": medicine_id or "med_001",
            "medicineName": "Aspirin",
            "suggestedPreReminderTime": "07:45",
            "suggestedPreReminderMinutes": 15,
            "reason": "Based on your activity, you take morning doses 15 minutes after the alarm. Shifting reminder to 15 mins earlier will increase adherence.",
            "confidence": 0.88,
            "suggestions": [
                "Set a pre-reminder 15 minutes before 08:00 AM",
                "Keep medicine near breakfast table",
                "Enable secondary caregiver alert for evening doses"
            ]
        }
    ]

def analyze_user_routine(user_id: str, medicine_id: str, history_logs: list):
    late_count = sum(1 for log in history_logs if log.get("status") == "LATE")
    missed_count = sum(1 for log in history_logs if log.get("status") == "MISSED")

    suggested_offset = 0
    reason = "Adherence pattern is optimal."

    if missed_count >= 2:
        suggested_offset = 20
        reason = "Multiple missed doses detected. Suggesting a 20-minute early pre-reminder."
    elif late_count >= 2:
        suggested_offset = 15
        reason = "Consistent delayed dose times detected. Shifting alarm 15 minutes earlier."

    return {
        "userId": user_id,
        "medicineId": medicine_id,
        "suggestedPreReminderMinutes": suggested_offset,
        "reason": reason,
        "confidence": 0.92
    }
