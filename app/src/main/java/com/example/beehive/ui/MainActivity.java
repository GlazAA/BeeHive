// отвечает за главный экран
// относится к SyncManager.java, SyncScheduler.java, SyncWorker.java, SyncEntry.java, SyncDao.java, SyncQueue.java
// логика

package com.example.beehive.ui;

import android.os.Bundle;
import android.widget.Toast;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.beehive.R;
import com.example.beehive.ui.sync.SyncManager;
import com.example.beehive.ui.sync.SyncScheduler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private MainPagerAdapter pagerAdapter;
    private FloatingActionButton fabAdd, fabSync;
    private SyncManager syncManager;
    private TextView syncStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initSyncManager();
        setupViewPager();
        setupFabButtons();
        
        // Запускаем фоновую синхронизацию
        //SyncScheduler.schedulePeriodicSync(this);
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        fabAdd = findViewById(R.id.fabAdd);
        fabSync = findViewById(R.id.fabSync);
        syncStatusText = findViewById(R.id.syncStatusText);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("BeeHive");
        }
    }

    private void initSyncManager() {
        syncManager = new SyncManager(this);
        syncManager.setListener(new SyncManager.SyncListener() {
            @Override
            public void onSyncStarted() {
                runOnUiThread(() -> {
                    syncStatusText.setText("Синхронизация...");
                    syncStatusText.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onSyncProgress(int current, int total) {
                runOnUiThread(() -> {
                    syncStatusText.setText("Синхронизация: " + current + "/" + total);
                });
            }

            @Override
            public void onSyncCompleted(int uploaded, int downloaded) {
                runOnUiThread(() -> {
                    syncStatusText.setText("Синхронизация завершена");
                    syncStatusText.postDelayed(() -> 
                        syncStatusText.setVisibility(View.GONE), 2000);
                    Toast.makeText(MainActivity.this, 
                        "Отправлено: " + uploaded + ", Получено: " + downloaded, 
                        Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onSyncFailed(String error) {
                runOnUiThread(() -> {
                    syncStatusText.setText("Ошибка: " + error);
                    syncStatusText.postDelayed(() -> 
                        syncStatusText.setVisibility(View.GONE), 3000);
                });
            }
        });
    }

    private void setupViewPager() {
        pagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

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
        if (fabAdd != null) {
            fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Получаем текущего пользователя (нужно передать из LoginActivity)
                    int userId = 1; // Временно
                    String userRole = "Admin"; // Временно
                    AddEntryDialog dialog = AddEntryDialog.newInstance(userId, userRole);
                    dialog.show(getSupportFragmentManager(), "AddEntryDialog");
                }
            });
        }

        if (fabSync != null) {
            fabSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (syncManager.isNetworkAvailable()) {
                        syncManager.sync();
                    } else {
                        Toast.makeText(MainActivity.this, 
                            "Нет интернета. Синхронизация отложена", 
                            Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (syncManager != null) {
            syncManager.shutdown();
        }
    }
}