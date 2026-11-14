package com.example.meutreino.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meutreino.data.repository.AccountSecurityRepository;

public class AccountSecurityViewModel extends ViewModel {
    public enum Status {
        IDLE, LOADING, SUCCESS, ERROR
    }

    public static class State {
        public final Status status;
        public final String message;

        private State(Status status, String message) {
            this.status = status;
            this.message = message;
        }
    }

    private final AccountSecurityRepository repository;
    private final MutableLiveData<State> state = new MutableLiveData<>();

    public AccountSecurityViewModel() {
        this(new AccountSecurityRepository());
    }

    public AccountSecurityViewModel(AccountSecurityRepository repository) {
        this.repository = repository;
    }

    public LiveData<State> getState() {
        return state;
    }

    public String getCurrentEmail() {
        return repository.getCurrentEmail();
    }

    public void changeEmail(String currentPassword, String newEmail) {
        if (newEmail.trim().isEmpty()) {
            setState(Status.ERROR, "Digite o novo e-mail.");
            return;
        }
        runOperation(callback -> repository.changeEmail(currentPassword, newEmail, callback));
    }

    public void changePassword(String currentPassword, String newPassword) {
        if (newPassword.length() < 6) {
            setState(Status.ERROR, "A nova senha deve conter no mínimo 6 caracteres.");
            return;
        }
        runOperation(callback -> repository.changePassword(
                currentPassword, newPassword, callback));
    }

    public void deleteAccount(String currentPassword) {
        runOperation(callback -> repository.deleteAccount(currentPassword, callback));
    }

    private void runOperation(Operation operation) {
        setState(Status.LOADING, null);
        operation.run((success, message) ->
                setState(success ? Status.SUCCESS : Status.ERROR, message));
    }

    private void setState(Status status, String message) {
        state.setValue(new State(status, message));
    }

    private interface Operation {
        void run(AccountSecurityRepository.OperationCallback callback);
    }
}
