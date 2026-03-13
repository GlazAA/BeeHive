// Файл: EntriesFragment.java
// Назначение: Фрагмент для отображения списка записей.
package com.example.beehive.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.beehive.R;
import com.example.beehive.ui.adapters.EntriesAdapter;
import com.example.beehive.ui.model.Entry;
import com.example.beehive.ui.repository.EntryRepository;
import java.util.List;
import java.util.stream.Collectors;

public class EntriesFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";
    private static final String ARG_USER_ROLE = "userRole";
    private static final String ARG_FILTER_TYPE = "filterType";

    private RecyclerView recyclerView;
    private EntriesAdapter adapter;
    private EntryRepository entryRepository;

    private int userId;
    private String userRole;
    private String filterType; // "all", "lesson", "password"

    public static EntriesFragment newInstance(int userId, String userRole, String filterType) {
        EntriesFragment fragment = new EntriesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        args.putString(ARG_USER_ROLE, userRole);
        args.putString(ARG_FILTER_TYPE, filterType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(ARG_USER_ID);
            userRole = getArguments().getString(ARG_USER_ROLE);
            filterType = getArguments().getString(ARG_FILTER_TYPE);
        }
        entryRepository = new EntryRepository(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entries, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewEntries);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadEntries();
    }

    private void loadEntries() {
        entryRepository.getAllEntries(allEntries -> {
            List<Entry> filteredEntries = filterEntries(allEntries);
            requireActivity().runOnUiThread(() -> {
                adapter = new EntriesAdapter(filteredEntries, userRole, entryRepository);
                recyclerView.setAdapter(adapter);
            });
        });
    }

    private List<Entry> filterEntries(List<Entry> entries) {
        return entries.stream()
                .filter(entry -> {
                    // Фильтр по типу (всё, занятие, пароль)
                    boolean typeMatch = filterType.equals("all") || 
                                        (filterType.equals("lesson") && entry.getType().name().equalsIgnoreCase("LESSON")) ||
                                        (filterType.equals("password") && entry.getType().name().equalsIgnoreCase("PASSWORD"));
                    
                    // Фильтр по роли
                    if ("Child".equals(userRole)) {
                        // Ребенок видит только свои записи или те, к которым есть доступ
                        // Эта логика будет добавлена позже, когда будет реализована таблица entry_visibility
                        return typeMatch && !entry.isDeleted();
                    } else {
                        // Родители видят всё
                        return typeMatch;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (entryRepository != null) {
            entryRepository.shutdown();
        }
    }
}
