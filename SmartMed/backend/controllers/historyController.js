const mongoose = require('mongoose');
const MedicationHistory = require('../models/MedicationHistory');
const Medicine = require('../models/Medicine');
const User = require('../models/User');
const Caregiver = require('../models/Caregiver');
const { sendEmail } = require('../utils/emailService');

const getIstTodayStr = () => {
    return new Intl.DateTimeFormat('en-CA', { timeZone: 'Asia/Kolkata', year: 'numeric', month: '2-digit', day: '2-digit' }).format(new Date());
};

const createHistory = async (req, res) => {
    try {
        const todayStr = getIstTodayStr();
        const medName = req.body.medicineName || 'Medication';
        const dosage = req.body.medicineDosage || '';
        const scheduledTime = req.body.scheduledTime || '08:00 AM';
        const takenTime = req.body.takenTime || new Date().toLocaleTimeString();
        const status = req.body.status || 'TAKEN';

        const history = await MedicationHistory.create({
            userId: req.user.id,
            medicineId: req.body.medicineId || 'med_general',
            medicineName: medName,
            medicineDosage: dosage,
            scheduledTime: scheduledTime,
            takenTime: takenTime,
            status: status,
            date: req.body.date || todayStr
        });

        // Decrement remaining tablets if taken
        let remainingTablets = -1;
        if (status === 'TAKEN') {
            let med = null;
            if (req.body.medicineId && mongoose.Types.ObjectId.isValid(req.body.medicineId)) {
                med = await Medicine.findById(req.body.medicineId);
            }
            if (!med && req.body.medicineName) {
                med = await Medicine.findOne({ userId: req.user.id, name: req.body.medicineName, active: true });
            }
            if (!med && req.body.medicineId) {
                med = await Medicine.findOne({ userId: req.user.id, name: req.body.medicineId, active: true });
            }
            if (med) {
                const doseToSubtract = med.tabletsPerDose || 1;
                med.remainingTablets = Math.max(0, (med.remainingTablets || 0) - doseToSubtract);
                await med.save();
                remainingTablets = med.remainingTablets;
                console.log(`[STOCK UPDATED] ${med.name}: Reduced stock by ${doseToSubtract}, ${remainingTablets} remaining.`);
            }
        }

        // Fetch user for Email notifications

        const user = await User.findById(req.user.id);
        const userEmail = user ? user.email : null;
        const userName = user ? user.name : 'Patient';

        if (userEmail) {
            if (status === 'TAKEN') {
                // Send TAKEN confirmation email to patient
                await sendEmail(
                    userEmail,
                    `✅ SmartMed Confirmation: ${medName} Taken On Time!`,
                    `Hello ${userName},\n\nGreat job! You recorded taking your dose of ${medName} (${dosage}) on time at ${takenTime} (scheduled: ${scheduledTime}).\n\nKeep up the great adherence streak!\n\nBest regards,\nSmartMed Care Team`,
                    `<div style="font-family: Arial, sans-serif; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                        <h2 style="color: #2E7D32;">✅ Medication Taken Confirmation</h2>
                        <p>Hello <b>${userName}</b>,</p>
                        <p>Great job! You successfully marked your dose as <b>TAKEN</b>:</p>
                        <ul>
                            <li><b>Medication:</b> ${medName} ${dosage ? '(' + dosage + ')' : ''}</li>
                            <li><b>Scheduled Time:</b> ${scheduledTime}</li>
                            <li><b>Taken Time:</b> ${takenTime}</li>
                            <li><b>Date:</b> ${req.body.date || todayStr}</li>
                        </ul>
                        <div style="background-color: #E8F5E9; padding: 12px; border-left: 4px solid #2E7D32; margin: 15px 0;">
                            <strong>Adherence Streak:</strong> Excellent job staying on track with your health plan!
                        </div>
                    </div>`
                );

                // Notify Caregivers if enabled
                const caregivers = await Caregiver.find({ userId: req.user.id, active: true });
                for (const caregiver of caregivers) {
                    if (caregiver.email) {
                        await sendEmail(
                            caregiver.email,
                            `💚 Caregiver Update: ${userName} Took ${medName}`,
                            `Hello ${caregiver.name},\n\nPatient ${userName} took their dose of ${medName} (${dosage}) on time at ${takenTime}.`
                        );
                    }
                }
            } else if (status === 'MISSED' || status === 'LATE') {
                const statusText = status === 'MISSED' ? 'missed' : 'delayed';
                await sendEmail(
                    userEmail,
                    `⚠️ SmartMed Alert: ${status} Medication Notification for ${medName}`,
                    `Hello ${userName},\n\nOur system recorded a ${statusText} dose for ${medName} (${dosage}) scheduled at ${scheduledTime}.\n\nPlease ensure to take your medicine as prescribed.\n\nBest regards,\nSmartMed Team`
                );

                // Notify Caregivers
                const caregivers = await Caregiver.find({ userId: req.user.id, active: true });
                for (const caregiver of caregivers) {
                    if (caregiver.email) {
                        await sendEmail(
                            caregiver.email,
                            `🚨 Caregiver Notification: ${userName} ${status} Dose`,
                            `Hello ${caregiver.name},\n\nPatient ${userName} logged a ${status} dose for ${medName} (${dosage}) scheduled at ${scheduledTime}.`
                        );
                    }
                }
            }
        }

        res.status(201).json({ success: true, data: history, remainingTablets: remainingTablets });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

const getHistory = async (req, res) => {
    try {
        const { startDate, endDate, medicineId } = req.query;
        let query = { userId: req.user.id };
        if (medicineId) query.medicineId = medicineId;
        if (startDate && endDate) query.date = { $gte: startDate, $lte: endDate };

        const history = await MedicationHistory.find(query).sort({ date: -1, scheduledTime: -1 });
        res.status(200).json({ success: true, data: history });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

const getTodayHistory = async (req, res) => {
    try {
        const today = getIstTodayStr();
        const history = await MedicationHistory.find({ userId: req.user.id, date: today });
        res.status(200).json({ success: true, data: history });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

module.exports = { createHistory, getHistory, getTodayHistory };
