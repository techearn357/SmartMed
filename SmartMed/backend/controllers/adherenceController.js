const MedicationHistory = require('../models/MedicationHistory');

const getSummary = async (req, res) => {
    try {
        const { period } = req.query; // 'today', 'week', 'month'
        let days = 1;
        if (period === 'week') days = 7;
        else if (period === 'month') days = 30;

        const startDate = new Date();
        if (period === 'week') startDate.setDate(startDate.getDate() - 7);
        else if (period === 'month') startDate.setDate(startDate.getDate() - 30);
        const startStr = startDate.toISOString().split('T')[0];

        const history = await MedicationHistory.find({
            userId: req.user.id,
            date: { $gte: startStr }
        });

        const totalDoses = history.length;
        const takenDoses = history.filter(h => h.status === 'TAKEN').length;
        const missedDoses = history.filter(h => h.status === 'MISSED').length;
        const lateDoses = history.filter(h => h.status === 'LATE').length;
        const adherencePercentage = totalDoses > 0 ? (takenDoses / totalDoses) * 100 : 100;

        res.status(200).json({
            success: true,
            data: {
                totalDoses,
                takenDoses,
                missedDoses,
                lateDoses,
                adherencePercentage: Math.round(adherencePercentage),
                currentStreak: takenDoses > 0 ? days : 0
            }
        });
    } catch (e) { res.status(500).json({ success: false, message: e.message }); }
};

module.exports = { getSummary };
