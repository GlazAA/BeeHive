// Файл: EntryRepository.java
// Пакет: com.example.beehive.ui.repository
// Назначение: Репозиторий для работы с записями, промежуточный слой между UI и базой данных.

package com.example.beehive.ui.repository;

import android.content.Context;
import com.example.beehive.ui.database.AppDatabase;
import com.example.beehive.ui.database.EntryDao;
import com.example.beehive.ui.model.Entry;
import com.example.beehive.ui.sync.SyncManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntryRepository {

    private final EntryDao entryDao;
    private final SyncManager syncManager;
    private final ExecutorService executorService;

    public EntryRepository(Context context) {
        AppDatabase database = AppDatabase.getDatabase(context);
        entryDao = database.entryDao();
        syncManager = new SyncManager(context); // Создаем экземпляр SyncManager
        executorService = Executors.newSingleThreadExecutor();
    }

    // --- Методы для работы с данными --- //

    public long insert(Entry entry) {
        long id = entryDao.insert(entry);
        // Добавляем операцию в очередь на синхронизацию
        if (id != -1) {
            entry.setId((int) id);
            syncManager.addToSyncQueue("INSERT", "entries", (int) id, entry);
        }
        return id;
    }

    public void update(Entry entry) {
        entryDao.update(entry);
        // Добавляем операцию в очередь на синхронизацию
        syncManager.addToSyncQueue("UPDATE", "entries", entry.getId(), entry);
    }

    public void markAsDeleted(int entryId) {
        entryDao.markAsDeleted(entryId);
        // Добавляем операцию в очередь на синхронизацию (мягкое удаление)
         syncManager.addToSyncQueue("DELETE_SOFT", "entries", entryId, null);
    }

    public void restore(int entryId) {
        entryDao.restore(entryId);
         // Добавляем операцию в очередь на синхронизацию
        syncManager.addToSyncQueue("RESTORE", "entries", entryId, null);
    }
    
    public void deletePermanently(int entryId) {
        entryDao.deletePermanently(entryId);
        // Добавляем операцию в очередь на синхронизацию (полное удаление)
        syncManager.addToSyncQueue("DELETE_HARD", "entries", entryId, null);
    }


    // --- Методы для получения данных (выполняются в фоновом потоке) --- //

    public void getEntriesByUser(int userId, OnDataReadyCallback<List<Entry>> callback) {
        executorService.execute(() -> {
            List<Entry> entries = entryDao.getEntriesByUser(userId);
            callback.onDataReady(entries);
        });
    }

    public void getEntryById(int entryId, OnDataReadyCallback<Entry> callback) {
        executorService.execute(() -> {
            Entry entry = entryDao.getEntryById(entryId);
            callback.onDataReady(entry);
        });
    }
    
    public void getAllEntries(OnDataReadyCallback<List<Entry>> callback) {
        executorService.execute(() -> {
            List<Entry> entries = entryDao.getAllEntries();
            callback.onDataReady(entries);
        });
    }

    // Интерфейс для асинхронного получения данных
    public interface OnDataReadyCallback<T> {
        void onDataReady(T data);
    }
    
     public void shutdown() {
        if (syncManager != null) {
            syncManager.shutdown();
        }
         if (executorService != null) {
            executorService.shutdown();
        }
    }
}
