const Caregiver = require('../models/Caregiver');

const getCaregivers = async (req, res) => {
    try {
        const list = await Caregiver.find({ userId: req.user.id, active: true });
        res.status(200).json({ success: true, data: list });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

const createCaregiver = async (req, res) => {
    try {
        const c = await Caregiver.create({ ...req.body, userId: req.user.id });
        res.status(201).json({ success: true, data: c });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

const deleteCaregiver = async (req, res) => {
    try {
        await Caregiver.findByIdAndUpdate(req.params.id, { active: false });
        res.status(200).json({ success: true, message: 'Deleted' });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

const sendAlert = async (req, res) => {
    try {
        const { medicineName, missedCount } = req.body;
        console.log(`[CAREGIVER ALERT] ${req.user.id}: ${medicineName} missed ${missedCount} times!`);
        res.status(200).json({ success: true, message: 'Alert sent to caregivers' });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

module.exports = { getCaregivers, createCaregiver, deleteCaregiver, sendAlert };
