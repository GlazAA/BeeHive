// Файл: EntryVisibilityDao.java
// Пакет: com.example.beehive.database
// Связан с: EntryVisibility.java, DatabaseClient.java
// Назначение: Операции с таблицей видимости.

package com.example.beehive.ui.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.beehive.ui.model.EntryVisibility;

@Dao
public interface EntryVisibilityDao {

    @Insert
    void insert(EntryVisibility visibility);

    @Update
    void update(EntryVisibility visibility);

    @Query("SELECT * FROM entry_visibility WHERE entryId = :entryId AND userId = :userId LIMIT 1")
    EntryVisibility getVisibilityForUser(int entryId, int userId);

    // Удалить все правила для записи (например, перед обновлением)
    @Query("DELETE FROM entry_visibility WHERE entryId = :entryId")
    void deleteByEntryId(int entryId);
}