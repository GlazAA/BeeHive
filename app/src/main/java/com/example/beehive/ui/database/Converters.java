// Файл: Converters.java
// Пакет: com.example.beehive.database
// Назначение: Преобразует сложные типы в простые для сохранения в БД и обратно.

package com.example.beehive.ui.database;

import androidx.room.TypeConverter;
import com.example.beehive.ui.model.EntryType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Converters {

    // --- Конвертеры для EntryType ---
    @TypeConverter
    public static String fromEntryType(EntryType type) {
        return type == null ? null : type.getType();
    }

    @TypeConverter
    public static EntryType toEntryType(String type) {
        return EntryType.fromString(type);
    }

    // --- Конвертеры для List<Integer> (дни недели) ---
    // Сохраняем список как строку "1,2,3"
    @TypeConverter
    public static String fromIntegerList(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        // Преобразуем каждый элемент в строку и объединяем через запятую
        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    @TypeConverter
    public static List<Integer> toIntegerList(String data) {
        if (data == null || data.trim().isEmpty()) {
            return Arrays.asList(); // Пустой список
        }
        // Разбиваем строку, преобразуем каждую часть в Integer и собираем список
        return Arrays.stream(data.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}/////////////////////////