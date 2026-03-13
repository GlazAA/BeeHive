package com.example.beehive.ui.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "entry_visibility",
        foreignKeys = {
                @ForeignKey(entity = Entry.class,
                        parentColumns = "id",
                        childColumns = "entryId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("userId")}, // ДОБАВЛЕН ИНДЕКС
        primaryKeys = {"entryId", "userId"})
public class EntryVisibility {
    private int entryId;
    private int userId;
    private boolean canView;
    private boolean canViewPassword;
    private boolean canViewComment;

    public EntryVisibility(int entryId, int userId, boolean canView, boolean canViewPassword, boolean canViewComment) {
        this.entryId = entryId;
        this.userId = userId;
        this.canView = canView;
        this.canViewPassword = canViewPassword;
        this.canViewComment = canViewComment;
    }

    // Геттеры и сеттеры
    public int getEntryId() { return entryId; }
    public void setEntryId(int entryId) { this.entryId = entryId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public boolean isCanView() { return canView; }
    public void setCanView(boolean canView) { this.canView = canView; }
    public boolean isCanViewPassword() { return canViewPassword; }
    public void setCanViewPassword(boolean canViewPassword) { this.canViewPassword = canViewPassword; }
    public boolean isCanViewComment() { return canViewComment; }
    public void setCanViewComment(boolean canViewComment) { this.canViewComment = canViewComment; }
}