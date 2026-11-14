package com.example.meutreino.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {
    public interface ResultCallback {
        void onResult(boolean success, String error);
    }

    private final FirebaseAuth auth;

    public AuthRepository() {
        this(FirebaseAuth.getInstance());
    }

    public AuthRepository(FirebaseAuth auth) {
        this.auth = auth;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public void login(String email, String password, ResultCallback callback) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onResult(true, null);
            } else {
                callback.onResult(false, task.getException() == null
                        ? null : task.getException().getLocalizedMessage());
            }
        });
    }

    public void register(String email, String password, ResultCallback callback) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onResult(true, null);
            } else {
                callback.onResult(false, task.getException() == null
                        ? null : task.getException().getLocalizedMessage());
            }
        });
    }

    public void resetPassword(String email, ResultCallback callback) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onResult(true, null);
            } else {
                callback.onResult(false, task.getException() == null
                        ? null : task.getException().getLocalizedMessage());
            }
        });
    }

    public void logout() {
        auth.signOut();
    }
}
