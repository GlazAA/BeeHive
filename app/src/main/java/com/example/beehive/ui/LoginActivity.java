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
import com.example.beehive.ui.model.User;
import com.example.beehive.ui.repository.UserRepository;
import com.example.beehive.ui.security.PasswordHasher; // ДОБАВЛЕН ИМПОРТ

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private Button btnLogin;
    private TextView tvError;

    private UserRepository userRepository;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRepository = new UserRepository(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupListeners();

        createDefaultUsersInBackground();
    }

    private void initViews() {
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void createDefaultUsersInBackground() {
        executorService.execute(() -> userRepository.createDefaultUsersIfNeeded());
    }

    private void attemptLogin() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (username.isEmpty()) {
            editUsername.setError("Введите логин");
            return;
        }
        if (password.isEmpty()) {
            editPassword.setError("Введите пароль");
            return;
        }

        btnLogin.setEnabled(false);
        tvError.setVisibility(View.GONE);

        executorService.execute(() -> {
            User user = userRepository.getUserByUsername(username);
            // Используем PasswordHasher для проверки пароля
            boolean isPasswordCorrect = (user != null) && PasswordHasher.checkPassword(password, user.getPasswordHash());

            if (isPasswordCorrect) {
                int roleId = userRepository.getUserRoleId(user.getId());
                String role = (roleId == 1) ? "Admin" : "Child";

                mainHandler.post(() -> {
                    Toast.makeText(LoginActivity.this, "Добро пожаловать, " + username + "!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("userId", user.getId());
                    intent.putExtra("userRole", role);
                    startActivity(intent);
                    finish();
                });
            } else {
                mainHandler.post(() -> {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Неверный логин или пароль");
                    btnLogin.setEnabled(true);
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
