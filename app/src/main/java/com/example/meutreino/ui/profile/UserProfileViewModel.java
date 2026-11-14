package com.example.meutreino.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meutreino.data.model.User;
import com.example.meutreino.data.repository.UserRepository;

public class UserProfileViewModel extends ViewModel {
    public enum Status {
        IDLE, LOADING, SUCCESS, SAVED, ERROR
    }

    public static class UserState {
        public final Status status;
        public final User user;
        public final String message;

        private UserState(Status status, User user, String message) {
            this.status = status;
            this.user = user;
            this.message = message;
        }
    }

    private final UserRepository userRepository;
    private final MutableLiveData<UserState> userState = new MutableLiveData<>();

    public UserProfileViewModel() {
        this(new UserRepository());
    }

    public UserProfileViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<UserState> getUserState() {
        return userState;
    }

    public void loadUserProfile() {
        setState(Status.LOADING, null, null);
        userRepository.getUserProfile((user, error) -> {
            if (user != null) {
                setState(Status.SUCCESS, user, null);
            } else {
                setState(Status.ERROR, null, messageOr(error, "Erro ao carregar perfil"));
            }
        });
    }

    public void saveUserProfile(String nome, String strPeso, String strAltura,
                                String dataNascimento) {
        if (nome.trim().isEmpty() || strPeso.trim().isEmpty()
                || strAltura.trim().isEmpty() || dataNascimento.trim().isEmpty()) {
            setState(Status.ERROR, null, "Preencha todos os campos para atualizar o perfil");
            return;
        }

        double peso;
        double altura;
        try {
            peso = Double.parseDouble(strPeso.replace(',', '.'));
            altura = Double.parseDouble(strAltura.replace(',', '.'));
        } catch (NumberFormatException exception) {
            setState(Status.ERROR, null, "Por favor, digite valores numéricos válidos");
            return;
        }
        int idade = User.calculateAge(dataNascimento);
        if (peso <= 0 || altura <= 0 || idade < 0) {
            setState(Status.ERROR, null, "Por favor, digite valores numéricos válidos");
            return;
        }

        setState(Status.LOADING, null, null);
        userRepository.saveUserProfile(nome, peso, altura, dataNascimento, (success, error) -> {
            if (success) {
                setState(Status.SAVED,
                        new User("", nome, peso, altura, idade, dataNascimento), null);
            } else {
                setState(Status.ERROR, null, messageOr(error, "Erro ao salvar perfil"));
            }
        });
    }

    public void selectVideo(String videoKey, Runnable onComplete) {
        userRepository.saveSelectedVideoKey(videoKey, (success, error) -> {
            if (success) {
                onComplete.run();
            } else {
                setState(Status.ERROR, null, messageOr(error, "Erro ao selecionar o vídeo"));
            }
        });
    }

    private void setState(Status status, User user, String message) {
        userState.setValue(new UserState(status, user, message));
    }

    private static String messageOr(String message, String fallback) {
        return message == null || message.trim().isEmpty() ? fallback : message;
    }
}
