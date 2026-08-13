package com.smartmed.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.smartmed.app.data.model.*;
import com.smartmed.app.data.repository.*;
import java.util.List;

/** ViewModel for medication history. */
public class HistoryViewModel extends ViewModel {
    private final HistoryRepository repository;
    public HistoryViewModel() { this.repository = new HistoryRepository(); }

    public LiveData<Resource<List<MedicationHistory>>> getHistory(String start, String end, String medicineId) {
        return repository.getHistory(start, end, medicineId);
    }
    public LiveData<Resource<List<MedicationHistory>>> getTodayHistory() { return repository.getTodayHistory(); }
    public LiveData<Resource<MedicationHistory>> recordAction(MedicationHistory history) { return repository.createHistory(history); }
}
