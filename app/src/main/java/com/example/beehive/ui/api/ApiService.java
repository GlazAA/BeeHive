// относится к Entry.java, EntryVisibility.java и SyncEntry.java
package com.example.beehive.ui.api;

import com.example.beehive.ui.model.Entry;
import com.example.beehive.ui.model.EntryVisibility;
import com.example.beehive.ui.model.SyncEntry;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("sync/upload")
    Call<SyncResponse> uploadChanges(@Body List<SyncEntry> changes);

    @GET("sync/download")
    Call<List<RemoteEntry>> downloadChanges(@Query("lastSync") long lastSyncTimestamp);

    class SyncResponse {
        private boolean success;
        private String message;
        private List<Long> syncedIds;

        // геттеры и сеттеры
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<Long> getSyncedIds() { return syncedIds; }
        public void setSyncedIds(List<Long> syncedIds) { this.syncedIds = syncedIds; }
    }

    class RemoteEntry {
        private int id;
        private String title;
        private String type;
        private String login;
        private String encryptedPassword;
        private String comment;
        private String url;
        private String startTime;
        private String daysOfWeek;
        private int ownerUserId;
        private boolean isDeleted;
        private long updatedAt;

        // геттеры и сеттеры
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }
        public String getEncryptedPassword() { return encryptedPassword; }
        public void setEncryptedPassword(String encryptedPassword) { this.encryptedPassword = encryptedPassword; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getDaysOfWeek() { return daysOfWeek; }
        public void setDaysOfWeek(String daysOfWeek) { this.daysOfWeek = daysOfWeek; }
        public int getOwnerUserId() { return ownerUserId; }
        public void setOwnerUserId(int ownerUserId) { this.ownerUserId = ownerUserId; }
        public boolean isDeleted() { return isDeleted; }
        public void setDeleted(boolean deleted) { isDeleted = deleted; }
        public long getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    }
}