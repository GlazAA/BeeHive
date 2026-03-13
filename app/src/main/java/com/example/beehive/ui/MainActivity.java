// Файл: MainActivity.java
// Пакет: com.example.beehive.ui
// Связан с: activity_main.xml, MainPagerAdapter.java, LoginActivity.java
// Методы: onCreate(Bundle savedInstanceState), setupViewPager(), setupFabButtons()
// Назначение: главный экран с вкладками. Получает данные пользователя из Intent.

package com.example.beehive.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.beehive.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private MainPagerAdapter pagerAdapter;
    private FloatingActionButton fabAdd, fabSync;

    private String currentUsername; // Имя текущего пользователя

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Получаем имя пользователя из Intent
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            currentUsername = "Гость"; // На случай, если что-то пошло не так
        }

        // Устанавливаем тулбар с приветствием
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("BeeHive - " + currentUsername);
        }

        initViews();
        setupViewPager();
        setupFabButtons();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        fabAdd = findViewById(R.id.fabAdd);
        fabSync = findViewById(R.id.fabSync);
    }

    private void setupViewPager() {
        pagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Привязываем TabLayout к ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Всё");
                            break;
                        case 1:
                            tab.setText("Занятия");
                            break;
                        case 2:
                            tab.setText("Пароли");
                            break;
                    }
                }).attach();
    }

    private void setupFabButtons() {
        fabAdd.setOnClickListener(v -> {
            Toast.makeText(this, "Добавить запись (будет реализовано)", Toast.LENGTH_SHORT).show();
            // TODO: открывать экран создания записи
        });

        fabSync.setOnClickListener(v -> {
            Toast.makeText(this, "Синхронизация... (будет реализовано)", Toast.LENGTH_SHORT).show();
            // TODO: запускать синхронизацию
        });
    }
}