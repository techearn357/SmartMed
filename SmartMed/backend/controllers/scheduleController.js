const Schedule = require('../models/Schedule');
const MedicationHistory = require('../models/MedicationHistory');
const Medicine = require('../models/Medicine');

const parseTimeToMinutes = (timeStr) => {
    if (!timeStr) return 0;
    let s = timeStr.trim().toUpperCase();
    let isPM = s.includes('PM');
    let isAM = s.includes('AM');
    s = s.replace('AM', '').replace('PM', '').trim();
    let parts = s.split(':');
    let hours = parseInt(parts[0], 10) || 0;
    let minutes = parseInt(parts[1], 10) || 0;
    if (isPM && hours < 12) hours += 12;
    if (isAM && hours === 12) hours = 0;
    return hours * 60 + minutes;
};

const createSchedule = async (req, res) => {
    try {
        const durationDays = req.body.durationDays ? parseInt(req.body.durationDays, 10) : 7;
        const schedule = await Schedule.create({
            ...req.body,
            userId: req.user.id,
            durationDays: durationDays,
            startDate: req.body.startDate || new Date()
        });
        res.status(201).json({ success: true, data: schedule });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

const getSchedules = async (req, res) => {
    try {
        const activeMeds = await Medicine.find({ userId: req.user.id, active: true });
        const activeMedIds = new Set(activeMeds.map(m => m._id.toString()));
        const activeMedNames = new Set(activeMeds.map(m => m.name));

        const rawSchedules = await Schedule.find({ userId: req.user.id, active: true });
        const schedules = rawSchedules.filter(s =>
            (s.medicineId && activeMedIds.has(s.medicineId.toString())) ||
            (s.medicineName && activeMedNames.has(s.medicineName))
        );

        res.status(200).json({ success: true, data: schedules });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

const getTodaySchedules = async (req, res) => {
    try {
        const now = new Date();
        const todayStr = now.toISOString().split('T')[0];

        // Fetch active medicines for this user first
        const activeMeds = await Medicine.find({ userId: req.user.id, active: true });
        const activeMedIds = new Set(activeMeds.map(m => m._id.toString()));
        const activeMedNames = new Set(activeMeds.map(m => m.name));

        // Fetch active schedules, filtering out those for deleted medicines
        let allSchedules = await Schedule.find({ userId: req.user.id, active: true });
        allSchedules = allSchedules.filter(s =>
            (s.medicineId && activeMedIds.has(s.medicineId.toString())) ||
            (s.medicineName && activeMedNames.has(s.medicineName))
        );

        // Sort chronologically by 24h time in minutes
        allSchedules.sort((a, b) => parseTimeToMinutes(a.scheduledTime) - parseTimeToMinutes(b.scheduledTime));

        // Filter schedules within their duration window (default 7 days)
        const validSchedules = allSchedules.filter(s => {
            const start = s.startDate ? new Date(s.startDate) : new Date(s.createdAt);
            const duration = s.durationDays || 7;
            const diffTime = Math.abs(now - start);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
            return diffDays <= duration;
        });

        // Filter out schedules already marked as TAKEN today for THIS specific scheduled time
        const history = await MedicationHistory.find({ userId: req.user.id, date: todayStr, status: 'TAKEN' });

        const pendingSchedules = validSchedules.filter(s => {
            const isTaken = history.some(h =>
                (h.scheduleId && h.scheduleId.toString() === s._id.toString()) ||
                ((h.medicineId === s.medicineId || h.medicineName === s.medicineName) && h.scheduledTime === s.scheduledTime)
            );
            return !isTaken;
        });

        // Attach remainingTablets from Medicine model
        const medicines = await Medicine.find({ userId: req.user.id, active: true });
        const medMap = new Map();
        medicines.forEach(m => {
            medMap.set(m._id.toString(), m.remainingTablets);
            medMap.set(m.name, m.remainingTablets);
        });

        if (pendingSchedules.length > 0) {
            const result = pendingSchedules.map(s => {
                const sObj = s.toObject();
                sObj.remainingTablets = medMap.get(s.medicineId) ?? medMap.get(s.medicineName) ?? 0;
                sObj.isTomorrow = false;
                return sObj;
            });
            return res.status(200).json({ success: true, data: result });
        } else if (validSchedules.length > 0) {
            // All today's doses taken -> Return ALL of tomorrow's schedules with isTomorrow = true
            const result = validSchedules.map(s => {
                const sObj = s.toObject();
                sObj.remainingTablets = medMap.get(s.medicineId) ?? medMap.get(s.medicineName) ?? 0;
                sObj.isTomorrow = true;
                return sObj;
            });
            return res.status(200).json({ success: true, data: result });
        } else {
            return res.status(200).json({ success: true, data: [] });
        }
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

const deleteSchedule = async (req, res) => {
    try {
        await Schedule.findByIdAndUpdate(req.params.id, { active: false });
        res.status(200).json({ success: true, message: 'Deleted' });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

module.exports = { createSchedule, getSchedules, getTodaySchedules, deleteSchedule };

