package com.example.meutreino.data.repository;

import com.example.meutreino.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    public interface ProfileCallback {
        void onResult(User user, String error);
    }

    public interface SaveCallback {
        void onResult(boolean success, String error);
    }

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public UserRepository() {
        this(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
    }

    public UserRepository(FirebaseFirestore db, FirebaseAuth auth) {
        this.db = db;
        this.auth = auth;
    }

    private String getUserId() {
        return auth.getCurrentUser() == null ? null : auth.getCurrentUser().getUid();
    }

    public void getUserProfile(ProfileCallback callback) {
        String uid = getUserId();
        if (uid == null) {
            callback.onResult(null, "Usuário não autenticado");
            return;
        }

        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        callback.onResult(new User(uid, "", 0.0, 0.0, 0, ""), null);
                        return;
                    }
                    String nome = document.getString("nome");
                    Object rawBirthDate = document.get("dataNascimento");
                    String dataNascimento = rawBirthDate instanceof String
                            ? (String) rawBirthDate : "";
                    Double peso = parseDouble(document.get("peso"));
                    Double altura = parseDouble(document.get("altura"));
                    Long idade = parseLong(document.get("idade"));
                    callback.onResult(new User(uid, nome == null ? "" : nome,
                            peso == null ? 0.0 : peso,
                            altura == null ? 0.0 : altura,
                            idade == null ? 0 : idade.intValue(),
                            dataNascimento == null ? "" : dataNascimento), null);
                })
                .addOnFailureListener(exception ->
                        callback.onResult(null, exception.getLocalizedMessage()));
    }

    public void saveUserProfile(String nome, double peso, double altura, String dataNascimento,
                                SaveCallback callback) {
        String uid = getUserId();
        if (uid == null) {
            callback.onResult(false, "Usuário não autenticado");
            return;
        }
        Map<String, Object> userData = new HashMap<>();
        userData.put("nome", nome);
        userData.put("peso", peso);
        userData.put("altura", altura);
        userData.put("dataNascimento", dataNascimento);
        userData.put("idade", User.calculateAge(dataNascimento));

        db.collection("usuarios").document(uid).set(userData, SetOptions.merge())
                .addOnSuccessListener(unused -> callback.onResult(true, null))
                .addOnFailureListener(exception ->
                        callback.onResult(false, exception.getLocalizedMessage()));
    }

    public void saveSelectedVideoKey(String videoKey, SaveCallback callback) {
        String uid = getUserId();
        if (uid == null) {
            callback.onResult(false, "Usuário não autenticado");
            return;
        }
        Map<String, Object> update = new HashMap<>();
        update.put("video", videoKey);
        db.collection("usuarios").document(uid).set(update, SetOptions.merge())
                .addOnSuccessListener(unused -> callback.onResult(true, null))
                .addOnFailureListener(exception ->
                        callback.onResult(false, exception.getLocalizedMessage()));
    }

    private static Double parseDouble(Object value) {
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (!(value instanceof String)) return null;
        try {
            return Double.parseDouble(((String) value).replace(',', '.'));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static Long parseLong(Object value) {
        if (value instanceof Number) return ((Number) value).longValue();
        if (!(value instanceof String)) return null;
        try {
            return Long.parseLong((String) value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
