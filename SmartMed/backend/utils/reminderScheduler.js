const Schedule = require('../models/Schedule');
const Medicine = require('../models/Medicine');
const MedicationHistory = require('../models/MedicationHistory');
const User = require('../models/User');
const Caregiver = require('../models/Caregiver');
const { sendEmail } = require('./emailService');

const TIMEZONE = 'Asia/Kolkata';

/**
 * Returns formatted date string "YYYY-MM-DD" for a given Date in IST.
 */
const getIstDateString = (dateObj) => {
    return new Intl.DateTimeFormat('en-CA', {
        timeZone: TIMEZONE,
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    }).format(dateObj);
};

/**
 * Parses time string (e.g. "08:00 AM", "8:00 PM", "06:30", "21:33") into hours and minutes.
 */
const parseTimeString = (timeStr) => {
    if (!timeStr) return { hours: 8, minutes: 0 };
    let s = timeStr.trim().toUpperCase();
    let isPM = s.includes('PM');
    let isAM = s.includes('AM');
    s = s.replace('AM', '').replace('PM', '').trim();
    const parts = s.split(':');
    let hours = parseInt(parts[0], 10) || 0;
    let minutes = parseInt(parts[1], 10) || 0;
    if (isPM && hours < 12) hours += 12;
    if (isAM && hours === 12) hours = 0;
    return { hours, minutes };
};

/**
 * Creates an exact Date object representing the scheduled time on a given date in IST.
 * IST is UTC+5:30.
 */
const createIstDateTime = (dateStr, hours, minutes) => {
    const [year, month, day] = dateStr.split('-').map(Number);
    // Construct ISO string for IST: YYYY-MM-DDTHH:mm:00.000+05:30
    const pad = (n) => String(n).padStart(2, '0');
    const isoString = `${year}-${pad(month)}-${pad(day)}T${pad(hours)}:${pad(minutes)}:00.000+05:30`;
    return new Date(isoString);
};

/**
 * Checks for overdue medication doses and sends automated email reminders.
 * Accurately tracks date + time occurrences across the midnight boundary in IST.
 */
