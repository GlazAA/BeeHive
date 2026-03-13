// Файл: UserRepository.java
// Пакет: com.example.beehive.repository
// Связан с: UserDao.java, AppDatabase.java, LoginActivity.java
// Методы: getUserByUsername(String username), checkUserPassword(String username, String password), createDefaultUsers()
// Назначение: работа с пользователями.

package com.example.beehive.ui.repository;

import android.content.Context;
import com.example.beehive.ui.database.AppDatabase;  // ВАЖНО: добавить ui.
import com.example.beehive.ui.database.UserDao;      // ВАЖНО: добавить ui.
import com.example.beehive.ui.model.Role;            // ВАЖНО: добавить ui.
import com.example.beehive.ui.model.User;            // ВАЖНО: добавить ui.
import com.example.beehive.ui.security.PasswordHasher; // ВАЖНО: добавить ui.
import java.util.List;

public class UserRepository {

    private final UserDao userDao;
    private final RoleRepository roleRepository; // Создадим его позже

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.userDao = db.userDao();
        this.roleRepository = new RoleRepository(context);
    }

    // Получить пользователя по логину
    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    // Проверить пароль пользователя
    public boolean checkUserPassword(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null) {
            return false;
        }
        // Проверяем хеш пароля
        return PasswordHasher.checkPassword(password, user.getPasswordHash());
    }

    // Создать пользователя (с хешированием пароля)
    public void createUser(String username, String plainPassword, String roleName) {
        // Получаем ID роли
        Role role = roleRepository.getRoleByName(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Роль не найдена: " + roleName);
        }

        // Хешируем пароль
        String passwordHash = PasswordHasher.hashPassword(plainPassword);

        // Создаем и сохраняем пользователя
        User user = new User(username, passwordHash, role.getId());
        userDao.insert(user);
    }

    // Получить роль пользователя по ID
    public int getUserRoleId(int userId) {
        return userDao.getRoleIdByUserId(userId);
    }

    // Вспомогательный метод для создания тестовых пользователей при первом запуске
    public void createDefaultUsersIfNeeded() {
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            // Создаем роли (если их нет)
            roleRepository.createDefaultRoles();

            // Создаем Админа (папа/мама)
            createUser("father", "f", "Admin");
            createUser("mother", "m", "Admin");
            // Создаем ребенка
            createUser("child", "c", "Child");
        }
    }
}