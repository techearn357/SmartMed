require('dotenv').config();
const express = require('express');
const cors = require('cors');
const connectDB = require('./config/db');

const app = express();
app.use(cors());
app.use(express.json());

// Connect Database
connectDB();

// Routes
app.use('/api/auth', require('./routes/authRoutes'));
app.use('/api/medicines', require('./routes/medicineRoutes'));
app.use('/api/schedules', require('./routes/scheduleRoutes'));
app.use('/api/history', require('./routes/historyRoutes'));
app.use('/api/caregivers', require('./routes/caregiverRoutes'));
app.use('/api/adherence', require('./routes/adherenceRoutes'));

// Health check endpoint
app.get('/api/health', (req, res) => {
    res.status(200).json({ status: 'OK', message: 'SmartMed Backend API is running' });
});

// Debug: manually trigger the missed dose reminder check
app.get('/api/debug/trigger-reminder', async (req, res) => {
    try {
        const Schedule = require('./models/Schedule');
        const schedCount = await Schedule.find({}).countDocuments();
        const activeCount = await Schedule.find({ active: true }).countDocuments();
        const { checkAndSendMissedDoseReminders } = require('./utils/reminderScheduler');
        await checkAndSendMissedDoseReminders();
        res.json({ success: true, message: 'Reminder check triggered', totalSchedules: schedCount, activeSchedules: activeCount });
    } catch (e) {
        res.status(500).json({ success: false, message: e.message });
    }
});

const { startReminderScheduler } = require('./utils/reminderScheduler');

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
    startReminderScheduler();
});

