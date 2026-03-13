// отвечает за синхронизацию данных в фоновом режиме
// относится к SyncManager.java

package com.example.beehive.ui.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SyncWorker extends Worker {
    private static final String TAG = "SyncWorker";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Background sync started");
        
        SyncManager syncManager = new SyncManager(getApplicationContext());
        
        if (!syncManager.isNetworkAvailable()) {
            Log.d(TAG, "No network, retrying later");
            return Result.retry();
        }

        try {
            syncManager.sync();
            Log.d(TAG, "Background sync completed");
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Background sync failed", e);
            return Result.retry();
        }
    }
}