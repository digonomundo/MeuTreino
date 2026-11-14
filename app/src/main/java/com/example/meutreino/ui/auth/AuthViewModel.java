package com.example.meutreino.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meutreino.data.repository.AuthRepository;

public class AuthViewModel extends ViewModel {
    public enum Status {
        IDLE, LOADING, SUCCESS, PASSWORD_RESET_SENT, ERROR
    }

    public static class AuthState {
        public final Status status;
        public final String message;

        private AuthState(Status status, String message) {
            this.status = status;
            this.message = message;
        }
    }

    private final AuthRepository authRepository;
    private final MutableLiveData<AuthState> authState = new MutableLiveData<>();

    public AuthViewModel() {
        this(new AuthRepository());
    }

    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<AuthState> getAuthState() {
        return authState;
    }

    public boolean isUserLoggedIn() {
        return authRepository.isUserLoggedIn();
    }

    public void login(String email, String password) {
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            setState(Status.ERROR, "Preencha todos os campos");
            return;
        }
        setState(Status.LOADING, null);
        authRepository.login(email, password, (success, error) ->
                setState(success ? Status.SUCCESS : Status.ERROR,
                        success ? null : messageOr(error, "Erro ao realizar login")));
    }

    public void register(String email, String password) {
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            setState(Status.ERROR, "Preencha todos os campos");
            return;
        }
        if (password.length() < 6) {
            setState(Status.ERROR, "A senha deve conter no mínimo 6 caracteres");
            return;
        }
        setState(Status.LOADING, null);
        authRepository.register(email, password, (success, error) ->
                setState(success ? Status.SUCCESS : Status.ERROR,
                        success ? null : messageOr(error, "Erro ao realizar cadastro")));
    }

    public void resetPassword(String email) {
        if (email.trim().isEmpty()) {
            setState(Status.ERROR, "Digite seu e-mail");
            return;
        }
        setState(Status.LOADING, null);
        authRepository.resetPassword(email, (success, error) ->
                setState(success ? Status.PASSWORD_RESET_SENT : Status.ERROR,
                        success ? null : messageOr(error, "Erro ao enviar e-mail de recuperação")));
    }

    public void logout() {
        authRepository.logout();
    }

    private void setState(Status status, String message) {
        authState.setValue(new AuthState(status, message));
    }

    private static String messageOr(String message, String fallback) {
        return message == null || message.trim().isEmpty() ? fallback : message;
    }
}
