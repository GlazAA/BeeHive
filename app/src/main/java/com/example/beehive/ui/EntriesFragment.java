// Файл: EntriesFragment.java
// Пакет: com.example.beehive.ui
// Связан с: fragment_entries.xml, MainPagerAdapter.java, EntryRepository.java
// Методы: newInstance(String mode), onCreateView(...), onViewCreated(...), setupRecyclerView(), setupSearch(), setupFilters()
// Назначение: фрагмент со списком записей для разных режимов (все/занятия/пароли)

package com.example.beehive.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beehive.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class EntriesFragment extends Fragment {

    private static final String ARG_MODE = "mode";
    private String currentMode; // "all", "lesson", "password"

    private TextInputEditText inputSearch;
    private MaterialButton btnFilter;
    private MaterialCardView filterPanel;
    private RecyclerView recyclerEntries;
    private TextView tvEmpty;
    private ChipGroup chipGroupUsers, chipGroupDays;
    private MaterialButton btnResetFilters;

    // Адаптер и список данных (временные, потом заменим на настоящие)
    private List<Object> entriesList = new ArrayList<>();
    private boolean isFilterVisible = false;

    public static EntriesFragment newInstance(String mode) {
        EntriesFragment fragment = new EntriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentMode = getArguments().getString(ARG_MODE, "all");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация view
        initViews(view);
        
        // Настройка слушателей
        setupListeners();
        
        // Настройка RecyclerView
        setupRecyclerView();
        
        // Загрузка данных (пока заглушка)
        loadDummyData();
    }

    private void initViews(View view) {
        inputSearch = view.findViewById(R.id.inputSearch);
        btnFilter = view.findViewById(R.id.btnFilter);
        filterPanel = view.findViewById(R.id.filterPanel);
        recyclerEntries = view.findViewById(R.id.recyclerEntries);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        chipGroupUsers = view.findViewById(R.id.chipGroupUsers);
        chipGroupDays = view.findViewById(R.id.chipGroupDays);
        btnResetFilters = view.findViewById(R.id.btnResetFilters);

        // Скрываем панель фильтров по умолчанию
        filterPanel.setVisibility(View.GONE);
    }

    private void setupListeners() {
        // Кнопка фильтра - показывает/скрывает панель
        btnFilter.setOnClickListener(v -> {
            isFilterVisible = !isFilterVisible;
            filterPanel.setVisibility(isFilterVisible ? View.VISIBLE : View.GONE);
        });

        // Поиск при вводе текста
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Кнопка сброса фильтров
        btnResetFilters.setOnClickListener(v -> resetFilters());

        // Слушатель для выбора пользователя в чип-группе
        chipGroupUsers.setOnCheckedStateChangeListener((group, checkedIds) -> {
            applyFilters();
        });

        // Слушатель для выбора дней недели
        chipGroupDays.setOnCheckedStateChangeListener((group, checkedIds) -> {
            applyFilters();
        });
    }

    private void setupRecyclerView() {
        recyclerEntries.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO: установить адаптер, когда он будет создан
        // recyclerEntries.setAdapter(adapter);
    }

    private void loadDummyData() {
        // Временные данные для тестирования
        tvEmpty.setVisibility(entriesList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void performSearch(String query) {
        // TODO: реализовать поиск
        // Пока просто обновляем фильтры
        applyFilters();
    }

    private void applyFilters() {
        // TODO: применить все выбранные фильтры
        // Получаем выбранного пользователя
        int selectedUserId = -1;
        if (chipGroupUsers.getCheckedChipId() != View.NO_ID) {
            Chip selectedChip = chipGroupUsers.findViewById(chipGroupUsers.getCheckedChipId());
            if (selectedChip != null) {
                String userText = selectedChip.getText().toString();
                // Преобразуем текст в ID пользователя
                // TODO: реализовать логику
            }
        }

        // Получаем выбранные дни
        List<Integer> selectedDays = new ArrayList<>();
        for (int id : chipGroupDays.getCheckedChipIds()) {
            Chip chip = chipGroupDays.findViewById(id);
            if (chip != null) {
                String dayText = chip.getText().toString();
                // Преобразуем текст в номер дня (1-7)
                // TODO: реализовать логику
            }
        }

        // TODO: обновить список записей с учетом:
        // - currentMode (all/lesson/password)
        // - search query
        // - selectedUserId
        // - selectedDays
    }

    private void resetFilters() {
        // Сбрасываем выбор пользователя на "Все"
        chipGroupUsers.check(R.id.chipAllUsers);
        
        // Снимаем все выделения с дней недели
        chipGroupDays.clearCheck();
        
        // Очищаем поиск
        inputSearch.setText("");
        
        // Применяем сброшенные фильтры
        applyFilters();
    }

    public void updateData() {
        // Метод для обновления данных извне (например, при синхронизации)
        loadDummyData();
        applyFilters();
    }
}