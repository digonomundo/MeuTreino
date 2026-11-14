package com.example.meutreino.data.repository;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountSecurityRepository {
    public interface OperationCallback {
        void onResult(boolean success, String message);
    }

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public AccountSecurityRepository() {
        this(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());
    }

    public AccountSecurityRepository(FirebaseAuth auth, FirebaseFirestore db) {
        this.auth = auth;
        this.db = db;
    }

    public String getCurrentEmail() {
        FirebaseUser user = auth.getCurrentUser();
        return user == null || user.getEmail() == null ? "" : user.getEmail();
    }

    public void changeEmail(String currentPassword, String newEmail, OperationCallback callback) {
        reauthenticate(currentPassword, (user, error) -> {
            if (error != null) {
                callback.onResult(false, error);
                return;
            }
            user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onResult(true,
                            "Enviamos um link de confirmação para o novo e-mail.");
                } else {
                    callback.onResult(false, messageOr(task.getException(),
                            "Não foi possível solicitar a troca do e-mail."));
                }
            });
        });
    }

    public void changePassword(String currentPassword, String newPassword,
                               OperationCallback callback) {
        reauthenticate(currentPassword, (user, error) -> {
            if (error != null) {
                callback.onResult(false, error);
                return;
            }
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onResult(true, "Senha alterada com sucesso.");
                } else {
                    callback.onResult(false, messageOr(task.getException(),
                            "Não foi possível alterar a senha."));
                }
            });
        });
    }

    public void deleteAccount(String currentPassword, OperationCallback callback) {
        reauthenticate(currentPassword, (user, error) -> {
            if (error != null) {
                callback.onResult(false, error);
                return;
            }
            String uid = user.getUid();
            db.collection("usuarios").document(uid).delete()
                    .addOnSuccessListener(unused -> user.delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onResult(true, "Conta excluída.");
                        } else {
                            callback.onResult(false, messageOr(task.getException(),
                                    "Os dados do perfil foram removidos, mas não foi possível "
                                            + "excluir a conta. Entre novamente e tente outra vez."));
                        }
                    }))
                    .addOnFailureListener(exception -> callback.onResult(false,
                            messageOr(exception, "Não foi possível remover os dados da conta.")));
        });
    }

    private void reauthenticate(String currentPassword, ReauthenticationCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            callback.onResult(null, "Não foi possível identificar a conta atual.");
            return;
        }
        user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), currentPassword))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onResult(user, null);
                    } else {
                        callback.onResult(null, messageOr(task.getException(),
                                "A senha atual está incorreta ou a sessão expirou."));
                    }
                });
    }

    private interface ReauthenticationCallback {
        void onResult(FirebaseUser user, String error);
    }

    private static String messageOr(Exception exception, String fallback) {
        String message = exception == null ? null : exception.getLocalizedMessage();
        return message == null || message.trim().isEmpty() ? fallback : message;
    }
}
