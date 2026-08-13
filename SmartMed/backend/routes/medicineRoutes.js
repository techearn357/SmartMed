const express = require('express');
const router = express.Router();
const auth = require('../middleware/authMiddleware');
const { getMedicines, getMedicine, createMedicine, updateMedicine, deleteMedicine } = require('../controllers/medicineController');

router.use(auth);
router.get('/', getMedicines);
router.get('/:id', getMedicine);
router.post('/', createMedicine);
router.put('/:id', updateMedicine);
router.delete('/:id', deleteMedicine);

module.exports = router;
