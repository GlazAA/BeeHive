// Файл: UserRepository.java
// Пакет: com.example.beehive.ui.repository
// Связан с: UserDao.java, AppDatabase.java, LoginActivity.java
// Методы: getUserByUsername, checkUserPassword, createDefaultUsers
// Назначение: работа с пользователями

package com.example.beehive.ui.repository;

import android.content.Context;
import com.example.beehive.ui.database.AppDatabase;
import com.example.beehive.ui.database.UserDao;
import com.example.beehive.ui.model.Role;
import com.example.beehive.ui.model.User;
import com.example.beehive.ui.security.PasswordHasher;
import java.util.List;

public class UserRepository {

    private final UserDao userDao;
    private final RoleRepository roleRepository;

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.userDao = db.userDao();
        this.roleRepository = new RoleRepository(context);
    }

    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public boolean checkUserPassword(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null) {
            return false;
        }
        return PasswordHasher.checkPassword(password, user.getPasswordHash());
    }

    public void createUser(String username, String plainPassword, String roleName) {
        Role role = roleRepository.getRoleByName(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Роль не найдена: " + roleName);
        }

        String passwordHash = PasswordHasher.hashPassword(plainPassword);
        User user = new User(username, passwordHash, role.getId());
        userDao.insert(user);
    }

    public int getUserRoleId(int userId) {
        return userDao.getRoleIdByUserId(userId);
    }

    public String getRoleNameById(int roleId) {
        Role role = roleRepository.getRoleById(roleId);
        return role != null ? role.getName() : null;
    }

    public void createDefaultUsersIfNeeded() {
        // Этот метод вызывается в фоновом потоке из LoginActivity
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            roleRepository.createDefaultRoles();
            createUser("father", "f", "Admin");
            createUser("mother", "m", "Admin");
            createUser("child", "c", "Child");
        }
    }
}