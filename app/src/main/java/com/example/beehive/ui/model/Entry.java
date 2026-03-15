// Файл: Entry.java
// Пакет: com.example.beehive.ui.model
// Назначение: Модель для записи (пароль или занятие)

package com.example.beehive.ui.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.beehive.ui.database.Converters;
import java.io.Serializable;

@Entity(tableName = "entries")
@TypeConverters({Converters.class})
public class Entry implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private EntryType type;
    private String login;
    private String encryptedPassword;
    private String comment;
    private String url;
    private String startTime;
    private String daysOfWeek;
    private int ownerUserId;
    private boolean isDeleted;

    // Конструктор
    public Entry(String title, EntryType type, String login, String encryptedPassword, 
                 String comment, String url, String startTime, String daysOfWeek, 
                 int ownerUserId, boolean isDeleted) {
        this.title = title;
        this.type = type;
        this.login = login;
        this.encryptedPassword = encryptedPassword;
        this.comment = comment;
        this.url = url;
        this.startTime = startTime;
        this.daysOfWeek = daysOfWeek;
        this.ownerUserId = ownerUserId;
        this.isDeleted = isDeleted;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public EntryType getType() { return type; }
    public void setType(EntryType type) { this.type = type; }

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
}