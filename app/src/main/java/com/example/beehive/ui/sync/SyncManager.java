// отвечает за синхронизацию данных
// логика
// относится к ApiClient.java, ApiService.java, AppDatabase.java, EntryDao.java, EntryVisibilityDao.java, SyncDao.java, SyncEntry.java
package com.example.beehive.ui.sync;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.beehive.ui.api.ApiClient;
import com.example.beehive.ui.api.ApiService;
import com.example.beehive.ui.database.AppDatabase;
import com.example.beehive.ui.database.EntryDao;
import com.example.beehive.ui.database.EntryVisibilityDao;
import com.example.beehive.ui.database.SyncDao;
import com.example.beehive.ui.model.Entry;
import com.example.beehive.ui.model.EntryType;
import com.example.beehive.ui.model.EntryVisibility;
import com.example.beehive.ui.model.SyncEntry;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class SyncManager {
    private static final String TAG = "SyncManager";
    private final Context context;
    private final AppDatabase database;
    private final SyncDao syncDao;
    private final EntryDao entryDao;
    private final EntryVisibilityDao visibilityDao;
    private final ApiService apiService;
    private final ExecutorService executorService;
    private final Gson gson;
    private SyncListener listener;

    public interface SyncListener {
        void onSyncStarted();
        void onSyncProgress(int current, int total);
        void onSyncCompleted(int uploaded, int downloaded);
        void onSyncFailed(String error);
    }

    public SyncManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getDatabase(context);
        this.syncDao = database.syncDao();
        this.entryDao = database.entryDao();
        this.visibilityDao = database.entryVisibilityDao();
        this.apiService = ApiClient.getApiService();
        this.executorService = Executors.newSingleThreadExecutor();
        this.gson = new Gson();
    }

    public void setListener(SyncListener listener) {
        this.listener = listener;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void sync() {
        if (!isNetworkAvailable()) {
            if (listener != null) {
                listener.onSyncFailed("Нет подключения к интернету");
            }
            return;
        }

        executorService.execute(() -> {
            try {
                if (listener != null) listener.onSyncStarted();

                // Шаг 1: Отправляем локальные изменения на сервер
                List<SyncEntry> pendingChanges = syncDao.getPendingSync();
                int uploaded = uploadChanges(pendingChanges);

                // Шаг 2: Загружаем изменения с сервера
                long lastSync = getLastSyncTimestamp();
                int downloaded = downloadChanges(lastSync);

                // Шаг 3: Очищаем синхронизированные записи
                syncDao.deleteSynced();

                if (listener != null) {
                    listener.onSyncCompleted(uploaded, downloaded);
                }

            } catch (Exception e) {
                Log.e(TAG, "Sync failed", e);
                if (listener != null) {
                    listener.onSyncFailed(e.getMessage());
                }
            }
        });
    }

    private int uploadChanges(List<SyncEntry> changes) throws IOException {
        if (changes.isEmpty()) return 0;

        int total = changes.size();
        for (int i = 0; i < changes.size(); i++) {
            SyncEntry entry = changes.get(i);
            if (listener != null) {
                int finalI = i;
                executorService.execute(() -> listener.onSyncProgress(finalI + 1, total));
            }

            // TODO: реальная отправка на сервер
            // Call<ApiService.SyncResponse> call = apiService.uploadChanges(List.of(entry));
            // Response<ApiService.SyncResponse> response = call.execute();

            // Пока просто помечаем как синхронизированные
            entry.setSynced(true);
            syncDao.update(entry);
        }
        return total;
    }

    private int downloadChanges(long lastSync) throws IOException {
        // TODO: загрузить изменения с сервера
        // Call<List<ApiService.RemoteEntry>> call = apiService.downloadChanges(lastSync);
        // Response<List<ApiService.RemoteEntry>> response = call.execute();

        // Пока просто заглушка
        return 0;
    }

    private long getLastSyncTimestamp() {
        // Получаем время последней успешной синхронизации
        return System.currentTimeMillis() - 24 * 60 * 60 * 1000; // Заглушка - сутки назад
    }

    public void addToSyncQueue(String operation, String tableName, int recordId, Object data) {
        executorService.execute(() -> {
            // Проверяем, нет ли уже такой записи в очереди
            SyncEntry existing = syncDao.getByRecordId(tableName, recordId);
            if (existing != null && !existing.isSynced()) {
                // Обновляем существующую
                existing.setData(gson.toJson(data));
                existing.setTimestamp(System.currentTimeMillis());
                existing.setOperation(operation);
                syncDao.update(existing);
            } else {
                // Создаем новую
                SyncEntry entry = new SyncEntry(
                        operation,
                        tableName,
                        recordId,
                        gson.toJson(data),
                        System.currentTimeMillis(),
                        false
                );
                syncDao.insert(entry);
            }
        });
    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}