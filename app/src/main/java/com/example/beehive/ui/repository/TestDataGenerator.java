package com.example.beehive.ui.repository;

import android.content.Context;

import com.example.beehive.ui.database.AppDatabase;
import com.example.beehive.ui.database.EntryDao;
import com.example.beehive.ui.database.EntryVisibilityDao;
import com.example.beehive.ui.model.Entry;
import com.example.beehive.ui.model.EntryType;
import com.example.beehive.ui.model.EntryVisibility;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestDataGenerator {
    private final EntryDao entryDao;
    private final EntryVisibilityDao visibilityDao;
    private final ExecutorService executorService;

    public TestDataGenerator(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.entryDao = db.entryDao();
        this.visibilityDao = db.entryVisibilityDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void generateTestData() {
        executorService.execute(() -> {
            if (entryDao.getAllEntries().isEmpty()) {
                Entry netflix = new Entry(
                    "Netflix", EntryType.PASSWORD, "family@mail.com",
                    "encrypted_netflix123", "Семейный аккаунт",
                    null, null, null, 1, false
                );
                long netflixId = entryDao.insert(netflix);
                visibilityDao.insert(new EntryVisibility((int)netflixId, 1, true, true, true));
                visibilityDao.insert(new EntryVisibility((int)netflixId, 2, true, true, true));
                visibilityDao.insert(new EntryVisibility((int)netflixId, 3, true, false, true));

                Entry bank = new Entry(
                    "Банк", EntryType.PASSWORD, "dad@bank.com",
                    "encrypted_bank456", "Основной счет",
                    null, null, null, 1, false
                );
                long bankId = entryDao.insert(bank);
                visibilityDao.insert(new EntryVisibility((int)bankId, 1, true, true, true));
                visibilityDao.insert(new EntryVisibility((int)bankId, 2, true, true, true));
                visibilityDao.insert(new EntryVisibility((int)bankId, 3, false, false, false));

                Entry english = new Entry(
                    "Английский онлайн", EntryType.LESSON, "student@english.com",
                    "encrypted_english789", "Занятие с репетитором",
                    "https://zoom.us/english", "18:00", "1,3,5", 3, false
                );
                long englishId = entryDao.insert(english);
                visibilityDao.insert(new EntryVisibility((int)englishId, 1, true, true, true));
                visibilityDao.insert(new EntryVisibility((int)englishId, 2, true, true, true));
                visibilityDao.insert(new EntryVisibility((int)englishId, 3, true, false, true));

                Entry sport = new Entry(
                    "Спортзал", EntryType.LESSON, "family@sport.com",
                    "encrypted_sport000", "Семейный абонемент",
                    "https://gym.com", "19:00", "2,4", 1, false
                );
                long sportId = entryDao.insert(sport);
                visibilityDao.insert(new EntryVisibility((int)sportId, 1, true, true, true));
                visibilityDao.insert(new EntryVisibility((int)sportId, 2, true, true, true));
                visibilityDao.insert(new EntryVisibility((int)sportId, 3, true, false, true));
            }
        });
    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}