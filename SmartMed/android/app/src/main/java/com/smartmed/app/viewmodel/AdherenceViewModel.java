package com.smartmed.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.smartmed.app.data.model.Adherence;
import com.smartmed.app.data.repository.AdherenceRepository;
import com.smartmed.app.data.repository.Resource;
import java.util.List;

/** ViewModel for adherence data. */
public class AdherenceViewModel extends ViewModel {
    private final AdherenceRepository repository;
    public AdherenceViewModel() { this.repository = new AdherenceRepository(); }

    public LiveData<Resource<Adherence>> getSummary(String period) { return repository.getAdherenceSummary(period); }
    public LiveData<Resource<List<Adherence>>> getHistory(String period) { return repository.getAdherenceHistory(period); }
}
