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
    long insert(Entry entry);

    @Update
    void update(Entry entry);

    @Query("SELECT * FROM entries WHERE ownerUserId = :userId")
    List<Entry> getEntriesByUser(int userId);

    @Query("SELECT * FROM entries WHERE id = :entryId")
    Entry getEntryById(int entryId);

    // Получаем все неудаленные записи. Используется для роли "Admin".
    @Query("SELECT * FROM entries WHERE isDeleted = 0")
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
    
    // Получение записей, видимых для "Ребенка"
    // Включает записи, созданные им, и те, на которые есть разрешение в EntryVisibility
    @Query("SELECT * FROM entries WHERE isDeleted = 0 AND (ownerUserId = :userId OR id IN (SELECT entryId FROM entry_visibility WHERE userId = :userId AND canView = 1))")
    List<Entry> getVisibleEntriesForChild(int userId);

    @Query("DELETE FROM entry_visibility WHERE entryId = :entryId")
    void deleteEntryVisibility(int entryId);

    @Query("INSERT INTO entry_visibility (entryId, userId, canView) VALUES (:entryId, :userId, 1)")
    void insertEntryVisibility(int entryId, int userId);
}
