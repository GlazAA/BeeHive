// Файл: MainPagerAdapter.java
// Пакет: com.example.beehive.ui
// Связан с: MainActivity.java, EntriesFragment.java
// Методы: createFragment(int position), getItemCount()
// Назначение: адаптер для ViewPager2, создает фрагменты для трех вкладок

package com.example.beehive.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPagerAdapter extends FragmentStateAdapter {

    // Конструктор принимает FragmentActivity (в нашем случае MainActivity)
    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // В зависимости от позиции создаем фрагмент с нужным режимом
        switch (position) {
            case 0:
                // Вкладка "Всё" - показываем все типы записей
                return EntriesFragment.newInstance("all");
            case 1:
                // Вкладка "Занятия" - только занятия
                return EntriesFragment.newInstance("lesson");
            case 2:
                // Вкладка "Пароли" - только пароли
                return EntriesFragment.newInstance("password");
            default:
                // По умолчанию показываем всё
                return EntriesFragment.newInstance("all");
        }
    }

    @Override
    public int getItemCount() {
        // У нас три вкладки: Все, Занятия, Пароли
        return 3;
    }
}