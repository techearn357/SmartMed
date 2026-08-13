const Schedule = require('../models/Schedule');
const Medicine = require('../models/Medicine');
const MedicationHistory = require('../models/MedicationHistory');
const User = require('../models/User');
const Caregiver = require('../models/Caregiver');
const { sendEmail } = require('./emailService');

/**
 * Checks for overdue medication doses and sends automated email reminders to patients and caregivers.
 */
const checkAndSendMissedDoseReminders = async () => {
    try {
        // Calculate date and time in IST (Asia/Kolkata) explicitly
        const now = new Date();
        const istDateFormatter = new Intl.DateTimeFormat('en-CA', { timeZone: 'Asia/Kolkata', year: 'numeric', month: '2-digit', day: '2-digit' });
        const todayStr = istDateFormatter.format(now); // Produces YYYY-MM-DD in IST

        const istTimeParts = new Intl.DateTimeFormat('en-US', { 
            timeZone: 'Asia/Kolkata', 
            hour: 'numeric', 
            minute: 'numeric', 
            hour12: false 
        }).formatToParts(now);

        let currentHours = 0;
        let currentMinutes = 0;
        for (const part of istTimeParts) {
            if (part.type === 'hour') currentHours = parseInt(part.value, 10) % 24;
            if (part.type === 'minute') currentMinutes = parseInt(part.value, 10);
        }
        const currentTotalMinutes = currentHours * 60 + currentMinutes;

        console.log(`[REMINDER SCHEDULER] Checking at IST time ${String(currentHours).padStart(2,'0')}:${String(currentMinutes).padStart(2,'0')} (${currentTotalMinutes} mins), date: ${todayStr}`);

        // Fetch all active schedules
        const schedules = await Schedule.find({ active: true });
        console.log(`[REMINDER SCHEDULER] Found ${schedules.length} active schedule(s) to check`);

        for (const schedule of schedules) {
            const timeStr = schedule.scheduledTime || schedule.time;
            if (!timeStr) {
                console.log(`[REMINDER SCHEDULER] Skipping schedule ${schedule._id}: no time set`);
                continue;
            }

            // Enforce duration check (default 7 days)
            const start = schedule.startDate ? new Date(schedule.startDate) : new Date(schedule.createdAt);
            const durationDays = schedule.durationDays || 7;
            const diffMs = now - start;
            const diffDays = diffMs / (1000 * 60 * 60 * 24);
            if (diffDays > durationDays) {
                console.log(`[REMINDER SCHEDULER] Skipping ${schedule.medicineName}: duration of ${durationDays} days exceeded (${Math.floor(diffDays)} days passed)`);
                continue;
            }

            // Check if medicine has been deleted or deactivated
            const medCheck = await Medicine.findOne({
                $or: [
                    { _id: schedule.medicineId },
                    { userId: schedule.userId, name: schedule.medicineName }
                ]
            }).catch(() => null);

            if (medCheck && medCheck.active === false) {
                console.log(`[REMINDER SCHEDULER] Skipping ${schedule.medicineName}: medicine is deleted/inactive`);
                await Schedule.findByIdAndUpdate(schedule._id, { active: false });
                continue;
            }

            // Parse time string e.g. "08:00 AM", "08:00", "18:30", "8:00 PM"
            let schedHours = 8, schedMinutes = 0;
            const cleanTime = timeStr.trim().toUpperCase();
            if (cleanTime.includes('AM') || cleanTime.includes('PM')) {
                const isPM = cleanTime.includes('PM');
                const timeOnly = cleanTime.replace('AM', '').replace('PM', '').trim();
                const timeParts = timeOnly.split(':');
                schedHours = parseInt(timeParts[0], 10);
                schedMinutes = parseInt(timeParts[1] || '0', 10);
                if (isPM && schedHours < 12) schedHours += 12;
                if (!isPM && schedHours === 12) schedHours = 0;
            } else {
                const timeParts = cleanTime.split(':');
                if (timeParts.length >= 2) {
                    schedHours = parseInt(timeParts[0], 10);
                    schedMinutes = parseInt(timeParts[1], 10);
                }
            }

            const schedTotalMinutes = schedHours * 60 + schedMinutes;
            console.log(`[REMINDER SCHEDULER] ${schedule.medicineName} scheduled at ${String(schedHours).padStart(2,'0')}:${String(schedMinutes).padStart(2,'0')} (${schedTotalMinutes} mins) | Current: ${currentTotalMinutes} mins | 15min window: ${schedTotalMinutes + 15}`);

            // Trigger alert if scheduled time was over 15 minutes ago
            if (currentTotalMinutes >= schedTotalMinutes + 15) {
                const existingHistory = await MedicationHistory.findOne({
                    userId: schedule.userId,
                    medicineId: schedule.medicineId,
                    date: todayStr
                });

                if (existingHistory) {
                    console.log(`[REMINDER SCHEDULER] ${schedule.medicineName} already logged as ${existingHistory.status} today, skipping`);
                    continue;
                }

                // Lookup user by string _id (handle both ObjectId and plain strings)
                const user = await User.findOne({ _id: schedule.userId }).catch(() => null);
                const med = await Medicine.findOne({ _id: schedule.medicineId }).catch(() => null);
                const medicineName = schedule.medicineName || (med ? med.name : 'Medication');
                const dosage = schedule.medicineDosage || (med ? med.dosage : '');
                const userName = user ? user.name : 'Patient';
                const userEmail = user ? user.email : null;

                console.log(`[MISSED DOSE DETECTED] ${medicineName} for user ${userEmail || schedule.userId}`);

                // Log missed dose in database
                await MedicationHistory.create({
                    userId: schedule.userId,
                    medicineId: schedule.medicineId,
                    medicineName: medicineName,
                    medicineDosage: dosage,
                    scheduledTime: timeStr,
                    date: todayStr,
                    status: 'MISSED'
                });

                // Send Email Reminder to User
                if (userEmail) {
                    await sendEmail(
                        userEmail,
                        `⚠️ SmartMed Alert: You Missed ${medicineName}!`,
                        `Hello ${userName},\n\nYou missed your scheduled dose of ${medicineName} (${dosage}) which was set for ${timeStr} today.\n\nPlease take your medication as soon as possible to stay on track.\n\nBest regards,\nSmartMed Care Team`,
                        `<div style="font-family: Arial, sans-serif; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                            <h2 style="color: #D32F2F;">⚠️ Missed Medication Alert</h2>
                            <p>Hello <b>${userName}</b>,</p>
                            <p>You have not taken your dose of <b>${medicineName} ${dosage}</b> which was scheduled for <b>${timeStr}</b> today.</p>
                            <div style="background-color: #FFEBEE; padding: 12px; border-left: 4px solid #D32F2F; margin: 15px 0;">
                                <strong>Action Required:</strong> Please take your medication as soon as possible or consult your caregiver.
                            </div>
                            <p style="font-size: 12px; color: #777;">Sent automatically by SmartMed Reminder System</p>
                        </div>`
                    );
                } else {
                    console.log(`[REMINDER SCHEDULER] No email found for user ${schedule.userId}, cannot send missed dose alert`);
                }

                // Send Caregiver Email Alert
                const caregivers = await Caregiver.find({ userId: schedule.userId, active: true });
                for (const caregiver of caregivers) {
                    if (caregiver.email) {
                        await sendEmail(
                            caregiver.email,
                            `🚨 Caregiver Alert: ${userName} Missed ${medicineName}`,
                            `Hello ${caregiver.name},\n\nPatient ${userName} missed their dose of ${medicineName} (${dosage}) scheduled for ${timeStr} today.\n\nPlease check in on them.`
                        );
                    }
                }
            }
        }
    } catch (err) {
        console.error(`[REMINDER SCHEDULER ERROR] ${err.message}`);
        console.error(err.stack);
    }
};

/**
 * Starts periodic background polling for missed doses.
 */
const startReminderScheduler = () => {
    // Run initial check and set 60-second polling interval
    checkAndSendMissedDoseReminders();
    setInterval(checkAndSendMissedDoseReminders, 60000);
    console.log('[REMINDER SCHEDULER] Automated missed dose email checker active (polling every 60s).');
};

module.exports = { startReminderScheduler, checkAndSendMissedDoseReminders };
