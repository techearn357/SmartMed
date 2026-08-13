const express = require('express');
const router = express.Router();
const auth = require('../middleware/authMiddleware');
const { getCaregivers, createCaregiver, deleteCaregiver, sendAlert } = require('../controllers/caregiverController');

router.use(auth);
router.get('/', getCaregivers);
router.post('/', createCaregiver);
router.delete('/:id', deleteCaregiver);
router.post('/alert', sendAlert);

module.exports = router;
