// Файл: EntryDao.java
// Пакет: com.example.beehive.database
// Связан с: Entry.java, DatabaseClient.java, EntryRepository.java
// Назначение: Операции с таблицей entries.

package com.example.beehive.ui.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.beehive.ui.model.Entry;
import java.util.List;

@Dao
public interface EntryDao {

    @Insert
    void insert(Entry entry);

    @Update
    void update(Entry entry);

    @Query("SELECT * FROM entries WHERE ownerUserId = :userId")
    List<Entry> getEntriesByUser(int userId);

    @Query("SELECT * FROM entries WHERE id = :entryId")
    Entry getEntryById(int entryId);

    // Временный метод для получения всех записей. Позже будем фильтровать через видимость.
    @Query("SELECT * FROM entries")
    List<Entry> getAllEntries();

    // Пометить на удаление (soft delete)
    @Query("UPDATE entries SET isDeleted = 1 WHERE id = :entryId")
    void markAsDeleted(int entryId);

    // Восстановить после мягкого удаления
    @Query("UPDATE entries SET isDeleted = 0 WHERE id = :entryId")
    void restore(int entryId);

    // Полное удаление (для родителей)
    @Query("DELETE FROM entries WHERE id = :entryId")
    void deletePermanently(int entryId);
}