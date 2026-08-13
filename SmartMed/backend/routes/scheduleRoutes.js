const express = require('express');
const router = express.Router();
const auth = require('../middleware/authMiddleware');
const { createSchedule, getSchedules, getTodaySchedules, deleteSchedule } = require('../controllers/scheduleController');

router.use(auth);
router.get('/', getSchedules);
router.get('/today', getTodaySchedules);
router.post('/', createSchedule);
router.delete('/:id', deleteSchedule);

module.exports = router;
