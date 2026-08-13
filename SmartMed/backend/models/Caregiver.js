const mongoose = require('mongoose');

const caregiverSchema = new mongoose.Schema({
    userId: { type: String, required: true, index: true },
    name: { type: String, required: true },
    email: { type: String },
    phone: { type: String },
    relationship: { type: String, default: 'Family' },
    active: { type: Boolean, default: true },
    createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Caregiver', caregiverSchema);
