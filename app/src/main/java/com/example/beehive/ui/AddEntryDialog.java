// Файл: AddEntryDialog.java
// Назначение: Диалоговое окно для добавления и редактирования записей.
package com.example.beehive.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
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
import androidx.core.content.ContextCompat;
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
        setupDayChips();

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

        // Поля для "Пароля"
        editLogin = view.findViewById(R.id.editLoginPassword);
        editPassword = view.findViewById(R.id.editPasswordField);
        editComment = view.findViewById(R.id.editCommentPassword);

        // Поля для "Занятия"
        editUrl = view.findViewById(R.id.editUrl);
        editStartTime = view.findViewById(R.id.editStartTime);

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
        chipGroupVisibility.setSingleSelection(true);
        if ("Child".equals(currentUserRole)) {
            for (int i = 0; i < chipGroupVisibility.getChildCount(); i++) {
                chipGroupVisibility.getChildAt(i).setEnabled(false);
            }
        } else {
             for (int i = 0; i < chipGroupVisibility.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupVisibility.getChildAt(i);
                chip.setCheckable(true);
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                     updateChipAppearance(chip, isChecked);
                });
            }
        }
    }

    private void setupDayChips(){
        for (int i = 0; i < chipGroupDays.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupDays.getChildAt(i);
            chip.setCheckable(true);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateChipAppearance(chip, isChecked);
            });
        }
    }

    private void updateChipAppearance(Chip chip, boolean isChecked) {
        if (isChecked) {
            chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.purple_500)));
            chip.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white)));
        } else {
            chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey)));
            chip.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black)));
        }
    }

    private void saveEntry() {
        String title = editTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Введите название", Toast.LENGTH_SHORT).show();
            return;
        }

        EntryType type = spinnerType.getSelectedItemPosition() == 0 ? EntryType.PASSWORD : EntryType.LESSON;
        String login = "";
        String password = "";
        String comment = "";
        String url = "";
        String startTime = "";
        String daysString = "";

        if (type == EntryType.PASSWORD) {
            login = editLogin.getText().toString().trim();
            password = editPassword.getText().toString().trim();
            comment = editComment.getText().toString().trim();
        } else {
            url = editUrl.getText().toString().trim();
            startTime = editStartTime.getText().toString().trim();
            // Используем те же поля для логина, пароля и комментария
            login = editLogin.getText().toString().trim(); 
            password = editPassword.getText().toString().trim();
            comment = editComment.getText().toString().trim();
            
            List<Integer> selectedDays = new ArrayList<>();
            for (int id : chipGroupDays.getCheckedChipIds()) {
                Chip chip = chipGroupDays.findViewById(id);
                if (chip != null) {
                    selectedDays.add(convertDayToNumber(chip.getText().toString()));
                }
            }
            daysString = android.text.TextUtils.join(",", selectedDays);
        }

        int visibilityOption = 2; // 2 = 'Только я' по-умолчанию
        int checkedId = chipGroupVisibility.getCheckedChipId();
        if (checkedId != View.NO_ID) {
            Chip selectedChip = chipGroupVisibility.findViewById(checkedId);
            String chipText = selectedChip.getText().toString();
            if (chipText.equalsIgnoreCase("Все")) {
                visibilityOption = 0;
            } else if (chipText.equalsIgnoreCase("Только родители")) {
                visibilityOption = 1;
            }
        }
        
        Entry entry = new Entry(0, title, type.name(), url, login, password, daysString, startTime, comment, false, currentUserId);

        int finalVisibilityOption = visibilityOption;
        executorService.execute(() -> {
            long entryId = entryRepository.insert(entry);
            if (entryId != -1) {
                entryRepository.updateEntryVisibility((int)entryId, finalVisibilityOption, currentUserId);
            }
            
            requireActivity().runOnUiThread(() -> {
                if(entryId != -1){
                    Toast.makeText(getContext(), "Запись сохранена", Toast.LENGTH_SHORT).show();
                    // Обновляем список записей в MainActivity
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).loadEntries();
                    }
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
                }
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
