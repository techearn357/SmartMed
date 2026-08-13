const mongoose = require('mongoose');

const scheduleSchema = new mongoose.Schema({
    userId: { type: String, required: true, index: true },
    medicineId: { type: String, required: true },
    medicineName: { type: String, required: true },
    medicineDosage: { type: String },
    scheduledTime: { type: String, required: true }, // e.g. "08:00 AM" or "08:00"
    time: { type: String },
    repeatPattern: { type: String, default: 'daily' },
    durationDays: { type: Number, default: 7 }, // 7 days duration default
    startDate: { type: Date, default: Date.now },
    active: { type: Boolean, default: true },
    createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Schedule', scheduleSchema);
