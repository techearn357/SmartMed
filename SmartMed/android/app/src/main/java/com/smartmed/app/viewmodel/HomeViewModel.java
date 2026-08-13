package com.smartmed.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.smartmed.app.data.model.*;
import com.smartmed.app.data.repository.*;
import java.util.List;

/** ViewModel for home dashboard data. */
public class HomeViewModel extends ViewModel {
    private final ScheduleRepository scheduleRepo;
    private final HistoryRepository historyRepo;
    private final AdherenceRepository adherenceRepo;
    private final MedicineRepository medicineRepo;

    private MutableLiveData<List<Schedule>> todaySchedules;
    private MutableLiveData<List<MedicationHistory>> todayHistory;
    private MutableLiveData<Adherence> todayAdherence;

    public HomeViewModel() {
        scheduleRepo = new ScheduleRepository();
        historyRepo = new HistoryRepository();
        adherenceRepo = new AdherenceRepository();
        medicineRepo = new MedicineRepository();
    }

    public LiveData<Resource<List<Schedule>>> loadTodaySchedules() {
        return scheduleRepo.getTodaySchedules();
    }

    public LiveData<Resource<List<MedicationHistory>>> loadTodayHistory() {
        return historyRepo.getTodayHistory();
    }

    public LiveData<Resource<Adherence>> loadTodayAdherence() {
        return adherenceRepo.getAdherenceSummary("today");
    }

    public LiveData<Resource<List<Medicine>>> loadMedicines() {
        return medicineRepo.getMedicines();
    }

    public LiveData<Resource<MedicationHistory>> recordHistory(String medicineId, String medicineName, String dosage, String scheduledTime, String status) {
        MedicationHistory history = new MedicationHistory();
        history.setMedicineId(medicineId);
        history.setMedicineName(medicineName);
        history.setMedicineDosage(dosage);
        history.setScheduledTime(scheduledTime);
        history.setStatus(status);
        history.setDate(com.smartmed.app.utils.DateUtils.formatDateForApi(new java.util.Date()));
        history.setTakenTime(com.smartmed.app.utils.DateUtils.getCurrentTimeString());
        return historyRepo.createHistory(history);
    }
}
