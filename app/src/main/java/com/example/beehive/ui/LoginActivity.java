// Файл: LoginActivity.java
// Пакет: com.example.beehive.ui
// Связан с: activity_login.xml, UserRepository.java, MainActivity.java
// Методы: onCreate(...), initViews(), setupListeners(), attemptLogin()
// Назначение: Экран входа. Проверяет логин/пароль и запускает MainActivity.

// Файл: LoginActivity.java
package com.example.beehive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.beehive.R;
import com.example.beehive.ui.repository.UserRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private Button btnLogin;
    private TextView tvError;

    private UserRepository userRepository;
    private ExecutorService executorService; // Для фоновых задач
    private Handler mainHandler; // Для возврата в главный поток

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализируем репозиторий
        userRepository = new UserRepository(this);
        
        // Создаем пул потоков (один поток)
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Находим view по id
        initViews();

        // Устанавливаем слушатели
        setupListeners();

        // Создаем тестовых пользователей в фоновом потоке
        createDefaultUsersInBackground();
    }

    private void initViews() {
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void createDefaultUsersInBackground() {
        // Выполняем в фоновом потоке
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Это выполняется в фоновом потоке
                userRepository.createDefaultUsersIfNeeded();
                
                // После завершения можно ничего не делать в UI
            }
        });
    }

    private void attemptLogin() {
        // Получаем введенные данные
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Простейшая валидация
        if (username.isEmpty()) {
            editUsername.setError("Введите логин");
            return;
        }
        if (password.isEmpty()) {
            editPassword.setError("Введите пароль");
            return;
        }

        // Отключаем кнопку, чтобы не было двойного нажатия
        btnLogin.setEnabled(false);
        tvError.setVisibility(View.GONE);

        // Выполняем проверку пароля в фоновом потоке
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Это выполняется в фоновом потоке
                boolean isPasswordCorrect = userRepository.checkUserPassword(username, password);

                // Возвращаемся в главный поток для обновления UI
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isPasswordCorrect) {
                            // Успешный вход
                            Toast.makeText(LoginActivity.this, 
                                "Добро пожаловать, " + username + "!", 
                                Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            finish();
                        } else {
                            // Ошибка входа
                            tvError.setVisibility(View.VISIBLE);
                            tvError.setText("Неверный логин или пароль");
                            btnLogin.setEnabled(true);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Завершаем executor, чтобы не было утечек памяти
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}