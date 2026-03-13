package com.example.beehive.ui.repository;

import android.content.Context;

import com.example.beehive.ui.database.AppDatabase;
import com.example.beehive.ui.database.EntryDao;
import com.example.beehive.ui.model.Entry;
import com.example.beehive.ui.sync.SyncManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntryRepository {
    private final EntryDao entryDao;
    private final SyncManager syncManager;
    private final ExecutorService executorService;
    private boolean isChild = false; // Это должно устанавливаться извне

    public EntryRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.entryDao = db.entryDao();
        this.syncManager = new SyncManager(context);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void insertEntry(Entry entry) {
        executorService.execute(() -> {
            long id = entryDao.insert(entry);
            entry.setId((int) id);
            syncManager.addToSyncQueue("INSERT", "entries", (int) id, entry);
        });
    }

    public void updateEntry(Entry entry) {
        executorService.execute(() -> {
            entryDao.update(entry);
            syncManager.addToSyncQueue("UPDATE", "entries", entry.getId(), entry);
        });
    }

    public void deleteEntry(Entry entry) {
        executorService.execute(() -> {
            if (isChild) {
                entry.setDeleted(true);
                entryDao.update(entry);
            } else {
                entryDao.deletePermanently(entry.getId());
            }
            syncManager.addToSyncQueue("DELETE", "entries", entry.getId(), entry);
        });
    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
        if (syncManager != null) {
            syncManager.shutdown();
        }
    }
}