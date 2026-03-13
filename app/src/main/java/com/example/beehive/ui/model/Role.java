package com.example.beehive.ui.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "roles")
public class Role {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;

    public Role(String name) {
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}