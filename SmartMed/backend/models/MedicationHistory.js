const mongoose = require('mongoose');

const historySchema = new mongoose.Schema({
    userId: { type: String, required: true, index: true },
    medicineId: { type: String, required: true },
    medicineName: { type: String, required: true },
    medicineDosage: { type: String },
    scheduledTime: { type: String, required: true },
    takenTime: { type: String },
    status: { type: String, enum: ['TAKEN', 'MISSED', 'SNOOZED', 'LATE'], required: true },
    date: { type: String, required: true }, // "YYYY-MM-DD"
    createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('MedicationHistory', historySchema);
