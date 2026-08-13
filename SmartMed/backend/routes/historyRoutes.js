const express = require('express');
const router = express.Router();
const auth = require('../middleware/authMiddleware');
const { createHistory, getHistory, getTodayHistory } = require('../controllers/historyController');

router.use(auth);
router.get('/', getHistory);
router.get('/today', getTodayHistory);
router.post('/', createHistory);

module.exports = router;
