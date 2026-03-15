// Файл: RoleDao.java
// Пакет: com.example.beehive.database
// Связан с: Role.java, DatabaseClient.java
// Назначение: Операции с таблицей roles.

package com.example.beehive.ui.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.beehive.ui.model.Role;

@Dao
public interface RoleDao {

    @Insert
    void insert(Role role);

    @Query("SELECT * FROM roles WHERE name = :name LIMIT 1")
    Role getRoleByName(String name);

    @Query("SELECT * FROM roles WHERE id = :roleId LIMIT 1")
    Role getRoleById(int roleId);

    @Query("SELECT id FROM roles WHERE name = :name")
    int getRoleIdByName(String name);
}