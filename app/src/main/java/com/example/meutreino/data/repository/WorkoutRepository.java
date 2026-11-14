package com.example.meutreino.data.repository;

import com.example.meutreino.data.model.Exercise;
import com.example.meutreino.data.model.ExerciseCatalog;
import com.example.meutreino.data.model.WorkoutCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutRepository {
    public interface ExercisesCallback {
        void onResult(List<Exercise> exercises, String error);
    }

    public interface SaveCallback {
        void onResult(boolean success, String error);
    }

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public WorkoutRepository() {
        this(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
    }

    public WorkoutRepository(FirebaseFirestore db, FirebaseAuth auth) {
        this.db = db;
        this.auth = auth;
    }

    private String getUserId() {
        return auth.getCurrentUser() == null ? null : auth.getCurrentUser().getUid();
    }

    public void getExercisesForCategory(WorkoutCategory category, ExercisesCallback callback) {
        String uid = getUserId();
        if (uid == null) {
            callback.onResult(new ArrayList<>(), "Usuário não autenticado");
            return;
        }

        List<Exercise> categoryExercises = ExerciseCatalog.getByCategory(category);
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(document -> {
                    List<Exercise> result = new ArrayList<>();
                    for (Exercise exercise : categoryExercises) {
                        Exercise copy = exercise.copy();
                        if (document.exists()) {
                            copy.isSelecionado = "Selecionado".equals(
                                    document.getString(copy.firestoreKey));
                            Long savedTotal = document.getLong(getSeriesField(copy));
                            if (savedTotal != null && savedTotal >= 1 && savedTotal <= 20) {
                                copy.seriesTotal = savedTotal.intValue();
                            }
                        }
                        result.add(copy);
                    }
                    callback.onResult(result, null);
                })
                .addOnFailureListener(exception -> callback.onResult(categoryExercises, null));
    }

    public void toggleExerciseSelection(Exercise exercise, boolean selected,
                                       SaveCallback callback) {
        String uid = getUserId();
        if (uid == null) {
            callback.onResult(false, "Usuário não autenticado");
            return;
        }
        Map<String, Object> update = new HashMap<>();
        update.put(exercise.firestoreKey, selected ? "Selecionado" : "Excluído");
        db.collection("usuarios").document(uid).set(update, SetOptions.merge())
                .addOnSuccessListener(unused -> callback.onResult(true, null))
                .addOnFailureListener(exception ->
                        callback.onResult(false, exception.getLocalizedMessage()));
    }

    public void saveSeriesTotal(Exercise exercise, int total, SaveCallback callback) {
        String uid = getUserId();
        if (uid == null) {
            callback.onResult(false, "Usuário não autenticado");
            return;
        }
        if (total < 1 || total > 20) {
            callback.onResult(false, "Escolha entre 1 e 20 séries");
            return;
        }
        Map<String, Object> update = new HashMap<>();
        update.put(getSeriesField(exercise), total);
        db.collection("usuarios").document(uid).set(update, SetOptions.merge())
                .addOnSuccessListener(unused -> callback.onResult(true, null))
                .addOnFailureListener(exception ->
                        callback.onResult(false, exception.getLocalizedMessage()));
    }

    private static String getSeriesField(Exercise exercise) {
        return exercise.firestoreKey + "_seriesTotal";
    }
}
