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
import com.example.beehive.ui.sync.SyncScheduler;

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

        // ВАЖНО: создаем пользователей в фоновом потоке!
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
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Это выполняется в фоновом потоке - безопасно для БД
                userRepository.createDefaultUsersIfNeeded();
            }
        });
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

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                boolean isPasswordCorrect = userRepository.checkUserPassword(username, password);

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isPasswordCorrect) {
                            User currentUser = userRepository.getUserByUsername(username);
                            
                            if (currentUser != null) {
                                Toast.makeText(LoginActivity.this, 
                                    "Добро пожаловать, " + username + "!", 
                                    Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("username", username);
                                intent.putExtra("userId", currentUser.getId());
                                
                                int roleId = userRepository.getUserRoleId(currentUser.getId());
                                intent.putExtra("userRole", roleId == 1 ? "Admin" : "Child");
                                
                                startActivity(intent);
                                finish();
                            } else {
                                tvError.setVisibility(View.VISIBLE);
                                tvError.setText("Ошибка при загрузке пользователя");
                                btnLogin.setEnabled(true);
                            }
                        } else {
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
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}