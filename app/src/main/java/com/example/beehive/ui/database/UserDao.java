// Файл: UserDao.java
// Пакет: com.example.beehive.database
// Связан с: User.java, DatabaseClient.java, UserRepository.java
// Назначение: Операции с таблицей users.

package com.example.beehive.ui.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.beehive.ui.model.User;
import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(int userId);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT roleId FROM users WHERE id = :userId")
    int getRoleIdByUserId(int userId);
}