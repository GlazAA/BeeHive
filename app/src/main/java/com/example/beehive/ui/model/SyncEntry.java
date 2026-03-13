// отвечает за синхронизацию данных модель синхронизации записей в очередь
package com.example.beehive.ui.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sync_queue")
public class SyncEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String operation; // "INSERT", "UPDATE", "DELETE"
    private String tableName; // "entries", "entry_visibility"
    private int recordId;      // ID записи в локальной БД
    private String data;       // JSON с данными
    private long timestamp;    // время создания
    private boolean synced;    // отправлено на сервер?

    public SyncEntry(String operation, String tableName, int recordId, String data, long timestamp, boolean synced) {
        this.operation = operation;
        this.tableName = tableName;
        this.recordId = recordId;
        this.data = data;
        this.timestamp = timestamp;
        this.synced = synced;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }
}