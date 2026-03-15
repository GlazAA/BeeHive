// Файл: EntriesAdapter.java
// Назначение: Адаптер для RecyclerView, отображающий список записей.
package com.example.beehive.ui.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Paint;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
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
    private final OnEntryClickListener listener;

    public interface OnEntryClickListener {
        void onEditClick(Entry entry);
    }

    public EntriesAdapter(List<Entry> entries, String userRole, EntryRepository entryRepository, OnEntryClickListener listener) {
        this.entries = entries;
        this.userRole = userRole;
        this.entryRepository = entryRepository;
        this.listener = listener;
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
        holder.bind(entry, userRole, listener);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
    
    public void setEntries(List<Entry> newEntries) {
        this.entries = newEntries;
        notifyDataSetChanged();
    }

    class EntryViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle, textLogin, textPassword;
        private ImageButton btnDelete, btnRestore, btnEdit;
        private ImageButton btnCopyTitle, btnCopyLogin, btnCopyPassword, btnTogglePasswordVisibility;
        private boolean isPasswordVisible = false;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textLogin = itemView.findViewById(R.id.textLogin);
            textPassword = itemView.findViewById(R.id.textPassword);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnRestore = itemView.findViewById(R.id.btnRestore);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnCopyTitle = itemView.findViewById(R.id.btnCopyTitle);
            btnCopyLogin = itemView.findViewById(R.id.btnCopyLogin);
            btnCopyPassword = itemView.findViewById(R.id.btnCopyPassword);
            btnTogglePasswordVisibility = itemView.findViewById(R.id.btnTogglePasswordVisibility);
        }

        public void bind(final Entry entry, String userRole, final OnEntryClickListener listener) {
            textTitle.setText(entry.getTitle());
            textLogin.setText(entry.getLogin());
            
            // Устанавливаем начальное состояние пароля
            setPasswordVisibility(false, entry.getEncryptedPassword());

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

            // Обработка нажатий
            btnDelete.setOnClickListener(v -> {
                int currentPosition = getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    if ("Child".equals(userRole)) {
                        // entryRepository.markAsDeleted(entry.getId(), true);
                        entry.setDeleted(true);
                        notifyItemChanged(currentPosition);
                    } else {
                        // entryRepository.deletePermanently(String.valueOf(entry.getId()));
                        entries.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                        notifyItemRangeChanged(currentPosition, entries.size());
                    }
                }
            });

            btnRestore.setOnClickListener(v -> {
                 int currentPosition = getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    // entryRepository.markAsDeleted(entry.getId(), false);
                    entry.setDeleted(false);
                    notifyItemChanged(currentPosition);
                }
            });

            btnEdit.setOnClickListener(v -> listener.onEditClick(entry));
            itemView.setOnClickListener(v -> listener.onEditClick(entry));

            btnCopyTitle.setOnClickListener(v -> copyToClipboard("Title", entry.getTitle()));
            btnCopyLogin.setOnClickListener(v -> copyToClipboard("Login", entry.getLogin()));
            btnCopyPassword.setOnClickListener(v -> copyToClipboard("Password", entry.getEncryptedPassword()));
            
            btnTogglePasswordVisibility.setOnClickListener(v -> {
                isPasswordVisible = !isPasswordVisible;
                setPasswordVisibility(isPasswordVisible, entry.getEncryptedPassword());
            });
        }

        private void setPasswordVisibility(boolean isVisible, String password) {
            if (isVisible) {
                textPassword.setText(password);
                btnTogglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off);
            } else {
                textPassword.setText("***");
                btnTogglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on);
            }
        }

        private void copyToClipboard(String label, String text) {
            ClipboardManager clipboard = (ClipboardManager) itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(itemView.getContext(), label + " copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }
}
