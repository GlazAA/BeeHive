// Файл: EntriesAdapter.java
// Назначение: Адаптер для RecyclerView, отображающий список записей.
package com.example.beehive.ui.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.beehive.R;
import com.example.beehive.ui.model.Entry;
import com.example.beehive.ui.repository.EntryRepository;
import java.util.List;

public class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.EntryViewHolder> {

    private List<Entry> entries;
    private String userRole;
    private EntryRepository entryRepository;

    public EntriesAdapter(List<Entry> entries, String userRole, EntryRepository entryRepository) {
        this.entries = entries;
        this.userRole = userRole;
        this.entryRepository = entryRepository;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        Entry entry = entries.get(position);
        holder.bind(entry, userRole);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    class EntryViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle, textLogin, textPassword;
        private ImageButton btnDelete, btnRestore;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textLogin = itemView.findViewById(R.id.textLogin);
            textPassword = itemView.findViewById(R.id.textPassword);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnRestore = itemView.findViewById(R.id.btnRestore);
        }

        public void bind(final Entry entry, String userRole) {
            textTitle.setText(entry.getTitle());
            textLogin.setText(entry.getLogin());

            // Скрытие/отображение пароля в зависимости от роли
            if ("Child".equals(userRole)) {
                textPassword.setText("***");
            } else {
                textPassword.setText(entry.getEncryptedPassword());
            }
            
            // Обработка "мягкого" удаления
            if (entry.isDeleted()) {
                textTitle.setPaintFlags(textTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                btnDelete.setVisibility(View.GONE);
                if ("Admin".equals(userRole)) {
                    btnRestore.setVisibility(View.VISIBLE);
                }
            } else {
                textTitle.setPaintFlags(textTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                btnDelete.setVisibility(View.VISIBLE);
                btnRestore.setVisibility(View.GONE);
            }

            // Обработка нажатий на кнопки
            btnDelete.setOnClickListener(v -> {
                if ("Child".equals(userRole)) {
                    entryRepository.markAsDeleted(entry.getId());
                } else {
                    entryRepository.deletePermanently(entry.getId());
                }
                // Обновляем UI
                int currentPosition = getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                     if ("Child".equals(userRole)) {
                        entry.setDeleted(true);
                        notifyItemChanged(currentPosition);
                    } else {
                        entries.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                    }
                }
            });

            btnRestore.setOnClickListener(v -> {
                entryRepository.restore(entry.getId());
                // Обновляем UI
                int currentPosition = getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    entry.setDeleted(false);
                    notifyItemChanged(currentPosition);
                }
            });
        }
    }
}
