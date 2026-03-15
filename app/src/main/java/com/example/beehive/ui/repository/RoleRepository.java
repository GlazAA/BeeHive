// Файл: RoleRepository.java
// Пакет: com.example.beehive.repository
// Связан с: RoleDao.java, AppDatabase.java
// Назначение: работа с ролями.

package com.example.beehive.ui.repository;

import android.content.Context;
import com.example.beehive.ui.database.AppDatabase;
import com.example.beehive.ui.database.RoleDao;
import com.example.beehive.ui.model.Role;

public class RoleRepository {

    private final RoleDao roleDao;

    public RoleRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.roleDao = db.roleDao();
    }

    public Role getRoleByName(String name) {
        return roleDao.getRoleByName(name);
    }

    public Role getRoleById(int roleId) {
        return roleDao.getRoleById(roleId);
    }

    public int getRoleIdByName(String name) {
        return roleDao.getRoleIdByName(name);
    }

    public void createDefaultRoles() {
        // Этот метод лучше вызывать в фоновом потоке
        if (roleDao.getRoleByName("Admin") == null) {
            roleDao.insert(new Role("Admin"));
        }
        if (roleDao.getRoleByName("Child") == null) {
            roleDao.insert(new Role("Child"));
        }
    }
}
