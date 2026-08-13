const mongoose = require('mongoose');

const historySchema = new mongoose.Schema({
    userId: { type: String, required: true, index: true },
    scheduleId: { type: String, index: true },
    medicineId: { type: String, required: true, index: true },
    medicineName: { type: String, required: true },
    medicineDosage: { type: String },
    scheduledTime: { type: String, required: true },
    takenTime: { type: String },
    status: { type: String, enum: ['TAKEN', 'MISSED', 'SNOOZED', 'LATE'], required: true },
    date: { type: String, required: true, index: true }, // "YYYY-MM-DD" in IST
    createdAt: { type: Date, default: Date.now }
});

// Compound index to guarantee uniqueness per dose occurrence
historySchema.index({ userId: 1, medicineId: 1, scheduledTime: 1, date: 1 });

module.exports = mongoose.model('MedicationHistory', historySchema);
