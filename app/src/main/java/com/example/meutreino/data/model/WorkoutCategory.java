package com.example.meutreino.data.model;

public enum WorkoutCategory {
    PEITO("Peito", "peito"),
    ABDOMEN("Abdômen", "abdomen"),
    BRACOS("Braços", "bracos"),
    COSTAS("Costas", "costas"),
    OMBROS("Ombros", "ombros"),
    PERNAS("Pernas", "pernas");

    public final String title;
    public final String firestoreKey;

    WorkoutCategory(String title, String firestoreKey) {
        this.title = title;
        this.firestoreKey = firestoreKey;
    }

    public static WorkoutCategory fromKey(String key) {
        for (WorkoutCategory category : values()) {
            if (category.firestoreKey.equalsIgnoreCase(key)) {
                return category;
            }
        }
        return PEITO;
    }
}
