const Medicine = require('../models/Medicine');
const Schedule = require('../models/Schedule');

const getMedicines = async (req, res) => {
    try {
        const medicines = await Medicine.find({ userId: req.user.id, active: true });
        res.status(200).json({ success: true, data: medicines });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

const getMedicine = async (req, res) => {
    try {
        const med = await Medicine.findById(req.params.id);
        if (!med) return res.status(404).json({ success: false, message: 'Not found' });
        res.status(200).json({ success: true, data: med });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

const createMedicine = async (req, res) => {
    try {
        const medData = { ...req.body, userId: req.user.id };
        const med = await Medicine.create(medData);
        res.status(201).json({ success: true, data: med });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

const updateMedicine = async (req, res) => {
    try {
        const med = await Medicine.findByIdAndUpdate(req.params.id, req.body, { new: true });
        res.status(200).json({ success: true, data: med });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

const deleteMedicine = async (req, res) => {
    try {
        const med = await Medicine.findByIdAndUpdate(req.params.id, { active: false }, { new: true });
        if (med) {
            await Schedule.updateMany(
                { userId: req.user.id, $or: [{ medicineId: req.params.id }, { medicineName: med.name }] },
                { active: false }
            );
        }
        res.status(200).json({ success: true, message: 'Deleted' });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

module.exports = { getMedicines, getMedicine, createMedicine, updateMedicine, deleteMedicine };
