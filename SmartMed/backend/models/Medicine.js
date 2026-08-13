const mongoose = require('mongoose');

const medicineSchema = new mongoose.Schema({
    userId: { type: String, required: true, index: true },
    name: { type: String, required: true },
    dosage: { type: String, required: true },
    dosageUnit: { type: String, default: 'mg' },
    frequency: { type: String, default: 'once_daily' },
    times: [{ type: String }],
    startDate: { type: String },
    endDate: { type: String },
    duration: { type: Number, default: 0 },
    totalTablets: { type: Number, default: 0 },
    remainingTablets: { type: Number, default: 0 },
    tabletsPerDose: { type: Number, default: 1 },
    instructions: { type: String },
    active: { type: Boolean, default: true },
    createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Medicine', medicineSchema);
