// Файл: EntriesViewModel.java
// Назначение: ViewModel для управления данными о записях.
package com.example.beehive.ui.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.beehive.ui.model.Entry;
import com.example.beehive.ui.repository.EntryRepository;
import java.util.List;

public class EntriesViewModel extends ViewModel {
    private EntryRepository entryRepository = new EntryRepository();
    private MutableLiveData<List<Entry>> entries = new MutableLiveData<>();

    public LiveData<List<Entry>> getEntries(String userId, String filter) {
        loadEntries(userId, filter);
        return entries;
    }

    private void loadEntries(String userId, String filter) {
        entryRepository.getEntries(userId, filter, new EntryRepository.OnDataReadyCallback() {
            @Override
            public void onDataReady(List<Entry> entryList) {
                entries.setValue(entryList);
            }
        });
    }
}
