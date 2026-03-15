// Файл: EntryDetailFragment.java
// Назначение: Фрагмент для создания и редактирования записей.
package com.example.beehive.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.beehive.R;
import com.example.beehive.ui.model.Entry;
import com.example.beehive.ui.model.EntryType;
import com.example.beehive.ui.repository.EntryRepository;
import com.google.android.material.textfield.TextInputEditText;

public class EntryDetailFragment extends Fragment {

    private TextInputEditText editTitle, editLogin, editPassword;
    private Button btnSave;
    private EntryRepository entryRepository;
    private Entry currentEntry;
    private String userId;

    public EntryDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entryRepository = new EntryRepository();
        if (getArguments() != null) {
            currentEntry = EntryDetailFragmentArgs.fromBundle(getArguments()).getEntry();
            userId = EntryDetailFragmentArgs.fromBundle(getArguments()).getUserId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entry_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTitle = view.findViewById(R.id.editTitle);
        editLogin = view.findViewById(R.id.editLogin);
        editPassword = view.findViewById(R.id.editPassword);
        btnSave = view.findViewById(R.id.btnSave);

        if (currentEntry != null) {
            editTitle.setText(currentEntry.getTitle());
            editLogin.setText(currentEntry.getLogin());
            editPassword.setText(currentEntry.getEncryptedPassword());
        }

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String login = editLogin.getText().toString();
            String password = editPassword.getText().toString();

            if (currentEntry != null) {
                // Update existing entry
                currentEntry.setTitle(title);
                currentEntry.setLogin(login);
                currentEntry.setEncryptedPassword(password);
                entryRepository.updateEntry(currentEntry);
            } else {
                // Create new entry
                Entry newEntry = new Entry(title, EntryType.PASSWORD, login, password, "", "", "", "", Integer.parseInt(userId), false);
                entryRepository.addEntry(newEntry);
            }
            NavController navController = Navigation.findNavController(view);
            navController.navigateUp();
        });
    }
}
