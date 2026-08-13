const express = require('express');
const router = express.Router();
const { register, verifyOtp, login, forgotPassword, resendOtp, resetPassword } = require('../controllers/authController');

router.post('/register', register);
router.post('/verify-otp', verifyOtp);
router.post('/login', login);
router.post('/forgot-password', forgotPassword);
router.post('/resend-otp', resendOtp);
router.post('/reset-password', resetPassword);

module.exports = router;