const checkAndSendMissedDoseReminders = async () => {
    try {
        const now = new Date();
        const todayStr = getIstDateString(now);

        // Calculate yesterday's date in IST
        const yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000);
        const yesterdayStr = getIstDateString(yesterday);

        // Fetch all active schedules
        const schedules = await Schedule.find({ active: true });

        for (const schedule of schedules) {
            const timeStr = schedule.scheduledTime || schedule.time;
            if (!timeStr) continue;

            // Check schedule start date and duration window
            const start = schedule.startDate ? new Date(schedule.startDate) : new Date(schedule.createdAt);
            const durationDays = schedule.durationDays || 7;
            const diffDays = (now - start) / (1000 * 60 * 60 * 24);
            if (diffDays > durationDays) continue;

            // Check if parent medicine is deleted / inactive
            const medCheck = await Medicine.findOne({
                $or: [
                    { _id: schedule.medicineId },
                    { userId: schedule.userId, name: schedule.medicineName }
                ]
            }).catch(() => null);

            if (medCheck && medCheck.active === false) {
                await Schedule.findByIdAndUpdate(schedule._id, { active: false });
                continue;
            }

            const { hours, minutes } = parseTimeString(timeStr);

            // Check both yesterday and today occurrences to handle late-night & midnight boundary
            const datesToCheck = [yesterdayStr, todayStr];

            for (const occurrenceDate of datesToCheck) {
                const scheduledTimestamp = createIstDateTime(occurrenceDate, hours, minutes);
                const graceEndTimestamp = new Date(scheduledTimestamp.getTime() + 15 * 60 * 1000); // 15-minute window

                // If scheduled time is in the future, it is UPCOMING or currently DUE
                if (now < graceEndTimestamp) {
                    continue; // Not yet missed
                }

                // If scheduled timestamp is before the schedule's startDate (e.g. yesterday before start), skip
                const startDayStr = getIstDateString(start);
                if (occurrenceDate < startDayStr) {
                    continue;
                }

                // Check if this occurrence was already logged (TAKEN, MISSED, LATE, etc.)
                const existingHistory = await MedicationHistory.findOne({
                    userId: schedule.userId,
                    medicineId: schedule.medicineId,
                    scheduledTime: schedule.scheduledTime,
                    date: occurrenceDate
                });

                if (existingHistory) {
                    // Already logged for this occurrence date, idempotent skip
                    continue;
                }

                // Dose is MISSED -> Create log and send notifications
                const user = await User.findOne({ _id: schedule.userId }).catch(() => null);
                const med = await Medicine.findOne({ _id: schedule.medicineId }).catch(() => null);
                const medicineName = schedule.medicineName || (med ? med.name : 'Medication');
                const dosage = schedule.medicineDosage || (med ? med.dosage : '');
                const userName = user ? user.name : 'Patient';
                const userEmail = user ? user.email : null;

                console.log(`[MISSED DOSE DETECTED] ${medicineName} (${timeStr}) on ${occurrenceDate} for user ${userEmail || schedule.userId}`);

                // Idempotent insert with error handling for duplicate index keys
                try {
                    await MedicationHistory.create({
                        userId: schedule.userId,
                        scheduleId: schedule._id ? schedule._id.toString() : undefined,
                        medicineId: schedule.medicineId,
                        medicineName: medicineName,
                        medicineDosage: dosage,
                        scheduledTime: schedule.scheduledTime,
                        date: occurrenceDate,
                        status: 'MISSED'
                    });
                } catch (dupErr) {
                    if (dupErr.code === 11000) {
                        console.log(`[IDEMPOTENT] Duplicate key caught for ${medicineName} on ${occurrenceDate}`);
                        continue;
                    }
                    throw dupErr;
                }

                // Send Email Notification to Patient
                if (userEmail) {
                    await sendEmail(
                        userEmail,
                        `⚠️ SmartMed Alert: You Missed ${medicineName}!`,
                        `Hello ${userName},\n\nYou missed your scheduled dose of ${medicineName} (${dosage}) set for ${timeStr} on ${occurrenceDate}.\n\nPlease take your medication as soon as possible to stay on track.\n\nBest regards,\nSmartMed Care Team`,
                        `<div style="font-family: Arial, sans-serif; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                            <h2 style="color: #D32F2F;">⚠️ Missed Medication Alert</h2>
                            <p>Hello <b>${userName}</b>,</p>
                            <p>You missed your dose of <b>${medicineName} ${dosage}</b> scheduled for <b>${timeStr}</b> on <b>${occurrenceDate}</b>.</p>
                            <div style="background-color: #FFEBEE; padding: 12px; border-left: 4px solid #D32F2F; margin: 15px 0;">
                                <strong>Action Required:</strong> Please take your medication as soon as possible or consult your caregiver.
                            </div>
                            <p style="font-size: 12px; color: #777;">Sent automatically by SmartMed Care System</p>
                        </div>`
                    );
                }

                // Send Caregiver Email Alert
                const caregivers = await Caregiver.find({ userId: schedule.userId, active: true });
                for (const caregiver of caregivers) {
                    if (caregiver.email) {
                        await sendEmail(
                            caregiver.email,
                            `🚨 Caregiver Alert: ${userName} Missed ${medicineName}`,
                            `Hello ${caregiver.name},\n\nPatient ${userName} missed their dose of ${medicineName} (${dosage}) scheduled for ${timeStr} on ${occurrenceDate}.\n\nPlease check in on them.`
                        );
                    }
                }
            }
        }
    } catch (err) {
        console.error(`[REMINDER SCHEDULER ERROR] ${err.message}`);
    }
};

/**
 * Starts periodic background polling for missed doses.
 */
const startReminderScheduler = () => {
    checkAndSendMissedDoseReminders();
    setInterval(checkAndSendMissedDoseReminders, 60000);
    console.log('[REMINDER SCHEDULER] Date-aware missed dose checker active (polling every 60s in Asia/Kolkata).');
};

module.exports = { startReminderScheduler, checkAndSendMissedDoseReminders };
