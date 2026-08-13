package com.smartmed.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.smartmed.app.data.model.Caregiver;
import com.smartmed.app.data.repository.CaregiverRepository;
import com.smartmed.app.data.repository.Resource;
import java.util.List;

/** ViewModel for caregiver operations. */
public class CaregiverViewModel extends ViewModel {
    private final CaregiverRepository repository;
    public CaregiverViewModel() { this.repository = new CaregiverRepository(); }

    public LiveData<Resource<List<Caregiver>>> getCaregivers() { return repository.getCaregivers(); }
    public LiveData<Resource<Caregiver>> addCaregiver(Caregiver c) { return repository.createCaregiver(c); }
    public LiveData<Resource<Void>> deleteCaregiver(String id) { return repository.deleteCaregiver(id); }
    public LiveData<Resource<Void>> sendAlert(String medId, String medName, int count) { return repository.sendCaregiverAlert(medId, medName, count); }
}
