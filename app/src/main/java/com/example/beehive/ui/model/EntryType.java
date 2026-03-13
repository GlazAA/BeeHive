package com.example.beehive.ui.model;

public enum EntryType {
    PASSWORD("password"),
    LESSON("lesson");

    private final String type;

    EntryType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static EntryType fromString(String type) {
        for (EntryType et : EntryType.values()) {
            if (et.type.equals(type)) {
                return et;
            }
        }
        return PASSWORD;
    }
}