package com.smartmed.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.smartmed.app.data.model.Medicine;
import com.smartmed.app.data.repository.MedicineRepository;
import com.smartmed.app.data.repository.Resource;
import java.util.List;

/** ViewModel for medicine list and CRUD. */
public class MedicineViewModel extends ViewModel {
    private final MedicineRepository repository;

    public MedicineViewModel() {
        this.repository = new MedicineRepository();
    }

    public LiveData<Resource<List<Medicine>>> getMedicines() {
        return repository.getMedicines();
    }

    public LiveData<Resource<Medicine>> getMedicine(String id) {
        return repository.getMedicine(id);
    }

    public LiveData<Resource<Medicine>> createMedicine(Medicine medicine) {
        return repository.createMedicine(medicine);
    }

    public LiveData<Resource<Medicine>> updateMedicine(String id, Medicine medicine) {
        return repository.updateMedicine(id, medicine);
    }

    public LiveData<Resource<Void>> deleteMedicine(String id) {
        return repository.deleteMedicine(id);
    }
}
