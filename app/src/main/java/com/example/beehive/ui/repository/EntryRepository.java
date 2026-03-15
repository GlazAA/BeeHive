// Файл: EntryRepository.java
// Назначение: Репозиторий для управления данными о записях.
package com.example.beehive.ui.repository;

import com.example.beehive.ui.model.Entry;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class EntryRepository {

    //private DatabaseReference databaseReference;

    public interface OnDataReadyCallback {
        void onDataReady(List<Entry> entries);
    }

    public EntryRepository() {
        //databaseReference = FirebaseDatabase.getInstance().getReference("entries");
    }

    public void getEntries(String userId, String filter, OnDataReadyCallback callback) {
        /*
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Entry> entries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Entry entry = snapshot.getValue(Entry.class);
                    if (entry != null) {
                        boolean matches = false;
                        switch (filter) {
                            case "my":
                                if (entry.getUserId().equals(userId)) {
                                    matches = true;
                                }
                                break;
                            case "deleted":
                                if (entry.isDeleted()) {
                                    matches = true;
                                }
                                break;
                            case "all":
                            default:
                                matches = true;
                                break;
                        }
                        if (matches) {
                            entries.add(entry);
                        }
                    }
                }
                callback.onDataReady(entries);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
        */
        if (callback != null) {
            callback.onDataReady(new ArrayList<>());
        }
    }

    public void addEntry(Entry entry) {
        /*
        String id = databaseReference.push().getKey();
        entry.setId(id);
        databaseReference.child(id).setValue(entry);
        */
    }

    public void updateEntry(Entry entry) {
        //databaseReference.child(entry.getId()).setValue(entry);
    }

    public void markAsDeleted(String entryId, boolean deleted) {
        //databaseReference.child(entryId).child("deleted").setValue(deleted);
    }

    public void deletePermanently(String entryId) {
        //databaseReference.child(entryId).removeValue();
    }
}
