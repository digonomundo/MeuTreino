package com.example.meutreino.ui.workout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meutreino.data.model.Exercise;
import com.example.meutreino.data.model.WorkoutCategory;
import com.example.meutreino.data.repository.WorkoutRepository;

import java.util.List;

public class WorkoutViewModel extends ViewModel {
    public enum Status {
        IDLE, LOADING, SUCCESS, ERROR
    }

    public static class ExercisesState {
        public final Status status;
        public final List<Exercise> exercises;
        public final String message;

        private ExercisesState(Status status, List<Exercise> exercises, String message) {
            this.status = status;
            this.exercises = exercises;
            this.message = message;
        }
    }

    private final WorkoutRepository workoutRepository;
    private final MutableLiveData<ExercisesState> exercisesState = new MutableLiveData<>();

    public WorkoutViewModel() {
        this(new WorkoutRepository());
    }

    public WorkoutViewModel(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public LiveData<ExercisesState> getExercisesState() {
        return exercisesState;
    }

    public void loadExercises(WorkoutCategory category) {
        setState(Status.LOADING, null, null);
        workoutRepository.getExercisesForCategory(category, (exercises, error) -> {
            if (error != null && exercises.isEmpty()) {
                setState(Status.ERROR, null, error);
            } else {
                setState(Status.SUCCESS, exercises, null);
            }
        });
    }

    public void toggleExercise(Exercise exercise, boolean selected) {
        workoutRepository.toggleExerciseSelection(exercise, selected, (success, error) -> {
            if (!success) {
                setState(Status.ERROR, null, messageOr(error, "Erro ao salvar exercício"));
            }
        });
    }

    public void saveSeriesTotal(Exercise exercise, int total) {
        workoutRepository.saveSeriesTotal(exercise, total, (success, error) -> {
            if (!success) {
                setState(Status.ERROR, null, messageOr(error, "Erro ao salvar a quantidade de séries"));
            }
        });
    }

    private void setState(Status status, List<Exercise> exercises, String message) {
        exercisesState.setValue(new ExercisesState(status, exercises, message));
    }

    private static String messageOr(String message, String fallback) {
        return message == null || message.trim().isEmpty() ? fallback : message;
    }
}
