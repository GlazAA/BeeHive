// Файл: AddEntryDialog.java
package com.example.beehive.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.beehive.R;
import com.example.beehive.ui.model.Entry;
import com.example.beehive.ui.model.EntryType;
import com.example.beehive.ui.repository.EntryRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEntryDialog extends DialogFragment {

    private Spinner spinnerType;
    private LinearLayout layoutPassword, layoutLesson;
    private EditText editTitle, editLogin, editPassword, editComment, editUrl, editStartTime;
    private ChipGroup chipGroupDays, chipGroupVisibility;
    private Button btnSave, btnCancel;

    private int currentUserId;
    private String currentUserRole;
    private EntryRepository entryRepository;
    private ExecutorService executorService;

    public static AddEntryDialog newInstance(int userId, String userRole) {
        AddEntryDialog dialog = new AddEntryDialog();
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        args.putString("userRole", userRole);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserId = getArguments().getInt("userId");
            currentUserRole = getArguments().getString("userRole");
        }
        entryRepository = new EntryRepository(requireContext());
        executorService = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_entry, null);

        initViews(view);
        setupTypeSpinner();
        setupVisibilityChips();

        builder.setView(view)
                .setTitle("Новая запись");

        btnSave.setOnClickListener(v -> saveEntry());
        btnCancel.setOnClickListener(v -> dismiss());

        return builder.create();
    }

    private void initViews(View view) {
        spinnerType = view.findViewById(R.id.spinnerType);
    layoutPassword = view.findViewById(R.id.layoutPassword);
    layoutLesson = view.findViewById(R.id.layoutLesson);
    editTitle = view.findViewById(R.id.editTitle);
    
    // Поля для пароля
    editLogin = view.findViewById(R.id.editLoginPassword);        // Было: editLogin
    editPassword = view.findViewById(R.id.editPasswordField);     // Было: editPassword
    editComment = view.findViewById(R.id.editCommentPassword);    // Было: editComment
    
    // Поля для занятия
    editUrl = view.findViewById(R.id.editUrl);
    editStartTime = view.findViewById(R.id.editStartTime);
    // Для занятия используем другие поля:
    // editLoginLesson, editPasswordLesson, editCommentLesson
    
    chipGroupDays = view.findViewById(R.id.chipGroupDays);
    chipGroupVisibility = view.findViewById(R.id.chipGroupVisibility);
    btnSave = view.findViewById(R.id.btnSave);
    btnCancel = view.findViewById(R.id.btnCancel);
}

    private void setupTypeSpinner() {
        String[] types = {"Пароль", "Занятие"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        spinnerType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Пароль
                    layoutPassword.setVisibility(View.VISIBLE);
                    layoutLesson.setVisibility(View.GONE);
                } else { // Занятие
                    layoutPassword.setVisibility(View.GONE);
                    layoutLesson.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void setupVisibilityChips() {
        // Для ребенка блокируем выбор видимости пароля
        if ("Child".equals(currentUserRole)) {
            for (int i = 0; i < chipGroupVisibility.getChildCount(); i++) {
                chipGroupVisibility.getChildAt(i).setEnabled(false);
            }
        }
    }

    private void saveEntry() {
        String title = editTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Введите название", Toast.LENGTH_SHORT).show();
            return;
        }

        EntryType type = spinnerType.getSelectedItemPosition() == 0 ? EntryType.PASSWORD : EntryType.LESSON;
        String login = editLogin.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String comment = editComment.getText().toString().trim();
        String url = editUrl.getText().toString().trim();
        String startTime = editStartTime.getText().toString().trim();

        // Получаем выбранные дни
        List<Integer> selectedDays = new ArrayList<>();
        for (int id : chipGroupDays.getCheckedChipIds()) {
            Chip chip = chipGroupDays.findViewById(id);
            if (chip != null) {
                String dayText = chip.getText().toString();
                int day = convertDayToNumber(dayText);
                if (day > 0) selectedDays.add(day);
            }
        }
        String daysString = selectedDays.isEmpty() ? "" : android.text.TextUtils.join(",", selectedDays);

        // Получаем видимость
        int visibilityOption = 0; // 0 - все, 1 - только родители, 2 - только я
        if (chipGroupVisibility.getCheckedChipId() != View.NO_ID) {
            Chip selected = chipGroupVisibility.findViewById(chipGroupVisibility.getCheckedChipId());
            if (selected != null) {
                if (selected.getText().equals("Только родители")) visibilityOption = 1;
                else if (selected.getText().equals("Только я")) visibilityOption = 2;
            }
        }

        // Сохраняем в фоне
        int finalVisibilityOption = visibilityOption;
        executorService.execute(() -> {
            // TODO: создать запись в БД с учетом видимости
            // Пока просто заглушка
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Запись добавлена", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        });
    }

    private int convertDayToNumber(String day) {
        switch (day) {
            case "Пн": return 1;
            case "Вт": return 2;
            case "Ср": return 3;
            case "Чт": return 4;
            case "Пт": return 5;
            case "Сб": return 6;
            case "Вс": return 7;
            default: return 0;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}