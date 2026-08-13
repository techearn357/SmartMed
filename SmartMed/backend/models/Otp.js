const mongoose = require('mongoose');

const otpSchema = new mongoose.Schema({
    email: { type: String, required: true },
    otp: { type: String, required: true },
    purpose: { type: String, enum: ['registration', 'password_reset'], required: true },
    expiresAt: { type: Date, required: true },
    createdAt: { type: Date, default: Date.now, expires: 600 } // Auto expire after 10 mins
});

module.exports = mongoose.model('Otp', otpSchema);
