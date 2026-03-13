// Файл: AppDatabase.java
package com.example.beehive.ui.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.beehive.ui.model.User;
import com.example.beehive.ui.model.Role;
import com.example.beehive.ui.model.Entry;
import com.example.beehive.ui.model.EntryVisibility;

@Database(entities = {User.class, Role.class, Entry.class, EntryVisibility.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract RoleDao roleDao();
    public abstract EntryDao entryDao();
    public abstract EntryVisibilityDao entryVisibilityDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "beehive_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}