// Файл: SyncDao.java
package com.example.beehive.ui.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.beehive.ui.model.SyncEntry;

import java.util.List;

@Dao
public interface SyncDao {

    @Insert
    void insert(SyncEntry syncEntry);

    @Update
    void update(SyncEntry syncEntry);

    @Query("SELECT * FROM sync_queue WHERE synced = 0 ORDER BY timestamp ASC")
    List<SyncEntry> getPendingSync();

    @Query("DELETE FROM sync_queue WHERE synced = 1")
    void deleteSynced();

    @Query("SELECT * FROM sync_queue WHERE tableName = :tableName AND recordId = :recordId")
    SyncEntry getByRecordId(String tableName, int recordId);
}